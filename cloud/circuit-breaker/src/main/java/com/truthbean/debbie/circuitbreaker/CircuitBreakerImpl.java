/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker;

import com.truthbean.debbie.circuitbreaker.event.CircuitBreakerStateTransitionEvent;
import com.truthbean.debbie.circuitbreaker.fallback.FallbackHandler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default {@link CircuitBreaker} implementation with a sliding-window counter
 * and a three-state machine (CLOSED → OPEN → HALF_OPEN → CLOSED/OPEN).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CircuitBreakerImpl implements CircuitBreaker {

    private final String name;
    private final CircuitBreakerConfig config;
    private final List<CircuitBreakerListener> listeners = new CopyOnWriteArrayList<>();

    private final AtomicReference<CircuitBreakerState> state = new AtomicReference<>(CircuitBreakerState.CLOSED);
    private volatile long openedAt = 0L;

    private final AtomicInteger[] slidingWindow;
    private final AtomicInteger windowIndex = new AtomicInteger(0);
    private final AtomicInteger bufferedCalls = new AtomicInteger(0);
    private final AtomicInteger failedCalls = new AtomicInteger(0);
    private final AtomicInteger slowCalls = new AtomicInteger(0);

    private final AtomicInteger halfOpenCallCount = new AtomicInteger(0);
    private final AtomicInteger halfOpenSuccessCount = new AtomicInteger(0);
    private final AtomicInteger halfOpenFailureCount = new AtomicInteger(0);

    public CircuitBreakerImpl(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
        int size = config.getSlidingWindowSize();
        this.slidingWindow = new AtomicInteger[size];
        for (int i = 0; i < size; i++) {
            slidingWindow[i] = new AtomicInteger(0);
        }
    }

    @Override
    public <T> T execute(Callable<T> task) throws Exception {
        acquirePermission();
        long start = System.currentTimeMillis();
        try {
            T result = task.call();
            long duration = System.currentTimeMillis() - start;
            onSuccess(duration);
            return result;
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            onError(t, duration);
            if (t instanceof Exception e) {
                throw e;
            }
            if (t instanceof Error e) {
                throw e;
            }
            throw new RuntimeException(t);
        }
    }

    @Override
    public <T> T execute(Callable<T> task, FallbackHandler<T> fallback) {
        try {
            return execute(task);
        } catch (CircuitBreakerOpenException e) {
            notifyCallRejected();
            return fallback.handle(null);
        } catch (Throwable t) {
            return fallback.handle(t);
        }
    }

    @Override
    public void execute(Runnable task) throws Exception {
        execute(() -> {
            task.run();
            return null;
        });
    }

    @Override
    public void execute(Runnable task, Runnable fallback) {
        try {
            execute(task);
        } catch (CircuitBreakerOpenException e) {
            notifyCallRejected();
            fallback.run();
        } catch (Throwable t) {
            fallback.run();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CircuitBreakerState getState() {
        CircuitBreakerState current = state.get();
        if (current == CircuitBreakerState.OPEN) {
            if (shouldTransitionToHalfOpen()) {
                if (state.compareAndSet(CircuitBreakerState.OPEN, CircuitBreakerState.HALF_OPEN)) {
                    halfOpenCallCount.set(0);
                    halfOpenSuccessCount.set(0);
                    halfOpenFailureCount.set(0);
                    notifyStateTransition(CircuitBreakerState.OPEN, CircuitBreakerState.HALF_OPEN);
                    return CircuitBreakerState.HALF_OPEN;
                }
                return state.get();
            }
        }
        return current;
    }

    @Override
    public CircuitBreakerConfig getConfig() {
        return config;
    }

    @Override
    public void reset() {
        CircuitBreakerState old = state.getAndSet(CircuitBreakerState.CLOSED);
        openedAt = 0L;
        for (AtomicInteger ai : slidingWindow) {
            ai.set(0);
        }
        windowIndex.set(0);
        bufferedCalls.set(0);
        failedCalls.set(0);
        slowCalls.set(0);
        halfOpenCallCount.set(0);
        halfOpenSuccessCount.set(0);
        halfOpenFailureCount.set(0);
        if (old != CircuitBreakerState.CLOSED) {
            notifyStateTransition(old, CircuitBreakerState.CLOSED);
        }
    }

    @Override
    public float getFailureRate() {
        int buffered = bufferedCalls.get();
        if (buffered < config.getMinimumNumberOfCalls()) {
            return 0f;
        }
        return (failedCalls.get() * 100.0f) / buffered;
    }

    @Override
    public int getNumberOfBufferedCalls() {
        return bufferedCalls.get();
    }

    @Override
    public int getNumberOfFailedCalls() {
        return failedCalls.get();
    }

    @Override
    public int getNumberOfSuccessfulCalls() {
        return bufferedCalls.get() - failedCalls.get();
    }

    @Override
    public void addListener(CircuitBreakerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(CircuitBreakerListener listener) {
        listeners.remove(listener);
    }

    // ---- internal ----

    private void acquirePermission() {
        CircuitBreakerState current = getState();
        if (current == CircuitBreakerState.OPEN) {
            throw new CircuitBreakerOpenException(name, CircuitBreakerState.OPEN);
        }
        if (current == CircuitBreakerState.HALF_OPEN) {
            int count = halfOpenCallCount.incrementAndGet();
            if (count > config.getPermittedNumberOfCallsInHalfOpenState()) {
                throw new CircuitBreakerOpenException(name, CircuitBreakerState.HALF_OPEN);
            }
        }
    }

    private void onSuccess(long durationMillis) {
        boolean slow = durationMillis > config.getSlowCallDurationThresholdMillis();

        CircuitBreakerState current = state.get();
        if (current == CircuitBreakerState.HALF_OPEN) {
            halfOpenSuccessCount.incrementAndGet();
            evaluateHalfOpen();
            notifySuccess(durationMillis);
            return;
        }

        recordResult(false, slow);
        evaluateClosed();
        notifySuccess(durationMillis);
    }

    private void onError(Throwable t, long durationMillis) {
        boolean slow = durationMillis > config.getSlowCallDurationThresholdMillis();

        CircuitBreakerState current = state.get();
        if (current == CircuitBreakerState.HALF_OPEN) {
            halfOpenFailureCount.incrementAndGet();
            evaluateHalfOpen();
            notifyError(t, durationMillis);
            return;
        }

        recordResult(true, slow);
        evaluateClosed();
        notifyError(t, durationMillis);
    }

    private void recordResult(boolean failed, boolean slow) {
        int idx = windowIndex.getAndUpdate(i -> (i + 1) % slidingWindow.length);
        int old = slidingWindow[idx].getAndSet(failed ? 1 : 0);
        if (old == 0 && failed) {
            failedCalls.incrementAndGet();
        } else if (old == 1 && !failed) {
            failedCalls.decrementAndGet();
        }
        int currentBuffered = bufferedCalls.incrementAndGet();
        if (currentBuffered > slidingWindow.length) {
            bufferedCalls.set(slidingWindow.length);
        }
        if (slow) {
            slowCalls.incrementAndGet();
        }
    }

    private void evaluateClosed() {
        CircuitBreakerState current = state.get();
        if (current != CircuitBreakerState.CLOSED) {
            return;
        }
        int buffered = bufferedCalls.get();
        if (buffered < config.getMinimumNumberOfCalls()) {
            return;
        }
        float failureRate = (failedCalls.get() * 100.0f) / buffered;
        float slowRate = (slowCalls.get() * 100.0f) / buffered;
        if (failureRate >= config.getFailureRateThreshold()
                || slowRate >= config.getSlowCallRateThreshold()) {
            if (state.compareAndSet(CircuitBreakerState.CLOSED, CircuitBreakerState.OPEN)) {
                openedAt = System.currentTimeMillis();
                notifyStateTransition(CircuitBreakerState.CLOSED, CircuitBreakerState.OPEN);
            }
        }
    }

    private void evaluateHalfOpen() {
        int permitted = config.getPermittedNumberOfCallsInHalfOpenState();
        int calls = halfOpenCallCount.get();
        if (calls < permitted) {
            return;
        }
        int successes = halfOpenSuccessCount.get();
        int failures = halfOpenFailureCount.get();
        int threshold = (int) Math.ceil(permitted * (config.getFailureRateThreshold() / 100.0f));
        if (failures >= threshold && failures > 0) {
            if (state.compareAndSet(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.OPEN)) {
                openedAt = System.currentTimeMillis();
                notifyStateTransition(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.OPEN);
            }
        } else if (successes + failures >= permitted) {
            if (state.compareAndSet(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.CLOSED)) {
                clearWindow();
                notifyStateTransition(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.CLOSED);
            }
        }
    }

    private boolean shouldTransitionToHalfOpen() {
        long elapsed = System.currentTimeMillis() - openedAt;
        if (elapsed >= config.getWaitDurationInOpenStateMillis()) {
            if (config.isAutomaticTransitionFromOpenToHalfOpen()) {
                return true;
            }
            return true;
        }
        return false;
    }

    private void clearWindow() {
        for (AtomicInteger ai : slidingWindow) {
            ai.set(0);
        }
        windowIndex.set(0);
        bufferedCalls.set(0);
        failedCalls.set(0);
        slowCalls.set(0);
    }

    private void notifyStateTransition(CircuitBreakerState from, CircuitBreakerState to) {
        var event = new CircuitBreakerStateTransitionEvent(this, name, from, to);
        for (var listener : listeners) {
            try {
                listener.onStateTransition(event);
            } catch (Exception ignored) {
            }
        }
    }

    private void notifySuccess(long durationMillis) {
        for (var listener : listeners) {
            try {
                listener.onSuccess(durationMillis);
            } catch (Exception ignored) {
            }
        }
    }

    private void notifyError(Throwable t, long durationMillis) {
        for (var listener : listeners) {
            try {
                listener.onError(t, durationMillis);
            } catch (Exception ignored) {
            }
        }
    }

    private void notifyCallRejected() {
        for (var listener : listeners) {
            try {
                listener.onCallRejected();
            } catch (Exception ignored) {
            }
        }
    }
}
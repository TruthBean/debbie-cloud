/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker.test;

import com.truthbean.debbie.circuitbreaker.CircuitBreaker;
import com.truthbean.debbie.circuitbreaker.CircuitBreakerConfig;
import com.truthbean.debbie.circuitbreaker.CircuitBreakerImpl;
import com.truthbean.debbie.circuitbreaker.CircuitBreakerOpenException;
import com.truthbean.debbie.circuitbreaker.CircuitBreakerRegistry;
import com.truthbean.debbie.circuitbreaker.CircuitBreakerState;
import com.truthbean.debbie.circuitbreaker.event.CircuitBreakerStateTransitionEvent;
import com.truthbean.debbie.circuitbreaker.fallback.DefaultFallbackHandler;
import com.truthbean.debbie.circuitbreaker.fallback.FallbackHandler;
import com.truthbean.debbie.circuitbreaker.ratelimit.RateLimiter;
import com.truthbean.debbie.circuitbreaker.ratelimit.RateLimiterConfig;
import com.truthbean.debbie.circuitbreaker.ratelimit.SimpleRateLimiter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CircuitBreakerTest {

    @Test
    public void circuitBreakerShouldStartClosed() {
        var cb = new CircuitBreakerImpl("test", CircuitBreakerConfig.ofDefaults());
        assertEquals(CircuitBreakerState.CLOSED, cb.getState());
        assertEquals(0, cb.getNumberOfBufferedCalls());
        assertEquals(0, cb.getNumberOfFailedCalls());
    }

    @Test
    public void shouldExecuteSuccessfulCall() throws Exception {
        var cb = new CircuitBreakerImpl("test", CircuitBreakerConfig.ofDefaults());
        String result = cb.execute(() -> "hello");
        assertEquals("hello", result);
        assertEquals(1, cb.getNumberOfBufferedCalls());
        assertEquals(0, cb.getNumberOfFailedCalls());
        assertEquals(1, cb.getNumberOfSuccessfulCalls());
    }

    @Test
    public void shouldCountFailures() {
        var cb = new CircuitBreakerImpl("test", CircuitBreakerConfig.ofDefaults());
        for (int i = 0; i < 5; i++) {
            assertThrows(RuntimeException.class, () -> cb.execute(() -> {
                throw new RuntimeException("fail");
            }));
        }
        assertEquals(5, cb.getNumberOfBufferedCalls());
        assertEquals(5, cb.getNumberOfFailedCalls());
    }

    @Test
    public void shouldOpenWhenFailureRateExceedsThreshold() throws Exception {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(4)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(60_000)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        cb.execute(() -> "ok");
        cb.execute(() -> "ok");
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));

        assertEquals(CircuitBreakerState.OPEN, cb.getState());
    }

    @Test
    public void shouldRejectCallsWhenOpen() {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(60_000)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertEquals(CircuitBreakerState.OPEN, cb.getState());

        assertThrows(CircuitBreakerOpenException.class, () -> cb.execute(() -> "should-not-reach"));
    }

    @Test
    public void shouldTransitionToHalfOpenAfterWaitDuration() throws InterruptedException {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(100)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertEquals(CircuitBreakerState.OPEN, cb.getState());

        Thread.sleep(150);
        assertEquals(CircuitBreakerState.HALF_OPEN, cb.getState());
    }

    @Test
    public void shouldCloseAfterHalfOpenSuccesses() throws Exception {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(50)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertEquals(CircuitBreakerState.OPEN, cb.getState());

        Thread.sleep(60);
        assertEquals(CircuitBreakerState.HALF_OPEN, cb.getState());

        cb.execute(() -> "ok1");
        cb.execute(() -> "ok2");
        cb.execute(() -> "ok3");

        assertEquals(CircuitBreakerState.CLOSED, cb.getState());
    }

    @Test
    public void shouldReopenAfterHalfOpenFailures() throws Exception {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(50)
                .permittedNumberOfCallsInHalfOpenState(4)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertEquals(CircuitBreakerState.OPEN, cb.getState());

        Thread.sleep(60);
        assertEquals(CircuitBreakerState.HALF_OPEN, cb.getState());

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));

        assertEquals(CircuitBreakerState.OPEN, cb.getState());
    }

    @Test
    public void fallbackShouldReturnDefaultValue() {
        var cb = new CircuitBreakerImpl("test", CircuitBreakerConfig.ofDefaults());
        FallbackHandler<String> fallback = new DefaultFallbackHandler<>("fallback-value");
        String result = cb.execute(() -> { throw new RuntimeException("fail"); }, fallback);
        assertEquals("fallback-value", result);
    }

    @Test
    public void fallbackShouldBeUsedWhenCircuitOpen() {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(60_000)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));

        String result = cb.execute(() -> "should-not-reach", t -> "fallback");
        assertEquals("fallback", result);
    }

    @Test
    public void runnableWithFallbackShouldWork() {
        var cb = new CircuitBreakerImpl("test", CircuitBreakerConfig.ofDefaults());
        var executed = new boolean[]{false};
        cb.execute(() -> { executed[0] = true; }, () -> {});
        assertTrue(executed[0]);
    }

    @Test
    public void runnableFallbackShouldRunOnFailure() {
        var cb = new CircuitBreakerImpl("test", CircuitBreakerConfig.ofDefaults());
        var fallbackExecuted = new boolean[]{false};
        cb.execute(() -> { throw new RuntimeException("fail"); }, () -> { fallbackExecuted[0] = true; });
        assertTrue(fallbackExecuted[0]);
    }

    @Test
    public void resetShouldClearState() {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(60_000)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertEquals(CircuitBreakerState.OPEN, cb.getState());

        cb.reset();
        assertEquals(CircuitBreakerState.CLOSED, cb.getState());
        assertEquals(0, cb.getNumberOfBufferedCalls());
        assertEquals(0, cb.getNumberOfFailedCalls());
    }

    @Test
    public void listenerShouldReceiveStateTransitions() {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(10)
                .waitDurationInOpenStateMillis(60_000)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        List<CircuitBreakerStateTransitionEvent> events = new ArrayList<>();
        cb.addListener(new com.truthbean.debbie.circuitbreaker.CircuitBreakerListener() {
            @Override
            public void onStateTransition(CircuitBreakerStateTransitionEvent event) {
                events.add(event);
            }
        });

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));

        assertFalse(events.isEmpty());
        var last = events.get(events.size() - 1);
        assertEquals(CircuitBreakerState.CLOSED, last.getFromState());
        assertEquals(CircuitBreakerState.OPEN, last.getState());
        assertEquals("test", last.getCircuitBreakerName());
    }

    @Test
    public void registryShouldCreateAndCacheBreakers() {
        var registry = new CircuitBreakerRegistry();
        var cb1 = registry.getOrCreate("service-a");
        var cb2 = registry.getOrCreate("service-a");
        assertSame(cb1, cb2);

        var cb3 = registry.getOrCreate("service-b");
        assertNotSame(cb1, cb3);
        assertEquals(2, registry.getAll().size());
    }

    @Test
    public void registryShouldCreateRateLimiters() {
        var registry = new CircuitBreakerRegistry();
        var rl1 = registry.getOrCreateRateLimiter("api");
        var rl2 = registry.getOrCreateRateLimiter("api");
        assertSame(rl1, rl2);
        assertEquals("api", rl1.getName());
    }

    @Test
    public void rateLimiterShouldAllowUpToLimit() {
        var config = RateLimiterConfig.builder()
                .limitForPeriod(3)
                .limitRefreshPeriodMillis(60_000)
                .build();
        RateLimiter limiter = new SimpleRateLimiter("test", config);

        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());
    }

    @Test
    public void rateLimiterShouldRefillAfterPeriod() throws InterruptedException {
        var config = RateLimiterConfig.builder()
                .limitForPeriod(2)
                .limitRefreshPeriodMillis(100)
                .build();
        RateLimiter limiter = new SimpleRateLimiter("test", config);

        assertTrue(limiter.tryAcquire(2));
        assertFalse(limiter.tryAcquire());

        Thread.sleep(120);
        assertTrue(limiter.tryAcquire());
    }

    @Test
    public void configBuilderShouldValidateFailureRateThreshold() {
        assertThrows(IllegalArgumentException.class,
                () -> CircuitBreakerConfig.builder().failureRateThreshold(0));
        assertThrows(IllegalArgumentException.class,
                () -> CircuitBreakerConfig.builder().failureRateThreshold(101));
    }

    @Test
    public void configBuilderShouldValidatePositiveValues() {
        assertThrows(IllegalArgumentException.class,
                () -> CircuitBreakerConfig.builder().waitDurationInOpenStateMillis(0));
        assertThrows(IllegalArgumentException.class,
                () -> CircuitBreakerConfig.builder().slidingWindowSize(0));
        assertThrows(IllegalArgumentException.class,
                () -> CircuitBreakerConfig.builder().minimumNumberOfCalls(-1));
    }

    @Test
    public void failureRateShouldBeZeroBeforeMinimumCalls() {
        var config = CircuitBreakerConfig.builder()
                .failureRateThreshold(50f)
                .minimumNumberOfCalls(10)
                .slidingWindowSize(20)
                .build();
        var cb = new CircuitBreakerImpl("test", config);

        assertThrows(RuntimeException.class, () -> cb.execute(() -> { throw new RuntimeException("fail"); }));
        assertEquals(0f, cb.getFailureRate(), 0.001f);
    }
}
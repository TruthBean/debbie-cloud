/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker.ratelimit;

import java.util.concurrent.locks.ReentrantLock;

/**
 * A token-bucket rate limiter.
 * <p>
 * The bucket is refilled to {@code limitForPeriod} every {@code limitRefreshPeriodMillis}.
 * Calls consume one (or more) tokens; if not enough tokens are available the call is
 * either rejected ({@link #tryAcquire}) or blocked ({@link #acquire}).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SimpleRateLimiter implements RateLimiter {

    private final String name;
    private final int limitForPeriod;
    private final long limitRefreshPeriodMillis;

    private final ReentrantLock lock = new ReentrantLock();
    private int availableTokens;
    private long lastRefillTime;

    public SimpleRateLimiter(String name, RateLimiterConfig config) {
        this.name = name;
        this.limitForPeriod = config.getLimitForPeriod();
        this.limitRefreshPeriodMillis = config.getLimitRefreshPeriodMillis();
        this.availableTokens = limitForPeriod;
        this.lastRefillTime = System.currentTimeMillis();
    }

    @Override
    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    @Override
    public boolean tryAcquire(int permits) {
        if (permits <= 0) {
            return true;
        }
        lock.lock();
        try {
            refillIfNeeded();
            if (availableTokens >= permits) {
                availableTokens -= permits;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void acquire() throws InterruptedException {
        acquire(1);
    }

    @Override
    public void acquire(int permits) throws InterruptedException {
        if (permits <= 0) {
            return;
        }
        while (!tryAcquire(permits)) {
            long waitMillis;
            lock.lock();
            try {
                waitMillis = limitRefreshPeriodMillis - (System.currentTimeMillis() - lastRefillTime);
            } finally {
                lock.unlock();
            }
            if (waitMillis > 0) {
                Thread.sleep(Math.min(waitMillis, 10));
            } else {
                Thread.sleep(1);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    private void refillIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastRefillTime >= limitRefreshPeriodMillis) {
            availableTokens = limitForPeriod;
            lastRefillTime = now;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    @Override
    public long getLimitRefreshPeriodMillis() {
        return limitRefreshPeriodMillis;
    }

    public int getAvailableTokens() {
        lock.lock();
        try {
            refillIfNeeded();
            return availableTokens;
        } finally {
            lock.unlock();
        }
    }
}
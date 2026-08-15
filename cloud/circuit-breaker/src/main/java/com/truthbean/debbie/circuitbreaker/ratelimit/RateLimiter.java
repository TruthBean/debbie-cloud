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

/**
 * Rate limiter abstraction.
 * <p>
 * Implementations control the rate at which calls are permitted, typically
 * using a token-bucket or sliding-window algorithm.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface RateLimiter {

    /**
     * Try to acquire one permit without blocking.
     *
     * @return {@code true} if a permit was acquired, {@code false} if rate limited
     */
    boolean tryAcquire();

    /**
     * Try to acquire {@code permits} permits without blocking.
     *
     * @param permits number of permits to acquire
     * @return {@code true} if all permits were acquired, {@code false} if rate limited
     */
    boolean tryAcquire(int permits);

    /**
     * Acquire one permit, blocking until a permit is available.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void acquire() throws InterruptedException;

    /**
     * Acquire {@code permits} permits, blocking until all are available.
     *
     * @param permits number of permits to acquire
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    void acquire(int permits) throws InterruptedException;

    /**
     * @return the name of this rate limiter
     */
    String getName();

    /**
     * @return the configured limit for each refresh period
     */
    int getLimitForPeriod();

    /**
     * @return the refresh period in milliseconds
     */
    long getLimitRefreshPeriodMillis();
}
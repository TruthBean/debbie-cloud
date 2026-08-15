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
 * Immutable configuration for a {@link RateLimiter}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class RateLimiterConfig {

    private final int limitForPeriod;
    private final long limitRefreshPeriodMillis;

    private RateLimiterConfig(Builder b) {
        this.limitForPeriod = b.limitForPeriod;
        this.limitRefreshPeriodMillis = b.limitRefreshPeriodMillis;
    }

    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    public long getLimitRefreshPeriodMillis() {
        return limitRefreshPeriodMillis;
    }

    public static RateLimiterConfig ofDefaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int limitForPeriod = 50;
        private long limitRefreshPeriodMillis = 1_000L;

        public Builder limitForPeriod(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("limitForPeriod must be > 0, got " + value);
            }
            this.limitForPeriod = value;
            return this;
        }

        public Builder limitRefreshPeriodMillis(long value) {
            if (value <= 0) {
                throw new IllegalArgumentException("limitRefreshPeriodMillis must be > 0, got " + value);
            }
            this.limitRefreshPeriodMillis = value;
            return this;
        }

        public RateLimiterConfig build() {
            return new RateLimiterConfig(this);
        }
    }
}
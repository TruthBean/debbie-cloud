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

/**
 * Per-circuit-breaker configuration.
 * <p>
 * Instances are immutable after construction. Use {@link #builder()} to create
 * a custom configuration, or {@link #ofDefaults()} for sensible defaults.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class CircuitBreakerConfig {

    private final float failureRateThreshold;
    private final float slowCallRateThreshold;
    private final long waitDurationInOpenStateMillis;
    private final int permittedNumberOfCallsInHalfOpenState;
    private final int slidingWindowSize;
    private final int minimumNumberOfCalls;
    private final long slowCallDurationThresholdMillis;
    private final boolean automaticTransitionFromOpenToHalfOpen;

    private CircuitBreakerConfig(Builder b) {
        this.failureRateThreshold = b.failureRateThreshold;
        this.slowCallRateThreshold = b.slowCallRateThreshold;
        this.waitDurationInOpenStateMillis = b.waitDurationInOpenStateMillis;
        this.permittedNumberOfCallsInHalfOpenState = b.permittedNumberOfCallsInHalfOpenState;
        this.slidingWindowSize = b.slidingWindowSize;
        this.minimumNumberOfCalls = b.minimumNumberOfCalls;
        this.slowCallDurationThresholdMillis = b.slowCallDurationThresholdMillis;
        this.automaticTransitionFromOpenToHalfOpen = b.automaticTransitionFromOpenToHalfOpen;
    }

    public float getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public float getSlowCallRateThreshold() {
        return slowCallRateThreshold;
    }

    public long getWaitDurationInOpenStateMillis() {
        return waitDurationInOpenStateMillis;
    }

    public int getPermittedNumberOfCallsInHalfOpenState() {
        return permittedNumberOfCallsInHalfOpenState;
    }

    public int getSlidingWindowSize() {
        return slidingWindowSize;
    }

    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    public long getSlowCallDurationThresholdMillis() {
        return slowCallDurationThresholdMillis;
    }

    public boolean isAutomaticTransitionFromOpenToHalfOpen() {
        return automaticTransitionFromOpenToHalfOpen;
    }

    public static CircuitBreakerConfig ofDefaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float failureRateThreshold = 50.0f;
        private float slowCallRateThreshold = 100.0f;
        private long waitDurationInOpenStateMillis = 60_000L;
        private int permittedNumberOfCallsInHalfOpenState = 10;
        private int slidingWindowSize = 100;
        private int minimumNumberOfCalls = 10;
        private long slowCallDurationThresholdMillis = 60_000L;
        private boolean automaticTransitionFromOpenToHalfOpen = false;

        public Builder failureRateThreshold(float value) {
            if (value <= 0 || value > 100) {
                throw new IllegalArgumentException("failureRateThreshold must be in (0, 100], got " + value);
            }
            this.failureRateThreshold = value;
            return this;
        }

        public Builder slowCallRateThreshold(float value) {
            if (value <= 0 || value > 100) {
                throw new IllegalArgumentException("slowCallRateThreshold must be in (0, 100], got " + value);
            }
            this.slowCallRateThreshold = value;
            return this;
        }

        public Builder waitDurationInOpenStateMillis(long value) {
            if (value <= 0) {
                throw new IllegalArgumentException("waitDurationInOpenStateMillis must be > 0, got " + value);
            }
            this.waitDurationInOpenStateMillis = value;
            return this;
        }

        public Builder permittedNumberOfCallsInHalfOpenState(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("permittedNumberOfCallsInHalfOpenState must be > 0, got " + value);
            }
            this.permittedNumberOfCallsInHalfOpenState = value;
            return this;
        }

        public Builder slidingWindowSize(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("slidingWindowSize must be > 0, got " + value);
            }
            this.slidingWindowSize = value;
            return this;
        }

        public Builder minimumNumberOfCalls(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("minimumNumberOfCalls must be > 0, got " + value);
            }
            this.minimumNumberOfCalls = value;
            return this;
        }

        public Builder slowCallDurationThresholdMillis(long value) {
            if (value <= 0) {
                throw new IllegalArgumentException("slowCallDurationThresholdMillis must be > 0, got " + value);
            }
            this.slowCallDurationThresholdMillis = value;
            return this;
        }

        public Builder automaticTransitionFromOpenToHalfOpen(boolean value) {
            this.automaticTransitionFromOpenToHalfOpen = value;
            return this;
        }

        public CircuitBreakerConfig build() {
            return new CircuitBreakerConfig(this);
        }
    }
}
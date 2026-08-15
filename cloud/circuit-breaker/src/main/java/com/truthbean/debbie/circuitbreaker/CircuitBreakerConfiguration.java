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

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.FloatTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;

/**
 * Configuration of debbie-circuit-breaker.
 * <p>
 * properties prefix: {@code debbie.circuit-breaker}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.circuit-breaker")
public class CircuitBreakerConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "failure-rate-threshold", transformer = FloatTransformer.class, defaultValue = "50")
    private float failureRateThreshold = 50f;

    @PropertyInject(value = "slow-call-rate-threshold", transformer = FloatTransformer.class, defaultValue = "100")
    private float slowCallRateThreshold = 100f;

    @PropertyInject(value = "wait-duration-in-open-state", transformer = LongTransformer.class, defaultValue = "60000")
    private long waitDurationInOpenStateMillis = 60000L;

    @PropertyInject(value = "permitted-number-of-calls-in-half-open", transformer = IntegerTransformer.class, defaultValue = "10")
    private int permittedNumberOfCallsInHalfOpenState = 10;

    @PropertyInject(value = "sliding-window-size", transformer = IntegerTransformer.class, defaultValue = "100")
    private int slidingWindowSize = 100;

    @PropertyInject(value = "minimum-number-of-calls", transformer = IntegerTransformer.class, defaultValue = "10")
    private int minimumNumberOfCalls = 10;

    @PropertyInject(value = "slow-call-duration-threshold", transformer = LongTransformer.class, defaultValue = "60000")
    private long slowCallDurationThresholdMillis = 60000L;

    @PropertyInject(value = "automatic-transition-from-open-to-half-open", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean automaticTransitionFromOpenToHalfOpen = false;

    @PropertyInject(value = "rate-limiter.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean rateLimiterEnable = true;

    @PropertyInject(value = "rate-limiter.limit-for-period", transformer = IntegerTransformer.class, defaultValue = "50")
    private int rateLimiterLimitForPeriod = 50;

    @PropertyInject(value = "rate-limiter.limit-refresh-period", transformer = LongTransformer.class, defaultValue = "1000")
    private long rateLimiterLimitRefreshPeriodMillis = 1000L;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public float getFailureRateThreshold() { return failureRateThreshold; }
    public void setFailureRateThreshold(float failureRateThreshold) { this.failureRateThreshold = failureRateThreshold; }

    public float getSlowCallRateThreshold() { return slowCallRateThreshold; }
    public void setSlowCallRateThreshold(float slowCallRateThreshold) { this.slowCallRateThreshold = slowCallRateThreshold; }

    public long getWaitDurationInOpenStateMillis() { return waitDurationInOpenStateMillis; }
    public void setWaitDurationInOpenStateMillis(long waitDurationInOpenStateMillis) { this.waitDurationInOpenStateMillis = waitDurationInOpenStateMillis; }

    public int getPermittedNumberOfCallsInHalfOpenState() { return permittedNumberOfCallsInHalfOpenState; }
    public void setPermittedNumberOfCallsInHalfOpenState(int permittedNumberOfCallsInHalfOpenState) { this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState; }

    public int getSlidingWindowSize() { return slidingWindowSize; }
    public void setSlidingWindowSize(int slidingWindowSize) { this.slidingWindowSize = slidingWindowSize; }

    public int getMinimumNumberOfCalls() { return minimumNumberOfCalls; }
    public void setMinimumNumberOfCalls(int minimumNumberOfCalls) { this.minimumNumberOfCalls = minimumNumberOfCalls; }

    public long getSlowCallDurationThresholdMillis() { return slowCallDurationThresholdMillis; }
    public void setSlowCallDurationThresholdMillis(long slowCallDurationThresholdMillis) { this.slowCallDurationThresholdMillis = slowCallDurationThresholdMillis; }

    public boolean isAutomaticTransitionFromOpenToHalfOpen() { return automaticTransitionFromOpenToHalfOpen; }
    public void setAutomaticTransitionFromOpenToHalfOpen(boolean automaticTransitionFromOpenToHalfOpen) { this.automaticTransitionFromOpenToHalfOpen = automaticTransitionFromOpenToHalfOpen; }

    public boolean isRateLimiterEnable() { return rateLimiterEnable; }
    public void setRateLimiterEnable(boolean rateLimiterEnable) { this.rateLimiterEnable = rateLimiterEnable; }

    public int getRateLimiterLimitForPeriod() { return rateLimiterLimitForPeriod; }
    public void setRateLimiterLimitForPeriod(int rateLimiterLimitForPeriod) { this.rateLimiterLimitForPeriod = rateLimiterLimitForPeriod; }

    public long getRateLimiterLimitRefreshPeriodMillis() { return rateLimiterLimitRefreshPeriodMillis; }
    public void setRateLimiterLimitRefreshPeriodMillis(long rateLimiterLimitRefreshPeriodMillis) { this.rateLimiterLimitRefreshPeriodMillis = rateLimiterLimitRefreshPeriodMillis; }

    public CircuitBreakerConfig toCircuitBreakerConfig() {
        return CircuitBreakerConfig.builder()
                .failureRateThreshold(failureRateThreshold)
                .slowCallRateThreshold(slowCallRateThreshold)
                .waitDurationInOpenStateMillis(waitDurationInOpenStateMillis)
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .slidingWindowSize(slidingWindowSize)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .slowCallDurationThresholdMillis(slowCallDurationThresholdMillis)
                .automaticTransitionFromOpenToHalfOpen(automaticTransitionFromOpenToHalfOpen)
                .build();
    }

    public com.truthbean.debbie.circuitbreaker.ratelimit.RateLimiterConfig toRateLimiterConfig() {
        return com.truthbean.debbie.circuitbreaker.ratelimit.RateLimiterConfig.builder()
                .limitForPeriod(rateLimiterLimitForPeriod)
                .limitRefreshPeriodMillis(rateLimiterLimitRefreshPeriodMillis)
                .build();
    }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new CircuitBreakerConfiguration();
        c.enable = this.enable;
        c.failureRateThreshold = this.failureRateThreshold;
        c.slowCallRateThreshold = this.slowCallRateThreshold;
        c.waitDurationInOpenStateMillis = this.waitDurationInOpenStateMillis;
        c.permittedNumberOfCallsInHalfOpenState = this.permittedNumberOfCallsInHalfOpenState;
        c.slidingWindowSize = this.slidingWindowSize;
        c.minimumNumberOfCalls = this.minimumNumberOfCalls;
        c.slowCallDurationThresholdMillis = this.slowCallDurationThresholdMillis;
        c.automaticTransitionFromOpenToHalfOpen = this.automaticTransitionFromOpenToHalfOpen;
        c.rateLimiterEnable = this.rateLimiterEnable;
        c.rateLimiterLimitForPeriod = this.rateLimiterLimitForPeriod;
        c.rateLimiterLimitRefreshPeriodMillis = this.rateLimiterLimitRefreshPeriodMillis;
        return (T) c;
    }

    @Override
    public void close() {}
}
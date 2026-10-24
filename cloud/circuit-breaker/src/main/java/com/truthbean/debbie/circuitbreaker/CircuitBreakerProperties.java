/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-circuit-breaker.
 * <p>
 * Reads the {@link CircuitBreakerConfiguration} from the environment using the prefix
 * {@code debbie.circuit-breaker.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CircuitBreakerProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<CircuitBreakerConfiguration> {

    /**
     * key prefix of the circuit-breaker properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.circuit-breaker.sliding-window-size}
     */
    private static final String KEY_PREFIX = "debbie.circuit-breaker.";

    private final Map<String, CircuitBreakerConfiguration> map = new HashMap<>();
    private CircuitBreakerConfiguration configuration;

    public CircuitBreakerProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, CircuitBreakerConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return Map.of();
    }

    @Override
    public Set<String> getCategories(String profile) {
        return Set.of();
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return false;
    }

    @Override
    public CircuitBreakerConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public CircuitBreakerConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new CircuitBreakerConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setFailureRateThreshold(getFloatValue(KEY_PREFIX + "failure-rate-threshold", 50));
        configuration.setSlowCallRateThreshold(getFloatValue(KEY_PREFIX + "slow-call-rate-threshold", 100));
        configuration.setWaitDurationInOpenStateMillis(getLongValue(KEY_PREFIX + "wait-duration-in-open-state", 60000));
        configuration.setPermittedNumberOfCallsInHalfOpenState(getIntegerValue(KEY_PREFIX + "permitted-number-of-calls-in-half-open", 10));
        configuration.setSlidingWindowSize(getIntegerValue(KEY_PREFIX + "sliding-window-size", 100));
        configuration.setMinimumNumberOfCalls(getIntegerValue(KEY_PREFIX + "minimum-number-of-calls", 10));
        configuration.setSlowCallDurationThresholdMillis(getLongValue(KEY_PREFIX + "slow-call-duration-threshold", 60000));
        configuration.setAutomaticTransitionFromOpenToHalfOpen(getBooleanValue(KEY_PREFIX + "automatic-transition-from-open-to-half-open", false));

        // rate limiter
        configuration.setRateLimiterEnable(getBooleanValue(KEY_PREFIX + "rate-limiter.enable", true));
        configuration.setRateLimiterLimitForPeriod(getIntegerValue(KEY_PREFIX + "rate-limiter.limit-for-period", 50));
        configuration.setRateLimiterLimitRefreshPeriodMillis(getLongValue(KEY_PREFIX + "rate-limiter.limit-refresh-period", 1000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }

    /**
     * Read a float valued property, returning the given default when it is absent or blank.
     *
     * @param key          the full property key
     * @param defaultValue the value to use when the property is not configured
     * @return the configured float or the default
     */
    private float getFloatValue(String key, float defaultValue) {
        String value = getValue(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Float.parseFloat(value.trim());
    }
}
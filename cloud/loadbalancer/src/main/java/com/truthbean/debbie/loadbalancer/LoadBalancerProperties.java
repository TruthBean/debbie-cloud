/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.loadbalancer;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-loadbalancer.
 * <p>
 * Reads the {@link LoadBalancerConfiguration} from the environment using the prefix
 * {@code debbie.loadbalancer.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LoadBalancerProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<LoadBalancerConfiguration> {

    /**
     * key prefix of the loadbalancer properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.loadbalancer.default-strategy}
     */
    private static final String KEY_PREFIX = "debbie.loadbalancer.";

    private final Map<String, LoadBalancerConfiguration> map = new HashMap<>();
    private LoadBalancerConfiguration configuration;

    public LoadBalancerProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, LoadBalancerConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public LoadBalancerConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public LoadBalancerConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new LoadBalancerConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setDefaultStrategy(getStringValue(KEY_PREFIX + "default-strategy", "round-robin"));
        configuration.setHealthCheckEnable(getBooleanValue(KEY_PREFIX + "health-check.enable", false));
        configuration.setHealthCheckInterval(getIntegerValue(KEY_PREFIX + "health-check.interval", 10000));
        configuration.setRetryEnable(getBooleanValue(KEY_PREFIX + "retry.enable", true));
        configuration.setRetryMaxAttempts(getIntegerValue(KEY_PREFIX + "retry.max-attempts", 3));
        configuration.setStickyEnable(getBooleanValue(KEY_PREFIX + "sticky.enable", false));
        configuration.setCacheEnable(getBooleanValue(KEY_PREFIX + "cache.enable", true));
        configuration.setCacheTtl(getIntegerValue(KEY_PREFIX + "cache.ttl", 30000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
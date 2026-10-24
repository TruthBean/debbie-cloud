/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.redisson;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-redisson.
 * <p>
 * Reads the {@link RedissonConfiguration} from the environment using the prefix
 * {@code debbie.redisson.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RedissonProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<RedissonConfiguration> {

    /**
     * key prefix of the redisson properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.redisson.host}
     */
    private static final String KEY_PREFIX = "debbie.redisson.";

    private final Map<String, RedissonConfiguration> map = new HashMap<>();
    private RedissonConfiguration configuration;

    public RedissonProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, RedissonConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public RedissonConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public RedissonConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new RedissonConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 6379));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));
        configuration.setDatabase(getIntegerValue(KEY_PREFIX + "database", 0));
        configuration.setConnectTimeout(getIntegerValue(KEY_PREFIX + "connect-timeout", 10000));
        configuration.setTimeout(getIntegerValue(KEY_PREFIX + "timeout", 3000));
        configuration.setRetryAttempts(getIntegerValue(KEY_PREFIX + "retry-attempts", 3));
        configuration.setRetryInterval(getIntegerValue(KEY_PREFIX + "retry-interval", 1500));

        // connection pool settings
        configuration.setConnectionMinIdleSize(getIntegerValue(KEY_PREFIX + "connection-min-idle-size", 32));
        configuration.setConnectionPoolSize(getIntegerValue(KEY_PREFIX + "connection-pool-size", 64));
        configuration.setIdleConnectionTimeout(getIntegerValue(KEY_PREFIX + "idle-connection-timeout", 10000));
        configuration.setPingConnectionInterval(getIntegerValue(KEY_PREFIX + "ping-connection-interval", 30000));
        configuration.setKeepAlive(getBooleanValue(KEY_PREFIX + "keep-alive", false));
        configuration.setDnsMonitoringInterval(getLongValue(KEY_PREFIX + "dns-monitoring-interval", 5000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
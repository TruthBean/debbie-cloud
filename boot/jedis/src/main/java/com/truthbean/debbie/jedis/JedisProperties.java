/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jedis;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-jedis.
 * <p>
 * Reads the {@link JedisConfiguration} from the environment using the prefix
 * {@code debbie.jedis.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class JedisProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<JedisConfiguration> {

    /**
     * key prefix of the jedis properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.jedis.host}
     */
    private static final String KEY_PREFIX = "debbie.jedis.";

    private final Map<String, JedisConfiguration> map = new HashMap<>();
    private JedisConfiguration configuration;

    public JedisProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, JedisConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public JedisConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public JedisConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new JedisConfiguration();

        // connection
        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 6379));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));
        configuration.setDatabase(getIntegerValue(KEY_PREFIX + "database", 0));
        configuration.setTimeout(getIntegerValue(KEY_PREFIX + "timeout", 2000));

        // pool settings
        configuration.setMaxTotal(getIntegerValue(KEY_PREFIX + "max-total", 8));
        configuration.setMaxIdle(getIntegerValue(KEY_PREFIX + "max-idle", 8));
        configuration.setMinIdle(getIntegerValue(KEY_PREFIX + "min-idle", 0));
        configuration.setMaxWaitMillis(getLongValue(KEY_PREFIX + "max-wait-millis", -1));
        configuration.setBlockWhenExhausted(getBooleanValue(KEY_PREFIX + "block-when-exhausted", true));
        configuration.setTestOnBorrow(getBooleanValue(KEY_PREFIX + "test-on-borrow", false));
        configuration.setTestWhileIdle(getBooleanValue(KEY_PREFIX + "test-while-idle", false));
        configuration.setTestOnReturn(getBooleanValue(KEY_PREFIX + "test-on-return", false));
        configuration.setTimeBetweenEvictionRunsMillis(getLongValue(KEY_PREFIX + "time-between-eviction-runs-millis", 30000));
        configuration.setMinEvictableIdleTimeMillis(getLongValue(KEY_PREFIX + "min-evictable-idle-time-millis", 60000));
        configuration.setNumTestsPerEvictionRun(getIntegerValue(KEY_PREFIX + "num-tests-per-eviction-run", 3));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.zookeeper;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-zookeeper.
 * <p>
 * Reads the {@link ZookeeperConfiguration} from the environment using the prefix
 * {@code debbie.zookeeper.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ZookeeperProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<ZookeeperConfiguration> {

    /**
     * key prefix of the zookeeper properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.zookeeper.connection-string}
     */
    private static final String KEY_PREFIX = "debbie.zookeeper.";

    private final Map<String, ZookeeperConfiguration> map = new HashMap<>();
    private ZookeeperConfiguration configuration;

    public ZookeeperProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, ZookeeperConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public ZookeeperConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public ZookeeperConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ZookeeperConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setConnectionString(getStringValue(KEY_PREFIX + "connection-string", "localhost:2181"));
        configuration.setSessionTimeout(getIntegerValue(KEY_PREFIX + "session-timeout", 30000));
        configuration.setConnectionTimeout(getIntegerValue(KEY_PREFIX + "connection-timeout", 15000));
        configuration.setBaseSleepTimeMs(getIntegerValue(KEY_PREFIX + "base-sleep-time-ms", 1000));
        configuration.setMaxRetries(getIntegerValue(KEY_PREFIX + "max-retries", 3));
        configuration.setNamespace(getValue(KEY_PREFIX + "namespace"));
        configuration.setDigest(getValue(KEY_PREFIX + "digest"));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
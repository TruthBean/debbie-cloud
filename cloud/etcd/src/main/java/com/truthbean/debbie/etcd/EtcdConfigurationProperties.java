/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-etcd.
 * <p>
 * Reads the {@link EtcdConfiguration} from the environment using the prefix
 * {@code debbie.etcd.} and fills a plain configuration object, instead of
 * relying on annotations.
 * <p>
 * Note: {@link EtcdProperties} is the existing etcd connection property holder,
 * so this reader is named differently to avoid a name clash.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdConfigurationProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<EtcdConfiguration> {

    /**
     * key prefix of the etcd properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.etcd.host}
     */
    private static final String KEY_PREFIX = "debbie.etcd.";

    private final Map<String, EtcdConfiguration> map = new HashMap<>();
    private EtcdConfiguration configuration;

    public EtcdConfigurationProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, EtcdConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public EtcdConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public EtcdConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new EtcdConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 2379));
        configuration.setScheme(getStringValue(KEY_PREFIX + "scheme", "http"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));
        configuration.setPrefix(getStringValue(KEY_PREFIX + "prefix", ""));
        configuration.setConnectTimeout(getIntegerValue(KEY_PREFIX + "connect-timeout", 5000));
        configuration.setReadTimeout(getIntegerValue(KEY_PREFIX + "read-timeout", 10000));

        // service discovery
        configuration.setDiscoveryEnable(getBooleanValue(KEY_PREFIX + "discovery.enable", true));

        // config
        configuration.setConfigEnable(getBooleanValue(KEY_PREFIX + "config.enable", false));
        configuration.setConfigAppName(getStringValue(KEY_PREFIX + "config.app-name", "application"));
        configuration.setConfigProfile(getStringValue(KEY_PREFIX + "config.profile", "default"));
        configuration.setConfigFailFast(getBooleanValue(KEY_PREFIX + "config.fail-fast", false));

        // health
        configuration.setHealthEnable(getBooleanValue(KEY_PREFIX + "health.enable", true));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
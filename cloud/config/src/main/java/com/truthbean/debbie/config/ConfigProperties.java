/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-config.
 * <p>
 * Reads the {@link ConfigConfiguration} from the environment using the prefix
 * {@code debbie.config.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConfigProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<ConfigConfiguration> {

    /**
     * key prefix of the config properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.config.server.enable}
     */
    private static final String KEY_PREFIX = "debbie.config.";

    private final Map<String, ConfigConfiguration> map = new HashMap<>();
    private ConfigConfiguration configuration;

    public ConfigProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, ConfigConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public ConfigConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public ConfigConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ConfigConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));

        // config server
        configuration.setServerEnable(getBooleanValue(KEY_PREFIX + "server.enable", false));
        configuration.setServerRepository(getStringValue(KEY_PREFIX + "server.repository", "file"));
        configuration.setServerBaseDir(getStringValue(KEY_PREFIX + "server.base-dir", "config"));
        configuration.setServerDefaultLabel(getStringValue(KEY_PREFIX + "server.default-label", "master"));
        configuration.setServerEncrypt(getBooleanValue(KEY_PREFIX + "server.encrypt", false));
        configuration.setServerPrefix(getStringValue(KEY_PREFIX + "server.prefix", "/config"));

        // config client
        configuration.setClientEnable(getBooleanValue(KEY_PREFIX + "client.enable", false));
        configuration.setClientUri(getStringValue(KEY_PREFIX + "client.uri", "http://localhost:8888/config"));
        configuration.setClientName(getStringValue(KEY_PREFIX + "client.name", "application"));
        configuration.setClientProfile(getStringValue(KEY_PREFIX + "client.profile", "default"));
        configuration.setClientLabel(getValue(KEY_PREFIX + "client.label"));
        configuration.setClientConnectTimeout(getIntegerValue(KEY_PREFIX + "client.connect-timeout", 5000));
        configuration.setClientReadTimeout(getIntegerValue(KEY_PREFIX + "client.read-timeout", 10000));
        configuration.setClientFailFast(getBooleanValue(KEY_PREFIX + "client.fail-fast", false));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
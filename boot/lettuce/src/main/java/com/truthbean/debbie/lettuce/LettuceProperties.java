/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lettuce;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-lettuce.
 * <p>
 * Reads the {@link LettuceConfiguration} from the environment using the prefix
 * {@code debbie.lettuce.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LettuceProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<LettuceConfiguration> {

    /**
     * key prefix of the lettuce properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.lettuce.host}
     */
    private static final String KEY_PREFIX = "debbie.lettuce.";

    private final Map<String, LettuceConfiguration> map = new HashMap<>();
    private LettuceConfiguration configuration;

    public LettuceProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, LettuceConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public LettuceConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public LettuceConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new LettuceConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 6379));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));
        configuration.setDatabase(getIntegerValue(KEY_PREFIX + "database", 0));
        configuration.setTimeout(getIntegerValue(KEY_PREFIX + "timeout", 5000));
        configuration.setClientName(getStringValue(KEY_PREFIX + "client-name", "debbie-lettuce"));
        configuration.setAutoReconnect(getBooleanValue(KEY_PREFIX + "auto-reconnect", true));
        configuration.setSsl(getBooleanValue(KEY_PREFIX + "ssl", false));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
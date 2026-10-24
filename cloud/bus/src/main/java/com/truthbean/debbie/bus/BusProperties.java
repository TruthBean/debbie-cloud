/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-bus.
 * <p>
 * Reads the {@link BusConfiguration} from the environment using the prefix
 * {@code debbie.bus.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class BusProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<BusConfiguration> {

    /**
     * key prefix of the bus properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.bus.topic}
     */
    private static final String KEY_PREFIX = "debbie.bus.";

    private final Map<String, BusConfiguration> map = new HashMap<>();
    private BusConfiguration configuration;

    public BusProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, BusConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public BusConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public BusConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new BusConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setId(getStringValue(KEY_PREFIX + "id", "default"));
        configuration.setDestination(getStringValue(KEY_PREFIX + "destination", "**"));
        configuration.setTopic(getStringValue(KEY_PREFIX + "topic", "debbie-bus"));
        configuration.setBroker(getStringValue(KEY_PREFIX + "broker", "simple"));
        configuration.setAck(getBooleanValue(KEY_PREFIX + "ack", false));
        configuration.setTrace(getBooleanValue(KEY_PREFIX + "trace", false));
        configuration.setPublishTimeout(getIntegerValue(KEY_PREFIX + "publish-timeout", 5000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
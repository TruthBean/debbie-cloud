/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.activemq;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-activemq.
 * <p>
 * Reads the {@link ActiveMqConfiguration} from the environment using the prefix
 * {@code debbie.activemq.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ActiveMqProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<ActiveMqConfiguration> {

    /**
     * key prefix of the activemq properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.activemq.broker-url}
     */
    private static final String KEY_PREFIX = "debbie.activemq.";

    private final Map<String, ActiveMqConfiguration> map = new HashMap<>();
    private ActiveMqConfiguration configuration;

    public ActiveMqProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, ActiveMqConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public ActiveMqConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public ActiveMqConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ActiveMqConfiguration();

        // connection
        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setBrokerUrl(getStringValue(KEY_PREFIX + "broker-url", "tcp://localhost:61616"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));
        configuration.setMaxConnections(getIntegerValue(KEY_PREFIX + "max-connections", 1));

        // send behaviour
        configuration.setUseAsyncSend(getBooleanValue(KEY_PREFIX + "use-async-send", true));
        configuration.setAlwaysSyncSend(getBooleanValue(KEY_PREFIX + "always-sync-send", false));
        configuration.setCloseTimeout(getIntegerValue(KEY_PREFIX + "close-timeout", 15000));
        configuration.setProducerWindowSize(getIntegerValue(KEY_PREFIX + "producer-window-size", 0));
        configuration.setDispatchAsync(getBooleanValue(KEY_PREFIX + "dispatch-async", true));

        // redelivery policy
        configuration.setRedeliveryMaxRedeliveries(getIntegerValue(KEY_PREFIX + "redelivery.max-redeliveries", 6));
        configuration.setRedeliveryInitialDelay(getIntegerValue(KEY_PREFIX + "redelivery.initial-redelivery-delay", 1000));
        configuration.setRedeliveryBackOffMultiplier(getDoubleValue(KEY_PREFIX + "redelivery.back-off-multiplier", 2.0));
        configuration.setRedeliveryUseExponentialBackOff(getBooleanValue(KEY_PREFIX + "redelivery.use-exponential-back-off", true));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
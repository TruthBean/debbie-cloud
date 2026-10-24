/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rabbitmq;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-rabbitmq.
 * <p>
 * Reads the {@link RabbitMqConfiguration} from the environment using the prefix
 * {@code debbie.rabbitmq.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RabbitMqProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<RabbitMqConfiguration> {

    /**
     * key prefix of the rabbitmq properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.rabbitmq.host}
     */
    private static final String KEY_PREFIX = "debbie.rabbitmq.";

    private final Map<String, RabbitMqConfiguration> map = new HashMap<>();
    private RabbitMqConfiguration configuration;

    public RabbitMqProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, RabbitMqConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public RabbitMqConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public RabbitMqConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new RabbitMqConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 5672));
        configuration.setUsername(getStringValue(KEY_PREFIX + "username", "guest"));
        configuration.setPassword(getStringValue(KEY_PREFIX + "password", "guest"));
        configuration.setVirtualHost(getStringValue(KEY_PREFIX + "virtual-host", "/"));
        configuration.setConnectionTimeout(getIntegerValue(KEY_PREFIX + "connection-timeout", 60000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
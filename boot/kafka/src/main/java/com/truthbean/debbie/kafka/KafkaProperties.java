/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-kafka.
 * <p>
 * Reads the {@link KafkaConfiguration} (including its nested consumer settings) from the
 * environment using the prefix {@code debbie.kafka.} and fills a plain configuration
 * object, instead of relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KafkaProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<KafkaConfiguration> {

    /**
     * key prefix of the kafka properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.kafka.bootstrap-servers}
     */
    private static final String KEY_PREFIX = "debbie.kafka.";

    /**
     * key prefix of the nested consumer properties, e.g. {@code debbie.kafka.consumer.group-id}
     */
    private static final String CONSUMER_KEY_PREFIX = KEY_PREFIX + "consumer.";

    /**
     * the separator used by the list valued properties such as bootstrap servers
     */
    private static final String LIST_SPLIT = ",";

    private final Map<String, KafkaConfiguration> map = new HashMap<>();
    private KafkaConfiguration configuration;

    public KafkaProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, KafkaConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public KafkaConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public KafkaConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new KafkaConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));

        // bootstrap servers, keep the built-in default when not configured
        var bootstrapServers = getStringListValue(KEY_PREFIX + "bootstrap-servers", LIST_SPLIT);
        if (bootstrapServers != null && !bootstrapServers.isEmpty()) {
            configuration.setBootstrapServers(bootstrapServers);
        }

        // nested consumer settings
        var consumer = configuration.getConsumer();
        var consumerBootstrapServers = getStringListValue(CONSUMER_KEY_PREFIX + "bootstrap-servers", LIST_SPLIT);
        if (consumerBootstrapServers != null && !consumerBootstrapServers.isEmpty()) {
            consumer.setBootstrapServers(consumerBootstrapServers);
        }
        consumer.setKeyDeserializer(getClassValue(CONSUMER_KEY_PREFIX + "key-deserializer", null));
        consumer.setValueDeserializer(getClassValue(CONSUMER_KEY_PREFIX + "value-deserializer", null));
        consumer.setGroupId(getValue(CONSUMER_KEY_PREFIX + "group-id"));
        consumer.setClientId(getValue(CONSUMER_KEY_PREFIX + "client-id"));
        consumer.setAutoOffsetReset(getStringValue(CONSUMER_KEY_PREFIX + "auto-offset-reset", "latest"));

        String enableAutoCommit = getValue(CONSUMER_KEY_PREFIX + "enable-auto-commit");
        if (enableAutoCommit != null && !enableAutoCommit.isBlank()) {
            consumer.setEnableAutocommit(Boolean.parseBoolean(enableAutoCommit));
        }
        String autoCommitInterval = getValue(CONSUMER_KEY_PREFIX + "auto-commit-interval");
        if (autoCommitInterval != null && !autoCommitInterval.isBlank()) {
            consumer.setAutoCommitInterval(Integer.parseInt(autoCommitInterval));
        }
        String sessionTimeout = getValue(CONSUMER_KEY_PREFIX + "session-timeout");
        if (sessionTimeout != null && !sessionTimeout.isBlank()) {
            consumer.setSessionTimeout(Integer.parseInt(sessionTimeout));
        }
        String pullTimeout = getValue(CONSUMER_KEY_PREFIX + "pull-timeout");
        if (pullTimeout != null && !pullTimeout.isBlank()) {
            consumer.setPullTimeout(Long.parseLong(pullTimeout));
        }

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
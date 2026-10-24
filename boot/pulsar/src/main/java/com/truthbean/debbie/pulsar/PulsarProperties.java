/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.pulsar;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-pulsar.
 * <p>
 * Reads the {@link PulsarConfiguration} from the environment using the prefix
 * {@code debbie.pulsar.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class PulsarProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<PulsarConfiguration> {

    /**
     * key prefix of the pulsar properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.pulsar.service-url}
     */
    private static final String KEY_PREFIX = "debbie.pulsar.";

    private final Map<String, PulsarConfiguration> map = new HashMap<>();
    private PulsarConfiguration configuration;

    public PulsarProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, PulsarConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public PulsarConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public PulsarConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new PulsarConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setServiceUrl(getStringValue(KEY_PREFIX + "service-url", "pulsar://localhost:6650"));
        configuration.setProducerName(getStringValue(KEY_PREFIX + "producer-name", "debbie-producer"));
        configuration.setConsumerName(getStringValue(KEY_PREFIX + "consumer-name", "debbie-consumer"));
        configuration.setSendTimeout(getIntegerValue(KEY_PREFIX + "send-timeout", 30000));
        configuration.setOperationTimeout(getIntegerValue(KEY_PREFIX + "operation-timeout", 30));
        configuration.setConnectionTimeout(getIntegerValue(KEY_PREFIX + "connection-timeout", 10000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
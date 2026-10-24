/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rocketmq;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-rocketmq.
 * <p>
 * Reads the {@link RocketMqConfiguration} from the environment using the prefix
 * {@code debbie.rocketmq.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RocketMqProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<RocketMqConfiguration> {

    /**
     * key prefix of the rocketmq properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.rocketmq.namesrv-addr}
     */
    private static final String KEY_PREFIX = "debbie.rocketmq.";

    private final Map<String, RocketMqConfiguration> map = new HashMap<>();
    private RocketMqConfiguration configuration;

    public RocketMqProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, RocketMqConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public RocketMqConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public RocketMqConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new RocketMqConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setNamesrvAddr(getStringValue(KEY_PREFIX + "namesrv-addr", "localhost:9876"));
        configuration.setProducerGroup(getStringValue(KEY_PREFIX + "producer-group", "debbie-producer"));
        configuration.setConsumerGroup(getStringValue(KEY_PREFIX + "consumer-group", "debbie-consumer"));
        configuration.setSendTimeout(getIntegerValue(KEY_PREFIX + "send-timeout", 3000));
        configuration.setMaxMessageSize(getIntegerValue(KEY_PREFIX + "max-message-size", 4194304));
        configuration.setRetryTimesWhenSendFailed(getIntegerValue(KEY_PREFIX + "retry-times-when-send-failed", 2));
        configuration.setPollTimeout(getIntegerValue(KEY_PREFIX + "poll-timeout", 10000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka;

import com.truthbean.transformer.collection.ListStringTransformer;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.NestedPropertiesConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.common.mini.util.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-27 14:51
 */
@PropertiesConfiguration(keyPrefix = "debbie.kafka")
public class KafkaConfiguration implements DebbieConfiguration {

    private String name = "default";

    private boolean enable = true;

    @PropertyInject(value = "bootstrap-servers", transformer = ListStringTransformer.class)
    private List<String> bootstrapServers = new ArrayList<>(Collections.singletonList("localhost:9092"));

    @NestedPropertiesConfiguration
    private Consumer consumer = new Consumer();

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Consumer getConsumer() {
        if (consumer.getBootstrapServers().isEmpty()) {
            consumer.setBootstrapServers(bootstrapServers);
        }
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public static class Consumer {
        @PropertyInject(value = "bootstrap-servers", transformer = ListStringTransformer.class)
        private List<String> bootstrapServers = new ArrayList<>();

        @PropertyInject("key-deserializer")
        private Class<?> keyDeserializer;

        @PropertyInject("value-deserializer")
        private Class<?> valueDeserializer;

        @PropertyInject("group-id")
        private String groupId;

        @PropertyInject("client-id")
        private String clientId;

        @PropertyInject("enable-auto-commit")
        private Boolean enableAutocommit;

        @PropertyInject("auto-commit-interval")
        private Integer autoCommitInterval;

        @PropertyInject("auto-offset-reset")
        private String autoOffsetReset;

        @PropertyInject("session-timeout")
        private Integer sessionTimeout;

        @PropertyInject("pull-timeout")
        private Long pullTimeout = 10000L;

        public List<String> getBootstrapServers() {
            return bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public Class<?> getKeyDeserializer() {
            return keyDeserializer;
        }

        public void setKeyDeserializer(Class<?> keyDeserializer) {
            this.keyDeserializer = keyDeserializer;
        }

        public Class<?> getValueDeserializer() {
            return valueDeserializer;
        }

        public void setValueDeserializer(Class<?> valueDeserializer) {
            this.valueDeserializer = valueDeserializer;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Boolean getEnableAutocommit() {
            return enableAutocommit;
        }

        public void setEnableAutocommit(Boolean enableAutocommit) {
            this.enableAutocommit = enableAutocommit;
        }

        public Integer getAutoCommitInterval() {
            return autoCommitInterval;
        }

        public void setAutoCommitInterval(Integer autoCommitInterval) {
            this.autoCommitInterval = autoCommitInterval;
        }

        public String getAutoOffsetReset() {
            return autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public Integer getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(Integer sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        public Long getPullTimeout() {
            return pullTimeout;
        }

        public void setPullTimeout(Long pullTimeout) {
            this.pullTimeout = pullTimeout;
        }

        public Properties toConsumerProperties() {
            Properties p = new Properties();
            if (bootstrapServers != null && !bootstrapServers.isEmpty()) {
                p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, StringUtils.joining(bootstrapServers));
            }
            if (keyDeserializer != null) {
                p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
            }
            if (valueDeserializer != null) {
                p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            }
            if (groupId != null) {
                p.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            }
            if (clientId != null) {
                p.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
            }
            if (enableAutocommit != null) {
                p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutocommit);
            }
            if (autoCommitInterval != null) {
                p.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
            }
            if (autoOffsetReset != null) {
                p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
            }
            if (sessionTimeout != null) {
                p.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
            }
            return p;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void close() {
        this.bootstrapServers.clear();
    }
}

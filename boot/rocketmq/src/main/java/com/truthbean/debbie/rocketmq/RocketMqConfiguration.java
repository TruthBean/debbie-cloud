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

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.rocketmq")
public class RocketMqConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Name Server address.
     * Default: localhost:9876
     */
    @PropertyInject(value = "namesrv-addr", defaultValue = "localhost:9876")
    private String namesrvAddr;

    /**
     * Producer group name.
     * Default: debbie-producer
     */
    @PropertyInject(value = "producer-group", defaultValue = "debbie-producer")
    private String producerGroup;

    /**
     * Consumer group name.
     * Default: debbie-consumer
     */
    @PropertyInject(value = "consumer-group", defaultValue = "debbie-consumer")
    private String consumerGroup;

    /**
     * Send message timeout in milliseconds.
     * Default: 3000
     */
    @PropertyInject(value = "send-timeout", transformer = IntegerTransformer.class, defaultValue = "3000")
    private int sendTimeout;

    /**
     * Max message size in bytes.
     * Default: 4194304 (4MB)
     */
    @PropertyInject(value = "max-message-size", transformer = IntegerTransformer.class, defaultValue = "4194304")
    private int maxMessageSize;

    /**
     * Retry times when send failed.
     * Default: 2
     */
    @PropertyInject(value = "retry-times-when-send-failed", transformer = IntegerTransformer.class, defaultValue = "2")
    private int retryTimesWhenSendFailed;

    /**
     * Poll timeout for consumer in milliseconds.
     * Default: 10000
     */
    @PropertyInject(value = "poll-timeout", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int pollTimeout;

    // ======================== Getter/Setter ========================

    public String getNamesrvAddr() { return namesrvAddr; }
    public void setNamesrvAddr(String namesrvAddr) { this.namesrvAddr = namesrvAddr; }

    public String getProducerGroup() { return producerGroup; }
    public void setProducerGroup(String producerGroup) { this.producerGroup = producerGroup; }

    public String getConsumerGroup() { return consumerGroup; }
    public void setConsumerGroup(String consumerGroup) { this.consumerGroup = consumerGroup; }

    public int getSendTimeout() { return sendTimeout; }
    public void setSendTimeout(int sendTimeout) { this.sendTimeout = sendTimeout; }

    public int getMaxMessageSize() { return maxMessageSize; }
    public void setMaxMessageSize(int maxMessageSize) { this.maxMessageSize = maxMessageSize; }

    public int getRetryTimesWhenSendFailed() { return retryTimesWhenSendFailed; }
    public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) { this.retryTimesWhenSendFailed = retryTimesWhenSendFailed; }

    public int getPollTimeout() { return pollTimeout; }
    public void setPollTimeout(int pollTimeout) { this.pollTimeout = pollTimeout; }

    // ======================== DebbieConfiguration ========================

    @Override
    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() { return (T) this; }

    @Override
    public void close() {}
}
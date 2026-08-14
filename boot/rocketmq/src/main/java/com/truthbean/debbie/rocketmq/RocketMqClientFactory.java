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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RocketMqClientFactory {

    private final RocketMqConfiguration configuration;
    private volatile DefaultMQProducer producer;
    private volatile DefaultLitePullConsumer consumer;

    public RocketMqClientFactory(RocketMqConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get or create the default producer.
     */
    public DefaultMQProducer getProducer() throws MQClientException {
        if (producer == null) {
            synchronized (this) {
                if (producer == null) {
                    producer = new DefaultMQProducer(configuration.getProducerGroup());
                    producer.setNamesrvAddr(configuration.getNamesrvAddr());
                    producer.setSendMsgTimeout(configuration.getSendTimeout());
                    producer.setMaxMessageSize(configuration.getMaxMessageSize());
                    producer.setRetryTimesWhenSendFailed(configuration.getRetryTimesWhenSendFailed());
                    producer.start();
                    LOGGER.info("RocketMQ producer started");
                }
            }
        }
        return producer;
    }

    /**
     * Send a message synchronously.
     */
    public SendResult send(String topic, String tags, byte[] body) throws Exception {
        Message message = new Message(topic, tags, body);
        return getProducer().send(message);
    }

    /**
     * Send a message synchronously with string body.
     */
    public SendResult send(String topic, String tags, String body) throws Exception {
        return send(topic, tags, body.getBytes());
    }

    /**
     * Get or create the default consumer.
     */
    public DefaultLitePullConsumer getConsumer() throws MQClientException {
        if (consumer == null) {
            synchronized (this) {
                if (consumer == null) {
                    consumer = new DefaultLitePullConsumer(configuration.getConsumerGroup());
                    consumer.setNamesrvAddr(configuration.getNamesrvAddr());
                    consumer.setPollTimeoutMillis(configuration.getPollTimeout());
                    consumer.start();
                    LOGGER.info("RocketMQ consumer started");
                }
            }
        }
        return consumer;
    }

    public RocketMqConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        if (producer != null) {
            producer.shutdown();
            LOGGER.info("RocketMQ producer shutdown");
        }
        if (consumer != null) {
            consumer.shutdown();
            LOGGER.info("RocketMQ consumer shutdown");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqClientFactory.class);
}
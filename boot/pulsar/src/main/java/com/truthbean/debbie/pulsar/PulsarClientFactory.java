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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class PulsarClientFactory {

    private final PulsarConfiguration configuration;
    private volatile PulsarClient client;

    public PulsarClientFactory(PulsarConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get or create the Pulsar client.
     */
    public PulsarClient getClient() throws PulsarClientException {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = PulsarClient.builder()
                            .serviceUrl(configuration.getServiceUrl())
                            .operationTimeout(configuration.getOperationTimeout(), TimeUnit.SECONDS)
                            .connectionTimeout(configuration.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                            .build();
                    LOGGER.info("Pulsar client created");
                }
            }
        }
        return client;
    }

    /**
     * Create a producer for the given topic.
     */
    public Producer<byte[]> createProducer(String topic) throws PulsarClientException {
        return getClient().newProducer()
                .topic(topic)
                .producerName(configuration.getProducerName())
                .sendTimeout(configuration.getSendTimeout(), TimeUnit.MILLISECONDS)
                .create();
    }

    /**
     * Send a message synchronously.
     */
    public MessageId send(String topic, byte[] body) throws PulsarClientException {
        try (Producer<byte[]> producer = createProducer(topic)) {
            return producer.send(body);
        }
    }

    /**
     * Send a message synchronously with string body.
     */
    public MessageId send(String topic, String body) throws PulsarClientException {
        return send(topic, body.getBytes());
    }

    /**
     * Create a consumer for the given topic and subscription.
     */
    public Consumer<byte[]> createConsumer(String topic, String subscriptionName) throws PulsarClientException {
        return getClient().newConsumer()
                .topic(topic)
                .subscriptionName(subscriptionName)
                .consumerName(configuration.getConsumerName())
                .subscribe();
    }

    public PulsarConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        if (client != null) {
            try {
                client.close();
                LOGGER.info("Pulsar client closed");
            } catch (PulsarClientException e) {
                LOGGER.error("Failed to close Pulsar client", e);
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PulsarClientFactory.class);
}
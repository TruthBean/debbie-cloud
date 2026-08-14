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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ActiveMqConnectionFactory {

    private final ActiveMqConfiguration configuration;
    private final ConnectionFactory connectionFactory;

    public ActiveMqConnectionFactory(ActiveMqConfiguration configuration) {
        this.configuration = configuration;
        this.connectionFactory = createConnectionFactory(configuration);
    }

    private ConnectionFactory createConnectionFactory(ActiveMqConfiguration configuration) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(configuration.getBrokerUrl());

        if (configuration.getUsername() != null && !configuration.getUsername().isBlank()) {
            factory.setUserName(configuration.getUsername());
        }
        if (configuration.getPassword() != null && !configuration.getPassword().isBlank()) {
            factory.setPassword(configuration.getPassword());
        }

        factory.setMaxThreadPoolSize(configuration.getMaxConnections());
        factory.setUseAsyncSend(configuration.isUseAsyncSend());
        factory.setAlwaysSyncSend(configuration.isAlwaysSyncSend());
        factory.setCloseTimeout(configuration.getCloseTimeout());
        factory.setProducerWindowSize(configuration.getProducerWindowSize());
        factory.setDispatchAsync(configuration.isDispatchAsync());

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(configuration.getRedeliveryMaxRedeliveries());
        redeliveryPolicy.setInitialRedeliveryDelay(configuration.getRedeliveryInitialDelay());
        redeliveryPolicy.setBackOffMultiplier(configuration.getRedeliveryBackOffMultiplier());
        redeliveryPolicy.setUseExponentialBackOff(configuration.isRedeliveryUseExponentialBackOff());
        factory.setRedeliveryPolicy(redeliveryPolicy);

        return factory;
    }

    /**
     * Create a JMS connection.
     */
    public Connection createConnection() throws JMSException {
        return connectionFactory.createConnection();
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public ActiveMqConfiguration getConfiguration() {
        return configuration;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqConnectionFactory.class);
}
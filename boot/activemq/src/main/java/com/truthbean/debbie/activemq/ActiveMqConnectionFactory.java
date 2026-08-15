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
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.Topic;

/**
 * Factory that creates and manages the ActiveMQ {@link ConnectionFactory} instance.
 * <p>
 * The underlying factory is created from the {@link ActiveMqConfiguration} and reused
 * for the lifetime of the application. Call {@link #close()} to release resources.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ActiveMqConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqConnectionFactory.class);

    private final ActiveMqConfiguration configuration;
    private final ActiveMQConnectionFactory connectionFactory;

    public ActiveMqConnectionFactory(ActiveMqConfiguration configuration) {
        this.configuration = configuration;
        this.connectionFactory = createConnectionFactory(configuration);
        LOGGER.info("ActiveMQ connection factory created, brokerUrl={}", configuration.getBrokerUrl());
    }

    private ActiveMQConnectionFactory createConnectionFactory(ActiveMqConfiguration configuration) {
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
     * Create a JMS connection using the configured credentials (if any).
     */
    public Connection createConnection() throws JMSException {
        return connectionFactory.createConnection();
    }

    /**
     * Create a JMS connection with the given username and password,
     * overriding any credentials from the configuration.
     */
    public Connection createConnection(String username, String password) throws JMSException {
        return connectionFactory.createConnection(username, password);
    }

    /**
     * Create a JMS session on a new connection.
     *
     * @param transacted whether the session is transacted
     * @param acknowledgeMode acknowledgment mode (e.g. {@link Session#AUTO_ACKNOWLEDGE})
     */
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        var connection = createConnection();
        connection.start();
        return connection.createSession(transacted, acknowledgeMode);
    }

    /**
     * Create a queue on the given session.
     */
    public Queue createQueue(Session session, String queueName) throws JMSException {
        return session.createQueue(queueName);
    }

    /**
     * Create a topic on the given session.
     */
    public Topic createTopic(Session session, String topicName) throws JMSException {
        return session.createTopic(topicName);
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public ActiveMqConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Close the underlying connection factory and release resources.
     * <p>
     * {@link ActiveMQConnectionFactory} does not implement {@link AutoCloseable},
     * so this method logs the closure. Active connections created via
     * {@link #createConnection()} should be closed by the caller.
     */
    public void close() {
        LOGGER.info("ActiveMQ connection factory closed");
    }
}

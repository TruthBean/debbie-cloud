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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RabbitMqClientFactory {

    private final RabbitMqConfiguration configuration;
    private volatile ConnectionFactory connectionFactory;
    private volatile Connection connection;

    public RabbitMqClientFactory(RabbitMqConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get or create the connection factory.
     */
    public ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            synchronized (this) {
                if (connectionFactory == null) {
                    connectionFactory = new ConnectionFactory();
                    connectionFactory.setHost(configuration.getHost());
                    connectionFactory.setPort(configuration.getPort());
                    connectionFactory.setUsername(configuration.getUsername());
                    connectionFactory.setPassword(configuration.getPassword());
                    connectionFactory.setVirtualHost(configuration.getVirtualHost());
                    connectionFactory.setConnectionTimeout(configuration.getConnectionTimeout());
                    LOGGER.info("RabbitMQ connection factory created");
                }
            }
        }
        return connectionFactory;
    }

    /**
     * Get or create a connection.
     */
    public Connection getConnection() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            synchronized (this) {
                if (connection == null || !connection.isOpen()) {
                    connection = getConnectionFactory().newConnection();
                    LOGGER.info("RabbitMQ connection created");
                }
            }
        }
        return connection;
    }

    /**
     * Create a new channel.
     */
    public Channel createChannel() throws IOException, TimeoutException {
        return getConnection().createChannel();
    }

    public RabbitMqConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
                LOGGER.info("RabbitMQ connection closed");
            } catch (IOException e) {
                LOGGER.error("Failed to close RabbitMQ connection", e);
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqClientFactory.class);
}
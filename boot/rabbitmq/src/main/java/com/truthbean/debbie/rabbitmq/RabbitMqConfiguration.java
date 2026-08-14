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

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.rabbitmq")
public class RabbitMqConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * RabbitMQ host.
     * Default: localhost
     */
    @PropertyInject(value = "host", defaultValue = "localhost")
    private String host;

    /**
     * RabbitMQ port.
     * Default: 5672
     */
    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "5672")
    private int port;

    /**
     * RabbitMQ username.
     * Default: guest
     */
    @PropertyInject(value = "username", defaultValue = "guest")
    private String username;

    /**
     * RabbitMQ password.
     * Default: guest
     */
    @PropertyInject(value = "password", defaultValue = "guest")
    private String password;

    /**
     * RabbitMQ virtual host.
     * Default: /
     */
    @PropertyInject(value = "virtual-host", defaultValue = "/")
    private String virtualHost;

    /**
     * Connection timeout in milliseconds.
     * Default: 60000
     */
    @PropertyInject(value = "connection-timeout", transformer = IntegerTransformer.class, defaultValue = "60000")
    private int connectionTimeout;

    // ======================== Getter/Setter ========================

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getVirtualHost() { return virtualHost; }
    public void setVirtualHost(String virtualHost) { this.virtualHost = virtualHost; }

    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }

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
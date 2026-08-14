/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lettuce;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.lettuce")
public class LettuceConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Redis host.
     * Default: localhost
     */
    @PropertyInject(value = "host", defaultValue = "localhost")
    private String host;

    /**
     * Redis port.
     * Default: 6379
     */
    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "6379")
    private int port;

    /**
     * Redis password.
     */
    @PropertyInject("password")
    private String password;

    /**
     * Redis database index.
     * Default: 0
     */
    @PropertyInject(value = "database", transformer = IntegerTransformer.class, defaultValue = "0")
    private int database;

    /**
     * Connection timeout in milliseconds.
     * Default: 5000
     */
    @PropertyInject(value = "timeout", transformer = IntegerTransformer.class, defaultValue = "5000")
    private int timeout;

    /**
     * Client name.
     */
    @PropertyInject(value = "client-name", defaultValue = "debbie-lettuce")
    private String clientName;

    /**
     * Whether to auto-reconnect.
     * Default: true
     */
    @PropertyInject(value = "auto-reconnect", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean autoReconnect;

    /**
     * Whether to use SSL.
     * Default: false
     */
    @PropertyInject(value = "ssl", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean ssl;

    // ======================== Getter/Setter ========================

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getDatabase() { return database; }
    public void setDatabase(int database) { this.database = database; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public boolean isAutoReconnect() { return autoReconnect; }
    public void setAutoReconnect(boolean autoReconnect) { this.autoReconnect = autoReconnect; }

    public boolean isSsl() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl = ssl; }

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
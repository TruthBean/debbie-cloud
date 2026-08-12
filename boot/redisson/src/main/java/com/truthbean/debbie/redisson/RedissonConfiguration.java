/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.redisson;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.redisson")
public class RedissonConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Redis server host.
     * Default: localhost
     */
    @PropertyInject(value = "host", defaultValue = "localhost")
    private String host;

    /**
     * Redis server port.
     * Default: 6379
     */
    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "6379")
    private int port;

    /**
     * Optional Redis password.
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
     * Connect timeout in milliseconds.
     * Default: 10000
     */
    @PropertyInject(value = "connect-timeout", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int connectTimeout;

    /**
     * Response timeout in milliseconds.
     * Default: 3000
     */
    @PropertyInject(value = "timeout", transformer = IntegerTransformer.class, defaultValue = "3000")
    private int timeout;

    /**
     * Retry attempts.
     * Default: 3
     */
    @PropertyInject(value = "retry-attempts", transformer = IntegerTransformer.class, defaultValue = "3")
    private int retryAttempts;

    /**
     * Retry interval in milliseconds.
     * Default: 1500
     */
    @PropertyInject(value = "retry-interval", transformer = IntegerTransformer.class, defaultValue = "1500")
    private int retryInterval;

    // ======================== Connection pool settings ========================

    /**
     * Minimum idle connections.
     * Default: 32
     */
    @PropertyInject(value = "connection-min-idle-size", transformer = IntegerTransformer.class, defaultValue = "32")
    private int connectionMinIdleSize;

    /**
     * Maximum pool size.
     * Default: 64
     */
    @PropertyInject(value = "connection-pool-size", transformer = IntegerTransformer.class, defaultValue = "64")
    private int connectionPoolSize;

    /**
     * Idle connection timeout in milliseconds.
     * Default: 10000
     */
    @PropertyInject(value = "idle-connection-timeout", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int idleConnectionTimeout;

    /**
     * Ping connection interval in milliseconds.
     * Default: 30000
     */
    @PropertyInject(value = "ping-connection-interval", transformer = IntegerTransformer.class, defaultValue = "30000")
    private int pingConnectionInterval;

    /**
     * Keep alive.
     * Default: false
     */
    @PropertyInject(value = "keep-alive", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean keepAlive;

    /**
     * DNS monitoring interval in milliseconds.
     * Default: 5000
     */
    @PropertyInject(value = "dns-monitoring-interval", transformer = LongTransformer.class, defaultValue = "5000")
    private long dnsMonitoringInterval;

    // ======================== Getter/Setter ========================

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getDatabase() { return database; }
    public void setDatabase(int database) { this.database = database; }

    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public int getRetryAttempts() { return retryAttempts; }
    public void setRetryAttempts(int retryAttempts) { this.retryAttempts = retryAttempts; }

    public int getRetryInterval() { return retryInterval; }
    public void setRetryInterval(int retryInterval) { this.retryInterval = retryInterval; }

    public int getConnectionMinIdleSize() { return connectionMinIdleSize; }
    public void setConnectionMinIdleSize(int connectionMinIdleSize) { this.connectionMinIdleSize = connectionMinIdleSize; }

    public int getConnectionPoolSize() { return connectionPoolSize; }
    public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }

    public int getIdleConnectionTimeout() { return idleConnectionTimeout; }
    public void setIdleConnectionTimeout(int idleConnectionTimeout) { this.idleConnectionTimeout = idleConnectionTimeout; }

    public int getPingConnectionInterval() { return pingConnectionInterval; }
    public void setPingConnectionInterval(int pingConnectionInterval) { this.pingConnectionInterval = pingConnectionInterval; }

    public boolean isKeepAlive() { return keepAlive; }
    public void setKeepAlive(boolean keepAlive) { this.keepAlive = keepAlive; }

    public long getDnsMonitoringInterval() { return dnsMonitoringInterval; }
    public void setDnsMonitoringInterval(long dnsMonitoringInterval) { this.dnsMonitoringInterval = dnsMonitoringInterval; }

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
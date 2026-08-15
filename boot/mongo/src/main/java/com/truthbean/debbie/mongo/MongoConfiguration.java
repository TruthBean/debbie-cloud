/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mongo;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;

/**
 * Configuration of debbie-mongo.
 * <p>
 * properties prefix: {@code debbie.mongo}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.mongo")
public class MongoConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject("uri")
    private String uri;

    @PropertyInject("host")
    private String host = "localhost";

    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "27017")
    private int port = 27017;

    @PropertyInject("database")
    private String database;

    @PropertyInject("username")
    private String username;

    @PropertyInject("password")
    private String password;

    @PropertyInject("auth-source")
    private String authSource;

    @PropertyInject(value = "connect-timeout", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int connectTimeout = 10000;

    @PropertyInject(value = "socket-timeout", transformer = IntegerTransformer.class, defaultValue = "0")
    private int socketTimeout = 0;

    @PropertyInject(value = "server-selection-timeout", transformer = IntegerTransformer.class, defaultValue = "30000")
    private int serverSelectionTimeout = 30000;

    @PropertyInject(value = "max-pool-size", transformer = IntegerTransformer.class, defaultValue = "100")
    private int maxPoolSize = 100;

    @PropertyInject(value = "min-pool-size", transformer = IntegerTransformer.class, defaultValue = "0")
    private int minPoolSize = 0;

    @PropertyInject(value = "max-idle-time", transformer = LongTransformer.class, defaultValue = "60000")
    private long maxIdleTime = 60000;

    @PropertyInject(value = "max-wait-time", transformer = LongTransformer.class, defaultValue = "120000")
    private long maxWaitTime = 120000;

    @PropertyInject(value = "ssl", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean ssl = false;

    @PropertyInject(value = "ssl-invalid-host-allowed", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean sslInvalidHostAllowed = false;

    @PropertyInject(value = "retry-writes", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean retryWrites = true;

    @PropertyInject(value = "retry-reads", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean retryReads = true;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAuthSource() { return authSource; }
    public void setAuthSource(String authSource) { this.authSource = authSource; }

    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int v) { this.connectTimeout = v; }

    public int getSocketTimeout() { return socketTimeout; }
    public void setSocketTimeout(int v) { this.socketTimeout = v; }

    public int getServerSelectionTimeout() { return serverSelectionTimeout; }
    public void setServerSelectionTimeout(int v) { this.serverSelectionTimeout = v; }

    public int getMaxPoolSize() { return maxPoolSize; }
    public void setMaxPoolSize(int v) { this.maxPoolSize = v; }

    public int getMinPoolSize() { return minPoolSize; }
    public void setMinPoolSize(int v) { this.minPoolSize = v; }

    public long getMaxIdleTime() { return maxIdleTime; }
    public void setMaxIdleTime(long v) { this.maxIdleTime = v; }

    public long getMaxWaitTime() { return maxWaitTime; }
    public void setMaxWaitTime(long v) { this.maxWaitTime = v; }

    public boolean isSsl() { return ssl; }
    public void setSsl(boolean v) { this.ssl = v; }

    public boolean isSslInvalidHostAllowed() { return sslInvalidHostAllowed; }
    public void setSslInvalidHostAllowed(boolean v) { this.sslInvalidHostAllowed = v; }

    public boolean isRetryWrites() { return retryWrites; }
    public void setRetryWrites(boolean v) { this.retryWrites = v; }

    public boolean isRetryReads() { return retryReads; }
    public void setRetryReads(boolean v) { this.retryReads = v; }

    public boolean hasUri() {
        return uri != null && !uri.isBlank();
    }

    public boolean hasCredentials() {
        return username != null && !username.isBlank() && password != null;
    }

    public String buildConnectionString() {
        if (hasUri()) return uri;
        var sb = new StringBuilder("mongodb://");
        if (hasCredentials()) {
            sb.append(username).append(":").append(password).append("@");
        }
        sb.append(host).append(":").append(port);
        if (database != null && !database.isBlank()) {
            sb.append("/").append(database);
        }
        var params = new StringBuilder();
        if (authSource != null && !authSource.isBlank()) {
            params.append("authSource=").append(authSource).append("&");
        }
        params.append("connectTimeoutMS=").append(connectTimeout).append("&");
        if (socketTimeout > 0) {
            params.append("socketTimeoutMS=").append(socketTimeout).append("&");
        }
        params.append("serverSelectionTimeoutMS=").append(serverSelectionTimeout).append("&");
        params.append("maxPoolSize=").append(maxPoolSize).append("&");
        params.append("minPoolSize=").append(minPoolSize).append("&");
        params.append("maxIdleTimeMS=").append(maxIdleTime).append("&");
        params.append("waitQueueTimeoutMS=").append(maxWaitTime).append("&");
        if (ssl) {
            params.append("tls=true&");
            if (sslInvalidHostAllowed) {
                params.append("tlsAllowInvalidHostnames=true&");
            }
        }
        params.append("retryWrites=").append(retryWrites).append("&");
        params.append("retryReads=").append(retryReads);
        sb.append("?").append(params);
        return sb.toString();
    }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new MongoConfiguration();
        c.enable = this.enable;
        c.uri = this.uri;
        c.host = this.host;
        c.port = this.port;
        c.database = this.database;
        c.username = this.username;
        c.password = this.password;
        c.authSource = this.authSource;
        c.connectTimeout = this.connectTimeout;
        c.socketTimeout = this.socketTimeout;
        c.serverSelectionTimeout = this.serverSelectionTimeout;
        c.maxPoolSize = this.maxPoolSize;
        c.minPoolSize = this.minPoolSize;
        c.maxIdleTime = this.maxIdleTime;
        c.maxWaitTime = this.maxWaitTime;
        c.ssl = this.ssl;
        c.sslInvalidHostAllowed = this.sslInvalidHostAllowed;
        c.retryWrites = this.retryWrites;
        c.retryReads = this.retryReads;
        return (T) c;
    }

    @Override
    public void close() {}

    @Override
    public String toString() {
        return "MongoConfiguration{host=" + host + ", port=" + port
                + ", database=" + database + ", maxPoolSize=" + maxPoolSize + "}";
    }
}
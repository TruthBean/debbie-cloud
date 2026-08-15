/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;

/**
 * Configuration of debbie-eureka.
 * <p>
 * properties prefix: {@code debbie.eureka}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.eureka")
public class EurekaConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "server.enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean serverEnable = false;

    @PropertyInject(value = "server.prefix", defaultValue = "/eureka")
    private String serverPrefix = "/eureka";

    @PropertyInject(value = "server.eviction-timeout", transformer = LongTransformer.class, defaultValue = "90000")
    private long serverEvictionTimeout = 90000L;

    @PropertyInject(value = "server.eviction-interval", transformer = LongTransformer.class, defaultValue = "60000")
    private long serverEvictionInterval = 60000L;

    @PropertyInject(value = "client.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean clientEnable = true;

    @PropertyInject(value = "client.server-url", defaultValue = "http://localhost:8761")
    private String clientServerUrl = "http://localhost:8761";

    @PropertyInject(value = "client.prefix", defaultValue = "/eureka")
    private String clientPrefix = "/eureka";

    @PropertyInject(value = "client.service-name")
    private String clientServiceName;

    @PropertyInject(value = "client.instance-id")
    private String clientInstanceId;

    @PropertyInject(value = "client.host")
    private String clientHost;

    @PropertyInject(value = "client.port", transformer = IntegerTransformer.class, defaultValue = "8080")
    private int clientPort = 8080;

    @PropertyInject(value = "client.secure-port", transformer = IntegerTransformer.class, defaultValue = "0")
    private int clientSecurePort = 0;

    @PropertyInject(value = "client.heartbeat-interval", transformer = LongTransformer.class, defaultValue = "30000")
    private long clientHeartbeatInterval = 30000L;

    @PropertyInject(value = "client.connect-timeout", transformer = IntegerTransformer.class, defaultValue = "5000")
    private int clientConnectTimeout = 5000;

    @PropertyInject(value = "client.read-timeout", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int clientReadTimeout = 10000;

    @PropertyInject(value = "client.auto-register", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean clientAutoRegister = true;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public boolean isServerEnable() { return serverEnable; }
    public void setServerEnable(boolean serverEnable) { this.serverEnable = serverEnable; }

    public String getServerPrefix() { return serverPrefix; }
    public void setServerPrefix(String serverPrefix) { this.serverPrefix = serverPrefix; }

    public long getServerEvictionTimeout() { return serverEvictionTimeout; }
    public void setServerEvictionTimeout(long v) { this.serverEvictionTimeout = v; }

    public long getServerEvictionInterval() { return serverEvictionInterval; }
    public void setServerEvictionInterval(long v) { this.serverEvictionInterval = v; }

    public boolean isClientEnable() { return clientEnable; }
    public void setClientEnable(boolean clientEnable) { this.clientEnable = clientEnable; }

    public String getClientServerUrl() { return clientServerUrl; }
    public void setClientServerUrl(String clientServerUrl) { this.clientServerUrl = clientServerUrl; }

    public String getClientPrefix() { return clientPrefix; }
    public void setClientPrefix(String clientPrefix) { this.clientPrefix = clientPrefix; }

    public String getClientServiceName() { return clientServiceName; }
    public void setClientServiceName(String clientServiceName) { this.clientServiceName = clientServiceName; }

    public String getClientInstanceId() { return clientInstanceId; }
    public void setClientInstanceId(String clientInstanceId) { this.clientInstanceId = clientInstanceId; }

    public String getClientHost() { return clientHost; }
    public void setClientHost(String clientHost) { this.clientHost = clientHost; }

    public int getClientPort() { return clientPort; }
    public void setClientPort(int clientPort) { this.clientPort = clientPort; }

    public int getClientSecurePort() { return clientSecurePort; }
    public void setClientSecurePort(int clientSecurePort) { this.clientSecurePort = clientSecurePort; }

    public long getClientHeartbeatInterval() { return clientHeartbeatInterval; }
    public void setClientHeartbeatInterval(long v) { this.clientHeartbeatInterval = v; }

    public int getClientConnectTimeout() { return clientConnectTimeout; }
    public void setClientConnectTimeout(int v) { this.clientConnectTimeout = v; }

    public int getClientReadTimeout() { return clientReadTimeout; }
    public void setClientReadTimeout(int v) { this.clientReadTimeout = v; }

    public boolean isClientAutoRegister() { return clientAutoRegister; }
    public void setClientAutoRegister(boolean v) { this.clientAutoRegister = v; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new EurekaConfiguration();
        c.enable = this.enable;
        c.serverEnable = this.serverEnable;
        c.serverPrefix = this.serverPrefix;
        c.serverEvictionTimeout = this.serverEvictionTimeout;
        c.serverEvictionInterval = this.serverEvictionInterval;
        c.clientEnable = this.clientEnable;
        c.clientServerUrl = this.clientServerUrl;
        c.clientPrefix = this.clientPrefix;
        c.clientServiceName = this.clientServiceName;
        c.clientInstanceId = this.clientInstanceId;
        c.clientHost = this.clientHost;
        c.clientPort = this.clientPort;
        c.clientSecurePort = this.clientSecurePort;
        c.clientHeartbeatInterval = this.clientHeartbeatInterval;
        c.clientConnectTimeout = this.clientConnectTimeout;
        c.clientReadTimeout = this.clientReadTimeout;
        c.clientAutoRegister = this.clientAutoRegister;
        return (T) c;
    }

    @Override
    public void close() {}
}
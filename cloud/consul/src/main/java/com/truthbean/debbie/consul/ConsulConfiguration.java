/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * Configuration of debbie-consul.
 * <p>
 * properties prefix: {@code debbie.consul}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    private String host = "localhost";

    private int port = 8500;

    private String scheme = "http";

    private String token;

    private int connectTimeout = 5000;

    private int readTimeout = 10000;

    private boolean discoveryEnable = true;

    private boolean discoveryRegister = true;

    private boolean discoveryPreferIpAddress = false;

    private String discoveryHeartbeatInterval = "10s";

    private String discoveryDeregisterCriticalAfter = "30s";

    private String discoveryHealthCheckPath = "/actuator/health";

    private boolean configEnable = false;

    private String configPrefix = "config";

    private String configProfileSeparator = "/";

    private boolean configWatch = false;

    private int configWatchInterval = 5000;

    private boolean configFailFast = false;

    private boolean healthEnable = true;

    private String serviceName;

    private String serviceAddress;

    private int servicePort = 8080;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getScheme() { return scheme; }
    public void setScheme(String scheme) { this.scheme = scheme; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }

    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }

    public boolean isDiscoveryEnable() { return discoveryEnable; }
    public void setDiscoveryEnable(boolean discoveryEnable) { this.discoveryEnable = discoveryEnable; }

    public boolean isDiscoveryRegister() { return discoveryRegister; }
    public void setDiscoveryRegister(boolean discoveryRegister) { this.discoveryRegister = discoveryRegister; }

    public boolean isDiscoveryPreferIpAddress() { return discoveryPreferIpAddress; }
    public void setDiscoveryPreferIpAddress(boolean v) { this.discoveryPreferIpAddress = v; }

    public String getDiscoveryHeartbeatInterval() { return discoveryHeartbeatInterval; }
    public void setDiscoveryHeartbeatInterval(String v) { this.discoveryHeartbeatInterval = v; }

    public String getDiscoveryDeregisterCriticalAfter() { return discoveryDeregisterCriticalAfter; }
    public void setDiscoveryDeregisterCriticalAfter(String v) { this.discoveryDeregisterCriticalAfter = v; }

    public String getDiscoveryHealthCheckPath() { return discoveryHealthCheckPath; }
    public void setDiscoveryHealthCheckPath(String v) { this.discoveryHealthCheckPath = v; }

    public boolean isConfigEnable() { return configEnable; }
    public void setConfigEnable(boolean configEnable) { this.configEnable = configEnable; }

    public String getConfigPrefix() { return configPrefix; }
    public void setConfigPrefix(String configPrefix) { this.configPrefix = configPrefix; }

    public String getConfigProfileSeparator() { return configProfileSeparator; }
    public void setConfigProfileSeparator(String v) { this.configProfileSeparator = v; }

    public boolean isConfigWatch() { return configWatch; }
    public void setConfigWatch(boolean configWatch) { this.configWatch = configWatch; }

    public int getConfigWatchInterval() { return configWatchInterval; }
    public void setConfigWatchInterval(int configWatchInterval) { this.configWatchInterval = configWatchInterval; }

    public boolean isConfigFailFast() { return configFailFast; }
    public void setConfigFailFast(boolean configFailFast) { this.configFailFast = configFailFast; }

    public boolean isHealthEnable() { return healthEnable; }
    public void setHealthEnable(boolean healthEnable) { this.healthEnable = healthEnable; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getServiceAddress() { return serviceAddress; }
    public void setServiceAddress(String serviceAddress) { this.serviceAddress = serviceAddress; }

    public int getServicePort() { return servicePort; }
    public void setServicePort(int servicePort) { this.servicePort = servicePort; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new ConsulConfiguration();
        c.enable = this.enable;
        c.host = this.host;
        c.port = this.port;
        c.scheme = this.scheme;
        c.token = this.token;
        c.connectTimeout = this.connectTimeout;
        c.readTimeout = this.readTimeout;
        c.discoveryEnable = this.discoveryEnable;
        c.discoveryRegister = this.discoveryRegister;
        c.discoveryPreferIpAddress = this.discoveryPreferIpAddress;
        c.discoveryHeartbeatInterval = this.discoveryHeartbeatInterval;
        c.discoveryDeregisterCriticalAfter = this.discoveryDeregisterCriticalAfter;
        c.discoveryHealthCheckPath = this.discoveryHealthCheckPath;
        c.configEnable = this.configEnable;
        c.configPrefix = this.configPrefix;
        c.configProfileSeparator = this.configProfileSeparator;
        c.configWatch = this.configWatch;
        c.configWatchInterval = this.configWatchInterval;
        c.configFailFast = this.configFailFast;
        c.healthEnable = this.healthEnable;
        c.serviceName = this.serviceName;
        c.serviceAddress = this.serviceAddress;
        c.servicePort = this.servicePort;
        return (T) c;
    }

    @Override
    public void close() {}
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.dubbo;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class DubboConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    // ======================== Application ========================

    /**
     * Application name.
     */
    private String applicationName;

    /**
     * Application owner.
     */
    private String applicationOwner;

    /**
     * Application organization.
     */
    private String applicationOrganization;

    // ======================== Protocol ========================

    /**
     * Protocol name.
     * Default: dubbo
     */
    private String protocolName;

    /**
     * Protocol port.
     * Default: -1 (auto)
     */
    private int protocolPort;

    /**
     * Protocol host.
     */
    private String protocolHost;

    /**
     * Protocol thread pool type.
     * Default: fixed
     */
    private String protocolThreadpool;

    /**
     * Protocol core threads.
     * Default: 200
     */
    private int protocolThreads;

    // ======================== Registry ========================

    /**
     * Registry address.
     * Default: N/A (no registry)
     */
    private String registryAddress;

    /**
     * Registry protocol.
     * Default: zookeeper
     */
    private String registryProtocol;

    /**
     * Registry group.
     */
    private String registryGroup;

    /**
     * Whether to check on startup.
     * Default: true
     */
    private boolean registryCheck;

    /**
     * Whether to register.
     * Default: true
     */
    private boolean registryRegister;

    // ======================== Provider ========================

    /**
     * Provider timeout in milliseconds.
     * Default: 1000
     */
    private int providerTimeout;

    /**
     * Provider retries.
     * Default: 2
     */
    private int providerRetries;

    /**
     * Provider delay in milliseconds.
     * Default: -1
     */
    private int providerDelay;

    // ======================== Consumer ========================

    /**
     * Consumer timeout in milliseconds.
     * Default: 1000
     */
    private int consumerTimeout;

    /**
     * Consumer retries.
     * Default: 2
     */
    private int consumerRetries;

    /**
     * Consumer check.
     * Default: true
     */
    private boolean consumerCheck;

    // ======================== Getter/Setter ========================

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getApplicationOwner() { return applicationOwner; }
    public void setApplicationOwner(String applicationOwner) { this.applicationOwner = applicationOwner; }

    public String getApplicationOrganization() { return applicationOrganization; }
    public void setApplicationOrganization(String applicationOrganization) { this.applicationOrganization = applicationOrganization; }

    public String getProtocolName() { return protocolName; }
    public void setProtocolName(String protocolName) { this.protocolName = protocolName; }

    public int getProtocolPort() { return protocolPort; }
    public void setProtocolPort(int protocolPort) { this.protocolPort = protocolPort; }

    public String getProtocolHost() { return protocolHost; }
    public void setProtocolHost(String protocolHost) { this.protocolHost = protocolHost; }

    public String getProtocolThreadpool() { return protocolThreadpool; }
    public void setProtocolThreadpool(String protocolThreadpool) { this.protocolThreadpool = protocolThreadpool; }

    public int getProtocolThreads() { return protocolThreads; }
    public void setProtocolThreads(int protocolThreads) { this.protocolThreads = protocolThreads; }

    public String getRegistryAddress() { return registryAddress; }
    public void setRegistryAddress(String registryAddress) { this.registryAddress = registryAddress; }

    public String getRegistryProtocol() { return registryProtocol; }
    public void setRegistryProtocol(String registryProtocol) { this.registryProtocol = registryProtocol; }

    public String getRegistryGroup() { return registryGroup; }
    public void setRegistryGroup(String registryGroup) { this.registryGroup = registryGroup; }

    public boolean isRegistryCheck() { return registryCheck; }
    public void setRegistryCheck(boolean registryCheck) { this.registryCheck = registryCheck; }

    public boolean isRegistryRegister() { return registryRegister; }
    public void setRegistryRegister(boolean registryRegister) { this.registryRegister = registryRegister; }

    public int getProviderTimeout() { return providerTimeout; }
    public void setProviderTimeout(int providerTimeout) { this.providerTimeout = providerTimeout; }

    public int getProviderRetries() { return providerRetries; }
    public void setProviderRetries(int providerRetries) { this.providerRetries = providerRetries; }

    public int getProviderDelay() { return providerDelay; }
    public void setProviderDelay(int providerDelay) { this.providerDelay = providerDelay; }

    public int getConsumerTimeout() { return consumerTimeout; }
    public void setConsumerTimeout(int consumerTimeout) { this.consumerTimeout = consumerTimeout; }

    public int getConsumerRetries() { return consumerRetries; }
    public void setConsumerRetries(int consumerRetries) { this.consumerRetries = consumerRetries; }

    public boolean isConsumerCheck() { return consumerCheck; }
    public void setConsumerCheck(boolean consumerCheck) { this.consumerCheck = consumerCheck; }

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
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * Configuration of debbie-integration.
 * <p>
 * properties prefix: {@code debbie.integration}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class IntegrationConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    private String defaultChannelType = "direct";

    private int queueCapacity = 1024;

    private boolean autoStart = true;

    private int globalTimeout = 30000;

    private String errorChannel;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public String getDefaultChannelType() { return defaultChannelType; }
    public void setDefaultChannelType(String defaultChannelType) { this.defaultChannelType = defaultChannelType; }

    public int getQueueCapacity() { return queueCapacity; }
    public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }

    public boolean isAutoStart() { return autoStart; }
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }

    public int getGlobalTimeout() { return globalTimeout; }
    public void setGlobalTimeout(int globalTimeout) { this.globalTimeout = globalTimeout; }

    public String getErrorChannel() { return errorChannel; }
    public void setErrorChannel(String errorChannel) { this.errorChannel = errorChannel; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new IntegrationConfiguration();
        c.enable = this.enable;
        c.defaultChannelType = this.defaultChannelType;
        c.queueCapacity = this.queueCapacity;
        c.autoStart = this.autoStart;
        c.globalTimeout = this.globalTimeout;
        c.errorChannel = this.errorChannel;
        return (T) c;
    }

    @Override
    public void close() {}
}
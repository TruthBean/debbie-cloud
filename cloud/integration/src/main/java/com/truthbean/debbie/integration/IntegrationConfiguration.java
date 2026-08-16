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
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * Configuration of debbie-integration.
 * <p>
 * properties prefix: {@code debbie.integration}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.integration")
public class IntegrationConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "default-channel-type", defaultValue = "direct")
    private String defaultChannelType = "direct";

    @PropertyInject(value = "queue-capacity", transformer = IntegerTransformer.class, defaultValue = "1024")
    private int queueCapacity = 1024;

    @PropertyInject(value = "auto-start", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean autoStart = true;

    @PropertyInject(value = "global-timeout", transformer = IntegerTransformer.class, defaultValue = "30000")
    private int globalTimeout = 30000;

    @PropertyInject(value = "error-channel")
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
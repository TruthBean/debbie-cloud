/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.activemq;

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
@PropertiesConfiguration(keyPrefix = "debbie.activemq")
public class ActiveMqConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Broker URL.
     * Default: tcp://localhost:61616
     */
    @PropertyInject(value = "broker-url", defaultValue = "tcp://localhost:61616")
    private String brokerUrl;

    /**
     * Username.
     */
    @PropertyInject("username")
    private String username;

    /**
     * Password.
     */
    @PropertyInject("password")
    private String password;

    /**
     * Maximum number of connections.
     * Default: 1
     */
    @PropertyInject(value = "max-connections", transformer = IntegerTransformer.class, defaultValue = "1")
    private int maxConnections;

    /**
     * Whether to use async send.
     * Default: true
     */
    @PropertyInject(value = "use-async-send", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean useAsyncSend;

    /**
     * Whether to always sync send.
     * Default: false
     */
    @PropertyInject(value = "always-sync-send", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean alwaysSyncSend;

    /**
     * Close timeout in milliseconds.
     * Default: 15000
     */
    @PropertyInject(value = "close-timeout", transformer = IntegerTransformer.class, defaultValue = "15000")
    private int closeTimeout;

    /**
     * Producer window size in bytes.
     * Default: 0 (disabled)
     */
    @PropertyInject(value = "producer-window-size", transformer = IntegerTransformer.class, defaultValue = "0")
    private int producerWindowSize;

    /**
     * Whether to dispatch async.
     * Default: true
     */
    @PropertyInject(value = "dispatch-async", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean dispatchAsync;

    // ======================== Redelivery Policy ========================

    /**
     * Maximum redeliveries.
     * Default: 6
     */
    @PropertyInject(value = "redelivery.max-redeliveries", transformer = IntegerTransformer.class, defaultValue = "6")
    private int redeliveryMaxRedeliveries;

    /**
     * Initial redelivery delay in milliseconds.
     * Default: 1000
     */
    @PropertyInject(value = "redelivery.initial-redelivery-delay", transformer = IntegerTransformer.class, defaultValue = "1000")
    private int redeliveryInitialDelay;

    /**
     * Redelivery backoff multiplier.
     * Default: 2.0
     */
    @PropertyInject(value = "redelivery.back-off-multiplier", defaultValue = "2.0")
    private double redeliveryBackOffMultiplier;

    /**
     * Whether to use exponential backoff.
     * Default: true
     */
    @PropertyInject(value = "redelivery.use-exponential-back-off", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean redeliveryUseExponentialBackOff;

    // ======================== Getter/Setter ========================

    public String getBrokerUrl() { return brokerUrl; }
    public void setBrokerUrl(String brokerUrl) { this.brokerUrl = brokerUrl; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }

    public boolean isUseAsyncSend() { return useAsyncSend; }
    public void setUseAsyncSend(boolean useAsyncSend) { this.useAsyncSend = useAsyncSend; }

    public boolean isAlwaysSyncSend() { return alwaysSyncSend; }
    public void setAlwaysSyncSend(boolean alwaysSyncSend) { this.alwaysSyncSend = alwaysSyncSend; }

    public int getCloseTimeout() { return closeTimeout; }
    public void setCloseTimeout(int closeTimeout) { this.closeTimeout = closeTimeout; }

    public int getProducerWindowSize() { return producerWindowSize; }
    public void setProducerWindowSize(int producerWindowSize) { this.producerWindowSize = producerWindowSize; }

    public boolean isDispatchAsync() { return dispatchAsync; }
    public void setDispatchAsync(boolean dispatchAsync) { this.dispatchAsync = dispatchAsync; }

    public int getRedeliveryMaxRedeliveries() { return redeliveryMaxRedeliveries; }
    public void setRedeliveryMaxRedeliveries(int redeliveryMaxRedeliveries) { this.redeliveryMaxRedeliveries = redeliveryMaxRedeliveries; }

    public int getRedeliveryInitialDelay() { return redeliveryInitialDelay; }
    public void setRedeliveryInitialDelay(int redeliveryInitialDelay) { this.redeliveryInitialDelay = redeliveryInitialDelay; }

    public double getRedeliveryBackOffMultiplier() { return redeliveryBackOffMultiplier; }
    public void setRedeliveryBackOffMultiplier(double redeliveryBackOffMultiplier) { this.redeliveryBackOffMultiplier = redeliveryBackOffMultiplier; }

    public boolean isRedeliveryUseExponentialBackOff() { return redeliveryUseExponentialBackOff; }
    public void setRedeliveryUseExponentialBackOff(boolean redeliveryUseExponentialBackOff) { this.redeliveryUseExponentialBackOff = redeliveryUseExponentialBackOff; }

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
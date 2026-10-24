/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.pulsar;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class PulsarConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Pulsar service URL.
     * Default: pulsar://localhost:6650
     */
    private String serviceUrl;

    /**
     * Producer name.
     * Default: debbie-producer
     */
    private String producerName;

    /**
     * Consumer name.
     * Default: debbie-consumer
     */
    private String consumerName;

    /**
     * Send timeout in milliseconds.
     * Default: 30000
     */
    private int sendTimeout;

    /**
     * Operation timeout in seconds.
     * Default: 30
     */
    private int operationTimeout;

    /**
     * Connection timeout in milliseconds.
     * Default: 10000
     */
    private int connectionTimeout;

    // ======================== Getter/Setter ========================

    public String getServiceUrl() { return serviceUrl; }
    public void setServiceUrl(String serviceUrl) { this.serviceUrl = serviceUrl; }

    public String getProducerName() { return producerName; }
    public void setProducerName(String producerName) { this.producerName = producerName; }

    public String getConsumerName() { return consumerName; }
    public void setConsumerName(String consumerName) { this.consumerName = consumerName; }

    public int getSendTimeout() { return sendTimeout; }
    public void setSendTimeout(int sendTimeout) { this.sendTimeout = sendTimeout; }

    public int getOperationTimeout() { return operationTimeout; }
    public void setOperationTimeout(int operationTimeout) { this.operationTimeout = operationTimeout; }

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
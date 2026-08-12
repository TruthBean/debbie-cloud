/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.grpc.server;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.grpc.server")
public class GrpcServerConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Server port.
     * Default: 9090
     */
    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "9090")
    private int port;

    /**
     * Maximum message size in bytes.
     * Default: 4194304 (4MB)
     */
    @PropertyInject(value = "max-inbound-message-size", transformer = IntegerTransformer.class, defaultValue = "4194304")
    private int maxInboundMessageSize;

    /**
     * Maximum inbound metadata size in bytes.
     * Default: 8192 (8KB)
     */
    @PropertyInject(value = "max-inbound-metadata-size", transformer = IntegerTransformer.class, defaultValue = "8192")
    private int maxInboundMetadataSize;

    // ======================== Getter/Setter ========================

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public int getMaxInboundMessageSize() { return maxInboundMessageSize; }
    public void setMaxInboundMessageSize(int maxInboundMessageSize) { this.maxInboundMessageSize = maxInboundMessageSize; }

    public int getMaxInboundMetadataSize() { return maxInboundMetadataSize; }
    public void setMaxInboundMetadataSize(int maxInboundMetadataSize) { this.maxInboundMetadataSize = maxInboundMetadataSize; }

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
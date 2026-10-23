/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.grpc.client;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GrpcClientConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Server host.
     * Default: localhost
     */
    private String host;

    /**
     * Server port.
     * Default: 9090
     */
    private int port;

    /**
     * Whether to use plaintext (no TLS).
     * Default: true
     */
    private boolean usePlaintext;

    /**
     * Maximum message size in bytes.
     * Default: 4194304 (4MB)
     */
    private int maxInboundMessageSize;

    /**
     * Keep alive time in seconds.
     * Default: 300
     */
    private int keepAliveTime;

    /**
     * Keep alive timeout in seconds.
     * Default: 20
     */
    private int keepAliveTimeout;

    /**
     * Keep alive without calls.
     * Default: true
     */
    private boolean keepAliveWithoutCalls;

    // ======================== Getter/Setter ========================

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public boolean isUsePlaintext() { return usePlaintext; }
    public void setUsePlaintext(boolean usePlaintext) { this.usePlaintext = usePlaintext; }

    public int getMaxInboundMessageSize() { return maxInboundMessageSize; }
    public void setMaxInboundMessageSize(int maxInboundMessageSize) { this.maxInboundMessageSize = maxInboundMessageSize; }

    public int getKeepAliveTime() { return keepAliveTime; }
    public void setKeepAliveTime(int keepAliveTime) { this.keepAliveTime = keepAliveTime; }

    public int getKeepAliveTimeout() { return keepAliveTimeout; }
    public void setKeepAliveTimeout(int keepAliveTimeout) { this.keepAliveTimeout = keepAliveTimeout; }

    public boolean isKeepAliveWithoutCalls() { return keepAliveWithoutCalls; }
    public void setKeepAliveWithoutCalls(boolean keepAliveWithoutCalls) { this.keepAliveWithoutCalls = keepAliveWithoutCalls; }

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
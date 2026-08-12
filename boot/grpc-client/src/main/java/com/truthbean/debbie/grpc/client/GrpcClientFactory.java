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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GrpcClientFactory {

    private final GrpcClientConfiguration configuration;
    private final ManagedChannel channel;

    public GrpcClientFactory(GrpcClientConfiguration configuration) {
        this.configuration = configuration;
        this.channel = createChannel(configuration);
    }

    private ManagedChannel createChannel(GrpcClientConfiguration configuration) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder
                .forAddress(configuration.getHost(), configuration.getPort())
                .maxInboundMessageSize(configuration.getMaxInboundMessageSize());

        if (configuration.isUsePlaintext()) {
            builder.usePlaintext();
        }

        if (configuration.getKeepAliveTime() > 0) {
            builder.keepAliveTime(configuration.getKeepAliveTime(), TimeUnit.SECONDS);
        }
        if (configuration.getKeepAliveTimeout() > 0) {
            builder.keepAliveTimeout(configuration.getKeepAliveTimeout(), TimeUnit.SECONDS);
        }
        builder.keepAliveWithoutCalls(configuration.isKeepAliveWithoutCalls());

        return builder.build();
    }

    /**
     * Get the managed channel for creating stubs.
     */
    public ManagedChannel getChannel() {
        return channel;
    }

    public GrpcClientConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        channel.shutdown();
        try {
            if (channel.awaitTermination(5, TimeUnit.SECONDS)) {
                LOGGER.info("gRPC client channel shutdown");
            } else {
                channel.shutdownNow();
                LOGGER.info("gRPC client channel shutdown now");
            }
        } catch (InterruptedException e) {
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcClientFactory.class);
}
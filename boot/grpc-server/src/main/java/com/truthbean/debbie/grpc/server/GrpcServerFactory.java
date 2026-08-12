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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GrpcServerFactory {

    private final GrpcServerConfiguration configuration;
    private final ServerBuilder<?> serverBuilder;
    private volatile Server server;

    public GrpcServerFactory(GrpcServerConfiguration configuration) {
        this.configuration = configuration;
        this.serverBuilder = ServerBuilder.forPort(configuration.getPort())
                .maxInboundMessageSize(configuration.getMaxInboundMessageSize())
                .maxInboundMetadataSize(configuration.getMaxInboundMetadataSize());
    }

    /**
     * Add a gRPC service to the server.
     */
    public GrpcServerFactory addService(BindableService service) {
        serverBuilder.addService(service);
        return this;
    }

    /**
     * Add a service definition to the server.
     */
    public GrpcServerFactory addService(ServerServiceDefinition serviceDefinition) {
        serverBuilder.addService(serviceDefinition);
        return this;
    }

    /**
     * Start the gRPC server.
     */
    public synchronized void start() throws IOException {
        if (server == null) {
            server = serverBuilder.build().start();
            LOGGER.info("gRPC server started on port " + configuration.getPort());
        }
    }

    public Server getServer() {
        return server;
    }

    public GrpcServerConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        if (server != null) {
            server.shutdown();
            try {
                if (server.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.info("gRPC server shutdown");
                } else {
                    server.shutdownNow();
                    LOGGER.info("gRPC server shutdown now");
                }
            } catch (InterruptedException e) {
                server.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerFactory.class);
}
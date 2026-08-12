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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class DubboServiceFactory {

    private final DubboConfiguration configuration;
    private final DubboBootstrap bootstrap;

    public DubboServiceFactory(DubboConfiguration configuration) {
        this.configuration = configuration;
        this.bootstrap = initBootstrap(configuration);
    }

    private DubboBootstrap initBootstrap(DubboConfiguration configuration) {
        ApplicationConfig application = new ApplicationConfig();
        application.setName(configuration.getApplicationName());
        if (configuration.getApplicationOwner() != null && !configuration.getApplicationOwner().isBlank()) {
            application.setOwner(configuration.getApplicationOwner());
        }
        if (configuration.getApplicationOrganization() != null && !configuration.getApplicationOrganization().isBlank()) {
            application.setOrganization(configuration.getApplicationOrganization());
        }

        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName(configuration.getProtocolName());
        if (configuration.getProtocolPort() > 0) {
            protocol.setPort(configuration.getProtocolPort());
        }
        if (configuration.getProtocolHost() != null && !configuration.getProtocolHost().isBlank()) {
            protocol.setHost(configuration.getProtocolHost());
        }
        protocol.setThreadpool(configuration.getProtocolThreadpool());
        protocol.setThreads(configuration.getProtocolThreads());

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(application).protocol(protocol);

        if (configuration.getRegistryAddress() != null && !configuration.getRegistryAddress().isBlank()) {
            RegistryConfig registry = new RegistryConfig();
            registry.setAddress(configuration.getRegistryAddress());
            registry.setProtocol(configuration.getRegistryProtocol());
            registry.setCheck(configuration.isRegistryCheck());
            registry.setRegister(configuration.isRegistryRegister());
            if (configuration.getRegistryGroup() != null && !configuration.getRegistryGroup().isBlank()) {
                registry.setGroup(configuration.getRegistryGroup());
            }
            bootstrap.registry(registry);
        }

        ProviderConfig provider = new ProviderConfig();
        provider.setTimeout(configuration.getProviderTimeout());
        provider.setRetries(configuration.getProviderRetries());
        provider.setDelay(configuration.getProviderDelay());
        bootstrap.provider(provider);

        ConsumerConfig consumer = new ConsumerConfig();
        consumer.setTimeout(configuration.getConsumerTimeout());
        consumer.setRetries(configuration.getConsumerRetries());
        consumer.setCheck(configuration.isConsumerCheck());
        bootstrap.consumer(consumer);

        bootstrap.start();
        LOGGER.info("Dubbo bootstrap started");
        return bootstrap;
    }

    /**
     * Export a service.
     */
    public <T> ServiceConfig<T> exportService(Class<T> interfaceClass, T ref) {
        ServiceConfig<T> service = new ServiceConfig<>();
        service.setInterface(interfaceClass);
        service.setRef(ref);
        service.export();
        return service;
    }

    /**
     * Create a reference (consumer) for a remote service.
     */
    public <T> T getReference(Class<T> interfaceClass) {
        ReferenceConfig<T> reference = new ReferenceConfig<>();
        reference.setInterface(interfaceClass);
        return reference.get();
    }

    public DubboBootstrap getBootstrap() {
        return bootstrap;
    }

    public DubboConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        bootstrap.stop();
        LOGGER.info("Dubbo bootstrap stopped");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboServiceFactory.class);
}
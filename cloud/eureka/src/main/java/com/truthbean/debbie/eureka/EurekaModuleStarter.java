/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.eureka.client.EurekaClient;
import com.truthbean.debbie.eureka.client.EurekaHeartbeatScheduler;
import com.truthbean.debbie.eureka.model.InstanceInfo;
import com.truthbean.debbie.eureka.server.EurekaServerEndpoint;
import com.truthbean.debbie.eureka.server.EurekaServerRegistry;

import java.net.InetAddress;
import java.util.UUID;

/**
 * debbie-eureka module starter.
 * <p>
 * Depending on configuration, it:
 * <ul>
 *   <li>if {@code debbie.eureka.server.enable=true}: creates
 *       {@link EurekaServerRegistry} and {@link EurekaServerEndpoint}</li>
 *   <li>if {@code debbie.eureka.client.enable=true}: creates
 *       {@link EurekaClient} and optionally auto-registers
 *       the current instance with the Eureka server</li>
 *   <li>if auto-register is enabled: starts
 *       {@link EurekaHeartbeatScheduler} for periodic heartbeats</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EurekaModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.eureka.enable";

    private EurekaServerRegistry serverRegistry;
    private EurekaHeartbeatScheduler heartbeatScheduler;

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(EurekaConfiguration.class);
        configBeanInfo.addBeanName("eurekaConfiguration", EurekaConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        EurekaConfiguration configuration = factory.factory(EurekaConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        if (configuration.isServerEnable()) {
            startServer(configuration, beanInfoManager);
        }

        if (configuration.isClientEnable()) {
            startClient(configuration, beanInfoManager);
        }

        LOGGER.info(() -> "debbie-eureka started, server=" + configuration.isServerEnable()
                + ", client=" + configuration.isClientEnable());
    }

    private void startServer(EurekaConfiguration config, BeanInfoManager beanInfoManager) {
        serverRegistry = new EurekaServerRegistry(
                config.getServerEvictionTimeout(), config.getServerEvictionInterval());

        var registryBean = new SimpleBeanFactory<>(serverRegistry,
                EurekaServerRegistry.class, "eurekaServerRegistry");
        beanInfoManager.registerBeanInfo(registryBean);

        var endpoint = new EurekaServerEndpoint(serverRegistry, config.getServerPrefix());
        var endpointBean = new SimpleBeanFactory<>(endpoint,
                EurekaServerEndpoint.class, "eurekaServerEndpoint");
        beanInfoManager.registerBeanInfo(endpointBean);

        LOGGER.info(() -> "eureka server started, prefix=" + config.getServerPrefix()
                + ", evictionTimeout=" + config.getServerEvictionTimeout() + "ms");
    }

    private void startClient(EurekaConfiguration config, BeanInfoManager beanInfoManager) {
        var client = new EurekaClient(config.getClientServerUrl(), config.getClientPrefix(),
                config.getClientConnectTimeout(), config.getClientReadTimeout());

        var clientBean = new SimpleBeanFactory<>(client,
                EurekaClient.class, "eurekaClient");
        beanInfoManager.registerBeanInfo(clientBean);

        if (config.isClientAutoRegister() && config.getClientServiceName() != null) {
            autoRegister(config, client, beanInfoManager);
        }

        LOGGER.info(() -> "eureka client started, serverUrl=" + config.getClientServerUrl()
                + ", autoRegister=" + config.isClientAutoRegister());
    }

    private void autoRegister(EurekaConfiguration config, EurekaClient client,
                               BeanInfoManager beanInfoManager) {
        var serviceName = config.getClientServiceName();
        String instanceId = config.getClientInstanceId();
        if (instanceId == null || instanceId.isBlank()) {
            instanceId = serviceName + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        final var finalInstanceId = instanceId;

        var host = config.getClientHost();
        if (host == null || host.isBlank()) {
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                host = "localhost";
            }
        }

        var instance = new InstanceInfo(serviceName.toUpperCase(), instanceId, host, config.getClientPort());
        instance.setSecurePort(config.getClientSecurePort());

        try {
            var registered = client.register(instance);
            if (registered) {
                LOGGER.info(() -> "registered " + finalInstanceId + " to " + serviceName
                        + " at " + config.getClientServerUrl());
                heartbeatScheduler = new EurekaHeartbeatScheduler(client, instance,
                        config.getClientHeartbeatInterval());
                heartbeatScheduler.start();

                var hbBean = new SimpleBeanFactory<>(heartbeatScheduler,
                        EurekaHeartbeatScheduler.class, "eurekaHeartbeatScheduler");
                beanInfoManager.registerBeanInfo(hbBean);
            } else {
                LOGGER.warn("failed to register " + finalInstanceId + " to eureka server");
            }
        } catch (EurekaException e) {
            LOGGER.warn("eureka server not reachable: " + e.getMessage());
        }
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 56;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.stop();
        }
        if (serverRegistry != null) {
            serverRegistry.shutdown();
        }
        LOGGER.info(() -> "debbie-eureka released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaModuleStarter.class);
}
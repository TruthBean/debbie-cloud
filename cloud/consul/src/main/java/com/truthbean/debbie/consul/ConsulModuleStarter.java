/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;

import com.truthbean.debbie.consul.config.ConsulConfigClient;
import com.truthbean.debbie.consul.config.ConsulConfigSource;
import com.truthbean.debbie.consul.discovery.ConsulDiscoveryClient;
import com.truthbean.debbie.consul.discovery.ConsulServiceRegistration;
import com.truthbean.debbie.consul.discovery.ConsulServiceRegistry;
import com.truthbean.debbie.consul.health.ConsulHealthIndicator;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;

import java.util.Map;

/**
 * debbie-consul module starter.
 * <p>
 * Depending on configuration, it:
 * <ul>
 *   <li>creates a {@link ConsulClient} and registers it as a bean</li>
 *   <li>if {@code debbie.consul.discovery.enable=true}: creates
 *       {@link ConsulServiceRegistry} and {@link ConsulDiscoveryClient}</li>
 *   <li>if {@code debbie.consul.discovery.register=true}: registers the
 *       current application as a service in Consul</li>
 *   <li>if {@code debbie.consul.config.enable=true}: creates
 *       {@link ConsulConfigClient} and injects KV config into
 *       {@link EnvironmentDepositoryHolder}</li>
 *   <li>if {@code debbie.consul.health.enable=true}: creates
 *       {@link ConsulHealthIndicator}</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.consul.enable";

    private volatile ConsulServiceRegistry serviceRegistry;
    private volatile ConsulClient consulClient;

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(ConsulConfiguration.class);
        configBeanInfo.addBeanName("consulConfiguration", ConsulConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        ConsulConfiguration configuration = factory.factory(ConsulConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var client = new ConsulClient(configuration.getHost(), configuration.getPort(),
                configuration.getScheme(), configuration.getToken(),
                configuration.getConnectTimeout(), configuration.getReadTimeout());
        this.consulClient = client;

        var clientBean = new SimpleBeanFactory<>(client, ConsulClient.class, "consulClient");
        beanInfoManager.registerBeanInfo(clientBean);

        if (!client.isAgentAvailable()) {
            LOGGER.warn("consul agent at " + configuration.getScheme() + "://"
                    + configuration.getHost() + ":" + configuration.getPort()
                    + " is not reachable");
            if (configuration.isConfigFailFast()) {
                throw new ConsulException("consul agent not reachable and fail-fast is enabled");
            }
        }

        if (configuration.isHealthEnable()) {
            var healthIndicator = new ConsulHealthIndicator(client);
            var healthBean = new SimpleBeanFactory<>(healthIndicator,
                    ConsulHealthIndicator.class, "consulHealthIndicator");
            beanInfoManager.registerBeanInfo(healthBean);
        }

        if (configuration.isDiscoveryEnable()) {
            startDiscovery(configuration, client, beanInfoManager);
        }

        if (configuration.isConfigEnable()) {
            startConfig(configuration, client, applicationContext, beanInfoManager);
        }

        LOGGER.info(() -> "debbie-consul started, agent=" + configuration.getScheme() + "://"
                + configuration.getHost() + ":" + configuration.getPort()
                + ", discovery=" + configuration.isDiscoveryEnable()
                + ", config=" + configuration.isConfigEnable());
    }

    private void startDiscovery(ConsulConfiguration config, ConsulClient client,
                                 BeanInfoManager beanInfoManager) {
        var registry = new ConsulServiceRegistry(client);
        var registryBean = new SimpleBeanFactory<>(registry,
                ConsulServiceRegistry.class, "consulServiceRegistry");
        beanInfoManager.registerBeanInfo(registryBean);
        this.serviceRegistry = registry;

        var discoveryClient = new ConsulDiscoveryClient(client);
        var discoveryBean = new SimpleBeanFactory<>(discoveryClient,
                ConsulDiscoveryClient.class, "consulDiscoveryClient");
        beanInfoManager.registerBeanInfo(discoveryBean);

        if (config.isDiscoveryRegister() && config.getServiceName() != null) {
            var registration = new ConsulServiceRegistration();
            registration.setId(config.getServiceName() + "-" + config.getServicePort());
            registration.setName(config.getServiceName());
            registration.setAddress(config.getServiceAddress() != null
                    ? config.getServiceAddress() : "localhost");
            registration.setPort(config.getServicePort());

            var healthUrl = config.getScheme() + "://" + registration.getAddress()
                    + ":" + config.getServicePort() + config.getDiscoveryHealthCheckPath();
            registration.setCheckHttp(healthUrl);
            registration.setCheckInterval(config.getDiscoveryHeartbeatInterval());
            registration.setCheckDeregisterCriticalAfter(config.getDiscoveryDeregisterCriticalAfter());

            try {
                registry.register(registration);
            } catch (ConsulException e) {
                LOGGER.warn("failed to register service in consul: " + e.getMessage());
            }
        }
    }

    private void startConfig(ConsulConfiguration config, ConsulClient client,
                              ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configClient = new ConsulConfigClient(client,
                config.getConfigPrefix(), config.getConfigProfileSeparator());
        var configClientBean = new SimpleBeanFactory<>(configClient,
                ConsulConfigClient.class, "consulConfigClient");
        beanInfoManager.registerBeanInfo(configClientBean);

        var appName = config.getServiceName() != null ? config.getServiceName() : "application";
        var environmentHolder = applicationContext.getEnvironmentHolder();
        var defaultProfile = EnvironmentDepositoryHolder.DEFAULT_PROFILE;

        try {
            var kvConfig = configClient.getConfig(appName, defaultProfile);
            if (!kvConfig.isEmpty()) {
                for (var entry : kvConfig.entrySet()) {
                    environmentHolder.addProperty(entry.getKey(), entry.getValue());
                }
                LOGGER.info(() -> "loaded " + kvConfig.size()
                        + " config entries from consul KV (app=" + appName + ", profile=" + defaultProfile + ")");
            }

            var configSource = new ConsulConfigSource(client,
                    "/" + config.getConfigPrefix() + "/" + appName + "/");
            var sourceBean = new SimpleBeanFactory<>(configSource,
                    ConsulConfigSource.class, "consulConfigSource");
            beanInfoManager.registerBeanInfo(sourceBean);
        } catch (ConsulException e) {
            LOGGER.warn("failed to load config from consul KV: " + e.getMessage());
            if (config.isConfigFailFast()) {
                throw e;
            }
        }
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 1320004;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        if (serviceRegistry != null && serviceRegistry.isRegistered()) {
            try {
                serviceRegistry.deregister();
            } catch (Exception e) {
                LOGGER.warn("failed to deregister service from consul: " + e.getMessage());
            }
        }
        LOGGER.info(() -> "debbie-consul released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulModuleStarter.class);
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;

import com.truthbean.debbie.kubernetes.config.KubernetesConfigClient;
import com.truthbean.debbie.kubernetes.discovery.KubernetesServiceDiscovery;
import com.truthbean.debbie.kubernetes.health.KubernetesHealthIndicator;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;

/**
 * debbie-kubernetes module starter.
 * <p>
 * Depending on configuration, it:
 * <ul>
 *   <li>auto-detects Kubernetes API server connection from pod environment</li>
 *   <li>creates a {@link KubernetesClient} and registers it as a bean</li>
 *   <li>if {@code debbie.kubernetes.discovery.enable=true}: creates
 *       {@link KubernetesServiceDiscovery}</li>
 *   <li>if {@code debbie.kubernetes.config.enable=true}: creates
 *       {@link KubernetesConfigClient} and injects ConfigMap/Secret
 *       values into {@link EnvironmentDepositoryHolder}</li>
 *   <li>if {@code debbie.kubernetes.health.enable=true}: creates
 *       {@link KubernetesHealthIndicator}</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.kubernetes.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(KubernetesConfiguration.class);
        configBeanInfo.addBeanName("kubernetesConfiguration", KubernetesConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        KubernetesConfiguration configuration = factory.factory(KubernetesConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var props = configuration.toProperties();
        var client = new KubernetesClient(props);

        var clientBean = new SimpleBeanFactory<>(client, KubernetesClient.class, "kubernetesClient");
        beanInfoManager.registerBeanInfo(clientBean);

        if (!client.isApiServerAvailable()) {
            LOGGER.warn("kubernetes api server at " + props.getBaseUrl() + " is not reachable");
            if (configuration.isConfigFailFast()) {
                throw new KubernetesException("kubernetes api server not reachable and fail-fast is enabled");
            }
        } else {
            LOGGER.info(() -> "kubernetes api server connected: " + props);
        }

        if (configuration.isHealthEnable()) {
            var health = new KubernetesHealthIndicator(client);
            var healthBean = new SimpleBeanFactory<>(health,
                    KubernetesHealthIndicator.class, "kubernetesHealthIndicator");
            beanInfoManager.registerBeanInfo(healthBean);
        }

        if (configuration.isDiscoveryEnable()) {
            var discovery = new KubernetesServiceDiscovery(client);
            var discoveryBean = new SimpleBeanFactory<>(discovery,
                    KubernetesServiceDiscovery.class, "kubernetesServiceDiscovery");
            beanInfoManager.registerBeanInfo(discoveryBean);
        }

        if (configuration.isConfigEnable()) {
            startConfig(configuration, client, applicationContext, beanInfoManager);
        }

        LOGGER.info(() -> "debbie-kubernetes started, discovery=" + configuration.isDiscoveryEnable()
                + ", config=" + configuration.isConfigEnable()
                + ", namespace=" + props.getNamespace());
    }

    private void startConfig(KubernetesConfiguration config, KubernetesClient client,
                              ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configClient = new KubernetesConfigClient(client);
        var configClientBean = new SimpleBeanFactory<>(configClient,
                KubernetesConfigClient.class, "kubernetesConfigClient");
        beanInfoManager.registerBeanInfo(configClientBean);

        var environmentHolder = applicationContext.getEnvironmentHolder();
        var appName = config.getServiceName() != null ? config.getServiceName() : "application";
        var profile = EnvironmentDepositoryHolder.DEFAULT_PROFILE;

        try {
            var configMapNames = config.getConfigMapNames();
            for (var cmName : configMapNames) {
                var data = configClient.getConfigMap(cmName);
                if (!data.isEmpty()) {
                    for (var entry : data.entrySet()) {
                        environmentHolder.addProperty(entry.getKey(), entry.getValue());
                    }
                    LOGGER.info(() -> "loaded " + data.size() + " entries from configmap [" + cmName + "]");
                }
            }

            var secretNames = config.getSecretNames();
            for (var sName : secretNames) {
                var data = configClient.getSecret(sName);
                if (!data.isEmpty()) {
                    for (var entry : data.entrySet()) {
                        environmentHolder.addProperty(entry.getKey(), entry.getValue());
                    }
                    LOGGER.info(() -> "loaded " + data.size() + " entries from secret [" + sName + "]");
                }
            }

            if (configMapNames.length == 0 && secretNames.length == 0) {
                var allConfig = configClient.getConfig(appName, profile);
                if (!allConfig.isEmpty()) {
                    for (var entry : allConfig.entrySet()) {
                        environmentHolder.addProperty(entry.getKey(), entry.getValue());
                    }
                    LOGGER.info(() -> "loaded " + allConfig.size()
                            + " config entries from configmaps/secrets (app=" + appName + ")");
                }
            }
        } catch (KubernetesException e) {
            LOGGER.warn("failed to load config from kubernetes: " + e.getMessage());
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
        return 55;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        LOGGER.info(() -> "debbie-kubernetes released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesModuleStarter.class);
}
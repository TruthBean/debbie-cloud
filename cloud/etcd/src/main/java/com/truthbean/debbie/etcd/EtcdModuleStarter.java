/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.etcd.config.EtcdConfigClient;
import com.truthbean.debbie.etcd.discovery.EtcdServiceDiscovery;

/**
 * debbie-etcd module starter.
 * <p>
 * Depending on configuration, it:
 * <ul>
 *   <li>creates an {@link EtcdClient} and registers it as a bean</li>
 *   <li>if {@code debbie.etcd.discovery.enable=true}: creates
 *       {@link EtcdServiceDiscovery}</li>
 *   <li>if {@code debbie.etcd.config.enable=true}: creates
 *       {@link EtcdConfigClient} and injects config values into
 *       {@link EnvironmentDepositoryHolder}</li>
 *   <li>if {@code debbie.etcd.health.enable=true}: creates
 *       {@link EtcdHealthIndicator}</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.etcd.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(EtcdConfiguration.class);
        configBeanInfo.addBeanName("etcdConfiguration", EtcdConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        EtcdConfiguration configuration = factory.factory(EtcdConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var props = configuration.toProperties();
        var client = new EtcdClient(props, configuration.getConnectTimeout(), configuration.getReadTimeout());

        var clientBean = new SimpleBeanFactory<>(client, EtcdClient.class, "etcdClient");
        beanInfoManager.registerBeanInfo(clientBean);

        if (!client.isAvailable()) {
            LOGGER.warn("etcd at " + props.getBaseUrl() + " is not reachable");
            if (configuration.isConfigFailFast()) {
                throw new EtcdException("etcd not reachable and fail-fast is enabled");
            }
        } else {
            LOGGER.info(() -> "etcd connected: " + props);
        }

        if (configuration.isHealthEnable()) {
            var health = new EtcdHealthIndicator(client);
            var healthBean = new SimpleBeanFactory<>(health,
                    EtcdHealthIndicator.class, "etcdHealthIndicator");
            beanInfoManager.registerBeanInfo(healthBean);
        }

        if (configuration.isDiscoveryEnable()) {
            var discovery = new EtcdServiceDiscovery(client);
            var discoveryBean = new SimpleBeanFactory<>(discovery,
                    EtcdServiceDiscovery.class, "etcdServiceDiscovery");
            beanInfoManager.registerBeanInfo(discoveryBean);
        }

        if (configuration.isConfigEnable()) {
            startConfig(configuration, client, applicationContext, beanInfoManager);
        }

        LOGGER.info(() -> "debbie-etcd started, discovery=" + configuration.isDiscoveryEnable()
                + ", config=" + configuration.isConfigEnable());
    }

    private void startConfig(EtcdConfiguration config, EtcdClient client,
                              ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configClient = new EtcdConfigClient(client);
        var configClientBean = new SimpleBeanFactory<>(configClient,
                EtcdConfigClient.class, "etcdConfigClient");
        beanInfoManager.registerBeanInfo(configClientBean);

        var environmentHolder = applicationContext.getEnvironmentHolder();
        var appName = config.getConfigAppName();
        var profile = config.getConfigProfile();

        try {
            var etcdConfig = configClient.getConfig(appName, profile);
            if (!etcdConfig.isEmpty()) {
                for (var entry : etcdConfig.entrySet()) {
                    environmentHolder.addProperty(entry.getKey(), entry.getValue());
                }
                LOGGER.info(() -> "loaded " + etcdConfig.size()
                        + " config entries from etcd (app=" + appName + ", profile=" + profile + ")");
            }
        } catch (EtcdException e) {
            LOGGER.warn("failed to load config from etcd: " + e.getMessage());
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
        return 1320002;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        LOGGER.info(() -> "debbie-etcd released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdModuleStarter.class);
}
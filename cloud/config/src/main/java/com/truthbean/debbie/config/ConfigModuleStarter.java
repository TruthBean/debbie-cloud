/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.config.client.ConfigClient;
import com.truthbean.debbie.config.encrypt.ConfigEncryptor;
import com.truthbean.debbie.config.encrypt.ConfigEncryptorFactory;
import com.truthbean.debbie.config.repository.ConfigRepository;
import com.truthbean.debbie.config.repository.ConfigRepositoryFactory;
import com.truthbean.debbie.config.server.ConfigServer;
import com.truthbean.debbie.config.server.ConfigServerEndpoint;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;

import java.util.ServiceLoader;

/**
 * debbie-config module starter.
 * <p>
 * depending on configuration, it starts:
 * <ul>
 *   <li>a {@link ConfigServer} if {@code debbie.config.server.enable=true}</li>
 *   <li>a {@link ConfigClient} if {@code debbie.config.client.enable=true}</li>
 * </ul>
 * both can run simultaneously (e.g. a config server that also pulls from an upstream server).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConfigModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.config.enable";

    private volatile ConfigClient configClient;

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(ConfigConfiguration.class);
        configBeanInfo.addBeanName("configConfiguration", ConfigConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        ConfigConfiguration configuration = factory.factory(ConfigConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        if (configuration.isServerEnable()) {
            startServer(configuration, beanInfoManager);
        }

        if (configuration.isClientEnable()) {
            startClient(configuration, applicationContext, beanInfoManager);
        }
    }

    private void startServer(ConfigConfiguration configuration, BeanInfoManager beanInfoManager) {
        var repository = resolveRepository(configuration);
        if (repository == null) {
            LOGGER.warn("no ConfigRepositoryFactory matched debbie.config.server.repository="
                    + configuration.getServerRepository() + ", config server disabled");
            return;
        }
        var repositoryBean = new SimpleBeanFactory<>(repository, ConfigRepository.class, "configRepository");
        beanInfoManager.registerBeanInfo(repositoryBean);

        var encryptor = resolveEncryptor(configuration);
        if (encryptor != null) {
            var encryptorBean = new SimpleBeanFactory<>(encryptor, ConfigEncryptor.class, "configEncryptor");
            beanInfoManager.registerBeanInfo(encryptorBean);
        }

        var server = new ConfigServer(configuration, repository,
                encryptor != null ? encryptor : new com.truthbean.debbie.config.encrypt.NoopConfigEncryptor());
        var serverBean = new SimpleBeanFactory<>(server, ConfigServer.class, "configServer");
        beanInfoManager.registerBeanInfo(serverBean);

        var endpointBean = new SimpleBeanFactory<>(server.getEndpoint(),
                ConfigServerEndpoint.class, "configServerEndpoint");
        beanInfoManager.registerBeanInfo(endpointBean);

        LOGGER.info(() -> "debbie-config server started, repository=" + repository.name()
                + ", prefix=" + configuration.getServerPrefix());
    }

    private void startClient(ConfigConfiguration configuration, ApplicationContext applicationContext,
                             BeanInfoManager beanInfoManager) {
        var environmentHolder = applicationContext.getEnvironmentHolder();
        var client = new ConfigClient(configuration, environmentHolder);
        var clientBean = new SimpleBeanFactory<>(client, ConfigClient.class, "configClient");
        beanInfoManager.registerBeanInfo(clientBean);
        this.configClient = client;

        LOGGER.info(() -> "debbie-config client enabled, uri=" + configuration.getClientUri()
                + ", name=" + configuration.getClientName()
                + ", profile=" + configuration.getClientProfile());
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        if (configClient != null) {
            var env = configClient.fetchAndInject();
            if (env != null) {
                LOGGER.info(() -> "debbie-config client initialized with " + env.getPropertySources().size()
                        + " property sources");
            }
        }
    }

    @Override
    public int getOrder() {
        return 50;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        LOGGER.info(() -> "debbie-config released");
    }

    private ConfigRepository resolveRepository(ConfigConfiguration configuration) {
        var loader = ServiceLoader.load(ConfigRepositoryFactory.class);
        for (var f : loader) {
            try {
                if (f.support(configuration)) {
                    return f.create(configuration);
                }
            } catch (Exception e) {
                LOGGER.error("config repository factory " + f + " failed", e);
            }
        }
        return null;
    }

    private ConfigEncryptor resolveEncryptor(ConfigConfiguration configuration) {
        var loader = ServiceLoader.load(ConfigEncryptorFactory.class);
        for (var f : loader) {
            try {
                if (f.support(configuration)) {
                    return f.create(configuration);
                }
            } catch (Exception e) {
                LOGGER.error("config encryptor factory " + f + " failed", e);
            }
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigModuleStarter.class);
}
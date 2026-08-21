/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.loadbalancer;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

/**
 * debbie-loadbalancer module starter.
 * <p>
 * Creates a {@link LoadBalancerRegistry} with the configured default strategy
 * and registers it as a bean for other modules to use.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LoadBalancerModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.loadbalancer.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(LoadBalancerConfiguration.class);
        configBeanInfo.addBeanName("loadBalancerConfiguration", LoadBalancerConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        LoadBalancerConfiguration configuration = factory.factory(LoadBalancerConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var registry = new LoadBalancerRegistry(configuration.getDefaultStrategy());

        var registryBean = new SimpleBeanFactory<>(registry,
                LoadBalancerRegistry.class, "loadBalancerRegistry");
        beanInfoManager.registerBeanInfo(registryBean);

        LOGGER.info(() -> "debbie-loadbalancer started, strategy=" + configuration.getDefaultStrategy()
                + ", retry=" + configuration.isRetryEnable()
                + ", cache=" + configuration.isCacheEnable());
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 1010001;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        LOGGER.info(() -> "debbie-loadbalancer released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancerModuleStarter.class);
}
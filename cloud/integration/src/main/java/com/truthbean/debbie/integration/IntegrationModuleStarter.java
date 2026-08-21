/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

/**
 * debbie-integration module starter.
 * <p>
 * Creates an {@link IntegrationFlowContext} and registers it as a bean
 * for other modules to register and manage integration flows.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class IntegrationModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.integration.enable";

    private IntegrationFlowContext flowContext;

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(IntegrationConfiguration.class);
        configBeanInfo.addBeanName("integrationConfiguration", IntegrationConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        IntegrationConfiguration configuration = factory.factory(IntegrationConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        flowContext = new IntegrationFlowContext();
        if (configuration.isAutoStart()) {
            flowContext.start();
        }

        var contextBean = new SimpleBeanFactory<>(flowContext,
                IntegrationFlowContext.class, "integrationFlowContext");
        beanInfoManager.registerBeanInfo(contextBean);

        LOGGER.info(() -> "debbie-integration started, defaultChannel=" + configuration.getDefaultChannelType()
                + ", queueCapacity=" + configuration.getQueueCapacity()
                + ", autoStart=" + configuration.isAutoStart());
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 1010009;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        if (flowContext != null) {
            flowContext.stop();
            flowContext.clear();
        }
        LOGGER.info(() -> "debbie-integration released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationModuleStarter.class);
}
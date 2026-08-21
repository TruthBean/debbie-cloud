/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mongo;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

/**
 * debbie-mongo module starter.
 * <p>
 * Creates a {@link MongoClientFactory} from the configuration and
 * registers it as a bean for injection.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MongoModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.mongo.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(MongoConfiguration.class);
        configBeanInfo.addBeanName("mongoConfiguration", MongoConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        MongoConfiguration configuration = factory.factory(MongoConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var clientFactory = new MongoClientFactory(configuration);
        var beanFactory = new SimpleBeanFactory<>(clientFactory, MongoClientFactory.class);
        beanInfoManager.registerBeanInfo(beanFactory);

        LOGGER.info(() -> "debbie-mongo started, " + configuration);
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 23001;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        MongoClientFactory clientFactory = factory.factory(MongoClientFactory.class);
        if (clientFactory != null) {
            clientFactory.close();
        }
        LOGGER.info(() -> "debbie-mongo released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoModuleStarter.class);
}
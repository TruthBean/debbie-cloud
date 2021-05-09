/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.spring;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-07 13:53
 */
public class SpringModuleStarter implements DebbieModuleStarter {

    private volatile ConfigurableApplicationContext applicationContext;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void starter(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();

        BeanScanConfiguration configuration = configurationFactory.factory(BeanScanConfiguration.class, applicationContext);
        String scanBasePackage = configuration.getScanBasePackage();
        Class<?> applicationClass = configuration.getApplicationClass();

        if (scanBasePackage != null) {
            this.applicationContext = new AnnotationConfigApplicationContext(scanBasePackage);
        } else if (applicationClass != null) {
            this.applicationContext = new AnnotationConfigApplicationContext(applicationClass);
        } else {
            this.applicationContext = new AnnotationConfigApplicationContext();
        }
        // if spring application context is refreshed
        if (!this.applicationContext.isActive()) {
            // just call 'refresh' once
            this.applicationContext.refresh();
        }
        String[] names = this.applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = this.applicationContext.getBean(name);
            Class<?> beanClass = bean.getClass();
            DebbieBeanInfo beanInfo = new DebbieBeanInfo<>(beanClass);
            beanInfo.setBean(bean);
            beanInfo.addBeanName(name);
            if (this.applicationContext.isSingleton(name)) {
                beanInfo.setBeanType(BeanType.SINGLETON);
            } else {
                beanInfo.setBeanType(BeanType.NO_LIMIT);
                AutowireCapableBeanFactory autowireCapableBeanFactory = this.applicationContext.getAutowireCapableBeanFactory();
                SpringDebbieBeanFactory beanFactory =
                        new SpringDebbieBeanFactory<>(applicationContext.getBeanInfoFactory(), beanClass, name);
                beanFactory.setSpringBeanFactory(autowireCapableBeanFactory);
                beanFactory.setSingleton(false);
                beanInfo.setBeanFactory(beanFactory);
            }

            if (!applicationContext.getGlobalBeanFactory().containsBean(name)) {
                beanInitialization.initBean(beanInfo);
            }
        }
        applicationContext.refreshBeans();
    }

    @Override
    public void release(DebbieConfigurationCenter configurationFactory, ApplicationContext debbieApplicationContext) {
        if (applicationContext != null)
            applicationContext.close();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}

/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.spring;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-07 13:53
 */
public class SpringModuleStarter implements DebbieModuleStarter {

    private volatile AnnotationConfigApplicationContext applicationContext;

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment) && environment.getBooleanValue("debbie.spring.enable", true);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
        this.applicationContext = new AnnotationConfigApplicationContext();
    }

    /**
     * 放到starter注册是因为new AnnotationConfigApplicationContext的时候启动并完成了注册的过程
     */
    @Override
    public void starter(ApplicationContext applicationContext) {
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        // cache
        String[] names = this.applicationContext.getBeanDefinitionNames();

        // scanned class to be registered to spring
        BeanScanConfiguration configuration = applicationContext.getGlobalBeanFactory().factory(BeanScanConfiguration.class);
        Set<String> scanBasePackages = configuration.getScanBasePackages();
        Set<Class<?>> classes = configuration.getScanClasses();
        Class<?> applicationClass = configuration.getApplicationClass();

        if (scanBasePackages != null && !scanBasePackages.isEmpty()) {
            for (String scanBasePackage : scanBasePackages) {
                this.applicationContext.scan(scanBasePackage);
            }
        } else if (applicationClass != null) {
            String packageName = applicationClass.getPackageName();
            this.applicationContext.scan(packageName);
        }
        if (applicationClass != null) {
            this.applicationContext.register(applicationClass);
        }
        if (!classes.isEmpty()) {
            for (Class<?> c : classes) {
                this.applicationContext.register(c);
            }
        }
        // if spring application context is refreshed
        if (!this.applicationContext.isActive()) {
            // just call 'refresh' once
            this.applicationContext.refresh();
        }

        // spring to debbie
        for (String name : names) {
            beanInfoManager.registerBeanInfo(new SpringBeanFactory(this.applicationContext, name));
        }
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        // if spring application context is refreshed
        if (!this.applicationContext.isActive()) {
            // just call 'refresh' once
            this.applicationContext.refresh();
        }
    }

    @Override
    public void release(ApplicationContext debbieApplicationContext) {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }
}

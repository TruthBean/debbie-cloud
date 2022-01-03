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

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-07 13:53
 */
public class SpringModuleStarter implements DebbieModuleStarter {

    private volatile ConfigurableApplicationContext applicationContext;

    @Override
    public boolean enable(EnvironmentContent envContent) {
        return envContent.getBooleanValue("debbie.spring.enable", true);
    }

    /**
     * 放到starter注册是因为new AnnotationConfigApplicationContext的时候启动并完成了注册的过程
     */
    @Override
    public void starter(ApplicationContext applicationContext) {
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        BeanScanConfiguration configuration = applicationContext.factory(BeanScanConfiguration.class);
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
            beanInfoManager.register(new SpringBeanFactory(this.applicationContext, name));
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

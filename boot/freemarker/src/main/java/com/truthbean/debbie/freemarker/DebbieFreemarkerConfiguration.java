/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.freemarker;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import freemarker.template.Configuration;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieFreemarkerConfiguration {

    public DebbieFreemarkerConfiguration() {
    }

    public void register(ApplicationContext context) {
        DebbieBeanInfo<Configuration> beanInfo = new DebbieBeanInfo<>(Configuration.class);
        beanInfo.addBeanName("freemarkerConfiguration");
        DefaultConfigurationBeanFactory beanFactory = new DefaultConfigurationBeanFactory();
        beanFactory.setGlobalBeanFactory(context.getGlobalBeanFactory());
        beanFactory.setClassLoader(context.getClassLoader());
        beanInfo.setBeanFactory(beanFactory);

        BeanInitialization beanInitialization = context.getBeanInitialization();
        beanInitialization.initSingletonBean(beanInfo);
        context.refreshBeans();
    }

    public void configure(ApplicationContext context) {
        context.refreshBeans();
    }
}

/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.freemarker;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class FreemarkerModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.freemarker.enable";

    @Override
    public boolean enable(Environment envContent) {
        return DebbieModuleStarter.super.enable(envContent) && envContent.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext context, BeanInfoManager beanInfoManager) {
        DefaultConfigurationBeanFactory beanFactory = new DefaultConfigurationBeanFactory("freemarkerConfiguration");
        beanInfoManager.registerBeanInfo(beanFactory);
    }

    @Override
    public void configure(ApplicationContext context) {
    }

    @Override
    public int getOrder() {
        return 53;
    }
}

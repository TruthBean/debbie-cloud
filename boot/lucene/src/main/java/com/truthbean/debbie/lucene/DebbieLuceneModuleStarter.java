/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lucene;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-13 19:54
 */
public class DebbieLuceneModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.lucene.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment) && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public int getOrder() {
        return 109;
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var beanFactory = new PropertiesConfigurationBeanFactory<>(new LuceneProperties(), LuceneConfiguration.class);
        beanInfoManager.registerBeanInfo(beanFactory);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }
}

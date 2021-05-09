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

import com.truthbean.debbie.bean.*;
import freemarker.template.Configuration;

import java.util.Locale;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DefaultConfigurationBeanFactory implements BeanFactory<Configuration>, ClassLoaderAware {

    private Configuration configuration;
    private ClassLoader classLoader;

    public DefaultConfigurationBeanFactory() {
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory beanFactory) {

    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Configuration getBean() {
        if (configuration == null) {
            configuration = new Configuration(Configuration.VERSION_2_3_30);
            configuration.setClassLoaderForTemplateLoading(this.classLoader, "/templates");
            configuration.setDefaultEncoding("UTF-8");
            configuration.setLocale(Locale.CHINA);
        }

        return configuration;
    }

    @Override
    public Class<Configuration> getBeanType() {
        return Configuration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (configuration != null) {
            configuration.clearEncodingMap();
            configuration.clearSharedVariables();
            configuration.clearTemplateCache();
            configuration = null;
        }
    }
}

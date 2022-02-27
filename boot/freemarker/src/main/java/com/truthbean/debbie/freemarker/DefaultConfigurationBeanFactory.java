/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.freemarker;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import freemarker.template.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DefaultConfigurationBeanFactory implements BeanFactory<Configuration> {

    private Configuration configuration;

    private final Set<String> names = new HashSet<>();

    public DefaultConfigurationBeanFactory(String...names) {
        if (names != null && names.length > 0) {
            Collections.addAll(this.names, names);
        }
    }

    @Override
    public Configuration factoryBean(ApplicationContext applicationContext) {
        if (configuration == null) {
            configuration = new Configuration(Configuration.VERSION_2_3_30);
            configuration.setClassLoaderForTemplateLoading(applicationContext.getClassLoader(), "/templates");
            configuration.setDefaultEncoding("UTF-8");
            configuration.setLocale(Locale.CHINA);
        }

        return configuration;
    }

    @Override
    public Configuration factoryNamedBean(String name, ApplicationContext applicationContext) {
        return factoryBean(applicationContext);
    }

    @Override
    public boolean isCreated() {
        return configuration != null;
    }

    @Override
    public Configuration getCreatedBean() {
        return configuration;
    }

    @Override
    public Class<?> getBeanClass() {
        return Configuration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Set<String> getBeanNames() {
        return names;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        if (configuration != null) {
            configuration.clearEncodingMap();
            configuration.clearSharedVariables();
            configuration.clearTemplateCache();
            configuration = null;
        }
    }
}

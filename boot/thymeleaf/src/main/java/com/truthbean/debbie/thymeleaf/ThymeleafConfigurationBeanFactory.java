/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.thymeleaf;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ThymeleafConfigurationBeanFactory implements BeanFactory<TemplateEngine> {

    private volatile TemplateEngine templateEngine;

    private final Set<String> names = new HashSet<>();

    public ThymeleafConfigurationBeanFactory(String... names) {
        if (names != null && names.length > 0) {
            Collections.addAll(this.names, names);
        }
    }

    @Override
    public TemplateEngine factoryBean(ApplicationContext applicationContext) {
        if (templateEngine == null) {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setCharacterEncoding("UTF-8");
            templateResolver.setCacheable(true);

            templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
        }

        return templateEngine;
    }

    @Override
    public boolean isCreated() {
        return templateEngine != null;
    }

    @Override
    public TemplateEngine getCreatedBean() {
        return templateEngine;
    }

    @Override
    public Class<?> getBeanClass() {
        return TemplateEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Set<String> getAllName() {
        return names;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        if (templateEngine != null) {
            templateEngine.clearTemplateCache();
            templateEngine = null;
        }
    }
}
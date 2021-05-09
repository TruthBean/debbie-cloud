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

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.LoggerFactory;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-14 13:40
 */
public class DebbieBeanInjectAnnotationBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {

    private int order = Ordered.LOWEST_PRECEDENCE - 3;

    @SuppressWarnings("unchecked")
    public DebbieBeanInjectAnnotationBeanPostProcessor() {
        super();
        Set<Class<? extends Annotation>> autowiredAnnotationTypes = new HashSet<>();
        autowiredAnnotationTypes.add(BeanInject.class);
        LOGGER.trace(() -> "Debbie @BeanInject supported for autowiring");
        // autowiredAnnotationTypes.add(PropertyInject.class);
        // logger.trace("Debbie @PropertyInject supported for autowiring");
        try {
            Class<?> inject = Class.forName("javax.inject.Inject");
            if (Annotation.class.isAssignableFrom(inject)) {
                autowiredAnnotationTypes.add((Class<? extends Annotation>) inject);
            }
            LOGGER.trace(() -> "Debbie @Inject supported for autowiring");
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class<?> resource = Class.forName("javax.annotation.Resource");
            if (Annotation.class.isAssignableFrom(resource)) {
                autowiredAnnotationTypes.add((Class<? extends Annotation>) resource);
            }
            LOGGER.trace(() -> "Debbie @Resource supported for autowiring");
        } catch (ClassNotFoundException ignored) {
        }
        autowiredAnnotationTypes.add(Autowired.class);
        LOGGER.trace(() -> "Debbie @Autowired supported for autowiring");
        setAutowiredAnnotationTypes(autowiredAnnotationTypes);
    }

    @Override
    public PropertyValues postProcessProperties(@NonNull PropertyValues pvs, Object bean, @NonNull String beanName) {
        Class<?> beanClass = bean.getClass();
        try {
            // ignored bean list
            Class<?> debbieSwaggerRouterClass = Class.forName("com.truthbean.debbie.swagger.DebbieSwaggerRouter");
            if (beanClass == debbieSwaggerRouterClass) {
                return pvs;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return super.postProcessProperties(pvs, bean, beanName);
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInjectAnnotationBeanPostProcessor.class);
}

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
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.NonNull;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-03 23:12
 */
public abstract class DebbieSpringBeanFactory<Bean> implements BeanFactory<Bean>,
        // spring
        FactoryBean<Bean>, BeanFactoryAware, BeanNameAware {

    private ApplicationContext applicationContext;

    private volatile Class<Bean> beanClass;
    private volatile String name;

    private final boolean singleton = false;
    private org.springframework.beans.factory.BeanFactory springBeanFactory;

    private volatile Logger logger;
    private volatile BeanInfoManager debbieBeanInfoFactory;

    private final boolean isCreateBySpring;

    public DebbieSpringBeanFactory() {
        this.isCreateBySpring = true;
    }

    @Override
    public void setBeanName(@NonNull String name) {
        this.name = name;
    }

    public DebbieSpringBeanFactory(ApplicationContext applicationContext, BeanInfo<Bean> beanInfo) {
        this.applicationContext = applicationContext;
        this.debbieBeanInfoFactory = applicationContext.getBeanInfoManager();
        this.beanClass = (Class<Bean>) beanInfo.getBeanClass();
        this.name = beanInfo.getServiceName();
        this.logger = LoggerFactory.getLogger("com.truthbean.debbie.spring.DebbieSpringBeanFactory<" + beanClass.getName() + ">");
        this.isCreateBySpring = false;
    }

    @Override
    public Bean factoryBean(ApplicationContext applicationContext) {
        if (springBeanFactory != null) {
            return springBeanFactory.getBean(beanClass);
        }
        try {
            return getObject();
        } catch (Exception e) {
            logger.error("", e);
            throw new BeanCreatedException(e);
        }
    }

    @Override
    public Bean factoryNamedBean(String name, ApplicationContext applicationContext) {
        if (springBeanFactory != null) {
            return springBeanFactory.getBean(name, beanClass);
        }
        try {
            return getObject();
        } catch (Exception e) {
            logger.error("", e);
            throw new BeanCreatedException(e);
        }
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public Class<Bean> getBeanClass() {
        return beanClass;
    }

    public void setSpringBeanFactory(org.springframework.beans.factory.BeanFactory springBeanFactory) {
        this.springBeanFactory = springBeanFactory;
    }

    @Override
    public void setBeanFactory(@NonNull org.springframework.beans.factory.BeanFactory beanFactory) throws BeansException {
        setSpringBeanFactory(beanFactory);
    }

    @Override
    public Bean getObject() throws Exception {
        if (!isCreateBySpring) {
            BeanInfo<Bean> beanInfo = debbieBeanInfoFactory.getBeanInfo(name, getBeanClass(), false, false);
            if (beanInfo instanceof BeanFactory<Bean> beanFactory) {
                return beanFactory.factoryNamedBean(name, applicationContext);
            }
        }
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return beanClass;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        // destory
    }
}

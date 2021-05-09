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
import com.truthbean.debbie.bean.BeanCreatedException;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInfoFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
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
public class SpringDebbieBeanFactory<Bean> implements BeanFactory<Bean>,
        // spring
        FactoryBean<Bean>, BeanFactoryAware, BeanNameAware {

    private GlobalBeanFactory globalBeanFactory;

    private volatile Class<Bean> beanClass;
    private volatile String name;

    private boolean singleton = false;
    private org.springframework.beans.factory.BeanFactory springBeanFactory;

    private volatile Logger logger;
    private volatile BeanInfoFactory debbieBeanInfoFactory;

    private final boolean isCreateBySpring;

    public SpringDebbieBeanFactory() {
        this.isCreateBySpring = true;
    }

    @Override
    public void setBeanName(@NonNull String name) {
        this.name = name;
    }

    public void setBeanClass(Class<Bean> beanClass) {
        this.beanClass = beanClass;
    }

    public SpringDebbieBeanFactory(BeanInfoFactory debbieBeanInfoFactory, Class<Bean> beanClass, String name) {
        this.debbieBeanInfoFactory = debbieBeanInfoFactory;
        this.beanClass = beanClass;
        this.name = name;
        this.logger = LoggerFactory.getLogger("com.truthbean.debbie.spring.SpringDebbieBeanFactory<" + beanClass.getName() + ">");
        this.isCreateBySpring = false;
    }

    public SpringDebbieBeanFactory(BeanInfoFactory debbieBeanInfoFactory, BeanInfo<Bean> beanInfo) {
        this.debbieBeanInfoFactory = debbieBeanInfoFactory;
        this.beanClass = beanInfo.getBeanClass();
        this.name = beanInfo.getServiceName();
        this.logger = LoggerFactory.getLogger("com.truthbean.debbie.spring.SpringDebbieBeanFactory<" + beanClass.getName() + ">");
        this.isCreateBySpring = false;
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }

    @Override
    public Bean getBean() {
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
    public Class<Bean> getBeanType() {
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
            BeanInfo<Bean> beanInfo = debbieBeanInfoFactory.getBeanInfo(name, getBeanType(), false, false);
            if (beanInfo != null && beanInfo.getBeanClass().isInterface()) {
                return globalBeanFactory.factoryBeanByDependenceProcessor(beanInfo, false);
            } else if (beanInfo != null && !beanInfo.getBeanClass().isInterface()) {
                return globalBeanFactory.factoryBeanByDependenceProcessor(beanInfo, true);
            }
        }
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return beanClass;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public void destroy() {
        // destory
    }
}

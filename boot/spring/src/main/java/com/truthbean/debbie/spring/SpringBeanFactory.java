package com.truthbean.debbie.spring;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.NonBean;
import com.truthbean.debbie.core.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/19 00:47.
 */
@NonBean
public class SpringBeanFactory implements BeanFactory<Object> {

    private final ConfigurableApplicationContext applicationContext;
    private final String name;

    public SpringBeanFactory(ConfigurableApplicationContext applicationContext, String name) {
        this.applicationContext = applicationContext;
        this.name = name;
    }

    @Override
    public Object factoryBean(ApplicationContext applicationContext) {
        return this.applicationContext.getBean(name);
    }

    @Override
    public Object factoryNamedBean(String name, ApplicationContext applicationContext) {
        return this.applicationContext.getBean(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object factoryProxiedBean(String name, Class beanInterface, ApplicationContext applicationContext) {
        return this.applicationContext.getBean(name, beanInterface);
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public Object getCreatedBean() {
        return this.applicationContext.getBean(name);
    }

    @Override
    public BeanFactory<Object> copy() {
        return new SpringBeanFactory(applicationContext, name);
    }

    @Override
    public Class<?> getBeanClass() {
        return this.applicationContext.getType(name);
    }

    @Override
    public boolean isLazyCreate() {
        return false;
    }

    @Override
    public boolean isSingleton() {
        return this.applicationContext.isSingleton(name);
    }

    @Override
    public BeanType getBeanType() {
        if (isSingleton()) {
            return BeanType.SINGLETON;
        } else {
            return BeanType.NO_LIMIT;
        }
    }

    @Override
    public Set<String> getBeanNames() {
        Set<String> names = new HashSet<>();
        names.add(name);
        return names;
    }
}

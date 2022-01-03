package com.truthbean.debbie.mybatis.annotation;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/18 20:35.
 */
public class MybatisBeanRegister implements BeanRegister {

    private final BeanInfoManager beanInfoManager;

    public MybatisBeanRegister(ApplicationContext context) {
        beanInfoManager = context.getBeanInfoManager();
    }

    @Override
    public <Bean> boolean support(ClassBeanInfo<Bean> beanInfo) {
        Class<?> beanClass = beanInfo.getBeanClass();
        return beanClass.isInterface()
                && (support(beanInfo, Alias.class) || support(beanInfo, MappedTypes.class) || support(beanInfo, MappedJdbcTypes.class))
                && !beanInfoManager.isBeanRegistered(beanClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo) {
        return new DebbieReflectionBeanFactory<>((Class<Bean>) beanInfo.getBeanClass());
    }

    @Override
    public int getOrder() {
        return 11;
    }
}

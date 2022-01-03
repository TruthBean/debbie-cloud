package com.truthbean.debbie.kafka;

import com.truthbean.debbie.bean.*;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/18 20:35.
 */
public class KafkaMessageConsumerBeanRegister implements BeanRegister {

    public KafkaMessageConsumerBeanRegister() {
    }

    @Override
    public <Bean> boolean support(ClassBeanInfo<Bean> beanInfo) {
        Class<?> beanClass = beanInfo.getBeanClass();
        return KafkaMessageConsumer.class.isAssignableFrom(beanClass);
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

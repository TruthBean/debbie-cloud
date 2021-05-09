/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.concurrent.Async;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.debbie.event.EventListenerBeanRegister;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-27 16:49
 */
public class KafkaModuleStarter implements DebbieModuleStarter {
    private static final String ENABLE_KEY = "debbie.kafka.enable";

    private volatile KafkaConsumerFactory<?, ?> consumerFactory;

    @Override
    public boolean enable(EnvironmentContent envContent) {
        return envContent.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInitialization beanInitialization) {
        DebbieClassBeanInfo<KafkaConfiguration> debbieBeanInfo = new DebbieClassBeanInfo<>(KafkaConfiguration.class);
        debbieBeanInfo.addBeanName("kafkaConfiguration");
        beanInitialization.initSingletonBean(debbieBeanInfo);
    }

    @Override
    public void configure(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        GlobalBeanFactory factory = applicationContext.getGlobalBeanFactory();
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();

        BeanFactory<ConsumerRecordsEventListener<?, ?>> beanFactory = new ConsumerRecordsEventListenerFactory();
        beanFactory.setGlobalBeanFactory(factory);
        DebbieBeanInfo<ConsumerRecordsEventListener<?, ?>> beanInfo = new DebbieBeanInfo(ConsumerRecordsEventListener.class);
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.setBeanFactory(beanFactory);
        beanInfo.addBeanName("consumerRecordsEventListener");
        beanInitialization.initSingletonBean(beanInfo);
        applicationContext.refreshBeans();
    }

    @Override
    public void starter(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        GlobalBeanFactory factory = applicationContext.getGlobalBeanFactory();
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();

        KafkaConfiguration kafkaConfiguration = factory.factory(KafkaConfiguration.class);
        DebbieEventPublisher eventPublisher = factory.factory(DebbieEventPublisher.class);
        // event
        BeanInfoFactory infoFactory = applicationContext.getBeanInfoFactory();
        EventListenerBeanRegister eventListenerBeanRegister = new EventListenerBeanRegister(applicationContext);
        BeanInfo<ConsumerRecordsEventListener> beanInfo = infoFactory.getBeanInfo("consumerRecordsEventListener", ConsumerRecordsEventListener.class, true);
        eventListenerBeanRegister.register(ConsumerRecordsEvent.class, beanInfo.getBeanFactory());
        // kafka consumer
        KafkaConsumerFactory<?, ?> consumerFactory = new KafkaConsumerFactory<>(kafkaConfiguration, eventPublisher);
        DebbieBeanInfo<KafkaConsumerFactory> info = new DebbieBeanInfo<>(KafkaConsumerFactory.class);
        info.setBean(consumerFactory);
        info.setBeanType(BeanType.SINGLETON);
        beanInitialization.initSingletonBean(info);
        applicationContext.refreshBeans();
        this.consumerFactory = consumerFactory;
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        if (this.consumerFactory != null) {
            BeanInitialization initialization = applicationContext.getBeanInitialization();
            Set<DebbieClassBeanInfo<?>> methodBean = initialization.getAnnotatedMethodBean(KafkaConsumerListener.class);
            List<KafkaConsumerListenerMethodInfo> list = new ArrayList<>();
            Set<String> topicSet = new HashSet<>();
            GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
            final ThreadPooledExecutor executor = globalBeanFactory.factory("threadPooledExecutor");
            for (DebbieClassBeanInfo<?> info : methodBean) {
                var bean = globalBeanFactory.factory(info);
                Set<Method> methods = info.getAnnotationMethod(KafkaConsumerListener.class);
                for (Method method : methods) {
                    KafkaConsumerListener kafkaConsumerListener = method.getAnnotation(KafkaConsumerListener.class);
                    Async async = method.getAnnotation(Async.class);
                    String[] topics = kafkaConsumerListener.topics();
                    topicSet.addAll(Arrays.asList(topics));
                    KafkaConsumerListenerMethodInfo methodInfo = new KafkaConsumerListenerMethodInfo();
                    methodInfo.setTopics(topics);
                    if (async != null) {
                        methodInfo.setAsync(true);
                    } else {
                        methodInfo.setAsync(kafkaConsumerListener.async());
                    }
                    methodInfo.setBean(bean);
                    int count = method.getParameterCount();
                    if (count != 1) {
                        throw new KafkaConsumerMethodParameterIllegalException("parameter count > 1");
                    }
                    var param = method.getParameters()[0];
                    methodInfo.setParameterType(param.getType());
                    methodInfo.setMethod(method);
                    list.add(methodInfo);
                }
            }
            this.consumerFactory.consumer(executor, list, topicSet);
        }
    }

    @Override
    public int getOrder() {
        return 130;
    }

    @Override
    public void release(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        if (this.consumerFactory != null) {
            this.consumerFactory.close();
        }
    }
}

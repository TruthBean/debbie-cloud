/*
  Copyright (c) 2023 TruthBean(Rogar·Q)
  Debbie is licensed under Mulan PSL v2.
  You can use this software according to the terms and conditions of the Mulan PSL v2.
  You may obtain a copy of Mulan PSL v2 at:
  http://license.coscl.org.cn/MulanPSL2
  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
  See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.event.DebbieEventPublisher;

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
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment) && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        DebbieReflectionBeanFactory<KafkaConfiguration> debbieBeanInfo = new DebbieReflectionBeanFactory<>(KafkaConfiguration.class);
        debbieBeanInfo.addBeanName("kafkaConfiguration");
        beanInfoManager.registerBeanInfo(debbieBeanInfo);

        var beanFactory = new ConsumerRecordsEventListenerFactory<>("consumerRecordsEventListener");
        beanInfoManager.registerBeanInfo(beanFactory);

        beanInfoManager.registerBeanRegister(new KafkaMessageConsumerBeanRegister());
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        GlobalBeanFactory factory = applicationContext.getGlobalBeanFactory();

        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        KafkaConfiguration kafkaConfiguration = factory.factory(KafkaConfiguration.class);
        DebbieEventPublisher eventPublisher = factory.factory(DebbieEventPublisher.class);
        // kafka consumer
        Environment environment = applicationContext.getDefaultEnvironment();
        Map<String, String> matchedKey = environment.getMatchedKey("debbie.kafka.x");
        Properties properties = new Properties();
        matchedKey.forEach((key, value) -> {
            var k = key.substring("debbie.kafka.x".length());
            properties.put(k, value);
        });
        KafkaConsumerFactory<?, ?> consumerFactory = new KafkaConsumerFactory<>(kafkaConfiguration, properties, eventPublisher);
        var beanFactory = new SimpleBeanFactory<>(consumerFactory, KafkaConsumerFactory.class);
        beanInfoManager.registerBeanInfo(beanFactory);
        this.consumerFactory = consumerFactory;
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        if (this.consumerFactory != null) {
            new KafkaMessageConsumerResolver(applicationContext).resolve(consumerFactory);
        }
    }

    @Override
    public int getOrder() {
        return 130;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        if (this.consumerFactory != null) {
            this.consumerFactory.close();
        }
    }
}

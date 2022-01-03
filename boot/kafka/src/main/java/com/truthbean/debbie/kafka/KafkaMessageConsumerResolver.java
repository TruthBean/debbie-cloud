package com.truthbean.debbie.kafka;

import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.concurrent.Async;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/25 19:49.
 */
public class KafkaMessageConsumerResolver {

    private final ApplicationContext applicationContext;

    public KafkaMessageConsumerResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void resolve(KafkaConsumerFactory<?, ?> consumerFactory) {
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        Set<BeanInfo<?>> methodBean = beanInfoManager.getAnnotatedMethodsBean(KafkaConsumerListener.class);
        List<KaKafkaMessageConsumerInfo> list = new ArrayList<>();
        Set<String> topicSet = new HashSet<>();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        final ThreadPooledExecutor executor = globalBeanFactory.factory("threadPooledExecutor");
        for (BeanInfo<?> info : methodBean) {
            if (info instanceof DebbieReflectionBeanFactory reflectionBeanFactory) {
                Set<Method> methods = reflectionBeanFactory.getAnnotationMethod(KafkaConsumerListener.class);
                for (Method method : methods) {
                    KafkaConsumerListener kafkaConsumerListener = method.getAnnotation(KafkaConsumerListener.class);
                    Async async = method.getAnnotation(Async.class);
                    String[] topics = kafkaConsumerListener.topics();
                    topicSet.addAll(Arrays.asList(topics));
                    KaKafkaMessageConsumerInfo methodInfo = new KaKafkaMessageConsumerInfo();
                    methodInfo.setTopics(topics);
                    if (async != null) {
                        methodInfo.setAsync(true);
                    } else {
                        methodInfo.setAsync(kafkaConsumerListener.async());
                    }
                    methodInfo.setBean(reflectionBeanFactory.factoryBean(applicationContext));
                    int count = method.getParameterCount();
                    if (count != 1) {
                        throw new KafkaConsumerMethodParameterIllegalException("parameter count > 1");
                    }
                    var param = method.getParameters()[0];
                    methodInfo.setParameterType(param.getType());
                    methodInfo.setConsumer(o -> ReflectionHelper.invokeMethod(methodInfo.getBean(), method, param));
                    list.add(methodInfo);
                }
            }
        }

        Set<KafkaMessageConsumer> beanList = globalBeanFactory.getBeanList(KafkaMessageConsumer.class);
        if (beanList != null && !beanList.isEmpty()) {
            for (KafkaMessageConsumer consumer : beanList) {
                KaKafkaMessageConsumerInfo info = new KaKafkaMessageConsumerInfo();
                info.setConsumer(consumer.consumer());
                info.setAsync(consumer.async());
                info.setParameterType(consumer.parameterType());
                info.setTopics(consumer.topics());
                list.add(info);
                Collections.addAll(topicSet, consumer.topics());
            }
        }

        if (!topicSet.isEmpty()) {
            KafkaConfiguration kafkaConfiguration = applicationContext.factory(KafkaConfiguration.class);
            consumerFactory.consumer(executor, list, topicSet, kafkaConfiguration.getConsumer().getPullTimeout());
        }
    }
}

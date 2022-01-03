package com.truthbean.debbie.kafka;

import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/25 19:24.
 */
public class KaKafkaMessageConsumerInfo<T> implements KafkaMessageConsumer<T> {
    private String[] topics;

    private Object bean;

    private boolean async;

    private Consumer<T> consumer;

    private Class<T> parameterType;

    public String[] getTopics() {
        return topics;
    }

    @Override
    public String[] topics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public boolean containTopic(String topic) {
        if (this.topics != null && this.topics.length > 0) {
            for (String s : this.topics) {
                if (s.equals(topic)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public Consumer<T> consumer() {
        return consumer;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public boolean async() {
        return async;
    }

    public Class<T> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<T> parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public Class<T> parameterType() {
        return parameterType;
    }

    public void invoke(T param) {
        consumer.accept(param);
    }
}

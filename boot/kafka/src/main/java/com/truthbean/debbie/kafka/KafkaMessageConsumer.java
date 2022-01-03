package com.truthbean.debbie.kafka;

import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/25 19:34.
 */
public interface KafkaMessageConsumer<T> {

    String[] topics();

    default boolean async() {
        return false;
    }

    Consumer<T> consumer();

    Class<T> parameterType();
}

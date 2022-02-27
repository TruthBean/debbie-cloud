/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka;

import com.truthbean.Logger;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-27 17:24
 */
public class KafkaConsumerFactory<K, V> implements Closeable {
    private final KafkaConsumer<K, V> consumer;
    private final DebbieEventPublisher eventPublisher;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("KafkaConsumer", true);
    private final ThreadPooledExecutor taskThreadPool = new ThreadPooledExecutor(1, 1, 10_0000, namedThreadFactory, 5000L);

    @SuppressWarnings("unchecked")
    public KafkaConsumerFactory(KafkaConfiguration configuration, Properties extraProperties, DebbieEventPublisher eventPublisher) {
        KafkaConfiguration.Consumer consumer = configuration.getConsumer();
        Class<?> keyDeserializerClass = consumer.getKeyDeserializer();
        Class<?> valueDeserializerClass = consumer.getValueDeserializer();
        Deserializer<K> keyDeserializer;
        if (keyDeserializerClass != null) {
            keyDeserializer = (Deserializer<K>) ReflectionHelper.newInstance(keyDeserializerClass);
        } else {
            keyDeserializer = (Deserializer<K>) new StringDeserializer();
        }
        Deserializer<V> valueDeserializer;
        if (valueDeserializerClass != null) {
            valueDeserializer = (Deserializer<V>) ReflectionHelper.newInstance(valueDeserializerClass);
        } else {
            valueDeserializer = (Deserializer<V>) new StringDeserializer();
        }
        Properties properties = consumer.toConsumerProperties();
        properties.putAll(extraProperties);
        this.consumer = new KafkaConsumer<>(properties, keyDeserializer, valueDeserializer);
        this.eventPublisher = eventPublisher;
    }

    public void consumer(final ThreadPooledExecutor executor,
                         final List<KaKafkaMessageConsumerInfo> list, final Set<String> topics, long timeout) {
        try {
            taskThreadPool.execute(() -> {
                consumer.subscribe(topics);
                running.set(true);
                // ConsumerRecordsEvent<K, V> event;
                while (running.get()) {
                    ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(timeout));
                    if (records != null && !records.isEmpty()) {
                        // event = new ConsumerRecordsEvent<>(this, records);
                        // eventPublisher.publishEvent(event);
                        for (ConsumerRecord<K, V> record : records) {
                            final String topic = record.topic();
                            final V value = record.value();
                            logger.trace(topic);
                            for (var info : list) {
                                if (info.containTopic(topic)) {
                                    if (info.isAsync()) {
                                        executor.execute(() -> {
                                            Class<?> type = info.getParameterType();
                                            if (type.isInstance(value)) {
                                                info.invoke(value);
                                            } else if (type == ConsumerRecord.class) {
                                                info.invoke(record);
                                            }
                                        });
                                    } else {
                                        Class<?> type = info.getParameterType();
                                        if (type.isInstance(value)) {
                                            info.invoke(value);
                                        } else if (type == ConsumerRecord.class) {
                                            info.invoke(record);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Override
    public void close() {
        running.set(false);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        taskThreadPool.destroy();
        consumer.close();
    }

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerFactory.class);
}

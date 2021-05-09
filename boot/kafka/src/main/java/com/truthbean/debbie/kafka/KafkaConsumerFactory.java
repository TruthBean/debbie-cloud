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

import com.truthbean.Logger;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;
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
    private final ThreadPooledExecutor taskThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);

    public KafkaConsumerFactory(KafkaConfiguration configuration, DebbieEventPublisher eventPublisher) {
        this.consumer = new KafkaConsumer<>(configuration.getConsumer().toConsumerProperties());
        this.eventPublisher = eventPublisher;
    }

    public void consumer(final ThreadPooledExecutor executor,
                         final List<KafkaConsumerListenerMethodInfo> list, final Set<String> topics) {
        try {
            taskThreadPool.execute(() -> {
                consumer.subscribe(topics);
                running.set(true);
                // ConsumerRecordsEvent<K, V> event;
                while (running.get()) {
                    ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(1000));
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
                                                info.invokeMethod(value);
                                            } else if (type == ConsumerRecord.class) {
                                                info.invokeMethod(record);
                                            }
                                        });
                                    } else {
                                        Class<?> type = info.getParameterType();
                                        if (type.isInstance(value)) {
                                            info.invokeMethod(value);
                                        } else if (type == ConsumerRecord.class) {
                                            info.invokeMethod(record);
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

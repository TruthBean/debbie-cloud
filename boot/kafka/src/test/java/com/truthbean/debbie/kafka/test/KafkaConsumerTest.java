/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka.test;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-21 14:47
 */
public class KafkaConsumerTest {

    public static void main(String[] args) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.12:19092");
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "hkvs");
        p.put(ConsumerConfig.CLIENT_ID_CONFIG, "hkvs-ehome-consumer");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        p.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(p)) {
            kafkaConsumer.subscribe(Collections.singletonList("hkvs-alarm"));

            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    logger.info(() -> "topic: " + record.topic());
                    logger.info(() -> "offset: " + record.offset());
                    logger.info(() -> "message: " + record.value());
                }
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerTest.class);
}

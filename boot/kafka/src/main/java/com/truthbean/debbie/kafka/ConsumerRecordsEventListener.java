/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kafka;

import com.truthbean.Logger;
import com.truthbean.debbie.event.DebbieEventListener;
import com.truthbean.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-27 17:27
 */
public class ConsumerRecordsEventListener<K, V> implements DebbieEventListener<ConsumerRecordsEvent<K, V>> {

    public ConsumerRecordsEventListener() {
    }

    @Override
    public void onEvent(ConsumerRecordsEvent<K, V> event) {
        logger.info("hello");
        ConsumerRecords<K, V> records = event.getConsumerRecords();
        for (ConsumerRecord<K, V> record : records) {
            logger.info(() -> "topic: " + record.topic());
            logger.info(() -> "offset: " + record.offset());
            logger.info(() -> "message: " + record.value());
        }
    }

    @Override
    public Class<ConsumerRecordsEvent> getEventType() {
        return ConsumerRecordsEvent.class;
    }

    private static final Logger logger = LoggerFactory.getLogger(ConsumerRecordsEventListener.class);
}

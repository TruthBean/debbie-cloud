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

import com.truthbean.debbie.event.AbstractDebbieEvent;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-27 17:27
 */
public class ConsumerRecordsEvent<K, V> extends AbstractDebbieEvent {
    private final ConsumerRecords<K, V> consumerRecords;
    /**
     * Create a new AbstractDebbieEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param consumerRecords kafka consumer records
     */
    public ConsumerRecordsEvent(Object source, ConsumerRecords<K, V> consumerRecords) {
        super(source);
        this.consumerRecords = consumerRecords;
    }

    public ConsumerRecords<K, V> getConsumerRecords() {
        return consumerRecords;
    }
}

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
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.kafka.KafkaConsumerListener;
import com.truthbean.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-28 17:53
 */
@BeanComponent
public class KafkaMessageListener {

    @KafkaConsumerListener(topics = "hkvs-alarm", async = true)
    public void onKafkaListener(ConsumerRecord<String, String> record) {
        logger.info(() -> "topic: " + record.topic());
        logger.info(() -> "offset: " + record.offset());
        logger.info(() -> "message: " + record.value());
    }

    private final Logger logger = LoggerFactory.getLogger(KafkaMessageListener.class);
}

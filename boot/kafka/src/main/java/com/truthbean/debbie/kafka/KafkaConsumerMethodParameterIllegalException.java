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

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-29 10:51
 */
public class KafkaConsumerMethodParameterIllegalException extends RuntimeException {
    public KafkaConsumerMethodParameterIllegalException() {
    }

    public KafkaConsumerMethodParameterIllegalException(String message) {
        super(message);
    }

    public KafkaConsumerMethodParameterIllegalException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaConsumerMethodParameterIllegalException(Throwable cause) {
        super(cause);
    }

    public KafkaConsumerMethodParameterIllegalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

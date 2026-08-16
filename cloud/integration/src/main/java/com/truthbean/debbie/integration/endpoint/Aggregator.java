/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration.endpoint;

import com.truthbean.debbie.integration.Message;

import java.util.List;
import java.util.function.Function;

/**
 * An aggregator that combines multiple messages into a single message.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Aggregator implements Function<List<Message<?>>, Message<?>> {

    private final Function<List<Object>, Object> aggregateFunction;

    public Aggregator(Function<List<Object>, Object> aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
    }

    @Override
    public Message<?> apply(List<Message<?>> messages) {
        var payloads = new java.util.ArrayList<Object>();
        var headers = new java.util.HashMap<String, Object>();
        for (var msg : messages) {
            payloads.add(msg.getPayload());
            headers.putAll(msg.getHeaders());
        }
        var result = aggregateFunction.apply(payloads);
        return Message.of(result, headers);
    }
}
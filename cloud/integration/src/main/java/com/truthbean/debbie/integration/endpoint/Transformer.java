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

import java.util.function.Function;

/**
 * A transformer that transforms the message payload.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Transformer implements java.util.function.UnaryOperator<Message<?>> {

    private final Function<Object, Object> transformFunction;

    public Transformer(Function<Object, Object> transformFunction) {
        this.transformFunction = transformFunction;
    }

    @Override
    public Message<?> apply(Message<?> message) {
        var transformed = transformFunction.apply(message.getPayload());
        return Message.of(transformed, message.getHeaders());
    }
}
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

import java.util.Collection;
import java.util.function.Function;

/**
 * A splitter that splits a single message into multiple messages.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Splitter implements Function<Message<?>, Collection<Message<?>>> {

    private final Function<Object, Collection<?>> splitFunction;

    public Splitter(Function<Object, Collection<?>> splitFunction) {
        this.splitFunction = splitFunction;
    }

    @Override
    public Collection<Message<?>> apply(Message<?> message) {
        var parts = splitFunction.apply(message.getPayload());
        var messages = new java.util.ArrayList<Message<?>>();
        for (var part : parts) {
            messages.add(Message.of(part, message.getHeaders()));
        }
        return messages;
    }
}
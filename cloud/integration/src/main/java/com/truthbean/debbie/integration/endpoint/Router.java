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
import com.truthbean.debbie.integration.MessageChannel;

import java.util.function.Function;

/**
 * A router that routes messages to different channels based on a routing function.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class Router implements java.util.function.Function<Message<?>, MessageChannel> {

    private final Function<Object, String> routingFunction;
    private final java.util.Map<String, MessageChannel> channelMap;

    public Router(Function<Object, String> routingFunction, java.util.Map<String, MessageChannel> channelMap) {
        this.routingFunction = routingFunction;
        this.channelMap = channelMap;
    }

    @Override
    public MessageChannel apply(Message<?> message) {
        var key = routingFunction.apply(message.getPayload());
        return channelMap.get(key);
    }

    public boolean route(Message<?> message) {
        var channel = apply(message);
        if (channel != null) {
            return channel.send(message);
        }
        return false;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration.channel;

import com.truthbean.debbie.integration.Message;
import com.truthbean.debbie.integration.MessageChannel;
import com.truthbean.debbie.integration.MessageHandler;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A direct channel that synchronously dispatches messages to a single subscriber.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class DirectChannel implements MessageChannel {

    private final String name;
    private final CopyOnWriteArrayList<MessageHandler> subscribers = new CopyOnWriteArrayList<>();

    public DirectChannel(String name) {
        this.name = name;
    }

    public DirectChannel() {
        this.name = "direct-" + System.identityHashCode(this);
    }

    public boolean subscribe(MessageHandler handler) {
        return subscribers.add(handler);
    }

    public boolean unsubscribe(MessageHandler handler) {
        return subscribers.remove(handler);
    }

    @Override
    public boolean send(Message<?> message) {
        if (subscribers.isEmpty()) {
            return false;
        }
        for (var handler : subscribers) {
            handler.handleMessage(message);
        }
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
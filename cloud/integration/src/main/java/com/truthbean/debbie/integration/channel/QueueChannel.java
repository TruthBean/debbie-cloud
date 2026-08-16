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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A queue channel that buffers messages in a blocking queue.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class QueueChannel implements MessageChannel {

    private final String name;
    private final LinkedBlockingQueue<Message<?>> queue;

    public QueueChannel(String name) {
        this(name, Integer.MAX_VALUE);
    }

    public QueueChannel(String name, int capacity) {
        this.name = name;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public QueueChannel() {
        this("queue-" + System.identityHashCode(new Object()));
    }

    @Override
    public boolean send(Message<?> message) {
        return queue.offer(message);
    }

    public Message<?> receive() {
        return queue.poll();
    }

    public Message<?> receive(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    public Message<?> receiveBlocking() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
    }

    @Override
    public String getName() {
        return name;
    }
}
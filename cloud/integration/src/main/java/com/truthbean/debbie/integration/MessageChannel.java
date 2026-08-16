/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration;

/**
 * A channel for sending and receiving messages.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface MessageChannel {

    /**
     * Send a message to this channel.
     *
     * @param message the message to send
     * @return true if the message was sent successfully
     */
    boolean send(Message<?> message);

    /**
     * Get the name of this channel.
     *
     * @return the channel name
     */
    String getName();
}
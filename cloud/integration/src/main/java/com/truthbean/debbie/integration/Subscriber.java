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
 * A subscriber that receives messages from a channel.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface Subscriber {

    /**
     * Subscribe to a channel with a handler.
     *
     * @param channel the channel to subscribe to
     * @param handler the message handler
     * @return true if subscription was successful
     */
    boolean subscribe(MessageChannel channel, MessageHandler handler);
}
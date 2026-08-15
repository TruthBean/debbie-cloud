/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus.broker;

import com.truthbean.debbie.bus.event.BusEvent;

/**
 * abstraction of a message broker that debbie-bus can use to broadcast events.
 * <p>
 * concrete implementations may wrap kafka, rabbitmq, rocketmq, redis pub/sub, etc.
 * the bus module itself does not depend on any concrete broker library.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface BusMessageBroker extends AutoCloseable {

    String name();

    void start();

    boolean isRunning();

    void publish(BusEvent event);

    void subscribe(BusMessageListener listener);

    void unsubscribe(BusMessageListener listener);

    @Override
    void close();
}
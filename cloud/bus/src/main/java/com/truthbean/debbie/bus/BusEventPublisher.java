/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bus.broker.BusMessageBroker;
import com.truthbean.debbie.bus.event.AckBusEvent;
import com.truthbean.debbie.bus.event.BusEvent;
import com.truthbean.debbie.bus.event.RefreshBusEvent;
import com.truthbean.debbie.bus.identity.BusDestination;
import com.truthbean.debbie.bus.identity.BusIdentity;
import com.truthbean.debbie.event.DebbieEventPublisher;

import java.util.Map;

/**
 * publisher of {@link BusEvent}s over the debbie-bus.
 * <p>
 * it publishes events to the underlying {@link BusMessageBroker} so that they get
 * broadcast to all matching service instances. locally it also fires the event
 * through the {@link DebbieEventPublisher} so that in-process listeners react too.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class BusEventPublisher {

    private final BusIdentity self;
    private final BusMessageBroker broker;
    private final DebbieEventPublisher eventPublisher;
    private final boolean ack;

    public BusEventPublisher(BusIdentity self, BusMessageBroker broker,
                             DebbieEventPublisher eventPublisher, boolean ack) {
        this.self = self;
        this.broker = broker;
        this.eventPublisher = eventPublisher;
        this.ack = ack;
    }

    public BusIdentity getSelf() {
        return self;
    }

    public void publish(BusEvent event) {
        if (broker != null && broker.isRunning()) {
            broker.publish(event);
        }
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
        LOGGER.debug(() -> "published bus event " + event);
    }

    public void refresh() {
        refresh(BusDestination.all(), Map.of());
    }

    public void refresh(BusDestination destination) {
        refresh(destination, Map.of());
    }

    public void refresh(Map<String, String> keys) {
        refresh(BusDestination.all(), keys);
    }

    public void refresh(BusDestination destination, Map<String, String> keys) {
        publish(new RefreshBusEvent(this, self, destination, keys));
    }

    public void ack(BusEvent original) {
        if (!ack) {
            return;
        }
        publish(new AckBusEvent(this, self, BusDestination.all(),
                original.getEventId(), original.getClass()));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BusEventPublisher.class);
}
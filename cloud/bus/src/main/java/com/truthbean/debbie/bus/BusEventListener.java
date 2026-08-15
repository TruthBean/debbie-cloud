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
import com.truthbean.debbie.bus.broker.BusMessageListener;
import com.truthbean.debbie.bus.event.AckBusEvent;
import com.truthbean.debbie.bus.event.BusEvent;
import com.truthbean.debbie.bus.event.RefreshBusEvent;
import com.truthbean.debbie.bus.identity.BusIdentity;
import com.truthbean.debbie.bus.refresh.RefreshHandler;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.debbie.event.DebbieEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * core listener of the debbie-bus.
 * <p>
 * it subscribes to the underlying {@link BusMessageBroker}, filters events whose
 * destination matches the local {@link BusIdentity}, dispatches them to in-process
 * {@link DebbieEventPublisher} and to registered {@link RefreshHandler}s.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class BusEventListener implements BusMessageListener {

    private final BusIdentity self;
    private final DebbieEventPublisher eventPublisher;
    private final BusEventPublisher busEventPublisher;
    private final boolean ack;
    private final List<RefreshHandler> refreshHandlers = new CopyOnWriteArrayList<>();

    public BusEventListener(BusIdentity self, DebbieEventPublisher eventPublisher,
                            BusEventPublisher busEventPublisher, boolean ack) {
        this.self = self;
        this.eventPublisher = eventPublisher;
        this.busEventPublisher = busEventPublisher;
        this.ack = ack;
    }

    public void addRefreshHandler(RefreshHandler handler) {
        if (handler != null) {
            refreshHandlers.add(handler);
        }
    }

    public void removeRefreshHandler(RefreshHandler handler) {
        if (handler != null) {
            refreshHandlers.remove(handler);
        }
    }

    @Override
    public void onMessage(BusEvent event) {
        if (event == null) {
            return;
        }
        if (!event.isForSelf(self)) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("skip bus event not for self: " + event);
            }
            return;
        }
        LOGGER.debug(() -> "received bus event " + event);

        if (event instanceof RefreshBusEvent refresh) {
            for (var handler : refreshHandlers) {
                try {
                    handler.onRefresh(refresh);
                } catch (Exception e) {
                    LOGGER.error("refresh handler failed for event " + event, e);
                }
            }
        }

        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }

        if (ack && !(event instanceof AckBusEvent) && busEventPublisher != null) {
            busEventPublisher.ack(event);
        }
    }

    public BusMessageListener asBrokerListener() {
        return this;
    }

    public <E extends BusEvent> DebbieEventListener<E> asDebbieEventListener() {
        return new DebbieEventListener<>() {
            @Override
            public void onEvent(E e) {
                BusEventListener.this.onMessage(e);
            }

            @Override
            public String getName() {
                return "debbie-bus-listener";
            }
        };
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BusEventListener.class);
}
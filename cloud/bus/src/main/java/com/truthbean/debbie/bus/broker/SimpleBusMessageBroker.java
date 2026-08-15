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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.debbie.bus.BusConfiguration;
import com.truthbean.debbie.bus.event.BusEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * default in-memory implementation of {@link BusMessageBroker}.
 * <p>
 * it simply dispatches published events to all local subscribers in a separate thread.
 * mainly used for testing and single-process scenarios. for real cross-process
 * broadcasting, plug in a kafka/rabbitmq/redis backed implementation via spi.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SimpleBusMessageBroker implements BusMessageBroker {

    private final BusConfiguration configuration;
    private final List<BusMessageListener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final java.util.concurrent.ExecutorService executor;

    public SimpleBusMessageBroker(BusConfiguration configuration) {
        this.configuration = configuration;
        var factory = new NamedThreadFactory("debbie-bus-simple", true);
        this.executor = java.util.concurrent.Executors.newSingleThreadExecutor(factory);
    }

    @Override
    public String name() {
        return "simple";
    }

    @Override
    public void start() {
        running.set(true);
        LOGGER.info("debbie-bus simple broker started");
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void publish(BusEvent event) {
        if (!running.get()) {
            LOGGER.warn("debbie-bus simple broker is not running, event " + event + " is dropped");
            return;
        }
        if (configuration.isTrace()) {
            LOGGER.trace("publishing bus event: " + event);
        }
        executor.execute(() -> {
            for (var listener : listeners) {
                try {
                    listener.onMessage(event);
                } catch (Exception e) {
                    LOGGER.error("bus listener failed to handle event " + event, e);
                }
            }
        });
    }

    @Override
    public void subscribe(BusMessageListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void unsubscribe(BusMessageListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void close() {
        running.set(false);
        executor.shutdown();
        listeners.clear();
        LOGGER.info("debbie-bus simple broker closed");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBusMessageBroker.class);
}
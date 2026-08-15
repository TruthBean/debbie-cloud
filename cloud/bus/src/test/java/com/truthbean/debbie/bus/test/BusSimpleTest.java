/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus.test;

import com.truthbean.debbie.bus.BusConfiguration;
import com.truthbean.debbie.bus.BusEventPublisher;
import com.truthbean.debbie.bus.BusEventListener;
import com.truthbean.debbie.bus.broker.BusMessageBroker;
import com.truthbean.debbie.bus.broker.BusMessageBrokerFactory;
import com.truthbean.debbie.bus.broker.SimpleBusMessageBrokerFactory;
import com.truthbean.debbie.bus.event.RefreshBusEvent;
import com.truthbean.debbie.bus.identity.BusDestination;
import com.truthbean.debbie.bus.identity.BusIdentity;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class BusSimpleTest {

    @Test
    public void spiShouldLoadSimpleBrokerFactory() {
        var loader = ServiceLoader.load(BusMessageBrokerFactory.class);
        boolean found = false;
        for (var f : loader) {
            if ("simple".equals(f.name())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "SimpleBusMessageBrokerFactory should be loadable via spi");
    }

    @Test
    public void simpleBrokerShouldDispatchRefreshEvent() throws InterruptedException {
        var config = new BusConfiguration();
        config.setBroker("simple");
        config.setId("test-service");
        config.setTrace(true);

        BusMessageBrokerFactory factory = new SimpleBusMessageBrokerFactory();
        assertTrue(factory.support(config));

        BusMessageBroker broker = factory.create(config);
        broker.start();
        assertTrue(broker.isRunning());

        var latch = new CountDownLatch(1);
        var received = new AtomicReference<RefreshBusEvent>();
        broker.subscribe(event -> {
            if (event instanceof RefreshBusEvent refresh) {
                received.set(refresh);
                latch.countDown();
            }
        });

        var self = new BusIdentity("test-service");
        var dest = BusDestination.all();
        broker.publish(new RefreshBusEvent(this, self, dest, Map.of("k", "v")));

        assertTrue(latch.await(2, TimeUnit.SECONDS), "event should be dispatched");
        assertNotNull(received.get());
        assertEquals("v", received.get().getKeys().get("k"));

        broker.close();
        assertFalse(broker.isRunning());
    }

    @Test
    public void destinationShouldMatch() {
        var all = BusDestination.all();
        assertTrue(all.matches("any-service"));

        var specific = BusDestination.of("order-service,product-service");
        assertTrue(specific.matches("order-service"));
        assertTrue(specific.matches("product-service"));
        assertFalse(specific.matches("user-service"));

        var withProfile = BusDestination.of("order-service:dev");
        assertTrue(withProfile.matches("order-service"));
        assertFalse(withProfile.matches("product-service"));
    }

    @Test
    public void eventShouldNotBeForSelfWhenOriginEqualsSelf() {
        var self = new BusIdentity("svc-a");
        var event = new RefreshBusEvent(this, self, BusDestination.all(), Map.of());
        assertFalse(event.isForSelf(self));

        var other = new BusIdentity("svc-b");
        assertTrue(event.isForSelf(other));
    }
}
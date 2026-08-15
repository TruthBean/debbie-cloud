/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus.event;

import com.truthbean.debbie.bus.identity.BusDestination;
import com.truthbean.debbie.bus.identity.BusIdentity;
import com.truthbean.debbie.event.AbstractDebbieEvent;

import java.util.UUID;

/**
 * base event that can be broadcast over the debbie-bus.
 * <p>
 * it carries the origin service identity and the destination.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public abstract class BusEvent extends AbstractDebbieEvent {

    private final String eventId;
    private final BusIdentity originService;
    private final BusDestination destination;

    protected BusEvent(Object source, BusIdentity originService, BusDestination destination) {
        super(source);
        this.eventId = UUID.randomUUID().toString();
        this.originService = originService;
        this.destination = destination == null ? BusDestination.all() : destination;
    }

    protected BusEvent(Object source, BusIdentity originService, BusDestination destination, String eventId) {
        super(source);
        this.eventId = eventId == null ? UUID.randomUUID().toString() : eventId;
        this.originService = originService;
        this.destination = destination == null ? BusDestination.all() : destination;
    }

    public String getEventId() {
        return eventId;
    }

    public BusIdentity getOriginService() {
        return originService;
    }

    public BusDestination getDestination() {
        return destination;
    }

    public boolean isForSelf(BusIdentity self) {
        if (self == null) {
            return false;
        }
        if (originService != null && originService.equals(self)) {
            return false;
        }
        return destination.matches(self);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "eventId='" + eventId + '\'' +
                ", originService=" + originService +
                ", destination=" + destination +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
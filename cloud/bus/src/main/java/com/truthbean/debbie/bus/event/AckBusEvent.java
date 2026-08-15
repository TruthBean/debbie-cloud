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

/**
 * acknowledge event sent back by a receiver to confirm that a bus event has been handled.
 * <p>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class AckBusEvent extends BusEvent {

    private final String ackEventId;
    private final Class<? extends BusEvent> ackEventType;

    public AckBusEvent(Object source, BusIdentity originService, BusDestination destination,
                       String ackEventId, Class<? extends BusEvent> ackEventType) {
        super(source, originService, destination);
        this.ackEventId = ackEventId;
        this.ackEventType = ackEventType;
    }

    public AckBusEvent(Object source, BusIdentity originService, BusDestination destination,
                       String eventId, String ackEventId, Class<? extends BusEvent> ackEventType) {
        super(source, originService, destination, eventId);
        this.ackEventId = ackEventId;
        this.ackEventType = ackEventType;
    }

    public String getAckEventId() {
        return ackEventId;
    }

    public Class<? extends BusEvent> getAckEventType() {
        return ackEventType;
    }
}
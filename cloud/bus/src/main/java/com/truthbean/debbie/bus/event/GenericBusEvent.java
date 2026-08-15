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

import java.util.Map;

/**
 * a generic bus event that carries an arbitrary payload map.
 * <p>
 * useful when users want to broadcast custom data without defining a new event subclass.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GenericBusEvent extends BusEvent {

    private final String type;
    private final Map<String, Object> payload;

    public GenericBusEvent(Object source, BusIdentity originService, BusDestination destination,
                           String type, Map<String, Object> payload) {
        super(source, originService, destination);
        this.type = type == null ? "generic" : type;
        this.payload = payload == null ? Map.of() : payload;
    }

    public GenericBusEvent(Object source, BusIdentity originService, BusDestination destination,
                           String eventId, String type, Map<String, Object> payload) {
        super(source, originService, destination, eventId);
        this.type = type == null ? "generic" : type;
        this.payload = payload == null ? Map.of() : payload;
    }

    public String getEventType() {
        return type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
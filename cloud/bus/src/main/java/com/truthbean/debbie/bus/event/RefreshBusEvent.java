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

import java.util.Collections;
import java.util.Map;

/**
 * event to refresh configuration of target services.
 * <p>
 * the optional {@code keys} map carries the changed properties.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RefreshBusEvent extends BusEvent {

    private final Map<String, String> keys;

    public RefreshBusEvent(Object source, BusIdentity originService, BusDestination destination) {
        this(source, originService, destination, Collections.emptyMap());
    }

    public RefreshBusEvent(Object source, BusIdentity originService, BusDestination destination,
                           Map<String, String> keys) {
        super(source, originService, destination);
        this.keys = keys == null ? Collections.emptyMap() : keys;
    }

    public RefreshBusEvent(Object source, BusIdentity originService, BusDestination destination,
                           String eventId, Map<String, String> keys) {
        super(source, originService, destination, eventId);
        this.keys = keys == null ? Collections.emptyMap() : keys;
    }

    public Map<String, String> getKeys() {
        return keys;
    }
}
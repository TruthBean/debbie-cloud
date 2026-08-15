/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker.event;

import com.truthbean.debbie.circuitbreaker.CircuitBreakerState;

import java.util.EventObject;

/**
 * Base class for circuit-breaker events.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public abstract class CircuitBreakerEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private final String circuitBreakerName;
    private final long timestamp;

    protected CircuitBreakerEvent(Object source, String circuitBreakerName) {
        super(source);
        this.circuitBreakerName = circuitBreakerName;
        this.timestamp = System.currentTimeMillis();
    }

    public String getCircuitBreakerName() {
        return circuitBreakerName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public abstract CircuitBreakerState getState();
}
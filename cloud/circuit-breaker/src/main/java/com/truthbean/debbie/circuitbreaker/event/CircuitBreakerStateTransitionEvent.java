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

/**
 * Fired when a circuit breaker transitions from one state to another.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CircuitBreakerStateTransitionEvent extends CircuitBreakerEvent {

    private static final long serialVersionUID = 1L;

    private final CircuitBreakerState fromState;
    private final CircuitBreakerState toState;

    public CircuitBreakerStateTransitionEvent(Object source, String circuitBreakerName,
                                               CircuitBreakerState fromState, CircuitBreakerState toState) {
        super(source, circuitBreakerName);
        this.fromState = fromState;
        this.toState = toState;
    }

    public CircuitBreakerState getFromState() {
        return fromState;
    }

    @Override
    public CircuitBreakerState getState() {
        return toState;
    }

    @Override
    public String toString() {
        return "CircuitBreakerStateTransitionEvent{" +
                "circuitBreakerName='" + getCircuitBreakerName() + '\'' +
                ", fromState=" + fromState +
                ", toState=" + toState +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
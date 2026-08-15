/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker;

/**
 * Thrown when a call is rejected because the circuit breaker is {@link CircuitBreakerState#OPEN}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CircuitBreakerOpenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String circuitBreakerName;
    private final CircuitBreakerState state;

    public CircuitBreakerOpenException(String circuitBreakerName, CircuitBreakerState state) {
        super("circuit breaker [" + circuitBreakerName + "] is " + state + ", calls are rejected");
        this.circuitBreakerName = circuitBreakerName;
        this.state = state;
    }

    public String getCircuitBreakerName() {
        return circuitBreakerName;
    }

    public CircuitBreakerState getState() {
        return state;
    }
}
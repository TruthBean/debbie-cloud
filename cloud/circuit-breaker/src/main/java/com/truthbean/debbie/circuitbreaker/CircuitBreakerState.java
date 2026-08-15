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
 * The three states of a circuit breaker.
 *
 * <ul>
 *   <li>{@link #CLOSED} – calls flow through normally; failures are counted.</li>
 *   <li>{@link #OPEN} – all calls are rejected immediately until the wait duration elapses.</li>
 *   <li>{@link #HALF_OPEN} – a limited number of trial calls are permitted; if enough succeed
 *       the circuit closes again, otherwise it re-opens.</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public enum CircuitBreakerState {
    CLOSED,
    OPEN,
    HALF_OPEN
}
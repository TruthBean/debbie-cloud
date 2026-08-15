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

import com.truthbean.debbie.circuitbreaker.event.CircuitBreakerStateTransitionEvent;

/**
 * Listener for circuit-breaker state transitions and call outcomes.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface CircuitBreakerListener {

    /**
     * Called when the circuit breaker transitions from one state to another.
     *
     * @param event the transition event
     */
    void onStateTransition(CircuitBreakerStateTransitionEvent event);

    /**
     * Called after a call succeeds (no exception thrown).
     *
     * @param durationMillis how long the call took
     */
    default void onSuccess(long durationMillis) {
    }

    /**
     * Called after a call fails (threw an exception).
     *
     * @param throwable the exception
     * @param durationMillis how long the call took
     */
    default void onError(Throwable throwable, long durationMillis) {
    }

    /**
     * Called when a call is rejected because the circuit is open.
     */
    default void onCallRejected() {
    }
}
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

import com.truthbean.debbie.circuitbreaker.fallback.FallbackHandler;

import java.util.concurrent.Callable;

/**
 * The core circuit-breaker abstraction.
 * <p>
 * Wrap a call in {@link #execute(Callable)} and the circuit breaker will:
 * <ul>
 *   <li>count failures and successes in a sliding window</li>
 *   <li>open the circuit when the failure rate exceeds the threshold</li>
 *   <li>reject calls while open (after a wait duration, transition to half-open)</li>
 *   <li>allow trial calls in half-open, closing or re-opening based on results</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface CircuitBreaker {

    /**
     * Execute a callable, protected by this circuit breaker.
     *
     * @param task the task to execute
     * @param <T>  the result type
     * @return the result of the task
     * @throws Exception                    if the task throws
     * @throws CircuitBreakerOpenException  if the circuit is open and the call is rejected
     */
    <T> T execute(Callable<T> task) throws Exception;

    /**
     * Execute a callable with a fallback handler.
     * <p>
     * If the circuit is open or the task throws, the fallback is invoked instead.
     *
     * @param task     the task to execute
     * @param fallback the fallback handler
     * @param <T>      the result type
     * @return the result of the task, or the fallback value
     */
    <T> T execute(Callable<T> task, FallbackHandler<T> fallback);

    /**
     * Execute a runnable, protected by this circuit breaker.
     *
     * @param task the task to execute
     * @throws Exception                    if the task throws
     * @throws CircuitBreakerOpenException  if the circuit is open and the call is rejected
     */
    void execute(Runnable task) throws Exception;

    /**
     * Execute a runnable with a fallback.
     *
     * @param task     the task to execute
     * @param fallback the fallback runnable, invoked if the circuit is open or the task throws
     */
    void execute(Runnable task, Runnable fallback);

    /**
     * @return the name of this circuit breaker
     */
    String getName();

    /**
     * @return the current state
     */
    CircuitBreakerState getState();

    /**
     * @return the configuration
     */
    CircuitBreakerConfig getConfig();

    /**
     * Reset the circuit breaker to {@link CircuitBreakerState#CLOSED} and clear all counters.
     */
    void reset();

    /**
     * @return the current failure rate as a percentage (0–100), or 0 if not enough calls yet
     */
    float getFailureRate();

    /**
     * @return the number of buffered calls in the current sliding window
     */
    int getNumberOfBufferedCalls();

    /**
     * @return the number of failed calls in the current sliding window
     */
    int getNumberOfFailedCalls();

    /**
     * @return the number of successful calls in the current sliding window
     */
    int getNumberOfSuccessfulCalls();

    /**
     * Add a listener.
     *
     * @param listener the listener
     */
    void addListener(CircuitBreakerListener listener);

    /**
     * Remove a listener.
     *
     * @param listener the listener
     */
    void removeListener(CircuitBreakerListener listener);
}
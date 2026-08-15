/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker.fallback;

/**
 * Handler that produces a fallback value when a protected call fails or is rejected.
 *
 * @param <T> the result type
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@FunctionalInterface
public interface FallbackHandler<T> {

    /**
     * Produce a fallback value.
     *
     * @param throwable the exception that triggered the fallback, or {@code null}
     *                  if the call was rejected by an open circuit breaker
     * @return the fallback value
     */
    T handle(Throwable throwable);
}
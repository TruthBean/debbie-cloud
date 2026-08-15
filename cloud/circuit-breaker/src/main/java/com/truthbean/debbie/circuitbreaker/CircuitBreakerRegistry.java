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

import com.truthbean.debbie.circuitbreaker.ratelimit.RateLimiter;
import com.truthbean.debbie.circuitbreaker.ratelimit.RateLimiterConfig;
import com.truthbean.debbie.circuitbreaker.ratelimit.SimpleRateLimiter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe registry that manages named {@link CircuitBreaker} and
 * {@link RateLimiter} instances.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CircuitBreakerRegistry {

    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    private final CircuitBreakerConfig defaultCircuitBreakerConfig;
    private final RateLimiterConfig defaultRateLimiterConfig;

    public CircuitBreakerRegistry() {
        this(CircuitBreakerConfig.ofDefaults(), RateLimiterConfig.ofDefaults());
    }

    public CircuitBreakerRegistry(CircuitBreakerConfig defaultCircuitBreakerConfig,
                                   RateLimiterConfig defaultRateLimiterConfig) {
        this.defaultCircuitBreakerConfig = defaultCircuitBreakerConfig;
        this.defaultRateLimiterConfig = defaultRateLimiterConfig;
    }

    public CircuitBreaker getOrCreate(String name) {
        return circuitBreakers.computeIfAbsent(name,
                n -> new CircuitBreakerImpl(n, defaultCircuitBreakerConfig));
    }

    public CircuitBreaker getOrCreate(String name, CircuitBreakerConfig config) {
        return circuitBreakers.computeIfAbsent(name, n -> new CircuitBreakerImpl(n, config));
    }

    public CircuitBreaker get(String name) {
        return circuitBreakers.get(name);
    }

    public Collection<CircuitBreaker> getAll() {
        return circuitBreakers.values();
    }

    public void remove(String name) {
        circuitBreakers.remove(name);
    }

    public RateLimiter getOrCreateRateLimiter(String name) {
        return rateLimiters.computeIfAbsent(name,
                n -> new SimpleRateLimiter(n, defaultRateLimiterConfig));
    }

    public RateLimiter getOrCreateRateLimiter(String name, RateLimiterConfig config) {
        return rateLimiters.computeIfAbsent(name, n -> new SimpleRateLimiter(n, config));
    }

    public RateLimiter getRateLimiter(String name) {
        return rateLimiters.get(name);
    }

    public Collection<RateLimiter> getAllRateLimiters() {
        return rateLimiters.values();
    }

    public void removeRateLimiter(String name) {
        rateLimiters.remove(name);
    }

    public void resetAll() {
        for (var cb : circuitBreakers.values()) {
            cb.reset();
        }
    }

    public CircuitBreakerConfig getDefaultCircuitBreakerConfig() {
        return defaultCircuitBreakerConfig;
    }

    public RateLimiterConfig getDefaultRateLimiterConfig() {
        return defaultRateLimiterConfig;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.loadbalancer;

import com.truthbean.debbie.loadbalancer.strategy.LeastConnectionsLoadBalancer;
import com.truthbean.debbie.loadbalancer.strategy.RandomLoadBalancer;
import com.truthbean.debbie.loadbalancer.strategy.RoundRobinLoadBalancer;
import com.truthbean.debbie.loadbalancer.strategy.WeightedRoundRobinLoadBalancer;

/**
 * Factory for creating load balancer instances by strategy name.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class LoadBalancerFactory {

    public static final String ROUND_ROBIN = "round-robin";
    public static final String RANDOM = "random";
    public static final String WEIGHTED_ROUND_ROBIN = "weighted-round-robin";
    public static final String LEAST_CONNECTIONS = "least-connections";

    private LoadBalancerFactory() {}

    public static LoadBalancer create(String strategy) {
        if (strategy == null || strategy.isBlank()) {
            return new RoundRobinLoadBalancer();
        }
        return switch (strategy.trim().toLowerCase()) {
            case ROUND_ROBIN -> new RoundRobinLoadBalancer();
            case RANDOM -> new RandomLoadBalancer();
            case WEIGHTED_ROUND_ROBIN -> new WeightedRoundRobinLoadBalancer();
            case LEAST_CONNECTIONS -> new LeastConnectionsLoadBalancer();
            default -> new RoundRobinLoadBalancer();
        };
    }

    public static LoadBalancer createDefault() {
        return new RoundRobinLoadBalancer();
    }
}
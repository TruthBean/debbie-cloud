/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.loadbalancer.strategy;

import com.truthbean.debbie.loadbalancer.LoadBalancer;
import com.truthbean.debbie.loadbalancer.ServiceInstance;

import java.util.Comparator;
import java.util.List;

/**
 * Least-connections load balancing strategy.
 * <p>
 * Selects the healthy instance with the fewest active connections.
 * Ties are broken by selecting the first encountered instance.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LeastConnectionsLoadBalancer implements LoadBalancer {

    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) return null;
        return instances.stream()
                .filter(ServiceInstance::isHealthy)
                .min(Comparator.comparingInt(ServiceInstance::getActiveConnections))
                .orElse(null);
    }

    @Override
    public String name() {
        return "least-connections";
    }
}
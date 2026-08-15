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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-robin load balancing strategy.
 * <p>
 * Cycles through instances in order, one by one.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) return null;
        var healthy = filterHealthy(instances);
        if (healthy.isEmpty()) return null;
        int idx = Math.abs(counter.getAndIncrement() % healthy.size());
        return healthy.get(idx);
    }

    @Override
    public String name() {
        return "round-robin";
    }

    public void reset() {
        counter.set(0);
    }

    private List<ServiceInstance> filterHealthy(List<ServiceInstance> instances) {
        return instances.stream().filter(ServiceInstance::isHealthy).toList();
    }
}
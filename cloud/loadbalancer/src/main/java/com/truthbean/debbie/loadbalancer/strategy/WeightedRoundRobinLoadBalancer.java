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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Weighted round-robin load balancing strategy.
 * <p>
 * Each instance is selected proportionally to its weight.
 * An instance with weight 3 will be selected 3 times for every
 * 1 selection of an instance with weight 1.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class WeightedRoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) return null;
        var healthy = instances.stream().filter(ServiceInstance::isHealthy).toList();
        if (healthy.isEmpty()) return null;

        var weightedList = new ArrayList<ServiceInstance>();
        for (var instance : healthy) {
            int w = Math.max(1, instance.getWeight());
            for (int i = 0; i < w; i++) {
                weightedList.add(instance);
            }
        }

        int idx = Math.abs(counter.getAndIncrement() % weightedList.size());
        return weightedList.get(idx);
    }

    @Override
    public String name() {
        return "weighted-round-robin";
    }

    public void reset() {
        counter.set(0);
    }
}
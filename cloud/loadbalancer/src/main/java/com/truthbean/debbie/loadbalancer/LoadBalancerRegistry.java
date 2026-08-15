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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that manages load balancers per service name.
 * <p>
 * Each service gets its own load balancer instance, allowing
 * independent state (e.g. round-robin counters) per service.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LoadBalancerRegistry {

    private final Map<String, LoadBalancer> balancers = new ConcurrentHashMap<>();
    private final String defaultStrategy;

    public LoadBalancerRegistry() {
        this(LoadBalancerFactory.ROUND_ROBIN);
    }

    public LoadBalancerRegistry(String defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    public LoadBalancer getOrCreate(String serviceName) {
        return balancers.computeIfAbsent(serviceName,
                name -> LoadBalancerFactory.create(defaultStrategy));
    }

    public LoadBalancer getOrCreate(String serviceName, String strategy) {
        return balancers.computeIfAbsent(serviceName,
                name -> LoadBalancerFactory.create(strategy));
    }

    public LoadBalancer get(String serviceName) {
        return balancers.get(serviceName);
    }

    public void put(String serviceName, LoadBalancer balancer) {
        balancers.put(serviceName, balancer);
    }

    public void remove(String serviceName) {
        balancers.remove(serviceName);
    }

    public void clear() {
        balancers.clear();
    }

    public int size() {
        return balancers.size();
    }

    public List<String> getServiceNames() {
        return List.copyOf(balancers.keySet());
    }

    public String getDefaultStrategy() {
        return defaultStrategy;
    }
}
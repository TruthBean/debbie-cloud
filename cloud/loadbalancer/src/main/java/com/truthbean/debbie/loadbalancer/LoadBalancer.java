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

/**
 * Load balancer interface for selecting a service instance from a list.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface LoadBalancer {

    /**
     * Choose a service instance from the given list of healthy instances.
     *
     * @param instances available instances
     * @return the selected instance, or {@code null} if none available
     */
    ServiceInstance choose(List<ServiceInstance> instances);

    /**
     * Returns the name of this load balancing strategy.
     *
     * @return strategy name
     */
    String name();
}
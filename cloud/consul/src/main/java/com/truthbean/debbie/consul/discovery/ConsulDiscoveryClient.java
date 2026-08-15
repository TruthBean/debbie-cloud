/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul.discovery;

import com.truthbean.debbie.consul.ConsulClient;

import java.util.List;
import java.util.Map;

/**
 * Service discovery client that queries Consul for healthy service instances.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulDiscoveryClient {

    private final ConsulClient client;

    public ConsulDiscoveryClient(ConsulClient client) {
        this.client = client;
    }

    public List<ConsulServiceInstance> getInstances(String serviceName) {
        return client.getHealthyInstances(serviceName);
    }

    public List<ConsulServiceInstance> getAllInstances(String serviceName) {
        return client.getAllInstances(serviceName);
    }

    public Map<String, List<ConsulServiceInstance>> getAllServices() {
        return client.getAllServices();
    }

    public ConsulServiceInstance getOneInstance(String serviceName) {
        var instances = getInstances(serviceName);
        if (instances.isEmpty()) {
            return null;
        }
        return instances.get(0);
    }

    public ConsulServiceInstance getOneInstanceRandom(String serviceName) {
        var instances = getInstances(serviceName);
        if (instances.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * instances.size());
        return instances.get(index);
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd.discovery;

import com.truthbean.debbie.etcd.EtcdClient;
import com.truthbean.debbie.etcd.EtcdException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service discovery via etcd.
 * <p>
 * Services are registered under the key prefix
 * {@code /services/{serviceName}/{instanceId}} with the instance
 * value encoded as {@code host:port?key=value&...}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdServiceDiscovery {

    private static final String SERVICE_PREFIX = "/services/";

    private final EtcdClient client;

    public EtcdServiceDiscovery(EtcdClient client) {
        this.client = client;
    }

    public void register(EtcdServiceInstance instance) {
        var key = buildKey(instance.getServiceName(), instance.getInstanceId());
        client.put(key, instance.toValue());
    }

    public void registerWithLease(EtcdServiceInstance instance, long leaseId) {
        var key = buildKey(instance.getServiceName(), instance.getInstanceId());
        client.putWithLease(key, instance.toValue(), leaseId);
    }

    public boolean deregister(String serviceName, String instanceId) {
        return client.delete(buildKey(serviceName, instanceId));
    }

    public List<EtcdServiceInstance> getInstances(String serviceName) {
        var prefix = SERVICE_PREFIX + serviceName + "/";
        Map<String, String> entries;
        try {
            entries = client.getByPrefix(prefix);
        } catch (EtcdException e) {
            return List.of();
        }
        var result = new ArrayList<EtcdServiceInstance>();
        for (var entry : entries.entrySet()) {
            var instanceId = extractInstanceId(entry.getKey(), serviceName);
            var instance = EtcdServiceInstance.fromValue(serviceName, instanceId, entry.getValue());
            if (instance != null) result.add(instance);
        }
        return result;
    }

    public List<EtcdServiceInstance> getHealthyInstances(String serviceName) {
        var instances = getInstances(serviceName);
        var result = new ArrayList<EtcdServiceInstance>();
        for (var i : instances) {
            if (i.isHealthy()) result.add(i);
        }
        return result;
    }

    public List<String> getAllServiceNames() {
        Map<String, String> entries;
        try {
            entries = client.getByPrefix(SERVICE_PREFIX);
        } catch (EtcdException e) {
            return List.of();
        }
        var result = new ArrayList<String>();
        for (var key : entries.keySet()) {
            var name = extractServiceName(key);
            if (name != null && !result.contains(name)) {
                result.add(name);
            }
        }
        return result;
    }

    private String buildKey(String serviceName, String instanceId) {
        return SERVICE_PREFIX + serviceName + "/" + instanceId;
    }

    private String extractInstanceId(String key, String serviceName) {
        var prefix = SERVICE_PREFIX + serviceName + "/";
        if (key.startsWith(prefix)) return key.substring(prefix.length());
        return key;
    }

    private String extractServiceName(String key) {
        if (!key.startsWith(SERVICE_PREFIX)) return null;
        var rest = key.substring(SERVICE_PREFIX.length());
        var slash = rest.indexOf('/');
        return slash >= 0 ? rest.substring(0, slash) : rest;
    }
}
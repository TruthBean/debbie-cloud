/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes.discovery;

import com.truthbean.debbie.kubernetes.KubernetesClient;
import com.truthbean.debbie.kubernetes.KubernetesException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service discovery client that queries the Kubernetes API for
 * service endpoints.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesServiceDiscovery {

    private final KubernetesClient client;

    public KubernetesServiceDiscovery(KubernetesClient client) {
        this.client = client;
    }

    public List<KubernetesServiceInstance> getInstances(String serviceName) {
        return getInstances(client.getProperties().getNamespace(), serviceName);
    }

    @SuppressWarnings("unchecked")
    public List<KubernetesServiceInstance> getInstances(String namespace, String serviceName) {
        var result = new ArrayList<KubernetesServiceInstance>();
        try {
            var endpoints = client.getEndpoints(namespace, serviceName);
            var subsets = endpoints.get("subsets");
            if (!(subsets instanceof List<?> subsetList)) {
                return result;
            }
            for (var subset : subsetList) {
                if (!(subset instanceof Map<?, ?> subsetMap)) continue;

                var addresses = subsetMap.get("addresses");
                var notReadyAddresses = subsetMap.get("notReadyAddresses");
                var ports = subsetMap.get("ports");

                if (addresses instanceof List<?> addrList) {
                    for (var addr : addrList) {
                        if (addr instanceof Map<?, ?> addrMap) {
                            result.add(toInstance(serviceName, namespace, addrMap, ports, true));
                        }
                    }
                }
                if (notReadyAddresses instanceof List<?> notReadyList) {
                    for (var addr : notReadyList) {
                        if (addr instanceof Map<?, ?> addrMap) {
                            result.add(toInstance(serviceName, namespace, addrMap, ports, false));
                        }
                    }
                }
            }
        } catch (KubernetesException e) {
            return result;
        }
        return result;
    }

    public List<String> getAllServiceNames() {
        return getAllServiceNames(client.getProperties().getNamespace());
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllServiceNames(String namespace) {
        var result = new ArrayList<String>();
        try {
            var services = client.listServices(namespace);
            for (var svc : services) {
                var metadata = (Map<String, Object>) svc.get("metadata");
                if (metadata != null) {
                    var name = metadata.get("name");
                    if (name != null) {
                        result.add(String.valueOf(name));
                    }
                }
            }
        } catch (KubernetesException e) {
            return result;
        }
        return result;
    }

    public KubernetesServiceInstance getOneInstance(String serviceName) {
        var instances = getInstances(serviceName);
        if (instances.isEmpty()) return null;
        return instances.get(0);
    }

    public KubernetesServiceInstance getOneInstanceRandom(String serviceName) {
        var instances = getInstances(serviceName);
        if (instances.isEmpty()) return null;
        return instances.get((int) (Math.random() * instances.size()));
    }

    @SuppressWarnings("unchecked")
    private KubernetesServiceInstance toInstance(String serviceName, String namespace,
                                                   Map<?, ?> addrMap, Object ports, boolean ready) {
        var instance = new KubernetesServiceInstance();
        instance.setServiceName(serviceName);
        instance.setNamespace(namespace);
        instance.setReady(ready);

        var ip = addrMap.get("ip");
        if (ip != null) instance.setHost(String.valueOf(ip));

        if (ports instanceof List<?> portList && !portList.isEmpty()) {
            var firstPort = (Map<String, Object>) portList.get(0);
            var portNum = firstPort.get("port");
            if (portNum instanceof Number n) instance.setPort(n.intValue());
            var proto = firstPort.get("protocol");
            if (proto != null) instance.setProtocol(String.valueOf(proto).toLowerCase());
        }

        return instance;
    }
}
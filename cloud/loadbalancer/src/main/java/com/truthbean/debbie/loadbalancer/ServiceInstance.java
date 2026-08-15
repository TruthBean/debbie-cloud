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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a service instance available for load balancing.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ServiceInstance {

    private String serviceName;
    private String instanceId;
    private String host;
    private int port;
    private String scheme = "http";
    private int weight = 1;
    private volatile int activeConnections = 0;
    private boolean healthy = true;
    private Map<String, String> metadata = new LinkedHashMap<>();

    public ServiceInstance() {
    }

    public ServiceInstance(String serviceName, String host, int port) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.instanceId = serviceName + "-" + host + ":" + port;
    }

    public ServiceInstance(String serviceName, String instanceId, String host, int port) {
        this.serviceName = serviceName;
        this.instanceId = instanceId;
        this.host = host;
        this.port = port;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getScheme() { return scheme; }
    public void setScheme(String scheme) { this.scheme = scheme; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getActiveConnections() { return activeConnections; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
    public void incrementConnections() { activeConnections++; }
    public void decrementConnections() { activeConnections = Math.max(0, activeConnections - 1); }

    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata != null ? metadata : new LinkedHashMap<>(); }

    public String getUrl() {
        return scheme + "://" + host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceInstance that)) return false;
        return port == that.port
                && (host != null ? host.equals(that.host) : that.host == null)
                && (serviceName != null ? serviceName.equals(that.serviceName) : that.serviceName == null);
    }

    @Override
    public int hashCode() {
        int result = serviceName != null ? serviceName.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "ServiceInstance{" + getUrl() + ", weight=" + weight
                + ", active=" + activeConnections + ", healthy=" + healthy + "}";
    }
}
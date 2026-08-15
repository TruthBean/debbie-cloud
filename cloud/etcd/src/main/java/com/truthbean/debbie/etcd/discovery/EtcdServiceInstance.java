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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a service instance registered in etcd.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdServiceInstance {

    private String serviceName;
    private String instanceId;
    private String host;
    private int port;
    private String scheme = "http";
    private boolean healthy = true;
    private Map<String, String> metadata = new LinkedHashMap<>();

    public EtcdServiceInstance() {
    }

    public EtcdServiceInstance(String serviceName, String host, int port) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.instanceId = serviceName + "-" + host + ":" + port;
    }

    public EtcdServiceInstance(String serviceName, String instanceId, String host, int port) {
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

    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata != null ? metadata : new LinkedHashMap<>(); }

    public String getUrl() {
        return scheme + "://" + host + ":" + port;
    }

    public String toValue() {
        var parts = new StringBuilder();
        parts.append(host).append(":").append(port);
        if (!"http".equals(scheme)) parts.append("?scheme=").append(scheme);
        if (!metadata.isEmpty()) {
            for (var e : metadata.entrySet()) {
                parts.append("&").append(e.getKey()).append("=").append(e.getValue());
            }
        }
        return parts.toString();
    }

    public static EtcdServiceInstance fromValue(String serviceName, String instanceId, String value) {
        if (value == null || value.isBlank()) return null;
        var instance = new EtcdServiceInstance();
        instance.setServiceName(serviceName);
        instance.setInstanceId(instanceId);

        var queryIdx = value.indexOf('?');
        var hostPort = queryIdx >= 0 ? value.substring(0, queryIdx) : value;
        var query = queryIdx >= 0 ? value.substring(queryIdx + 1) : "";

        var colonIdx = hostPort.indexOf(':');
        if (colonIdx >= 0) {
            instance.setHost(hostPort.substring(0, colonIdx));
            instance.setPort(Integer.parseInt(hostPort.substring(colonIdx + 1)));
        } else {
            instance.setHost(hostPort);
        }

        if (!query.isEmpty()) {
            for (var pair : query.split("&")) {
                var eqIdx = pair.indexOf('=');
                if (eqIdx >= 0) {
                    var k = pair.substring(0, eqIdx);
                    var v = pair.substring(eqIdx + 1);
                    if ("scheme".equals(k)) {
                        instance.setScheme(v);
                    } else {
                        instance.getMetadata().put(k, v);
                    }
                }
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        return "EtcdServiceInstance{" + getUrl() + ", healthy=" + healthy + "}";
    }
}
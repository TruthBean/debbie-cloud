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

import java.util.Map;

/**
 * Model representing a Kubernetes service instance (an endpoint address).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesServiceInstance {

    private String serviceName;
    private String namespace;
    private String host;
    private int port;
    private String protocol;
    private boolean ready;
    private Map<String, String> labels;

    public KubernetesServiceInstance() {
    }

    public KubernetesServiceInstance(String serviceName, String host, int port) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }

    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> labels) { this.labels = labels; }

    public String getUrl() {
        var scheme = protocol != null ? protocol : "http";
        return scheme + "://" + host + ":" + port;
    }

    @Override
    public String toString() {
        return "KubernetesServiceInstance{service='" + serviceName + "', namespace='" + namespace
                + "', host='" + host + "', port=" + port + ", ready=" + ready + '}';
    }
}
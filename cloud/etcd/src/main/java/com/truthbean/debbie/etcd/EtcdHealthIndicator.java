/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health indicator that checks etcd connectivity.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdHealthIndicator {

    private final EtcdClient client;

    public EtcdHealthIndicator(EtcdClient client) {
        this.client = client;
    }

    public HealthStatus check() {
        var status = new HealthStatus();
        if (client.isAvailable()) {
            status.setHealthy(true);
            status.addDetail("endpoint", client.getProperties().getBaseUrl());
            try {
                var clusterStatus = client.getClusterStatus();
                status.addDetail("cluster", clusterStatus);
            } catch (EtcdException e) {
                status.addDetail("clusterError", e.getMessage());
            }
        } else {
            status.setHealthy(false);
            status.addDetail("endpoint", client.getProperties().getBaseUrl());
            status.addDetail("error", "etcd not reachable");
        }
        return status;
    }

    public static final class HealthStatus {
        private boolean healthy;
        private final Map<String, Object> details = new LinkedHashMap<>();

        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }

        public Map<String, Object> getDetails() { return details; }
        public void addDetail(String key, Object value) { details.put(key, value); }

        @Override
        public String toString() {
            return "HealthStatus{healthy=" + healthy + ", details=" + details + "}";
        }
    }
}
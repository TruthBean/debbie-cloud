/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes.health;

import com.truthbean.debbie.kubernetes.KubernetesClient;
import com.truthbean.debbie.kubernetes.KubernetesProperties;

/**
 * Health indicator that checks whether the Kubernetes API server is reachable.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesHealthIndicator {

    private final KubernetesClient client;

    public KubernetesHealthIndicator(KubernetesClient client) {
        this.client = client;
    }

    public boolean isHealthy() {
        return client.isApiServerAvailable();
    }

    public HealthStatus check() {
        try {
            if (client.isApiServerAvailable()) {
                var version = client.getVersion();
                var gitVersion = version.get("gitVersion");
                return new HealthStatus(true, "kubernetes api server reachable, version="
                        + (gitVersion != null ? gitVersion : "unknown"));
            }
            return new HealthStatus(false, "kubernetes api server not reachable");
        } catch (Exception e) {
            return new HealthStatus(false, "kubernetes health check failed: " + e.getMessage());
        }
    }

    public KubernetesProperties getProperties() {
        return client.getProperties();
    }

    public static final class HealthStatus {
        private final boolean healthy;
        private final String message;

        HealthStatus(boolean healthy, String message) {
            this.healthy = healthy;
            this.message = message;
        }

        public boolean isHealthy() { return healthy; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return "HealthStatus{healthy=" + healthy + ", message='" + message + "'}";
        }
    }
}
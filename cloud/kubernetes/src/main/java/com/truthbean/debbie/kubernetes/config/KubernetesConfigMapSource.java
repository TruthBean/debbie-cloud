/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes.config;

import com.truthbean.debbie.kubernetes.KubernetesClient;
import com.truthbean.debbie.kubernetes.KubernetesException;

import java.util.Map;

/**
 * Config source backed by a Kubernetes ConfigMap.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesConfigMapSource {

    private final KubernetesClient client;
    private final String configMapName;

    public KubernetesConfigMapSource(KubernetesClient client, String configMapName) {
        this.client = client;
        this.configMapName = configMapName;
    }

    public Map<String, String> load() {
        try {
            return client.getConfigMap(configMapName);
        } catch (KubernetesException e) {
            return Map.of();
        }
    }

    public String getConfigMapName() {
        return configMapName;
    }
}
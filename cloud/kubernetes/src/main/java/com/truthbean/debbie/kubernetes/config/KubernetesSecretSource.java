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
 * Config source backed by a Kubernetes Secret (values are base64-decoded).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesSecretSource {

    private final KubernetesClient client;
    private final String secretName;

    public KubernetesSecretSource(KubernetesClient client, String secretName) {
        this.client = client;
        this.secretName = secretName;
    }

    public Map<String, String> load() {
        try {
            return client.getSecret(secretName);
        } catch (KubernetesException e) {
            return Map.of();
        }
    }

    public String getSecretName() {
        return secretName;
    }
}
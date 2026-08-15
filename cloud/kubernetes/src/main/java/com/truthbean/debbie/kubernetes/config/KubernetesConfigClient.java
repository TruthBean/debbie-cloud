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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client for reading distributed configuration from Kubernetes
 * ConfigMaps and Secrets.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesConfigClient {

    private final KubernetesClient client;

    public KubernetesConfigClient(KubernetesClient client) {
        this.client = client;
    }

    public Map<String, String> getConfigMap(String name) {
        try {
            return client.getConfigMap(name);
        } catch (KubernetesException e) {
            return Map.of();
        }
    }

    public Map<String, String> getConfigMap(String namespace, String name) {
        try {
            return client.getConfigMap(namespace, name);
        } catch (KubernetesException e) {
            return Map.of();
        }
    }

    public Map<String, String> getSecret(String name) {
        try {
            return client.getSecret(name);
        } catch (KubernetesException e) {
            return Map.of();
        }
    }

    public Map<String, String> getSecret(String namespace, String name) {
        try {
            return client.getSecret(namespace, name);
        } catch (KubernetesException e) {
            return Map.of();
        }
    }

    public Map<String, String> getConfigFromConfigMaps(String... names) {
        var result = new LinkedHashMap<String, String>();
        for (var name : names) {
            result.putAll(getConfigMap(name));
        }
        return result;
    }

    public Map<String, String> getConfigFromSecrets(String... names) {
        var result = new LinkedHashMap<String, String>();
        for (var name : names) {
            result.putAll(getSecret(name));
        }
        return result;
    }

    public Map<String, String> getConfig(String appName, String profile) {
        var result = new LinkedHashMap<String, String>();

        result.putAll(getConfigMap(appName));
        if (profile != null && !profile.isEmpty()) {
            result.putAll(getConfigMap(appName + "-" + profile));
        }

        result.putAll(getSecret(appName));
        if (profile != null && !profile.isEmpty()) {
            result.putAll(getSecret(appName + "-" + profile));
        }

        return result;
    }
}
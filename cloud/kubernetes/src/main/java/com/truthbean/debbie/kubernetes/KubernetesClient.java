/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes;

import com.truthbean.debbie.kubernetes.json.SimpleJson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * Low-level HTTP client for the Kubernetes API server.
 * <p>
 * Uses JDK built-in {@link HttpClient} with bearer token authentication.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesClient {

    private final KubernetesProperties properties;
    private final HttpClient httpClient;

    public KubernetesClient(KubernetesProperties properties) {
        this(properties, 5000, 10000);
    }

    public KubernetesClient(KubernetesProperties properties,
                             int connectTimeoutMillis, int readTimeoutMillis) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .build();
    }

    public KubernetesProperties getProperties() {
        return properties;
    }

    // ---- services ----

    public Map<String, Object> getService(String serviceName) {
        return getService(properties.getNamespace(), serviceName);
    }

    public Map<String, Object> getService(String namespace, String serviceName) {
        var body = get("/api/v1/namespaces/" + enc(namespace) + "/services/" + enc(serviceName));
        return SimpleJson.parseObject(body);
    }

    public java.util.List<Map<String, Object>> listServices() {
        return listServices(properties.getNamespace());
    }

    @SuppressWarnings("unchecked")
    public java.util.List<Map<String, Object>> listServices(String namespace) {
        var body = get("/api/v1/namespaces/" + enc(namespace) + "/services");
        var parsed = SimpleJson.parseObject(body);
        var items = parsed.get("items");
        if (items instanceof java.util.List<?> list) {
            var result = new java.util.ArrayList<Map<String, Object>>();
            for (var item : list) {
                if (item instanceof Map<?, ?> m) {
                    result.add((Map<String, Object>) m);
                }
            }
            return result;
        }
        return java.util.List.of();
    }

    // ---- endpoints ----

    public Map<String, Object> getEndpoints(String serviceName) {
        return getEndpoints(properties.getNamespace(), serviceName);
    }

    public Map<String, Object> getEndpoints(String namespace, String serviceName) {
        var body = get("/api/v1/namespaces/" + enc(namespace) + "/endpoints/" + enc(serviceName));
        return SimpleJson.parseObject(body);
    }

    // ---- configmaps ----

    public Map<String, String> getConfigMap(String configMapName) {
        return getConfigMap(properties.getNamespace(), configMapName);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getConfigMap(String namespace, String configMapName) {
        var body = get("/api/v1/namespaces/" + enc(namespace) + "/configmaps/" + enc(configMapName));
        var parsed = SimpleJson.parseObject(body);
        var data = parsed.get("data");
        if (data instanceof Map<?, ?> m) {
            var result = new java.util.LinkedHashMap<String, String>();
            for (var entry : m.entrySet()) {
                result.put(String.valueOf(entry.getKey()),
                        entry.getValue() != null ? String.valueOf(entry.getValue()) : null);
            }
            return result;
        }
        return Map.of();
    }

    // ---- secrets ----

    public Map<String, String> getSecret(String secretName) {
        return getSecret(properties.getNamespace(), secretName);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getSecret(String namespace, String secretName) {
        var body = get("/api/v1/namespaces/" + enc(namespace) + "/secrets/" + enc(secretName));
        var parsed = SimpleJson.parseObject(body);
        var data = parsed.get("data");
        if (data instanceof Map<?, ?> m) {
            var result = new java.util.LinkedHashMap<String, String>();
            for (var entry : m.entrySet()) {
                var value = entry.getValue();
                if (value != null) {
                    result.put(String.valueOf(entry.getKey()), decodeBase64(String.valueOf(value)));
                } else {
                    result.put(String.valueOf(entry.getKey()), null);
                }
            }
            return result;
        }
        return Map.of();
    }

    // ---- pods ----

    public Map<String, Object> getPod(String podName) {
        return getPod(properties.getNamespace(), podName);
    }

    public Map<String, Object> getPod(String namespace, String podName) {
        var body = get("/api/v1/namespaces/" + enc(namespace) + "/pods/" + enc(podName));
        return SimpleJson.parseObject(body);
    }

    // ---- health ----

    public boolean isApiServerAvailable() {
        try {
            var request = buildRequest("GET", "/version", null);
            var response = send(request);
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getVersion() {
        var body = get("/version");
        return SimpleJson.parseObject(body);
    }

    // ---- internal ----

    private String get(String path) {
        var request = buildRequest("GET", path, null);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new KubernetesException(response.statusCode(),
                    "GET " + path + " failed: " + response.body());
        }
        return response.body();
    }

    private HttpRequest buildRequest(String method, String path, String body) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(properties.getBaseUrl() + path))
                .timeout(Duration.ofMillis(10000));

        if (properties.hasToken()) {
            builder.header("Authorization", "Bearer " + properties.getToken());
        }
        builder.header("Accept", "application/json");

        var bodyBytes = body != null
                ? body.getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        switch (method) {
            case "GET" -> builder.GET();
            case "DELETE" -> builder.DELETE();
            case "POST" -> builder.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
            case "PUT" -> builder.header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
            default -> throw new IllegalArgumentException("unsupported method: " + method);
        }

        return builder.build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KubernetesException("kubernetes request interrupted", e);
        } catch (Exception e) {
            throw new KubernetesException("kubernetes request failed: " + e.getMessage(), e);
        }
    }

    private static String enc(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String decodeBase64(String encoded) {
        if (encoded == null || encoded.isEmpty()) return "";
        try {
            return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encoded;
        }
    }
}
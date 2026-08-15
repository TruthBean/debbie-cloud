/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka.client;

import com.truthbean.debbie.eureka.EurekaException;
import com.truthbean.debbie.eureka.EurekaJson;
import com.truthbean.debbie.eureka.model.ApplicationInfo;
import com.truthbean.debbie.eureka.model.InstanceInfo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP client for communicating with a Eureka server.
 * <p>
 * Supports service registration, heartbeat (renewal), deregistration,
 * status updates, and service discovery (fetching all applications or
 * a specific application's instances).
 * <p>
 * Uses JDK built-in {@link HttpClient}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EurekaClient {

    private final String serverUrl;
    private final String prefix;
    private final HttpClient httpClient;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;

    public EurekaClient(String serverUrl, String prefix) {
        this(serverUrl, prefix, 5000, 10000);
    }

    public EurekaClient(String serverUrl, String prefix,
                         int connectTimeoutMillis, int readTimeoutMillis) {
        this.serverUrl = normalizeUrl(serverUrl);
        this.prefix = prefix != null && !prefix.isBlank() ? prefix : "";
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .build();
    }

    public String getServerUrl() { return serverUrl; }
    public String getPrefix() { return prefix; }

    // ---- registration ----

    public boolean register(InstanceInfo instance) {
        var appName = instance.getAppName().toUpperCase();
        var body = EurekaJson.toJson(instance.toMap());
        var response = send("POST", "/apps/" + appName, body);
        return response.statusCode() == 200;
    }

    public boolean renew(String appName, String instanceId) {
        var response = send("PUT", "/apps/" + appName.toUpperCase() + "/" + instanceId, null);
        return response.statusCode() == 200;
    }

    public boolean cancel(String appName, String instanceId) {
        var response = send("DELETE", "/apps/" + appName.toUpperCase() + "/" + instanceId, null);
        return response.statusCode() == 200;
    }

    public boolean updateStatus(String appName, String instanceId, String status) {
        var path = "/apps/" + appName.toUpperCase() + "/" + instanceId + "/status?value=" + status;
        var response = send("PUT", path, null);
        return response.statusCode() == 200;
    }

    // ---- discovery ----

    public List<ApplicationInfo> getAllApplications() {
        var response = send("GET", "/apps", null);
        if (response.statusCode() != 200) {
            return List.of();
        }
        var parsed = EurekaJson.parseObject(response.body());
        var apps = parsed.get("applications");
        var result = new ArrayList<ApplicationInfo>();
        if (apps instanceof List<?> list) {
            for (var item : list) {
                if (item instanceof Map<?, ?> m) {
                    result.add(applicationFromMap(m));
                }
            }
        }
        return result;
    }

    public ApplicationInfo getApplication(String appName) {
        var response = send("GET", "/apps/" + appName.toUpperCase(), null);
        if (response.statusCode() != 200) {
            return null;
        }
        var parsed = EurekaJson.parseObject(response.body());
        return applicationFromMap(parsed);
    }

    @SuppressWarnings("unchecked")
    public List<InstanceInfo> getInstances(String appName) {
        var app = getApplication(appName);
        if (app == null) return List.of();
        return app.getInstances();
    }

    public List<InstanceInfo> getUpInstances(String appName) {
        var app = getApplication(appName);
        if (app == null) return List.of();
        return app.getUpInstances();
    }

    // ---- health ----

    public boolean isServerAvailable() {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + prefix + "/apps"))
                    .timeout(Duration.ofMillis(readTimeoutMillis))
                    .GET()
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // ---- internal ----

    private HttpResponse<String> send(String method, String path, String body) {
        try {
            var builder = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + prefix + path))
                    .timeout(Duration.ofMillis(readTimeoutMillis))
                    .header("Accept", "application/json");

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

            var request = builder.build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EurekaException("eureka request interrupted", e);
        } catch (Exception e) {
            throw new EurekaException("eureka request failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private ApplicationInfo applicationFromMap(Map<?, ?> m) {
        var name = m.get("name");
        var app = new ApplicationInfo(name != null ? String.valueOf(name) : "unknown");
        var instances = m.get("instances");
        if (instances instanceof List<?> list) {
            for (var item : list) {
                if (item instanceof Map<?, ?> im) {
                    app.addInstance(InstanceInfo.fromMap((Map<String, Object>) im));
                }
            }
        }
        return app;
    }

    private static String normalizeUrl(String url) {
        if (url == null || url.isBlank()) return "http://localhost:8761";
        url = url.trim();
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        return url;
    }

    @Override
    public String toString() {
        return "EurekaClient{serverUrl='" + serverUrl + "', prefix='" + prefix + "'}";
    }
}
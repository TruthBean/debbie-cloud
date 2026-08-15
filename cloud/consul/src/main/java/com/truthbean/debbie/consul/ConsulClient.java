/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul;

import com.truthbean.debbie.consul.discovery.ConsulServiceInstance;
import com.truthbean.debbie.consul.discovery.ConsulServiceRegistration;
import com.truthbean.debbie.consul.json.SimpleJson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Low-level HTTP client for the Consul agent API.
 * <p>
 * Uses JDK built-in {@link HttpClient} — no external HTTP dependency.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulClient {

    private final String baseUrl;
    private final String token;
    private final HttpClient httpClient;

    public ConsulClient(String host, int port, String scheme) {
        this(host, port, scheme, null, 5000, 10000);
    }

    public ConsulClient(String host, int port, String scheme, String token,
                         int connectTimeoutMillis, int readTimeoutMillis) {
        this.baseUrl = scheme + "://" + host + ":" + port;
        this.token = token;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .build();
    }

    // ---- agent: service register / deregister ----

    public void registerService(ConsulServiceRegistration registration) {
        var body = buildRegistrationJson(registration);
        var request = buildRequest("PUT", "/v1/agent/service/register", body);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to register service "
                    + registration.getName() + ": " + response.body());
        }
    }

    public void deregisterService(String serviceId) {
        var request = buildRequest("PUT", "/v1/agent/service/deregister/" + encode(serviceId), null);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to deregister service "
                    + serviceId + ": " + response.body());
        }
    }

    // ---- health: service instances ----

    public List<ConsulServiceInstance> getHealthyInstances(String serviceName) {
        var request = buildRequest("GET",
                "/v1/health/service/" + encode(serviceName) + "?passing=true", null);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to get healthy instances: "
                    + response.body());
        }
        return parseHealthServiceArray(response.body());
    }

    public List<ConsulServiceInstance> getAllInstances(String serviceName) {
        var request = buildRequest("GET",
                "/v1/health/service/" + encode(serviceName), null);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to get instances: "
                    + response.body());
        }
        return parseHealthServiceArray(response.body());
    }

    public Map<String, List<ConsulServiceInstance>> getAllServices() {
        var request = buildRequest("GET", "/v1/catalog/services", null);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to list services: "
                    + response.body());
        }
        var result = new LinkedHashMap<String, List<ConsulServiceInstance>>();
        var parsed = SimpleJson.parseObject(response.body());
        for (var key : parsed.keySet()) {
            if (!key.startsWith("consul-")) {
                result.put(key, getHealthyInstances(key));
            }
        }
        return result;
    }

    // ---- KV store ----

    public String getKeyValue(String key) {
        var request = buildRequest("GET", "/v1/kv/" + encode(key) + "?raw=true", null);
        var response = send(request);
        if (response.statusCode() == 404) {
            return null;
        }
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to get KV: " + response.body());
        }
        return response.body();
    }

    public Map<String, String> getKeyValuesRecursive(String prefix) {
        var request = buildRequest("GET",
                "/v1/kv/" + encode(prefix) + "?recurse=true", null);
        var response = send(request);
        if (response.statusCode() == 404) {
            return Map.of();
        }
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to get KV recursive: " + response.body());
        }
        var result = new LinkedHashMap<String, String>();
        var array = SimpleJson.parseArray(response.body());
        for (var item : array) {
            if (item instanceof Map<?, ?> m) {
                var key = String.valueOf(m.get("Key"));
                var value = m.get("Value");
                if (value != null) {
                    var decoded = decodeBase64(String.valueOf(value));
                    result.put(key, decoded);
                } else {
                    result.put(key, null);
                }
            }
        }
        return result;
    }

    public boolean setKeyValue(String key, String value) {
        var request = buildRequest("PUT", "/v1/kv/" + encode(key), value);
        var response = send(request);
        if (response.statusCode() == 200) {
            return "true".equalsIgnoreCase(response.body().trim());
        }
        throw new ConsulException(response.statusCode(), "failed to set KV: " + response.body());
    }

    public boolean deleteKeyValue(String key) {
        var request = buildRequest("DELETE", "/v1/kv/" + encode(key), null);
        var response = send(request);
        return response.statusCode() == 200;
    }

    public boolean deleteKeyValuesRecursive(String prefix) {
        var request = buildRequest("DELETE", "/v1/kv/" + encode(prefix) + "?recurse=true", null);
        var response = send(request);
        return response.statusCode() == 200;
    }

    // ---- status / agent ----

    public String getLeader() {
        var request = buildRequest("GET", "/v1/status/leader", null);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to get leader: " + response.body());
        }
        var parsed = SimpleJson.parse(response.body());
        return parsed != null ? String.valueOf(parsed) : null;
    }

    public Map<String, Object> getAgentInfo() {
        var request = buildRequest("GET", "/v1/agent/self", null);
        var response = send(request);
        if (response.statusCode() != 200) {
            throw new ConsulException(response.statusCode(), "failed to get agent info: " + response.body());
        }
        return SimpleJson.parseObject(response.body());
    }

    public boolean isAgentAvailable() {
        try {
            var request = buildRequest("GET", "/v1/status/leader", null);
            var response = send(request);
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // ---- internal ----

    private HttpRequest buildRequest(String method, String path, String body) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofMillis(10000));

        if (token != null && !token.isBlank()) {
            builder.header("X-Consul-Token", token);
        }

        var bodyBytes = body != null
                ? body.getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        switch (method) {
            case "GET" -> builder.GET();
            case "DELETE" -> builder.DELETE();
            case "PUT" -> builder.header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
            case "POST" -> builder.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
            default -> throw new IllegalArgumentException("unsupported method: " + method);
        }

        return builder.build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConsulException("consul request interrupted", e);
        } catch (Exception e) {
            throw new ConsulException("consul request failed: " + e.getMessage(), e);
        }
    }

    private String buildRegistrationJson(ConsulServiceRegistration reg) {
        var map = new LinkedHashMap<String, Object>();
        if (reg.getId() != null) {
            map.put("ID", reg.getId());
        }
        map.put("Name", reg.getName());
        if (reg.getAddress() != null) {
            map.put("Address", reg.getAddress());
        }
        if (reg.getPort() > 0) {
            map.put("Port", reg.getPort());
        }
        if (!reg.getTags().isEmpty()) {
            map.put("Tags", reg.getTags());
        }
        if (reg.hasHealthCheck()) {
            var check = new LinkedHashMap<String, Object>();
            if (reg.getCheckHttp() != null) {
                check.put("HTTP", reg.getCheckHttp());
            }
            if (reg.getCheckTcp() != null) {
                check.put("TCP", reg.getCheckTcp());
            }
            if (reg.getCheckInterval() != null) {
                check.put("Interval", reg.getCheckInterval());
            }
            if (reg.getCheckTimeout() != null) {
                check.put("Timeout", reg.getCheckTimeout());
            }
            if (reg.getCheckDeregisterCriticalAfter() != null) {
                check.put("DeregisterCriticalServiceAfter", reg.getCheckDeregisterCriticalAfter());
            }
            map.put("Check", check);
        }
        return SimpleJson.toJsonString(map);
    }

    @SuppressWarnings("unchecked")
    private List<ConsulServiceInstance> parseHealthServiceArray(String json) {
        var result = new ArrayList<ConsulServiceInstance>();
        var array = SimpleJson.parseArray(json);
        for (var item : array) {
            if (!(item instanceof Map<?, ?> m)) {
                continue;
            }
            var service = (Map<String, Object>) m.get("Service");
            if (service == null) {
                continue;
            }
            var instance = new ConsulServiceInstance();
            instance.setId(String.valueOf(service.get("ID")));
            instance.setName(String.valueOf(service.get("Service")));
            instance.setAddress(String.valueOf(service.get("Address")));
            var port = service.get("Port");
            if (port instanceof Number n) {
                instance.setPort(n.intValue());
            }
            var tags = service.get("Tags");
            if (tags instanceof List<?> tagList) {
                var tagStrings = new ArrayList<String>();
                for (var t : tagList) {
                    tagStrings.add(String.valueOf(t));
                }
                instance.setTags(tagStrings);
            }
            var checks = m.get("Checks");
            if (checks instanceof List<?> checkList && !checkList.isEmpty()) {
                var firstCheck = (Map<String, Object>) checkList.get(0);
                var status = firstCheck.get("Status");
                if (status != null) {
                    instance.setStatus(String.valueOf(status));
                }
            }
            result.add(instance);
        }
        return result;
    }

    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String decodeBase64(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }
        try {
            return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encoded;
        }
    }
}
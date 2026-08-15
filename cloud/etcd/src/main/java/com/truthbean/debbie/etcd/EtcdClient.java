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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Low-level HTTP client for the etcd v3 API (gRPC-gateway).
 * <p>
 * Uses JDK built-in {@link HttpClient}. Keys and values are
 * base64-encoded as required by the etcd v3 HTTP gateway.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdClient {

    private final EtcdProperties properties;
    private final HttpClient httpClient;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;

    public EtcdClient(EtcdProperties properties) {
        this(properties, 5000, 10000);
    }

    public EtcdClient(EtcdProperties properties, int connectTimeoutMillis, int readTimeoutMillis) {
        this.properties = properties;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .build();
    }

    public EtcdProperties getProperties() { return properties; }

    // ---- key-value operations ----

    public void put(String key, String value) {
        var body = Map.of("key", encode(key), "value", encode(value));
        var response = post("/v3/kv/put", EtcdJson.toJson(body));
        checkResponse(response, "put " + key);
    }

    public void putWithLease(String key, String value, long leaseId) {
        var body = new LinkedHashMap<String, Object>();
        body.put("key", encode(key));
        body.put("value", encode(value));
        body.put("lease", String.valueOf(leaseId));
        var response = post("/v3/kv/put", EtcdJson.toJson(body));
        checkResponse(response, "put " + key);
    }

    public String get(String key) {
        var body = Map.of("key", encode(key));
        var response = post("/v3/kv/range", EtcdJson.toJson(body));
        checkResponse(response, "get " + key);
        return extractValue(response.body());
    }

    public Map<String, String> getRange(String key, String rangeEnd) {
        var body = new LinkedHashMap<String, Object>();
        body.put("key", encode(key));
        if (rangeEnd != null) body.put("range_end", encode(rangeEnd));
        var response = post("/v3/kv/range", EtcdJson.toJson(body));
        checkResponse(response, "getRange " + key);
        return extractKvs(response.body());
    }

    public Map<String, String> getByPrefix(String prefix) {
        var rangeEnd = prefixEnd(prefix);
        return getRange(prefix, rangeEnd);
    }

    public boolean delete(String key) {
        var body = Map.of("key", encode(key));
        var response = post("/v3/kv/deleterange", EtcdJson.toJson(body));
        return response.statusCode() == 200;
    }

    public long deleteByPrefix(String prefix) {
        var rangeEnd = prefixEnd(prefix);
        var body = new LinkedHashMap<String, Object>();
        body.put("key", encode(prefix));
        body.put("range_end", encode(rangeEnd));
        var response = post("/v3/kv/deleterange", EtcdJson.toJson(body));
        checkResponse(response, "deleteByPrefix " + prefix);
        return response.statusCode() == 200 ? 1L : 0L;
    }

    // ---- lease operations ----

    public long grantLease(long ttl) {
        var body = Map.of("TTL", String.valueOf(ttl));
        var response = post("/v3/lease/grant", EtcdJson.toJson(body));
        checkResponse(response, "grantLease");
        var parsed = EtcdJson.parseObject(response.body());
        var id = parsed.get("ID");
        if (id instanceof String s) return Long.parseLong(s);
        if (id instanceof Number n) return n.longValue();
        return 0L;
    }

    public boolean keepAlive(long leaseId) {
        var body = Map.of("ID", String.valueOf(leaseId));
        var response = post("/v3/lease/keepalive", EtcdJson.toJson(body));
        return response.statusCode() == 200;
    }

    public boolean revokeLease(long leaseId) {
        var body = Map.of("ID", String.valueOf(leaseId));
        var response = post("/v3/lease/revoke", EtcdJson.toJson(body));
        return response.statusCode() == 200;
    }

    // ---- health ----

    public boolean isAvailable() {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getBaseUrl() + "/health"))
                    .timeout(Duration.ofMillis(readTimeoutMillis))
                    .GET()
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getClusterStatus() {
        var response = post("/v3/cluster/status", "{}");
        checkResponse(response, "getClusterStatus");
        return EtcdJson.parseObject(response.body());
    }

    // ---- internal ----

    private HttpResponse<String> post(String path, String body) {
        try {
            var builder = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getBaseUrl() + path))
                    .timeout(Duration.ofMillis(readTimeoutMillis))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            if (properties.hasCredentials()) {
                var credentials = properties.getUsername() + ":" + properties.getPassword();
                var encoded = Base64.getEncoder().encodeToString(
                        credentials.getBytes(StandardCharsets.UTF_8));
                builder.header("Authorization", "Basic " + encoded);
            }

            builder.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            var request = builder.build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EtcdException("etcd request interrupted", e);
        } catch (Exception e) {
            throw new EtcdException("etcd request failed: " + e.getMessage(), e);
        }
    }

    private void checkResponse(HttpResponse<String> response, String operation) {
        if (response.statusCode() != 200) {
            throw new EtcdException(response.statusCode(),
                    operation + " failed: " + response.body());
        }
    }

    @SuppressWarnings("unchecked")
    private String extractValue(String responseBody) {
        var parsed = EtcdJson.parseObject(responseBody);
        var kvs = parsed.get("kvs");
        if (kvs instanceof java.util.List<?> list && !list.isEmpty()) {
            if (list.get(0) instanceof Map<?, ?> kv) {
                var value = kv.get("value");
                if (value instanceof String s) return decode(s);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> extractKvs(String responseBody) {
        var result = new LinkedHashMap<String, String>();
        var parsed = EtcdJson.parseObject(responseBody);
        var kvs = parsed.get("kvs");
        if (kvs instanceof java.util.List<?> list) {
            for (var item : list) {
                if (item instanceof Map<?, ?> kv) {
                    var key = kv.get("key");
                    var value = kv.get("value");
                    if (key instanceof String k) {
                        result.put(decode(k), value instanceof String v ? decode(v) : null);
                    }
                }
            }
        }
        return result;
    }

    private static String encode(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String s) {
        try {
            return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    private static String prefixEnd(String prefix) {
        if (prefix == null || prefix.isEmpty()) return "\0";
        var bytes = prefix.getBytes(StandardCharsets.UTF_8);
        for (int i = bytes.length - 1; i >= 0; i--) {
            if (bytes[i] < (byte) 0xFF) {
                bytes[i]++;
                return new String(bytes, 0, i + 1, StandardCharsets.UTF_8);
            }
        }
        return "\0";
    }
}
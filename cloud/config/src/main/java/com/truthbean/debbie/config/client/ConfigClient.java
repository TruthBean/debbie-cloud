/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS(OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config.client;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.config.ConfigConfiguration;
import com.truthbean.debbie.config.env.ConfigEnvironment;
import com.truthbean.debbie.config.env.ConfigPropertySource;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * config client that fetches {@link ConfigEnvironment} from a remote config server
 * and injects the properties into the local {@link EnvironmentDepositoryHolder}.
 * <p>
 * uses jdk built-in {@link java.net.http.HttpClient}, no extra dependency needed.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConfigClient {

    private final ConfigConfiguration configuration;
    private final HttpClient httpClient;
    private final EnvironmentDepositoryHolder environmentHolder;

    private volatile ConfigEnvironment cachedEnvironment;

    public ConfigClient(ConfigConfiguration configuration,
                        EnvironmentDepositoryHolder environmentHolder) {
        this.configuration = configuration;
        this.environmentHolder = environmentHolder;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(configuration.getClientConnectTimeout()))
                .build();
    }

    /**
     * fetch configuration from the config server and inject into local environment.
     *
     * @return the fetched {@link ConfigEnvironment}, or null on failure
     */
    public ConfigEnvironment fetchAndInject() {
        var env = fetch();
        if (env != null) {
            inject(env);
            this.cachedEnvironment = env;
        }
        return env;
    }

    /**
     * fetch configuration from the config server.
     */
    public ConfigEnvironment fetch() {
        var uri = buildUri();
        LOGGER.info(() -> "config client fetching from " + uri);
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .timeout(Duration.ofMillis(configuration.getClientReadTimeout()))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                var env = parseEnvironment(response.body());
                LOGGER.info(() -> "config client fetched " + env.getPropertySources().size()
                        + " property sources from server");
                return env;
            } else {
                LOGGER.error("config server returned " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            if (configuration.isClientFailFast()) {
                throw new ConfigClientException("failed to fetch config from " + uri, e);
            }
            LOGGER.error("failed to fetch config from " + uri, e);
        }
        return null;
    }

    /**
     * inject a {@link ConfigEnvironment} into the local {@link EnvironmentDepositoryHolder}.
     */
    public void inject(ConfigEnvironment env) {
        if (env == null || environmentHolder == null) {
            return;
        }
        var count = 0;
        for (ConfigPropertySource ps : env.getPropertySources()) {
            for (var entry : ps.getSource().entrySet()) {
                environmentHolder.addProperty(entry.getKey(), entry.getValue());
                count++;
            }
        }
        var total = count;
        LOGGER.info(() -> "config client injected " + total + " properties into local environment");
    }

    /**
     * refresh: re-fetch and re-inject.
     */
    public ConfigEnvironment refresh() {
        return fetchAndInject();
    }

    public ConfigEnvironment getCachedEnvironment() {
        return cachedEnvironment;
    }

    private String buildUri() {
        var base = configuration.getClientUri();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        var name = configuration.getClientName();
        var profile = configuration.getClientProfile();
        var label = configuration.getClientLabel();
        var uri = base + "/" + name + "/" + profile;
        if (label != null && !label.isBlank()) {
            uri += "/" + label;
        }
        return uri;
    }

    private ConfigEnvironment parseEnvironment(String json) {
        var env = new ConfigEnvironment(
                configuration.getClientName(),
                java.util.List.of(configuration.getClientProfile()),
                configuration.getClientLabel()
        );
        var props = parseSimpleJson(json);
        if (!props.isEmpty()) {
            env.addPropertySource(new ConfigPropertySource("remote", props));
        }
        return env;
    }

    private Map<String, String> parseSimpleJson(String json) {
        var result = new java.util.LinkedHashMap<String, String>();
        if (json == null || json.isBlank()) {
            return result;
        }
        var trimmed = json.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        var depth = 0;
        var sb = new StringBuilder();
        for (var c : trimmed.toCharArray()) {
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            if (c == ',' && depth == 0) {
                parseEntry(sb.toString(), result);
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            parseEntry(sb.toString(), result);
        }
        return result;
    }

    private void parseEntry(String entry, Map<String, String> result) {
        var colon = entry.indexOf(':');
        if (colon < 0) return;
        var key = entry.substring(0, colon).trim();
        var value = entry.substring(colon + 1).trim();
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        if (!key.isEmpty()) {
            result.put(key, value);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigClient.class);
}
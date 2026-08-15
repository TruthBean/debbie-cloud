/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul.config;

import com.truthbean.debbie.consul.ConsulClient;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client for reading distributed configuration from Consul KV store.
 * <p>
 * Keys are organized as {@code prefix/application-name/profile/key}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulConfigClient {

    private final ConsulClient client;
    private final String prefix;
    private final String profileSeparator;

    public ConsulConfigClient(ConsulClient client, String prefix, String profileSeparator) {
        this.client = client;
        this.prefix = prefix;
        this.profileSeparator = profileSeparator != null ? profileSeparator : "/";
    }

    public Map<String, String> getConfig(String appName, String profile) {
        var result = new LinkedHashMap<String, String>();

        var commonPath = buildKvPath(prefix, appName, null);
        result.putAll(flatten(client.getKeyValuesRecursive(commonPath), commonPath));

        if (profile != null && !profile.isEmpty()) {
            var profilePath = buildKvPath(prefix, appName, profile);
            result.putAll(flatten(client.getKeyValuesRecursive(profilePath), profilePath));
        }

        return result;
    }

    public String getValue(String key) {
        return client.getKeyValue(key);
    }

    public boolean setValue(String key, String value) {
        return client.setKeyValue(key, value);
    }

    public boolean deleteValue(String key) {
        return client.deleteKeyValue(key);
    }

    public Map<String, String> watch(String appName, String profile) {
        return getConfig(appName, profile);
    }

    private String buildKvPath(String prefix, String appName, String profile) {
        var sb = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) {
            if (!prefix.startsWith("/")) {
                sb.append("/");
            }
            sb.append(prefix);
        }
        if (sb.length() == 0 || sb.charAt(sb.length() - 1) != '/') {
            sb.append("/");
        }
        sb.append(appName);
        if (profile != null && !profile.isEmpty()) {
            sb.append(profileSeparator).append(profile);
        }
        sb.append("/");
        return sb.toString();
    }

    private Map<String, String> flatten(Map<String, String> kv, String basePath) {
        var result = new LinkedHashMap<String, String>();
        for (var entry : kv.entrySet()) {
            var key = entry.getKey();
            if (key.startsWith(basePath)) {
                key = key.substring(basePath.length());
            }
            if (entry.getValue() != null) {
                result.put(key, entry.getValue());
            }
        }
        return result;
    }
}
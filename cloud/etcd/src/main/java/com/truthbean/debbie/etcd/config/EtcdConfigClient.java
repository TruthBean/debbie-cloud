/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd.config;

import com.truthbean.debbie.etcd.EtcdClient;
import com.truthbean.debbie.etcd.EtcdException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration client that reads and writes application configuration
 * from/to etcd.
 * <p>
 * Configuration keys are stored under the prefix
 * {@code /config/{appName}/{profile}/{key}}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdConfigClient {

    private static final String CONFIG_PREFIX = "/config/";

    private final EtcdClient client;

    public EtcdConfigClient(EtcdClient client) {
        this.client = client;
    }

    public String get(String appName, String profile, String key) {
        var fullKey = buildKey(appName, profile, key);
        try {
            return client.get(fullKey);
        } catch (EtcdException e) {
            return null;
        }
    }

    public void put(String appName, String profile, String key, String value) {
        var fullKey = buildKey(appName, profile, key);
        client.put(fullKey, value);
    }

    public boolean delete(String appName, String profile, String key) {
        var fullKey = buildKey(appName, profile, key);
        return client.delete(fullKey);
    }

    public Map<String, String> getAll(String appName, String profile) {
        var prefix = CONFIG_PREFIX + appName + "/" + profile + "/";
        try {
            return client.getByPrefix(prefix);
        } catch (EtcdException e) {
            return Map.of();
        }
    }

    public Map<String, String> getConfig(String appName, String profile) {
        var raw = getAll(appName, profile);
        var result = new LinkedHashMap<String, String>();
        var keyPrefix = CONFIG_PREFIX + appName + "/" + profile + "/";
        for (var entry : raw.entrySet()) {
            var key = entry.getKey();
            if (key.startsWith(keyPrefix)) {
                result.put(key.substring(keyPrefix.length()), entry.getValue());
            }
        }
        return result;
    }

    public void putAll(String appName, String profile, Map<String, String> config) {
        for (var entry : config.entrySet()) {
            put(appName, profile, entry.getKey(), entry.getValue());
        }
    }

    private String buildKey(String appName, String profile, String key) {
        return CONFIG_PREFIX + appName + "/" + profile + "/" + key;
    }
}
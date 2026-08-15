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
 * A config source backed by Consul KV store.
 * <p>
 * Fetches all key-value pairs under a given prefix and exposes them
 * as a flat {@code Map<String, String>} for injection into
 * {@link com.truthbean.debbie.environment.EnvironmentDepositoryHolder}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulConfigSource {

    private final ConsulClient client;
    private final String rootPath;

    public ConsulConfigSource(ConsulClient client, String rootPath) {
        this.client = client;
        this.rootPath = rootPath;
    }

    public Map<String, String> load() {
        var kv = client.getKeyValuesRecursive(rootPath);
        var result = new LinkedHashMap<String, String>();
        for (var entry : kv.entrySet()) {
            var key = entry.getKey();
            if (key.startsWith(rootPath)) {
                key = key.substring(rootPath.length());
            }
            key = key.replace('/', '.');
            if (entry.getValue() != null) {
                result.put(key, entry.getValue());
            }
        }
        return result;
    }

    public String getRootPath() {
        return rootPath;
    }
}
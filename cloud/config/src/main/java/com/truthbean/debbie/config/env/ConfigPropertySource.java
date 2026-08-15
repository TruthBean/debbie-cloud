/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config.env;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * a named source of configuration properties.
 * <p>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConfigPropertySource {

    private final String name;
    private final Map<String, String> source;

    public ConfigPropertySource(String name) {
        this(name, new LinkedHashMap<>());
    }

    public ConfigPropertySource(String name, Map<String, String> source) {
        this.name = name;
        this.source = source == null ? new LinkedHashMap<>() : new LinkedHashMap<>(source);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getSource() {
        return Collections.unmodifiableMap(source);
    }

    public void put(String key, String value) {
        source.put(key, value);
    }

    public void putAll(Map<String, String> map) {
        if (map != null) {
            source.putAll(map);
        }
    }

    public String get(String key) {
        return source.get(key);
    }

    public boolean contains(String key) {
        return source.containsKey(key);
    }

    public int size() {
        return source.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigPropertySource that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, source);
    }

    @Override
    public String toString() {
        return "ConfigPropertySource{name='" + name + "', size=" + source.size() + '}';
    }
}
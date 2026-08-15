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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * the environment returned by the config server.
 * <p>
 * it contains the application name, active profiles, label (e.g. git branch),
 * and an ordered list of {@link ConfigPropertySource}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConfigEnvironment {

    private String name;
    private List<String> profiles;
    private String label;
    private final List<ConfigPropertySource> propertySources;

    public ConfigEnvironment() {
        this("application", List.of("default"), "master");
    }

    public ConfigEnvironment(String name, List<String> profiles, String label) {
        this.name = name;
        this.profiles = profiles == null ? List.of("default") : new ArrayList<>(profiles);
        this.label = label == null ? "master" : label;
        this.propertySources = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles == null ? List.of("default") : new ArrayList<>(profiles);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ConfigPropertySource> getPropertySources() {
        return Collections.unmodifiableList(propertySources);
    }

    public void addPropertySource(ConfigPropertySource source) {
        if (source != null) {
            propertySources.add(source);
        }
    }

    public void addPropertySource(int index, ConfigPropertySource source) {
        if (source != null) {
            propertySources.add(index, source);
        }
    }

    /**
     * flatten all property sources into a single map.
     * later sources override earlier ones.
     */
    public Map<String, String> asFlattenedMap() {
        var result = new LinkedHashMap<String, String>();
        for (var ps : propertySources) {
            result.putAll(ps.getSource());
        }
        return result;
    }

    /**
     * flatten all property sources into a single map.
     * earlier sources take precedence over later ones.
     */
    public Map<String, String> asFlattenedMapFirstWins() {
        var result = new LinkedHashMap<String, String>();
        for (int i = propertySources.size() - 1; i >= 0; i--) {
            result.putAll(propertySources.get(i).getSource());
        }
        return result;
    }

    @Override
    public String toString() {
        return "ConfigEnvironment{name='" + name + "', profiles=" + profiles
                + ", label='" + label + "', propertySources=" + propertySources.size() + '}';
    }
}
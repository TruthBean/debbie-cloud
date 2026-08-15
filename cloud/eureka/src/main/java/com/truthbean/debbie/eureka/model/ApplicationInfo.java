/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an application with multiple registered instances.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ApplicationInfo {

    private String name;
    private List<InstanceInfo> instances = new ArrayList<>();

    public ApplicationInfo() {
    }

    public ApplicationInfo(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<InstanceInfo> getInstances() { return instances; }
    public void setInstances(List<InstanceInfo> instances) { this.instances = instances != null ? instances : new ArrayList<>(); }

    public void addInstance(InstanceInfo info) { instances.add(info); }

    public boolean removeInstance(String instanceId) {
        return instances.removeIf(i -> i.getInstanceId().equals(instanceId));
    }

    public InstanceInfo getInstance(String instanceId) {
        for (var i : instances) {
            if (i.getInstanceId().equals(instanceId)) return i;
        }
        return null;
    }

    public List<InstanceInfo> getUpInstances() {
        var result = new ArrayList<InstanceInfo>();
        for (var i : instances) {
            if (i.isUp()) result.add(i);
        }
        return result;
    }

    public Map<String, Object> toMap() {
        var m = new LinkedHashMap<String, Object>();
        m.put("name", name);
        var instList = new ArrayList<Map<String, Object>>();
        for (var i : instances) instList.add(i.toMap());
        m.put("instances", instList);
        return m;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka.server;

import com.truthbean.debbie.eureka.model.ApplicationInfo;
import com.truthbean.debbie.eureka.model.InstanceInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * In-memory service registry for the Eureka server.
 * <p>
 * Maintains a map of application name → application info (with instances).
 * Evicts instances that haven't sent a heartbeat within the eviction timeout.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EurekaServerRegistry {

    private final Map<String, ApplicationInfo> registry = new ConcurrentHashMap<>();
    private final long evictionTimeoutMillis;
    private final long evictionIntervalMillis;
    private final ScheduledExecutorService evictionExecutor;
    private volatile boolean selfPreservationEnabled;
    private volatile int renewalThreshold;

    public EurekaServerRegistry() {
        this(90_000L, 60_000L);
    }

    public EurekaServerRegistry(long evictionTimeoutMillis, long evictionIntervalMillis) {
        this.evictionTimeoutMillis = evictionTimeoutMillis;
        this.evictionIntervalMillis = evictionIntervalMillis;
        this.evictionExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            var t = new Thread(r, "eureka-eviction");
            t.setDaemon(true);
            return t;
        });
        this.evictionExecutor.scheduleAtFixedRate(this::evict,
                evictionIntervalMillis, evictionIntervalMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized void register(InstanceInfo instance) {
        var appName = instance.getAppName().toUpperCase();
        var app = registry.computeIfAbsent(appName, ApplicationInfo::new);
        var existing = app.getInstance(instance.getInstanceId());
        if (existing != null) {
            app.removeInstance(instance.getInstanceId());
        }
        instance.setRegistrationTime(System.currentTimeMillis());
        instance.setLastHeartbeatTime(System.currentTimeMillis());
        app.addInstance(instance);
    }

    public synchronized boolean renew(String appName, String instanceId) {
        var app = registry.get(appName.toUpperCase());
        if (app == null) return false;
        var instance = app.getInstance(instanceId);
        if (instance == null) return false;
        instance.renew();
        return true;
    }

    public synchronized boolean cancel(String appName, String instanceId) {
        var app = registry.get(appName.toUpperCase());
        if (app == null) return false;
        return app.removeInstance(instanceId);
    }

    public synchronized boolean updateStatus(String appName, String instanceId, String status) {
        var app = registry.get(appName.toUpperCase());
        if (app == null) return false;
        var instance = app.getInstance(instanceId);
        if (instance == null) return false;
        instance.setStatus(status);
        return true;
    }

    public ApplicationInfo getApplication(String appName) {
        return registry.get(appName.toUpperCase());
    }

    public Collection<ApplicationInfo> getAllApplications() {
        return registry.values();
    }

    public List<InstanceInfo> getAllInstances() {
        var result = new ArrayList<InstanceInfo>();
        for (var app : registry.values()) {
            result.addAll(app.getInstances());
        }
        return result;
    }

    public List<InstanceInfo> getUpInstances(String appName) {
        var app = registry.get(appName.toUpperCase());
        if (app == null) return List.of();
        return app.getUpInstances();
    }

    public int getApplicationCount() {
        return registry.size();
    }

    public int getInstanceCount() {
        int count = 0;
        for (var app : registry.values()) count += app.getInstances().size();
        return count;
    }

    public boolean isSelfPreservationEnabled() { return selfPreservationEnabled; }
    public void setSelfPreservationEnabled(boolean v) { this.selfPreservationEnabled = v; }

    public synchronized void evict() {
        long now = System.currentTimeMillis();
        int evicted = 0;
        for (var app : registry.values()) {
            var toRemove = new ArrayList<InstanceInfo>();
            for (var instance : app.getInstances()) {
                if (now - instance.getLastHeartbeatTime() > evictionTimeoutMillis) {
                    toRemove.add(instance);
                }
            }
            for (var instance : toRemove) {
                app.removeInstance(instance.getInstanceId());
                evicted++;
            }
        }
        if (evicted > 0) {
            cleanupEmptyApps();
        }
    }

    private void cleanupEmptyApps() {
        registry.entrySet().removeIf(e -> e.getValue().getInstances().isEmpty());
    }

    public void shutdown() {
        evictionExecutor.shutdown();
    }

    public long getEvictionTimeoutMillis() { return evictionTimeoutMillis; }
    public long getEvictionIntervalMillis() { return evictionIntervalMillis; }
}
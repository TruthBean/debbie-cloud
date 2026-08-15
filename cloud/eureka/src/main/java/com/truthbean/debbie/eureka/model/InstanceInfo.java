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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a service instance registered with Eureka.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class InstanceInfo {

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String STARTING = "STARTING";
    public static final String OUT_OF_SERVICE = "OUT_OF_SERVICE";

    private String appName;
    private String instanceId;
    private String hostName;
    private String ipAddr;
    private int port;
    private int securePort;
    private String status = UP;
    private String healthCheckUrl;
    private String homePageUrl;
    private String statusPageUrl;
    private Map<String, String> metadata = new LinkedHashMap<>();
    private long lastHeartbeatTime;
    private long registrationTime;

    public InstanceInfo() {
    }

    public InstanceInfo(String appName, String instanceId, String hostName, int port) {
        this.appName = appName;
        this.instanceId = instanceId;
        this.hostName = hostName;
        this.port = port;
        this.registrationTime = System.currentTimeMillis();
        this.lastHeartbeatTime = this.registrationTime;
    }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getIpAddr() { return ipAddr; }
    public void setIpAddr(String ipAddr) { this.ipAddr = ipAddr; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public int getSecurePort() { return securePort; }
    public void setSecurePort(int securePort) { this.securePort = securePort; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHealthCheckUrl() { return healthCheckUrl; }
    public void setHealthCheckUrl(String url) { this.healthCheckUrl = url; }

    public String getHomePageUrl() { return homePageUrl; }
    public void setHomePageUrl(String url) { this.homePageUrl = url; }

    public String getStatusPageUrl() { return statusPageUrl; }
    public void setStatusPageUrl(String url) { this.statusPageUrl = url; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata != null ? metadata : new LinkedHashMap<>(); }

    public long getLastHeartbeatTime() { return lastHeartbeatTime; }
    public void setLastHeartbeatTime(long t) { this.lastHeartbeatTime = t; }

    public long getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(long t) { this.registrationTime = t; }

    public boolean isUp() { return UP.equalsIgnoreCase(status); }

    public void renew() { this.lastHeartbeatTime = System.currentTimeMillis(); }

    public Map<String, Object> toMap() {
        var m = new LinkedHashMap<String, Object>();
        m.put("appName", appName);
        m.put("instanceId", instanceId);
        m.put("hostName", hostName);
        if (ipAddr != null) m.put("ipAddr", ipAddr);
        m.put("port", port);
        if (securePort > 0) m.put("securePort", securePort);
        m.put("status", status);
        if (healthCheckUrl != null) m.put("healthCheckUrl", healthCheckUrl);
        if (homePageUrl != null) m.put("homePageUrl", homePageUrl);
        if (statusPageUrl != null) m.put("statusPageUrl", statusPageUrl);
        if (!metadata.isEmpty()) m.put("metadata", metadata);
        return m;
    }

    @SuppressWarnings("unchecked")
    public static InstanceInfo fromMap(Map<String, Object> m) {
        var info = new InstanceInfo();
        info.setAppName(str(m.get("appName")));
        info.setInstanceId(str(m.get("instanceId")));
        info.setHostName(str(m.get("hostName")));
        info.setIpAddr(str(m.get("ipAddr")));
        var port = m.get("port");
        if (port instanceof Number n) info.setPort(n.intValue());
        var sp = m.get("securePort");
        if (sp instanceof Number n) info.setSecurePort(n.intValue());
        info.setStatus(str(m.get("status")));
        info.setHealthCheckUrl(str(m.get("healthCheckUrl")));
        info.setHomePageUrl(str(m.get("homePageUrl")));
        info.setStatusPageUrl(str(m.get("statusPageUrl")));
        var meta = m.get("metadata");
        if (meta instanceof Map<?, ?> mm) {
            var md = new LinkedHashMap<String, String>();
            for (var e : mm.entrySet()) md.put(str(e.getKey()), str(e.getValue()));
            info.setMetadata(md);
        }
        return info;
    }

    private static String str(Object o) { return o != null ? String.valueOf(o) : null; }

    @Override
    public String toString() {
        return "InstanceInfo{appName='" + appName + "', instanceId='" + instanceId
                + "', host='" + hostName + "', port=" + port + ", status='" + status + "'}";
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul.discovery;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a service registration request to Consul.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulServiceRegistration {

    private String id;
    private String name;
    private String address;
    private int port;
    private List<String> tags = new ArrayList<>();
    private String checkType;
    private String checkHttp;
    private String checkTcp;
    private String checkInterval = "10s";
    private String checkTimeout = "5s";
    private String checkDeregisterCriticalAfter = "30s";

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }

    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }

    public String getCheckHttp() { return checkHttp; }
    public void setCheckHttp(String checkHttp) { this.checkHttp = checkHttp; }

    public String getCheckTcp() { return checkTcp; }
    public void setCheckTcp(String checkTcp) { this.checkTcp = checkTcp; }

    public String getCheckInterval() { return checkInterval; }
    public void setCheckInterval(String checkInterval) { this.checkInterval = checkInterval; }

    public String getCheckTimeout() { return checkTimeout; }
    public void setCheckTimeout(String checkTimeout) { this.checkTimeout = checkTimeout; }

    public String getCheckDeregisterCriticalAfter() { return checkDeregisterCriticalAfter; }
    public void setCheckDeregisterCriticalAfter(String v) { this.checkDeregisterCriticalAfter = v; }

    public boolean hasHealthCheck() {
        return checkType != null || checkHttp != null || checkTcp != null;
    }
}
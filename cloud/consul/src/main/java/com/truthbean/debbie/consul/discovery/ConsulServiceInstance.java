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
import java.util.Map;

/**
 * Model representing a service instance discovered from Consul.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulServiceInstance {

    private String id;
    private String name;
    private String address;
    private int port;
    private List<String> tags = new ArrayList<>();
    private String status;
    private Map<String, String> metadata;

    public ConsulServiceInstance() {
    }

    public ConsulServiceInstance(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    public boolean isPassing() {
        return "passing".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "ConsulServiceInstance{name='" + name + "', id='" + id + "', address='" + address
                + "', port=" + port + ", status='" + status + "', tags=" + tags + '}';
    }
}
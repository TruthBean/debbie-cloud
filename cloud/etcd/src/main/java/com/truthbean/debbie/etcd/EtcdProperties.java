/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd;

/**
 * Connection properties for the etcd cluster.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdProperties {

    private String host;
    private int port;
    private String scheme;
    private String username;
    private String password;
    private String prefix;

    public EtcdProperties() {
        this.host = "localhost";
        this.port = 2379;
        this.scheme = "http";
        this.prefix = "";
    }

    public EtcdProperties(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

    public EtcdProperties(String host, int port, String scheme) {
        this(host, port);
        this.scheme = scheme;
    }

    public static EtcdProperties of(String host, int port) {
        return new EtcdProperties(host, port);
    }

    public static EtcdProperties of(String host, int port, String scheme) {
        return new EtcdProperties(host, port, scheme);
    }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getScheme() { return scheme; }
    public void setScheme(String scheme) { this.scheme = scheme; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix != null ? prefix : ""; }

    public String getBaseUrl() {
        return scheme + "://" + host + ":" + port;
    }

    public boolean hasCredentials() {
        return username != null && !username.isEmpty() && password != null;
    }

    @Override
    public String toString() {
        return "EtcdProperties{" + getBaseUrl() + ", prefix='" + prefix + "'}";
    }
}
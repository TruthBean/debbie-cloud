/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mail;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * Configuration of debbie-mail.
 * <p>
 * properties prefix: {@code debbie.mail}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MailConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    private String host = "localhost";

    private int port = 25;

    private String username;

    private String password;

    private String protocol = "smtp";

    private String defaultEncoding = "UTF-8";

    private boolean ssl = false;

    private boolean starttls = false;

    private boolean auth = true;

    private int connectionTimeout = 5000;

    private int timeout = 5000;

    private int writeTimeout = 5000;

    private String defaultFrom;

    private String defaultFromPersonal;

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getDefaultEncoding() { return defaultEncoding; }
    public void setDefaultEncoding(String defaultEncoding) { this.defaultEncoding = defaultEncoding; }

    public boolean isSsl() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl = ssl; }

    public boolean isStarttls() { return starttls; }
    public void setStarttls(boolean starttls) { this.starttls = starttls; }

    public boolean isAuth() { return auth; }
    public void setAuth(boolean auth) { this.auth = auth; }

    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public int getWriteTimeout() { return writeTimeout; }
    public void setWriteTimeout(int writeTimeout) { this.writeTimeout = writeTimeout; }

    public String getDefaultFrom() { return defaultFrom; }
    public void setDefaultFrom(String defaultFrom) { this.defaultFrom = defaultFrom; }

    public String getDefaultFromPersonal() { return defaultFromPersonal; }
    public void setDefaultFromPersonal(String defaultFromPersonal) { this.defaultFromPersonal = defaultFromPersonal; }

    public boolean hasCredentials() {
        return username != null && !username.isBlank() && password != null;
    }

    public boolean hasDefaultFrom() {
        return defaultFrom != null && !defaultFrom.isBlank();
    }

    public boolean isSmtp() {
        return "smtp".equalsIgnoreCase(protocol);
    }

    public boolean isSmtps() {
        return "smtps".equalsIgnoreCase(protocol);
    }

    @Override
    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new MailConfiguration();
        c.enable = this.enable;
        c.host = this.host;
        c.port = this.port;
        c.username = this.username;
        c.password = this.password;
        c.protocol = this.protocol;
        c.defaultEncoding = this.defaultEncoding;
        c.ssl = this.ssl;
        c.starttls = this.starttls;
        c.auth = this.auth;
        c.connectionTimeout = this.connectionTimeout;
        c.timeout = this.timeout;
        c.writeTimeout = this.writeTimeout;
        c.defaultFrom = this.defaultFrom;
        c.defaultFromPersonal = this.defaultFromPersonal;
        return (T) c;
    }

    @Override
    public void close() {}

    @Override
    public String toString() {
        return "MailConfiguration{host=" + host
                + ", port=" + port
                + ", protocol=" + protocol
                + ", ssl=" + ssl
                + ", starttls=" + starttls
                + ", auth=" + auth
                + ", defaultFrom=" + defaultFrom + "}";
    }
}
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

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * Configuration of debbie-etcd.
 * <p>
 * properties prefix: {@code debbie.etcd}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.etcd")
public class EtcdConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "host", defaultValue = "localhost")
    private String host = "localhost";

    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "2379")
    private int port = 2379;

    @PropertyInject(value = "scheme", defaultValue = "http")
    private String scheme = "http";

    @PropertyInject(value = "username")
    private String username;

    @PropertyInject(value = "password")
    private String password;

    @PropertyInject(value = "prefix", defaultValue = "")
    private String prefix = "";

    @PropertyInject(value = "discovery.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean discoveryEnable = true;

    @PropertyInject(value = "config.enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean configEnable = false;

    @PropertyInject(value = "config.app-name", defaultValue = "application")
    private String configAppName = "application";

    @PropertyInject(value = "config.profile", defaultValue = "default")
    private String configProfile = "default";

    @PropertyInject(value = "config.fail-fast", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean configFailFast = false;

    @PropertyInject(value = "health.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean healthEnable = true;

    @PropertyInject(value = "connect-timeout", transformer = IntegerTransformer.class, defaultValue = "5000")
    private int connectTimeout = 5000;

    @PropertyInject(value = "read-timeout", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int readTimeout = 10000;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

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
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public boolean isDiscoveryEnable() { return discoveryEnable; }
    public void setDiscoveryEnable(boolean v) { this.discoveryEnable = v; }

    public boolean isConfigEnable() { return configEnable; }
    public void setConfigEnable(boolean v) { this.configEnable = v; }

    public String getConfigAppName() { return configAppName; }
    public void setConfigAppName(String v) { this.configAppName = v; }

    public String getConfigProfile() { return configProfile; }
    public void setConfigProfile(String v) { this.configProfile = v; }

    public boolean isConfigFailFast() { return configFailFast; }
    public void setConfigFailFast(boolean v) { this.configFailFast = v; }

    public boolean isHealthEnable() { return healthEnable; }
    public void setHealthEnable(boolean v) { this.healthEnable = v; }

    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int v) { this.connectTimeout = v; }

    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int v) { this.readTimeout = v; }

    public EtcdProperties toProperties() {
        var props = new EtcdProperties(host, port, scheme);
        props.setUsername(username);
        props.setPassword(password);
        props.setPrefix(prefix);
        return props;
    }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new EtcdConfiguration();
        c.enable = this.enable;
        c.host = this.host;
        c.port = this.port;
        c.scheme = this.scheme;
        c.username = this.username;
        c.password = this.password;
        c.prefix = this.prefix;
        c.discoveryEnable = this.discoveryEnable;
        c.configEnable = this.configEnable;
        c.configAppName = this.configAppName;
        c.configProfile = this.configProfile;
        c.configFailFast = this.configFailFast;
        c.healthEnable = this.healthEnable;
        c.connectTimeout = this.connectTimeout;
        c.readTimeout = this.readTimeout;
        return (T) c;
    }

    @Override
    public void close() {}
}
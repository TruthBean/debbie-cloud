/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * configuration of debbie-config.
 * <p>
 * properties prefix: {@code debbie.config}
 * <p>
 * the module can run as config server, config client, or both.
 * set {@code debbie.config.server.enable=true} to act as a config server,
 * set {@code debbie.config.client.enable=true} to act as a config client.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.config")
public class ConfigConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "server.enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean serverEnable;

    @PropertyInject(value = "server.repository", defaultValue = "file")
    private String serverRepository = "file";

    @PropertyInject(value = "server.base-dir", defaultValue = "config")
    private String serverBaseDir = "config";

    @PropertyInject(value = "server.default-label", defaultValue = "master")
    private String serverDefaultLabel = "master";

    @PropertyInject(value = "server.encrypt", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean serverEncrypt;

    @PropertyInject(value = "server.prefix", defaultValue = "/config")
    private String serverPrefix = "/config";

    @PropertyInject(value = "client.enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean clientEnable;

    @PropertyInject(value = "client.uri", defaultValue = "http://localhost:8888/config")
    private String clientUri = "http://localhost:8888/config";

    @PropertyInject(value = "client.name", defaultValue = "application")
    private String clientName = "application";

    @PropertyInject(value = "client.profile", defaultValue = "default")
    private String clientProfile = "default";

    @PropertyInject(value = "client.label")
    private String clientLabel;

    @PropertyInject(value = "client.connect-timeout", transformer = IntegerTransformer.class, defaultValue = "5000")
    private int clientConnectTimeout = 5000;

    @PropertyInject(value = "client.read-timeout", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int clientReadTimeout = 10000;

    @PropertyInject(value = "client.fail-fast", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean clientFailFast;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public boolean isServerEnable() { return serverEnable; }
    public void setServerEnable(boolean serverEnable) { this.serverEnable = serverEnable; }

    public String getServerRepository() { return serverRepository; }
    public void setServerRepository(String serverRepository) { this.serverRepository = serverRepository; }

    public String getServerBaseDir() { return serverBaseDir; }
    public void setServerBaseDir(String serverBaseDir) { this.serverBaseDir = serverBaseDir; }

    public String getServerDefaultLabel() { return serverDefaultLabel; }
    public void setServerDefaultLabel(String serverDefaultLabel) { this.serverDefaultLabel = serverDefaultLabel; }

    public boolean isServerEncrypt() { return serverEncrypt; }
    public void setServerEncrypt(boolean serverEncrypt) { this.serverEncrypt = serverEncrypt; }

    public String getServerPrefix() { return serverPrefix; }
    public void setServerPrefix(String serverPrefix) { this.serverPrefix = serverPrefix; }

    public boolean isClientEnable() { return clientEnable; }
    public void setClientEnable(boolean clientEnable) { this.clientEnable = clientEnable; }

    public String getClientUri() { return clientUri; }
    public void setClientUri(String clientUri) { this.clientUri = clientUri; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientProfile() { return clientProfile; }
    public void setClientProfile(String clientProfile) { this.clientProfile = clientProfile; }

    public String getClientLabel() { return clientLabel; }
    public void setClientLabel(String clientLabel) { this.clientLabel = clientLabel; }

    public int getClientConnectTimeout() { return clientConnectTimeout; }
    public void setClientConnectTimeout(int clientConnectTimeout) { this.clientConnectTimeout = clientConnectTimeout; }

    public int getClientReadTimeout() { return clientReadTimeout; }
    public void setClientReadTimeout(int clientReadTimeout) { this.clientReadTimeout = clientReadTimeout; }

    public boolean isClientFailFast() { return clientFailFast; }
    public void setClientFailFast(boolean clientFailFast) { this.clientFailFast = clientFailFast; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new ConfigConfiguration();
        c.enable = this.enable;
        c.serverEnable = this.serverEnable;
        c.serverRepository = this.serverRepository;
        c.serverBaseDir = this.serverBaseDir;
        c.serverDefaultLabel = this.serverDefaultLabel;
        c.serverEncrypt = this.serverEncrypt;
        c.serverPrefix = this.serverPrefix;
        c.clientEnable = this.clientEnable;
        c.clientUri = this.clientUri;
        c.clientName = this.clientName;
        c.clientProfile = this.clientProfile;
        c.clientLabel = this.clientLabel;
        c.clientConnectTimeout = this.clientConnectTimeout;
        c.clientReadTimeout = this.clientReadTimeout;
        c.clientFailFast = this.clientFailFast;
        return (T) c;
    }

    @Override
    public void close() {}
}
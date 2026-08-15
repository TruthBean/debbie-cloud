/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * Configuration of debbie-kubernetes.
 * <p>
 * properties prefix: {@code debbie.kubernetes}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.kubernetes")
public class KubernetesConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "host")
    private String host;

    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "443")
    private int port = 443;

    @PropertyInject(value = "namespace", defaultValue = "default")
    private String namespace = "default";

    @PropertyInject(value = "token")
    private String token;

    @PropertyInject(value = "auto-detect", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean autoDetect = true;

    @PropertyInject(value = "discovery.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean discoveryEnable = true;

    @PropertyInject(value = "config.enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean configEnable = false;

    @PropertyInject(value = "config.configmaps")
    private String configMaps;

    @PropertyInject(value = "config.secrets")
    private String secrets;

    @PropertyInject(value = "config.fail-fast", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean configFailFast = false;

    @PropertyInject(value = "health.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean healthEnable = true;

    @PropertyInject(value = "service.name")
    private String serviceName;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public boolean isAutoDetect() { return autoDetect; }
    public void setAutoDetect(boolean autoDetect) { this.autoDetect = autoDetect; }

    public boolean isDiscoveryEnable() { return discoveryEnable; }
    public void setDiscoveryEnable(boolean discoveryEnable) { this.discoveryEnable = discoveryEnable; }

    public boolean isConfigEnable() { return configEnable; }
    public void setConfigEnable(boolean configEnable) { this.configEnable = configEnable; }

    public String getConfigMaps() { return configMaps; }
    public void setConfigMaps(String configMaps) { this.configMaps = configMaps; }

    public String getSecrets() { return secrets; }
    public void setSecrets(String secrets) { this.secrets = secrets; }

    public boolean isConfigFailFast() { return configFailFast; }
    public void setConfigFailFast(boolean configFailFast) { this.configFailFast = configFailFast; }

    public boolean isHealthEnable() { return healthEnable; }
    public void setHealthEnable(boolean healthEnable) { this.healthEnable = healthEnable; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String[] getConfigMapNames() {
        if (configMaps == null || configMaps.isBlank()) {
            return new String[0];
        }
        return configMaps.split("\\s*,\\s*");
    }

    public String[] getSecretNames() {
        if (secrets == null || secrets.isBlank()) {
            return new String[0];
        }
        return secrets.split("\\s*,\\s*");
    }

    public KubernetesProperties toProperties() {
        if (autoDetect) {
            var props = KubernetesProperties.autoDetect();
            if (host != null && !host.isEmpty()) {
                props.setApiServerHost(host);
                props.setApiServerPort(port);
            }
            if (namespace != null && !namespace.isEmpty()) {
                props.setNamespace(namespace);
            }
            if (token != null && !token.isEmpty()) {
                props.setToken(token);
            }
            return props;
        }
        return KubernetesProperties.of(
                host != null ? host : "localhost",
                port,
                namespace,
                token);
    }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new KubernetesConfiguration();
        c.enable = this.enable;
        c.host = this.host;
        c.port = this.port;
        c.namespace = this.namespace;
        c.token = this.token;
        c.autoDetect = this.autoDetect;
        c.discoveryEnable = this.discoveryEnable;
        c.configEnable = this.configEnable;
        c.configMaps = this.configMaps;
        c.secrets = this.secrets;
        c.configFailFast = this.configFailFast;
        c.healthEnable = this.healthEnable;
        c.serviceName = this.serviceName;
        return (T) c;
    }

    @Override
    public void close() {}
}
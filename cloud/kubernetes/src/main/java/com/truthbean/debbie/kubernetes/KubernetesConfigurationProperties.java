/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-kubernetes.
 * <p>
 * Reads the {@link KubernetesConfiguration} from the environment using the prefix
 * {@code debbie.kubernetes.} and fills a plain configuration object, instead of
 * relying on annotations.
 * <p>
 * Note: {@link KubernetesProperties} is the existing kubernetes connection property
 * holder, so this reader is named differently to avoid a name clash.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesConfigurationProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<KubernetesConfiguration> {

    /**
     * key prefix of the kubernetes properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.kubernetes.port}
     */
    private static final String KEY_PREFIX = "debbie.kubernetes.";

    private final Map<String, KubernetesConfiguration> map = new HashMap<>();
    private KubernetesConfiguration configuration;

    public KubernetesConfigurationProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, KubernetesConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return Map.of();
    }

    @Override
    public Set<String> getCategories(String profile) {
        return Set.of();
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return false;
    }

    @Override
    public KubernetesConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public KubernetesConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new KubernetesConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getValue(KEY_PREFIX + "host"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 443));
        configuration.setNamespace(getStringValue(KEY_PREFIX + "namespace", "default"));
        configuration.setToken(getValue(KEY_PREFIX + "token"));
        configuration.setAutoDetect(getBooleanValue(KEY_PREFIX + "auto-detect", true));

        // service discovery
        configuration.setDiscoveryEnable(getBooleanValue(KEY_PREFIX + "discovery.enable", true));

        // config
        configuration.setConfigEnable(getBooleanValue(KEY_PREFIX + "config.enable", false));
        configuration.setConfigMaps(getValue(KEY_PREFIX + "config.configmaps"));
        configuration.setSecrets(getValue(KEY_PREFIX + "config.secrets"));
        configuration.setConfigFailFast(getBooleanValue(KEY_PREFIX + "config.fail-fast", false));

        // health and service identity
        configuration.setHealthEnable(getBooleanValue(KEY_PREFIX + "health.enable", true));
        configuration.setServiceName(getValue(KEY_PREFIX + "service.name"));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-eureka.
 * <p>
 * Reads the {@link EurekaConfiguration} from the environment using the prefix
 * {@code debbie.eureka.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EurekaProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<EurekaConfiguration> {

    /**
     * key prefix of the eureka properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.eureka.client.server-url}
     */
    private static final String KEY_PREFIX = "debbie.eureka.";

    private final Map<String, EurekaConfiguration> map = new HashMap<>();
    private EurekaConfiguration configuration;

    public EurekaProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, EurekaConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public EurekaConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public EurekaConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new EurekaConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));

        // eureka server
        configuration.setServerEnable(getBooleanValue(KEY_PREFIX + "server.enable", false));
        configuration.setServerPrefix(getStringValue(KEY_PREFIX + "server.prefix", "/eureka"));
        configuration.setServerEvictionTimeout(getLongValue(KEY_PREFIX + "server.eviction-timeout", 90000));
        configuration.setServerEvictionInterval(getLongValue(KEY_PREFIX + "server.eviction-interval", 60000));

        // eureka client
        configuration.setClientEnable(getBooleanValue(KEY_PREFIX + "client.enable", true));
        configuration.setClientServerUrl(getStringValue(KEY_PREFIX + "client.server-url", "http://localhost:8761"));
        configuration.setClientPrefix(getStringValue(KEY_PREFIX + "client.prefix", "/eureka"));
        configuration.setClientServiceName(getValue(KEY_PREFIX + "client.service-name"));
        configuration.setClientInstanceId(getValue(KEY_PREFIX + "client.instance-id"));
        configuration.setClientHost(getValue(KEY_PREFIX + "client.host"));
        configuration.setClientPort(getIntegerValue(KEY_PREFIX + "client.port", 8080));
        configuration.setClientSecurePort(getIntegerValue(KEY_PREFIX + "client.secure-port", 0));
        configuration.setClientHeartbeatInterval(getLongValue(KEY_PREFIX + "client.heartbeat-interval", 30000));
        configuration.setClientConnectTimeout(getIntegerValue(KEY_PREFIX + "client.connect-timeout", 5000));
        configuration.setClientReadTimeout(getIntegerValue(KEY_PREFIX + "client.read-timeout", 10000));
        configuration.setClientAutoRegister(getBooleanValue(KEY_PREFIX + "client.auto-register", true));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.cloud.gateway;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-gateway.
 * <p>
 * Reads the {@link GatewayConfiguration} from the environment using the prefix
 * {@code debbie.gateway.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GatewayProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<GatewayConfiguration> {

    /**
     * key prefix of the gateway properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.gateway.connect-timeout}
     */
    private static final String KEY_PREFIX = "debbie.gateway.";

    private final Map<String, GatewayConfiguration> map = new HashMap<>();
    private GatewayConfiguration configuration;

    public GatewayProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, GatewayConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public GatewayConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public GatewayConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new GatewayConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setConnectTimeout(getIntegerValue(KEY_PREFIX + "connect-timeout", 5000));
        configuration.setReadTimeout(getIntegerValue(KEY_PREFIX + "read-timeout", 30000));
        configuration.setMaxRetries(getIntegerValue(KEY_PREFIX + "max-retries", 3));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
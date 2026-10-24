/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-function.
 * <p>
 * Reads the {@link FunctionConfiguration} from the environment using the prefix
 * {@code debbie.function.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FunctionProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<FunctionConfiguration> {

    /**
     * key prefix of the function properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.function.http.prefix}
     */
    private static final String KEY_PREFIX = "debbie.function.";

    private final Map<String, FunctionConfiguration> map = new HashMap<>();
    private FunctionConfiguration configuration;

    public FunctionProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, FunctionConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public FunctionConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public FunctionConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new FunctionConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHttpEnable(getBooleanValue(KEY_PREFIX + "http.enable", true));
        configuration.setHttpPrefix(getStringValue(KEY_PREFIX + "http.prefix", "/function"));
        configuration.setRoutingEnable(getBooleanValue(KEY_PREFIX + "routing.enable", true));
        configuration.setCompositionEnable(getBooleanValue(KEY_PREFIX + "composition.enable", true));
        configuration.setAutoScan(getBooleanValue(KEY_PREFIX + "auto-scan", false));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
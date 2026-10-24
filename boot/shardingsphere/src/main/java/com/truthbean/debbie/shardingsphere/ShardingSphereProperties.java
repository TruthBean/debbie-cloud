/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.shardingsphere;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-shardingsphere.
 * <p>
 * Reads the {@link ShardingSphereConfiguration} from the environment using the prefix
 * {@code debbie.shardingsphere.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ShardingSphereProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<ShardingSphereConfiguration> {

    /**
     * key prefix of the shardingsphere properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.shardingsphere.database-name}
     */
    private static final String KEY_PREFIX = "debbie.shardingsphere.";

    private final Map<String, ShardingSphereConfiguration> map = new HashMap<>();
    private ShardingSphereConfiguration configuration;

    public ShardingSphereProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, ShardingSphereConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public ShardingSphereConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public ShardingSphereConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ShardingSphereConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setYamlConfigLocation(getValue(KEY_PREFIX + "yaml-config-location"));
        configuration.setDatabaseName(getStringValue(KEY_PREFIX + "database-name", "sharding_db"));

        // mode configuration
        configuration.setModeType(getStringValue(KEY_PREFIX + "mode-type", "standalone"));
        configuration.setModeRepositoryType(getStringValue(KEY_PREFIX + "mode-repository-type", "file"));
        configuration.setModeRepositoryFilePath(getStringValue(KEY_PREFIX + "mode-repository-file-path", ".shardingSphere"));
        configuration.setModeOverwrite(getBooleanValue(KEY_PREFIX + "mode-overwrite", false));

        // cluster mode
        configuration.setModeClusterNamespace(getValue(KEY_PREFIX + "mode-cluster-namespace"));
        configuration.setModeClusterServerLists(getValue(KEY_PREFIX + "mode-cluster-server-lists"));

        // props
        configuration.setSqlShow(getBooleanValue(KEY_PREFIX + "sql-show", false));
        configuration.setSqlSimple(getBooleanValue(KEY_PREFIX + "sql-simple", false));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
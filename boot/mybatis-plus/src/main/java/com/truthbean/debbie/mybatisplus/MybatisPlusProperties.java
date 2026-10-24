/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatisplus;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-mybatis-plus.
 * <p>
 * Reads the {@link MybatisPlusConfiguration} from the environment using the prefix
 * {@code debbie.mybatis-plus.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MybatisPlusProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<MybatisPlusConfiguration> {

    /**
     * key prefix of the mybatis-plus properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.mybatis-plus.banner}
     */
    private static final String KEY_PREFIX = "debbie.mybatis-plus.";

    private final Map<String, MybatisPlusConfiguration> map = new HashMap<>();
    private MybatisPlusConfiguration configuration;

    public MybatisPlusProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, MybatisPlusConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public MybatisPlusConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public MybatisPlusConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new MybatisPlusConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setBanner(getBooleanValue(KEY_PREFIX + "banner", true));
        configuration.setCheckConfigLocation(getBooleanValue(KEY_PREFIX + "check-config-location", false));
        configuration.setConfigLocation(getValue(KEY_PREFIX + "config-location"));
        configuration.setMapperLocations(getValue(KEY_PREFIX + "mapper-locations"));

        // GlobalConfig.DbConfig
        configuration.setDbConfigMapUnderscoreToCamelCase(getBooleanValue(KEY_PREFIX + "db-config.map-underscore-to-camel-case", true));
        configuration.setDbConfigTablePrefix(getValue(KEY_PREFIX + "db-config.table-prefix"));
        configuration.setDbConfigIdType(getStringValue(KEY_PREFIX + "db-config.id-type", "ASSIGN_ID"));
        configuration.setDbConfigLogicDeleteField(getValue(KEY_PREFIX + "db-config.logic-delete-field"));
        configuration.setDbConfigLogicDeleteValue(getValue(KEY_PREFIX + "db-config.logic-delete-value"));
        configuration.setDbConfigLogicNotDeleteValue(getValue(KEY_PREFIX + "db-config.logic-not-delete-value"));
        configuration.setDbConfigUpdateStrategy(getStringValue(KEY_PREFIX + "db-config.update-strategy", "NOT_NULL"));
        configuration.setDbConfigInsertStrategy(getStringValue(KEY_PREFIX + "db-config.insert-strategy", "NOT_NULL"));
        configuration.setDbConfigWhereStrategy(getStringValue(KEY_PREFIX + "db-config.where-strategy", "NOT_NULL"));

        // pagination
        configuration.setPaginationMaxLimit(getIntegerValue(KEY_PREFIX + "pagination.max-limit", -1));
        configuration.setPaginationOverflow(getBooleanValue(KEY_PREFIX + "pagination.overflow", false));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
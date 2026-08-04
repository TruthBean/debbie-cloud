/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tomcat.jdbc;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class TomcatJdbcProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<TomcatJdbcConfiguration> {

    private final Map<String, Map<String, TomcatJdbcConfiguration>> map = new HashMap<>();
    private TomcatJdbcConfiguration configuration;

    //=================================================================================================================
    /**
     * key name is snake case
     */
    private static final String TOMCAT_JDBC_X_KEY_PREFIX = "debbie.datasource.tomcat-jdbc.x.";
    private static final int TOMCAT_JDBC_X_KEY_PREFIX_LENGTH = 37;
    //=================================================================================================================

    public TomcatJdbcProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, TomcatJdbcConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return map;
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        getConfiguration(applicationContext);
        return map.containsKey(profile) && map.get(profile).containsKey(category);
    }

    @Override
    public Set<String> getCategories(String profile) {
        return map.getOrDefault(profile, new HashMap<>()).keySet();
    }

    @Override
    public TomcatJdbcConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public TomcatJdbcConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        var beanFactory = new DebbieReflectionBeanFactory<>(TomcatJdbcConfiguration.class, new TomcatJdbcConfiguration(applicationContext));
        configuration = beanFactory.factoryBean(applicationContext);

        final Map<String, String> matchedKey = getMatchedKey(TOMCAT_JDBC_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(TOMCAT_JDBC_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getPoolProperties().setDbProperties(mergeProperty(configuration.getPoolProperties().getDbProperties(), k, value));
        });
        Map<String, TomcatJdbcConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(DEFAULT_CATEGORY, configuration);
        map.put(DEFAULT_PROFILE, configurationMap);
        return configuration;
    }

    private java.util.Properties mergeProperty(java.util.Properties properties, String key, String value) {
        if (properties == null) {
            properties = new java.util.Properties();
        }
        properties.setProperty(key, value);
        return properties;
    }

    @Override
    public void close() {
        map.forEach((k, m) -> m.clear());
        map.clear();
        configuration = null;
    }
}
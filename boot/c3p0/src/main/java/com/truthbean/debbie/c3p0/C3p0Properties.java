/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.c3p0;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class C3p0Properties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<C3p0Configuration> {

    private final Map<String, Map<String, C3p0Configuration>> map = new HashMap<>();
    private C3p0Configuration configuration;

    //=================================================================================================================
    /**
     * https://www.mchange.com/projects/c3p0/#configuration_properties
     *
     * key name is snake case
     */
    private static final String C3P0_X_KEY_PREFIX = "debbie.datasource.c3p0.x.";
    private static final int C3P0_X_KEY_PREFIX_LENGTH = 25;
    //=================================================================================================================

    public C3p0Properties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, C3p0Configuration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public C3p0Configuration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public C3p0Configuration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        var beanFactory = new DebbieReflectionBeanFactory<>(C3p0Configuration.class, new C3p0Configuration(applicationContext));
        configuration = beanFactory.factoryBean(applicationContext);

        final Map<String, String> matchedKey = getMatchedKey(C3P0_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(C3P0_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            var dataSource = configuration.getComboPooledDataSource();
            Properties properties = dataSource.getProperties();
            if (properties == null) {
                properties = new Properties();
            }
            properties.setProperty(k, value);
            dataSource.setProperties(properties);
        });
        Map<String, C3p0Configuration> configurationMap = new HashMap<>();
        configurationMap.put(DEFAULT_CATEGORY, configuration);
        map.put(DEFAULT_PROFILE, configurationMap);
        return configuration;
    }

    @Override
    public void close() {
        map.forEach((k, m) -> m.clear());
        map.clear();
        configuration = null;
    }
}

/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.dbcp2;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.6.3
 */
public class Dbcp2Properties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<Dbcp2Configuration> {

    private final Map<String, Map<String, Dbcp2Configuration>> map = new HashMap<>();
    private Dbcp2Configuration configuration;

    private static final String DBCP2_X_KEY_PREFIX = "debbie.datasource.dbcp2.x.";
    private static final int DBCP2_X_KEY_PREFIX_LENGTH = 28;

    public Dbcp2Properties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, Dbcp2Configuration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public Dbcp2Configuration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public Dbcp2Configuration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        var beanFactory = new DebbieReflectionBeanFactory<>(Dbcp2Configuration.class, new Dbcp2Configuration(applicationContext));
        configuration = beanFactory.factoryBean(applicationContext);

        final Map<String, String> matchedKey = getMatchedKey(DBCP2_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(DBCP2_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getBasicDataSource().addConnectionProperty(k, value);
        });
        Map<String, Dbcp2Configuration> configurationMap = new HashMap<>();
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
/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.hikari;

import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.common.mini.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 22:46.
 */
public class HikariProperties extends EnvironmentContentHolder implements DebbieProperties<HikariConfiguration> {

    private final Map<String, HikariConfiguration> map = new HashMap<>();
    private HikariConfiguration configuration;

    //=================================================================================================================
    /**
     * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md
     *
     *  key name is snake case
     */
    private static final String HIKARI_X_KEY_PREFIX = "debbie.datasource.hikari.x.";
    private static final int HIKARI_X_KEY_PREFIX_LENGTH = 27;
    //=================================================================================================================

    public HikariProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public HikariConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (StringUtils.hasText(name)) {
            return map.get(name);
        }
        return map.get(DEFAULT_PROFILE);
    }

    @Override
    public HikariConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        var beanFactory = new DebbieReflectionBeanFactory<>(HikariConfiguration.class, new HikariConfiguration());
        configuration = beanFactory.factoryBean(applicationContext);

        final Map<String, String> matchedKey = getMatchedKey(HIKARI_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(HIKARI_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getHikariConfig().addDataSourceProperty(k, value);
        });
        map.put(DEFAULT_PROFILE, configuration);
        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}

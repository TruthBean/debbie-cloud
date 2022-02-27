/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mybatis.configuration;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.*;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MybatisProperties extends EnvironmentContentHolder implements DebbieProperties<MybatisConfiguration> {

    public static final String ENABLE_KEY = "debbie.mybatis.enable";
    //===========================================================================
    private static final String MYBATIS_CONFIG_XML_LOCATION = "debbie.mybatis.config-xml-location";
    private static final String MYBATIS_ENVIRONMENT = "debbie.mybatis.environment";
    private static final String MYBATIS_PROPERTIES = "debbie.mybatis.properties.";
    private static final String MYBATIS_MAPPER_LOCATIONS = "debbie.mybatis.mapper-locations";
    //===========================================================================

    private final Map<String, MybatisConfiguration> map = new HashMap<>();
    private final MybatisConfiguration configuration;
    private static MybatisProperties instance;

    public MybatisProperties(ApplicationContext context) {
        configuration = new MybatisConfiguration();

        configuration.setMybatisConfigXmlLocation(getValue(MYBATIS_CONFIG_XML_LOCATION));
        configuration.setEnvironment(getStringValue(MYBATIS_ENVIRONMENT, "default"));

        MyBatisConfigurationSettings settings = context.getGlobalBeanFactory().factory(MyBatisConfigurationSettings.class);
        configuration.setSettings(settings);

        configuration.setConfigurationProperties(getMatchedKey(MYBATIS_PROPERTIES));

        List<String> list = getStringListValue(MYBATIS_MAPPER_LOCATIONS, ";");
        ResourceResolver resourceResolver = context.getResourceResolver();
        resolveMapperLocations(list, resourceResolver);
        map.put(DEFAULT_PROFILE, configuration);
    }

    private void resolveMapperLocations(List<String> patternList, ResourceResolver resourceResolver) {
        List<String> list = new ArrayList<>();
        if (patternList != null && !patternList.isEmpty()) {
            for (String pattern : patternList) {
                list.addAll(resourceResolver.getMatchedResources(pattern));
            }
        }
        configuration.setMapperLocations(list);
    }

    public static MybatisConfiguration toConfiguration(ApplicationContext context) {
        if (instance == null) {
            instance = new MybatisProperties(context);
        }
        return instance.configuration;
    }

    public MybatisConfiguration loadConfiguration() {
        return configuration;
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public MybatisConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        return map.get(name);
    }

    @Override
    public void close() throws Exception {
        map.clear();
        instance = null;
    }
}

/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MybatisProperties extends EnvironmentContentHolder {

    public static final String ENABLE_KEY = "debbie.mybatis.enable";
    //===========================================================================
    private static final String MYBATIS_CONFIG_XML_LOCATION = "debbie.mybatis.config-xml-location";
    private static final String MYBATIS_ENVIRONMENT = "debbie.mybatis.environment";
    private static final String MYBATIS_PROPERTIES = "debbie.mybatis.properties.";
    private static final String MYBATIS_MAPPER_LOCATIONS = "debbie.mybatis.mapper-locations";
    //===========================================================================

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
}

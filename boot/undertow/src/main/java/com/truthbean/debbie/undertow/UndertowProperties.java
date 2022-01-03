/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.server.BaseServerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/12 23:58.
 */
public class UndertowProperties extends BaseServerProperties<UndertowConfiguration> {
    private final Map<String, UndertowConfiguration> map = new HashMap<>();
    private UndertowConfiguration configuration;

    public static final String ENABLE_KEY = "debbie.undertow.enable";

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public UndertowConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (map.isEmpty() && DEFAULT_PROFILE.equals(name)) {
            return getConfiguration(applicationContext);
        }
        return map.get(name);
    }

    @Override
    public UndertowConfiguration getConfiguration(ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        ClassLoader classLoader = applicationContext.getClassLoader();
        configuration = new UndertowConfiguration(classLoader);

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);

        MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        UndertowProperties properties = new UndertowProperties();
        properties.loadAndSet(properties, configuration);

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() throws Exception {
        map.clear();
        configuration = null;
    }
}

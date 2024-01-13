/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet;

import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 17:39.
 */
public class ServletProperties extends EnvironmentContentHolder implements DebbieProperties<ServletConfiguration> {

    //========================================================================================

    //========================================================================================

    private final Map<String, ServletConfiguration> map = new HashMap<>();
    private static ServletConfiguration configuration;
    public static ServletConfiguration toConfiguration(ClassLoader classLoader) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ServletConfiguration(classLoader);

        MvcConfiguration webConfiguration = MvcProperties.toConfiguration(classLoader);
        configuration.copyFrom(webConfiguration);

        BeanScanConfiguration beanScanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
        configuration.copyFrom(beanScanConfiguration);

        return configuration;
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public ServletConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (DEFAULT_PROFILE.equals(name) || !StringUtils.hasText(name)) {
            return getConfiguration(applicationContext);
        }
        return map.get(name);
    }

    @Override
    public ServletConfiguration getConfiguration(ApplicationContext applicationContext) {
        ClassLoader classLoader = applicationContext.getClassLoader();
        return toConfiguration(classLoader);
    }

    @Override
    public void close() throws Exception {
        map.clear();
        configuration = null;
    }
}

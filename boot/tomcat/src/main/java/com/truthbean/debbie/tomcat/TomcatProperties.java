/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tomcat;

import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.server.BaseServerProperties;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 14:36.
 */
public class TomcatProperties extends BaseServerProperties<TomcatConfiguration> {

    public static final String ENABLE_KEY = "debbie.tomcat.enable";
    /**
     * The class name of default protocol used.
     */
    public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    //===========================================================================
    private static final String TOMCAT_WEBAPP = "debbie.server.tomcat.webapp";
    private static final String TOMCAT_WEBAPP_CLASSPATH = "debbie.server.tomcat.webapp.classpath";
    private static final String DISABLE_MBEAN_REGISTRY = "debbie.server.tomcat.disable-mbean-registry";
    private static final String AUTO_DEPLOY = "debbie.server.tomcat.autoDeploy";
    private static final String TOMCAT_CONNECTOR_PROTOCOL = "debbie.server.tomcat.connector.protocol";
    private static final String TOMCAT_URI_ENCODING = "debbie.server.tomcat.uri-encoding";

    private static final String TOMCAT_RESOURCES_CACHING_ALLOWED = "debbie.server.tomcat.resources.caching-allowed";
    private static final String TOMCAT_RESOURCES_MAX_CACHE = "debbie.server.tomcat.resources.max-cache";
    //===========================================================================

    private final Map<String, TomcatConfiguration> map = new HashMap<>();
    private TomcatConfiguration configuration;

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public TomcatConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (DEFAULT_PROFILE.equals(name) || !StringUtils.hasText(name)) {
            return getConfiguration(applicationContext);
        }
        return map.get(name);
    }

    @Override
    public TomcatConfiguration getConfiguration(ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        var classLoader = applicationContext.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoaderUtils.getDefaultClassLoader();
        }

        TomcatProperties properties = new TomcatProperties();
        configuration = new TomcatConfiguration(classLoader);

        properties.loadAndSet(properties, configuration);

        String userDir;
        URL userDirUrl = classLoader.getResource("");
        if (userDirUrl != null) {
            userDir = userDirUrl.getPath();
        } else {
            userDir = System.getProperty("user.dir");
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("user.dir: " + userDir);
        var webappPath = classLoader.getResource("webapp");
        if (webappPath == null) {
            userDir = userDir + "/../webapp";
        } else {
            userDir = webappPath.getPath();
        }
        configuration.setWebappDir(properties.getStringValue(TOMCAT_WEBAPP, userDir));
        // configuration.setWebappClasspath(properties.getStringValue(TOMCAT_WEBAPP_CLASSPATH, userDir + "/WEB-INF/classes"));
        configuration.setWebappClasspath(properties.getValue(TOMCAT_WEBAPP_CLASSPATH));

        configuration.setDisableMBeanRegistry(properties.getBooleanValue(DISABLE_MBEAN_REGISTRY, false));
        configuration.setAutoDeploy(properties.getBooleanValue(AUTO_DEPLOY,false));

        configuration.setConnectorProtocol(properties.getStringValue(TOMCAT_CONNECTOR_PROTOCOL, DEFAULT_PROTOCOL));
        configuration.setUriEncoding(properties.getCharsetValue(TOMCAT_URI_ENCODING, StandardCharsets.UTF_8));

        configuration.setCachingAllowed(properties.getBooleanValue(TOMCAT_RESOURCES_CACHING_ALLOWED, true));
        configuration.setCacheMaxSize(properties.getIntegerValue(TOMCAT_RESOURCES_MAX_CACHE, 102400));

        map.put(DEFAULT_PROTOCOL, configuration);

        return configuration;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatProperties.class);

    @Override
    public void close() throws Exception {
        map.clear();
        configuration = null;
    }
}

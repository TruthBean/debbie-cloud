/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.swagger;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.mvc.router.RouterPathSplicer;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class SwaggerModuleStarter implements DebbieModuleStarter {

    @Override
    public boolean enable(EnvironmentContent envContent) {
        return envContent.getBooleanValue(DebbieSwaggerProperties.ENABLE_KEY, true);
    }

    @Override
    public void configure(ApplicationContext context) {
        DebbieSwaggerConfiguration configuration = new DebbieSwaggerConfiguration();
        configuration.configure(context);
    }

    @Override
    public int getOrder() {
        return 52;
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        EnvironmentContent envContent = applicationContext.getEnvContent();
        String dispatcherMapping = envContent.getStringValue("debbie.web.dispatcher-mapping", "/**");
        String path = RouterPathSplicer.replaceDispatcherMapping(dispatcherMapping, "swagger-ui");
        String host = envContent.getStringValue("debbie.server.host", "localhost");
        String port = envContent.getStringValue("debbie.server.port", "8080");
        LOGGER.info("swagger page: http://" + host + ":/" + port + "/" + path);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerModuleStarter.class);
}
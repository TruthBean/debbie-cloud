/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 *    Debbie is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.swagger;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.ArrayList;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieSwaggerConfiguration {

    public void configure(ApplicationContext context) {
        OpenAPI oas = new OpenAPI();

        BeanInfoManager beanInitialization = context.getBeanInfoManager();
        beanInitialization.register(DebbieSwaggerRouter.class);

        GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();

        DebbieSwaggerProperties properties = new DebbieSwaggerProperties();
        Info info = globalBeanFactory.factoryIfPresentOrElse(Info.class, properties::getInfo);
        oas.info(info);

        Server server = globalBeanFactory.factoryIfPresentOrElse(Server.class, properties::getServer);
        var servers = new ArrayList<Server>();
        servers.add(server);
        oas.servers(servers);

        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true);

        OpenAPI result;
        try {
            result = new GenericOpenApiContextBuilder<>()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true)
                    .read();

        } catch (OpenApiConfigurationException e) {
            LOGGER.error("", e);

            result = oas;
        }

        var beanFactory = new SimpleBeanFactory<>(result, OpenAPI.class, BeanProxyType.NO, "openApi");
        beanInitialization.register(beanFactory);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieSwaggerConfiguration.class);
}

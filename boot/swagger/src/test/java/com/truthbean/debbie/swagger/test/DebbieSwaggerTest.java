/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 *    Debbie is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.swagger.test;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.swagger.SwaggerReader;
import com.truthbean.debbie.util.JacksonUtils;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@DebbieBootApplication
public class DebbieSwaggerTest {
    public static void main(String[] args) {
        ApplicationFactory factory = ApplicationFactory.configure(DebbieSwaggerTest.class);
        ApplicationContext context = factory.getApplicationContext();

        DebbieConfigurationCenter configurationFactory = context.getConfigurationCenter();
        MvcConfiguration configuration = configurationFactory.factory(MvcConfiguration.class, context);

        BeanInitialization beanInitialization = context.getBeanInitialization();

        MvcRouterRegister.registerRouter(configuration, context);

        OpenAPI oas = new OpenAPI();
        Info info = beanInitialization.getRegisterBean(Info.class);

        oas.info(info);
        oas.servers(List.of(new Server().url("http://localhost:8090").description("debbie swagger example")));

        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true);

        try {
            OpenAPI openAPI = new GenericOpenApiContextBuilder()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true)
                    .read();
            var reader = new SwaggerReader(openAPI, context.getClassLoader());
            OpenAPI newOpenApi = reader.read();
            System.out.println(newOpenApi);
            System.out.println(JacksonUtils.toYaml(newOpenApi));

        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
}

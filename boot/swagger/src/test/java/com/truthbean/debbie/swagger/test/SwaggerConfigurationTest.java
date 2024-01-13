/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 *    Debbie is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.swagger.test;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jackson.util.JacksonUtils;
import com.truthbean.debbie.swagger.SwaggerReader;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@DebbieApplicationTest
public class SwaggerConfigurationTest {

    @Test
    public void content(@BeanInject Info info) {
        OpenAPI oas = new OpenAPI();

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
            var reader = new SwaggerReader(openAPI, SwaggerConfigurationTest.class.getClassLoader());
            OpenAPI newOpenApi = reader.read();
            System.out.println(newOpenApi);
            System.out.println(JacksonUtils.toYaml(newOpenApi));

        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

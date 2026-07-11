/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 *    Debbie is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.AbstractRestResponseHandler;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import com.truthbean.debbie.mvc.router.CustomizeMvcRouterRegister;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.mvc.router.RouterPathSplicer;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

public class DebbieSwaggerRouter implements ApplicationContextAware, CustomizeMvcRouterRegister {

    private String dispatcherMapping;

    private OpenAPI openApi;

    private SwaggerConfiguration swaggerConfiguration;

    private String swagger;
    private final String prefix = "classpath*:/swagger-ui/3.37.0/";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        dispatcherMapping = applicationContext.getDefaultEnvironment().getStringValue("debbie.web.dispatcher-mapping", "**");
        openApi = applicationContext.getGlobalBeanFactory().factory("openApi", OpenAPI.class, false);
        swaggerConfiguration = applicationContext.getGlobalBeanFactory().factory(null, SwaggerConfiguration.class, false);
    }

    public void setDispatcherMapping(String dispatcherMapping) {
        this.dispatcherMapping = dispatcherMapping;
    }

    public void setOpenApi(OpenAPI openApi) {
        this.openApi = openApi;
    }

    public void setSwaggerConfiguration(SwaggerConfiguration swaggerConfiguration) {
        this.swaggerConfiguration = swaggerConfiguration;
    }

    @Override
    public void registerMvcRegister(MvcRouterRegister mvcRouterRegister) {
        mvcRouterRegister
                .all("/swagger", this::swagger)
                .all("/swagger-css", this::swaggerUiCss)
                .all("/favicon-32x32", this::favicon32)
                .all("/favicon-16x16", this::favicon16)
                .all("/swagger-ui-bundle", this::swaggerUiBundle)
                .all("/swagger-ui-bundle-map", this::swaggerUiBundleMap)
                .all("/swagger-ui-standalone-preset", this::swaggerUiStandaloneBundle)
                .all("/swagger-ui-standalone-preset-map", this::swaggerUiStandaloneBundleMap)
                .all("/swagger-ui", this::swaggerUiHtml)
                ;
    }

    public void swagger(RouterRequest request, RouterResponse response) {
        if (swagger == null) {
            SwaggerReader reader;
            var classLoader = DebbieSwaggerRouter.class.getClassLoader();
            if (swaggerConfiguration != null) {
                reader = new SwaggerReader(swaggerConfiguration, classLoader);
            } else if (openApi != null) {
                reader = new SwaggerReader(openApi, classLoader);
            } else {
                reader = new SwaggerReader(classLoader);
            }
            OpenAPI newOpenApi = reader.read();
            YAMLMapper yamlMapper = new YAMLMapper();
            yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            yamlMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            yamlMapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
            yamlMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
            try {
                swagger = yamlMapper.writeValueAsString(newOpenApi);
            } catch (JsonProcessingException e) {
                LOG.error("[Swagger] parse OpenAPI error. ", e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setContent(e.getMessage());
            }
        }
        response.setResponseType(MediaType.TEXT_PLAIN_UTF8);
        response.setContent(swagger);
    }

    public void swaggerUiCss(RouterRequest request, RouterResponse response) {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui");
        view.setSuffix(".css");
        view.setText(true);
        var content = view.render();
        response.setHandler(new AbstractRestResponseHandler<String>() {
            @Override
            public MediaTypeInfo getResponseType() {
                return MediaType.TEXT_CSS_UTF8.info();
            }

            @Override
            public String transform(String s) {
                return content.toString();
            }
        });
        response.setContent(content);
        response.setTemplatePrefix(prefix);
        response.setTemplateSuffix(".css");
        response.setHasTemplate(true);
        response.setResponseType(MediaType.TEXT_CSS_UTF8);
    }

    public void favicon32(RouterRequest request, RouterResponse response) {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("favicon-32x32");
        view.setSuffix(".png");
        view.setText(false);
        var content = view.render();
        response.setHandler(new AbstractRestResponseHandler<String>() {
            @Override
            public MediaTypeInfo getResponseType() {
                return MediaType.IMAGE_PNG.info();
            }

            @Override
            public String transform(String s) {
                return content.toString();
            }
        });
        response.setContent(content);
        response.setTemplatePrefix(prefix);
        response.setTemplateSuffix(".png");
        response.setHasTemplate(true);
        response.setResponseType(MediaType.IMAGE_PNG);
    }

    public void favicon16(RouterRequest request, RouterResponse response) {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("favicon-16x16");
        view.setSuffix(".png");
        view.setText(false);
        var content = view.render();
        response.setHandler(new AbstractRestResponseHandler<String>() {
            @Override
            public MediaTypeInfo getResponseType() {
                return MediaType.IMAGE_PNG.info();
            }

            @Override
            public String transform(String s) {
                return content.toString();
            }
        });
        response.setContent(content);
        response.setTemplatePrefix(prefix);
        response.setTemplateSuffix(".png");
        response.setHasTemplate(true);
        response.setResponseType(MediaType.IMAGE_PNG);
    }

    public void swaggerUiBundle(RouterRequest request, RouterResponse response) {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui-bundle");
        view.setSuffix(".js");
        view.setText(true);
        var content = view.render();
        response.setHandler(new AbstractRestResponseHandler<String>() {
            @Override
            public MediaTypeInfo getResponseType() {
                return MediaType.APPLICATION_JAVASCRIPT_UTF8.info();
            }

            @Override
            public String transform(String s) {
                return content.toString();
            }
        });
        response.setContent(content);
        response.setTemplatePrefix(prefix);
        response.setTemplateSuffix(".js");
        response.setHasTemplate(true);
        response.setResponseType(MediaType.APPLICATION_JAVASCRIPT_UTF8);
    }

    public void swaggerUiBundleMap(RouterRequest request, RouterResponse response) {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui-bundle");
        view.setSuffix(".js.map");
        view.setText(true);
        var content = view.render();
        response.setHandler(new AbstractRestResponseHandler<String>() {
            @Override
            public MediaTypeInfo getResponseType() {
                return MediaType.APPLICATION_JAVASCRIPT_UTF8.info();
            }

            @Override
            public String transform(String s) {
                return content.toString();
            }
        });
        response.setContent(content);
        response.setTemplatePrefix(prefix);
        response.setTemplateSuffix(".js.map");
        response.setHasTemplate(true);
        response.setResponseType(MediaType.APPLICATION_JAVASCRIPT_UTF8);
    }

    public void swaggerUiStandaloneBundle(RouterRequest request, RouterResponse response) {
        var view = new StaticResourcesView();
        view.setPrefix("classpath*:/swagger-ui/3.37.0/");
        view.setTemplate("swagger-ui-standalone-preset");
        view.setSuffix(".js");
        view.setText(true);
        var content = view.render();
        response.setHandler(new AbstractRestResponseHandler<String>() {
            @Override
            public MediaTypeInfo getResponseType() {
                return MediaType.APPLICATION_JAVASCRIPT_UTF8.info();
            }

            @Override
            public String transform(String s) {
                return content.toString();
            }
        });
        response.setContent(content);
        response.setTemplatePrefix(prefix);
        response.setTemplateSuffix(".js");
        response.setHasTemplate(true);
        response.setResponseType(MediaType.APPLICATION_JAVASCRIPT_UTF8);
    }

    public void swaggerUiStandaloneBundleMap(RouterRequest request, RouterResponse response) {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui-standalone-preset");
        view.setSuffix(".js.map");
        view.setText(true);
        var content = view.render();
        response.setHandler(new AbstractRestResponseHandler<String>() {
            @Override
            public MediaTypeInfo getResponseType() {
                return MediaType.APPLICATION_JAVASCRIPT_UTF8.info();
            }

            @Override
            public String transform(String s) {
                return content.toString();
            }
        });
        response.setContent(content);
        response.setTemplatePrefix(prefix);
        response.setTemplateSuffix(".js.map");
        response.setHasTemplate(true);
        response.setResponseType(MediaType.APPLICATION_JAVASCRIPT_UTF8);
    }

    public void swaggerUiHtml(RouterRequest request, RouterResponse response) {
        String content = "<!-- HTML for static distribution bundle build -->\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Swagger UI</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"/" + RouterPathSplicer.replaceDispatcherMapping(dispatcherMapping, "swagger-css") + "\" >\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"/" + RouterPathSplicer.replaceDispatcherMapping(dispatcherMapping, "favicon-32x32") + "\" sizes=\"32x32\" />\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"/" + RouterPathSplicer.replaceDispatcherMapping(dispatcherMapping, "favicon-16x16") + "\" sizes=\"16x16\" />\n" +
                "    <style>\n" +
                "      html\n" +
                "      {\n" +
                "        box-sizing: border-box;\n" +
                "        overflow: -moz-scrollbars-vertical;\n" +
                "        overflow-y: scroll;\n" +
                "      }\n" +
                "\n" +
                "      *,\n" +
                "      *:before,\n" +
                "      *:after\n" +
                "      {\n" +
                "        box-sizing: inherit;\n" +
                "      }\n" +
                "\n" +
                "      body\n" +
                "      {\n" +
                "        margin:0;\n" +
                "        background: #fafafa;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "\n" +
                "  <body>\n" +
                "    <div id=\"swagger-ui\"></div>\n" +
                "\n" +
                "    <script src=\"./" + dispatcherMapping.replace("**", "swagger-ui-bundle") + "\"> </script>\n" +
                "    <script src=\"./" + dispatcherMapping.replace("**", "swagger-ui-standalone-preset") + "\"> </script>\n" +
                "    <script>\n" +
                "      console.info(\"From Debbie Framework (http://www.truthbean.com/debbie) by TruthBean/Rogar·Q .\"); \n" +
                "      window.onload = function() {\n" +
                "      // Begin Swagger UI call region\n" +
                "      const ui = SwaggerUIBundle({\n" +
                "        url: \"/" + RouterPathSplicer.replaceDispatcherMapping(dispatcherMapping, "swagger") + "\",\n" +
                "        dom_id: '#swagger-ui',\n" +
                "        deepLinking: true,\n" +
                "        presets: [\n" +
                "          SwaggerUIBundle.presets.apis,\n" +
                "          SwaggerUIStandalonePreset\n" +
                "        ],\n" +
                "        plugins: [\n" +
                "          SwaggerUIBundle.plugins.DownloadUrl\n" +
                "        ],\n" +
                "        layout: \"StandaloneLayout\"\n" +
                "        })\n" +
                "        // End Swagger UI call region\n" +
                "        window.ui = ui\n" +
                "      }\n" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>\n";
        response.setContent(content);
        response.setHasTemplate(false);
        response.setResponseType(MediaType.TEXT_HTML_UTF8);
    }

    private static final Logger LOG = LoggerFactory.getLogger(DebbieSwaggerRouter.class);
}

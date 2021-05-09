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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.mvc.router.RouterPathSplicer;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.watcher.Watcher;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

@Watcher
@Router
public class DebbieSwaggerRouter {

    @PropertyInject(value = "debbie.web.dispatcher-mapping", defaultValue = "**")
    private String dispatcherMapping;

    @BeanInject(require = false, name = "openApi")
    private OpenAPI openApi;

    @BeanInject(require = false)
    private SwaggerConfiguration swaggerConfiguration;

    private String swagger;
    private final String prefix = "classpath*:/swagger-ui/3.37.0/";

    public void setDispatcherMapping(String dispatcherMapping) {
        this.dispatcherMapping = dispatcherMapping;
    }

    public void setOpenApi(OpenAPI openApi) {
        this.openApi = openApi;
    }

    public void setSwaggerConfiguration(SwaggerConfiguration swaggerConfiguration) {
        this.swaggerConfiguration = swaggerConfiguration;
    }

    @Router(value = "swagger", responseType = MediaType.TEXT_PLAIN_UTF8)
    public String swagger() throws JsonProcessingException {
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
            yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            swagger = yamlMapper.writeValueAsString(newOpenApi);
        }
        return swagger;
    }

    @Router(urlPatterns = "/swagger-css", hasTemplate = true, responseType = MediaType.TEXT_CSS_UTF8)
    public StaticResourcesView swaggerUiCss() {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui");
        view.setSuffix(".css");
        view.setText(true);
        return view;
    }

    @Router(urlPatterns = "/favicon-32x32", hasTemplate = true, responseType = MediaType.IMAGE_PNG)
    public StaticResourcesView favicon32() {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("favicon-32x32");
        view.setSuffix(".png");
        view.setText(false);
        return view;
    }

    @Router(urlPatterns = "/favicon-16x16", hasTemplate = true, responseType = MediaType.IMAGE_PNG)
    public StaticResourcesView favicon16() {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("favicon-16x16");
        view.setSuffix(".png");
        view.setText(false);
        return view;
    }

    @Router(urlPatterns = "swagger-ui-bundle", hasTemplate = true, responseType = MediaType.APPLICATION_JAVASCRIPT_UTF8)
    public StaticResourcesView swaggerUiBundle() {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui-bundle");
        view.setSuffix(".js");
        view.setText(true);
        return view;
    }

    @Router(urlPatterns = "swagger-ui-bundle-map", hasTemplate = true, responseType = MediaType.APPLICATION_JAVASCRIPT_UTF8)
    public StaticResourcesView swaggerUiBundleMap() {
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui-bundle");
        view.setSuffix(".js.map");
        view.setText(true);
        return view;
    }

    @Router(urlPatterns = "swagger-ui-standalone-preset", hasTemplate = true, responseType = MediaType.APPLICATION_JAVASCRIPT_UTF8)
    public StaticResourcesView swaggerUiStandaloneBundle() {
        var view = new StaticResourcesView();
        view.setPrefix("classpath*:/swagger-ui/3.37.0/");
        view.setTemplate("swagger-ui-standalone-preset");
        view.setSuffix(".js");
        view.setText(true);
        return view;
    }

    @Router(urlPatterns = "swagger-ui-standalone-preset-map", hasTemplate = true, responseType = MediaType.APPLICATION_JAVASCRIPT_UTF8)
    public StaticResourcesView swaggerUiStandaloneBundleMap(){
        var view = new StaticResourcesView();
        view.setPrefix(prefix);
        view.setTemplate("swagger-ui-standalone-preset");
        view.setSuffix(".js.map");
        view.setText(true);
        return view;
    }

    @Router(value = "swagger-ui", responseType = MediaType.TEXT_HTML_UTF8)
    public String swaggerUiHtml() throws JsonProcessingException {
        return "<!-- HTML for static distribution bundle build -->\n" +
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
    }

}

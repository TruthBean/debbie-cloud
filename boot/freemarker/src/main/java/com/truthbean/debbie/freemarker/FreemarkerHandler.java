/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.freemarker;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler;
import freemarker.template.Configuration;
import freemarker.template.Template;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class FreemarkerHandler extends AbstractTemplateViewHandler<Object, String> {

    private Configuration configuration;
    private MvcConfiguration mvcConfiguration;

    @Override
    public String transform(Object s) {
        configure();
        if (s instanceof String) {
            String string = (String) s;
            try {
                Template template = configuration.getTemplate(string + mvcConfiguration.getTemplateSuffix());
                return FreeMarkerTemplateUtils.processTemplateIntoString(template, null);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        } else if (s instanceof FreemarkerTemplateView) {
            ((FreemarkerTemplateView) s).setConfiguration(configuration);
            return ((FreemarkerTemplateView) s).render();
        }

        return s.toString();
    }

    @Override
    public void handleResponse(RouterResponse response, Object s) {
        configure();
        if (s instanceof String) {
            String string = (String) s;
            try {
                Template template = configuration.getTemplate(string + mvcConfiguration.getTemplateSuffix());
                String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, response.getModelAttributes());
                response.setContent(content);
            } catch (Exception e) {
                LOGGER.error("", e);
                response.setContent(e.getMessage());
            }
        } else if (s instanceof FreemarkerTemplateView) {
            ((FreemarkerTemplateView) s).setConfiguration(configuration);
            response.setContent(((FreemarkerTemplateView) s).render());
        }
    }

    private void configure() {
        ApplicationContext context = getApplicationContext();
        if (mvcConfiguration == null) {
            mvcConfiguration = MvcProperties.toConfiguration(context.getClassLoader());
        }
        if (configuration == null) {
            configuration = context.getGlobalBeanFactory().factory("freemarkerConfiguration");
        }
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.TEXT_HTML_UTF8.info();
    }

    @Override
    public String reverse(String o) {
        return o;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerHandler.class);
}

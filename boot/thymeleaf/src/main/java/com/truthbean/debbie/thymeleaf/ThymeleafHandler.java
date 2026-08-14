/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.thymeleaf;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ThymeleafHandler extends AbstractTemplateViewHandler<Object, String> {

    private TemplateEngine templateEngine;
    private MvcConfiguration mvcConfiguration;

    @Override
    public String transform(Object s) {
        configure();
        if (s instanceof String string) {
            try {
                Context context = new Context();
                return templateEngine.process(string + mvcConfiguration.getTemplateSuffix(), context);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        } else if (s instanceof ThymeleafTemplateView view) {
            view.setTemplateEngine(templateEngine);
            return view.render();
        }

        return s.toString();
    }

    @Override
    public void handleResponse(RouterResponse response, Object s) {
        configure();
        if (s instanceof String string) {
            try {
                Context context = new Context();
                context.setVariables(response.getModelAttributes());
                String content = templateEngine.process(string + mvcConfiguration.getTemplateSuffix(), context);
                response.setContent(content);
            } catch (Exception e) {
                LOGGER.error("", e);
                response.setContent(e.getMessage());
            }
        } else if (s instanceof ThymeleafTemplateView view) {
            view.setTemplateEngine(templateEngine);
            response.setContent(view.render());
        }
    }

    private void configure() {
        ApplicationContext context = getApplicationContext();
        GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
        if (mvcConfiguration == null) {
            mvcConfiguration = globalBeanFactory.factory(MvcConfiguration.class);
        }
        if (templateEngine == null) {
            templateEngine = globalBeanFactory.factory("thymeleafTemplateEngine");
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafHandler.class);
}
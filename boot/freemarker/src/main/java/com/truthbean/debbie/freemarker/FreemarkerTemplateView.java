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

import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.IOException;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class FreemarkerTemplateView extends AbstractTemplateView {
    private Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String render() {
        String tempResource = getTemplate() + getSuffix();
        try {
            Template template = configuration.getTemplate(tempResource);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, getAttributes());
        } catch (IOException | TemplateException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerTemplateView.class);
}

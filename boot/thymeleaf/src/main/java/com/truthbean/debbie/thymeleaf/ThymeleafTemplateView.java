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
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ThymeleafTemplateView extends AbstractTemplateView {
    private TemplateEngine templateEngine;

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String render() {
        String tempResource = getTemplate() + getSuffix();
        try {
            Context context = new Context();
            context.setVariables(getAttributes());
            return templateEngine.process(tempResource, context);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafTemplateView.class);
}
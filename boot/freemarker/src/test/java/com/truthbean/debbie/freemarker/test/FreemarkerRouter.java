/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.freemarker.test;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.freemarker.FreemarkerHandler;
import com.truthbean.debbie.freemarker.FreemarkerTemplateView;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.Router;
import freemarker.template.Configuration;

@Router
public class FreemarkerRouter {

    @BeanInject
    private Configuration freemarkerConfiguration;

    @Router(value = "/freemarker", hasTemplate = true, handlerClass = FreemarkerHandler.class)
    public FreemarkerTemplateView freemarker() {
        FreemarkerTemplateView view = new FreemarkerTemplateView();
        view.setAttribute("username", "test");
        view.setConfiguration(freemarkerConfiguration);
        view.setTemplate("test01");
        view.setSuffix(".ftl");
        return view;
    }

    @Router(value = "/freemarker2", hasTemplate = true, handlerClass = FreemarkerHandler.class)
    public String freemarker2(RouterResponse response,
                              @RequestParameter(paramType = RequestParameterType.QUERY, name = "name", require = false) String name) {
        response.addModelAttribute("username", name);
        return "test01";
    }
}

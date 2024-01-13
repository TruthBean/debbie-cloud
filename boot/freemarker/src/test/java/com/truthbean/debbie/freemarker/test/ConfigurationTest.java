/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.freemarker.test;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-04 19:58
 */
@DebbieApplicationTest
public class ConfigurationTest {

    @Test
    void test(@BeanInject Configuration configuration) {
        System.out.println(configuration);
    }

    @Test
    public void testFreemarker() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        ClassTemplateLoader templateLoader = new ClassTemplateLoader(ConfigurationTest.class, "/templates/");

        configuration.setTemplateLoader(templateLoader);

        Map<String, Object> data = new HashMap<>();
        data.put("username", "test");
        Template template = configuration.getTemplate("test01.ftl");

        StringWriter stringWriter = new StringWriter();
        template.process(data, stringWriter);
        System.out.println(stringWriter.toString());
    }
}

/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.spring.check;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.spring.EnableDebbieApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-01-29 23:17
 */
@ComponentScan(basePackages = "com.truthbean", includeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TruthBeanTypeFilter.class)
})
@EnableDebbieApplication
public class DebbieSpringTest {
    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "false");
        System.setProperty("logging.level.com.truthbean", "debug");
        System.setProperty("logging.level.org.springframework", "debug");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(DebbieSpringTest.class);
        String applicationName = applicationContext.getApplicationName();
        System.out.println(applicationName);
        TestSpringBean bean = applicationContext.getBean(TestSpringBean.class);
        System.out.println(bean);
        bean.setTest("123");
        bean = applicationContext.getBean(TestSpringBean.class);
        System.out.println(bean);
        applicationContext.close();
    }
}

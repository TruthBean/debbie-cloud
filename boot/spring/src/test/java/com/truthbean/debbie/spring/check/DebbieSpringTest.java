/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.spring.check;

import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.spring.EnableDebbieApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
// @SpringBootApplication
@EnableDebbieApplication(scan = @DebbieScan(basePackages = "com.truthbean"))
public class DebbieSpringTest {
    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "false");
        System.setProperty("debbie.spring.enable", "false");
        System.setProperty("logging.level.root", "info");
        System.setProperty("logging.level.com.truthbean", "debug");
        System.setProperty("logging.level.org.springframework", "debug");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringApplicationTest.class, args); //new AnnotationConfigApplicationContext(DebbieSpringTest.class);
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

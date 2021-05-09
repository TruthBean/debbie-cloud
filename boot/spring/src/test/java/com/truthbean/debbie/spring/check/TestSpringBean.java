/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.spring.check;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-01-29 23:11
 */
@Component
// @Scope("prototype")
public class TestSpringBean implements InitializingBean
        // , DisposableBean
        , AutoCloseable {

    private String test;

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public TestSpringBean() {
        LOGGER.info("constructor...");
    }

    // @Override
    public void destroy() throws Exception {
        LOGGER.info("destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("after constructor");
    }

    @Override
    public void close() throws Exception {
        LOGGER.info("close....");
    }

    @Override
    public String toString() {
        return "\"TestSpringBean\":{" +
                "\"test\":\"" + test + "\"" + "}";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSpringBean.class);
}

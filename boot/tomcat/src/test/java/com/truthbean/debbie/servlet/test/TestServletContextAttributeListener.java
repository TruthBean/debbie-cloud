/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.test;

import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.annotation.WebListener;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-01-29 23:00
 */
@WebListener
public class TestServletContextAttributeListener implements ServletContextAttributeListener {
    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {

    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {

    }
}

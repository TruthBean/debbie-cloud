/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.internal.ApplicationContextHolder;
import com.truthbean.debbie.internal.DebbieApplicationFactory;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-01-07 22:30.
 */
@HandlesTypes(value = {Watcher.class, Router.class})
public class ServletApplicationInitializer implements ServletContainerInitializer {
    public ServletApplicationInitializer() {
        if (ApplicationContextHolder.getApplicationContext() == null) {
            LOGGER.debug("run servlet module without application");
        }
    }

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        LOGGER.info("ServletContainerInitializer onStartup ...");
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        ApplicationFactory applicationFactory = null;
        boolean runWithWar = false;
        if (applicationContext == null) {
            applicationFactory = ApplicationFactory.newEmpty().preInit(ServletApplicationInitializer.class).init().config(ServletApplicationInitializer.class).create();
            applicationContext = ApplicationContextHolder.getApplicationContext();
            runWithWar = true;
        }
        var handler = new ServletContextHandler(ctx, applicationContext);
        handler.registerRouter();
        handler.registerFilter(ctx);

        // if run with a war package
        if (runWithWar && applicationFactory instanceof DebbieApplicationFactory) {
            ((DebbieApplicationFactory)applicationFactory).postCallStarter(applicationFactory.factory());
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletApplicationInitializer.class);
}

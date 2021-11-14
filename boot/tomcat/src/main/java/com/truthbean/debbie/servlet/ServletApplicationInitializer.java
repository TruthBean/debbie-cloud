/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.AbstractApplicationFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.internal.DebbieApplicationFactory;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.watcher.Watcher;
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
public class ServletApplicationInitializer extends DebbieApplicationFactory implements ServletContainerInitializer {

    private final ApplicationContext applicationContext;

    public ServletApplicationInitializer() {
        LOGGER.info("ServletApplicationInitializer init ...");
        if (debbieApplication == null) {
            debbieApplication = super.preInit().init(ServletApplicationInitializer.class).config().create().postCreate().build().factory();
            LOGGER.debug("run servlet module without application");
            applicationContext = this.getApplicationContext();
            super.config(ServletApplicationInitializer.class);
            super.callStarter();
        } else {
            applicationContext = debbieApplication.getApplicationContext();
        }
    }

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        LOGGER.info("ServletContainerInitializer onStartup ...");
        var handler = new ServletContextHandler(ctx, this.applicationContext);
        handler.registerRouter();
        handler.registerFilter(ctx);

        // if run with war package
        if (debbieApplication == null)
            super.postCallStarter();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletApplicationInitializer.class);
}

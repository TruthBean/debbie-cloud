/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.server.AbstractWebServerApplication;
import com.truthbean.debbie.undertow.handler.DispatcherHttpHandler;
import com.truthbean.debbie.undertow.handler.HttpHandlerFilter;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.time.Instant;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public final class UndertowServerApplication extends AbstractWebServerApplication {

    private Undertow server;
    private UndertowConfiguration configuration;

    @Override
    public boolean isWeb() {
        return true;
    }

    @Override
    public DebbieApplication init(ApplicationContext applicationContext, ClassLoader classLoader) {
        this.configuration = applicationContext.getGlobalBeanFactory().factory(UndertowConfiguration.class);
        if (this.configuration == null) {
            LOGGER.warn("debbie-undertow module is disabled, debbie.undertow.enable is false");
            return null;
        }
        MvcConfiguration mvcConfiguration = applicationContext.getGlobalBeanFactory().factory(MvcConfiguration.class);
        if (this.configuration == null) {
            LOGGER.warn("debbie-undertow module is disabled, debbie.undertow.enable is false");
            return null;
        }
        BeanInfoManager beanInitialization = applicationContext.getBeanInfoManager();
        MvcRouterRegister.registerRouter(mvcConfiguration, applicationContext);
        RouterFilterManager.registerFilter(mvcConfiguration, beanInitialization);
        RouterFilterManager.registerCharacterEncodingFilter(mvcConfiguration, "/**");
        RouterFilterManager.registerCorsFilter(mvcConfiguration, "/**");
        RouterFilterManager.registerCsrfFilter(mvcConfiguration, "/**");
        RouterFilterManager.registerSecurityFilter(mvcConfiguration, "/**");

        SessionManager sessionManager = new InMemorySessionManager(configuration.getName());
        SessionCookieConfig sessionConfig = new SessionCookieConfig();
        /*
         * Use the sessionAttachmentHandler to add the sessionManager and
         * sessionCofing to the exchange of every request
         */
        SessionAttachmentHandler sessionAttachmentHandler = new SessionAttachmentHandler(sessionManager, sessionConfig);

        // reverse order to fix the chain order
        List<RouterFilterInfo> filters = RouterFilterManager.getReverseOrderFilters();
        HttpHandler next = new DispatcherHttpHandler(configuration, applicationContext);
        for (RouterFilterInfo filter : filters) {
            next = new HttpHandlerFilter(next, filter, applicationContext, configuration);
        }

        // set as next handler your root handler
        sessionAttachmentHandler.setNext(next);

        // Undertow builder
        server = Undertow.builder()
                // Listener binding
                .addHttpListener(configuration.getPort(), configuration.getHost())
                // Default Handler
                .setHandler(sessionAttachmentHandler).build();

        super.setLogger(LOGGER);

        return this;
    }

    @Override
    public void start(Instant beforeStartTime, ApplicationArgs args) {
        server.start();
        printlnWebUrl(LOGGER, configuration.getPort());
        super.printStartTime();
        postBeforeStart();
    }

    @Override
    public void exit(Instant beforeStartTime, ApplicationArgs args) {
        server.stop();
        super.printExitTime();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowServerApplication.class);

}

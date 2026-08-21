/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.cloud.gateway;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GatewayModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.gateway.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        DebbieReflectionBeanFactory<GatewayConfiguration> debbieBeanInfo =
                new DebbieReflectionBeanFactory<>(GatewayConfiguration.class);
        debbieBeanInfo.addBeanName("gatewayConfiguration", GatewayConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(debbieBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        GatewayConfiguration configuration = factory.factory(GatewayConfiguration.class);

        // Create route locator
        GatewayRouteLocator routeLocator = new GatewayRouteLocator();
        var routeLocatorFactory = new SimpleBeanFactory<>(routeLocator, GatewayRouteLocator.class);
        beanInfoManager.registerBeanInfo(routeLocatorFactory);

        // Create proxy handler
        GatewayProxyHandler proxyHandler = new GatewayProxyHandler(configuration);
        var proxyHandlerFactory = new SimpleBeanFactory<>(proxyHandler, GatewayProxyHandler.class);
        beanInfoManager.registerBeanInfo(proxyHandlerFactory);

        // Create and register gateway router filter
        GatewayRouterFilter gatewayFilter = new GatewayRouterFilter(routeLocator, proxyHandler);
        RouterFilterInfo filterInfo = new RouterFilterInfo();
        filterInfo.setRouterFilterType(GatewayRouterFilter.class);
        filterInfo.setFilterInstance(gatewayFilter);
        filterInfo.setName("gatewayFilter");
        filterInfo.setOrder(-10);
        RouterFilterManager.registerFilter(filterInfo, "/**");
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 1010003;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
    }
}
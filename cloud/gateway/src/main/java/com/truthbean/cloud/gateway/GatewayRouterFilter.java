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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

/**
 * Gateway router filter that intercepts requests and proxies them to backend services.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GatewayRouterFilter implements RouterFilter {

    private final GatewayRouteLocator routeLocator;
    private final GatewayProxyHandler proxyHandler;

    public GatewayRouterFilter(GatewayRouteLocator routeLocator, GatewayProxyHandler proxyHandler) {
        this.routeLocator = routeLocator;
        this.proxyHandler = proxyHandler;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        GatewayRoute route = routeLocator.locate(request);
        if (route == null) {
            return true; // No matching route, continue to normal MVC routing
        }

        LOGGER.debug("Gateway matched route [{}] for {} {}", route.getId(), request.getMethod(), request.getUrl());

        // Apply pre-filters
        GatewayFilterChain filterChain = new GatewayFilterChain();
        for (GatewayFilter filter : route.getFilters()) {
            filterChain.addFilter(filter);
        }
        if (!filterChain.applyPreFilters(request, response, route)) {
            return false; // Pre-filter aborted
        }

        // Proxy the request
        proxyHandler.proxy(request, response, route);

        // Apply post-filters
        filterChain.applyPostFilters(request, response, route);

        return false; // Don't continue to MVC routing
    }

    @Override
    public Boolean postRouter(RouterRequest request, RouterResponse response) {
        return true;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayRouterFilter.class);
}
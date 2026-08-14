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

import com.truthbean.debbie.mvc.request.RouterRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Locates the matching gateway route for an incoming request.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GatewayRouteLocator {

    private final List<GatewayRoute> routes = new ArrayList<>();

    public GatewayRouteLocator() {
    }

    public void addRoute(GatewayRoute route) {
        routes.add(route);
        routes.sort(Comparator.comparingInt(GatewayRoute::getOrder));
    }

    /**
     * Find the first route that matches the request.
     *
     * @param request the incoming request
     * @return the matching route, or null if no route matches
     */
    public GatewayRoute locate(RouterRequest request) {
        for (GatewayRoute route : routes) {
            if (matches(route, request)) {
                return route;
            }
        }
        return null;
    }

    private boolean matches(GatewayRoute route, RouterRequest request) {
        if (route.getPredicates().isEmpty()) {
            return false;
        }
        for (GatewayPredicate predicate : route.getPredicates()) {
            if (!predicate.test(request)) {
                return false;
            }
        }
        return true;
    }

    public List<GatewayRoute> getRoutes() {
        return new ArrayList<>(routes);
    }
}
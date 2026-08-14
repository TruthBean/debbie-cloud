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
import com.truthbean.debbie.mvc.response.RouterResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Chain of gateway filters.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GatewayFilterChain {

    private final List<GatewayFilter> filters = new ArrayList<>();

    public void addFilter(GatewayFilter filter) {
        filters.add(filter);
        filters.sort(Comparator.comparingInt(GatewayFilter::getOrder));
    }

    /**
     * Execute all pre-filters. Returns false if any filter aborts.
     */
    public boolean applyPreFilters(RouterRequest request, RouterResponse response, GatewayRoute route) {
        for (GatewayFilter filter : filters) {
            if (!filter.preFilter(request, response, route)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Execute all post-filters in reverse order.
     */
    public void applyPostFilters(RouterRequest request, RouterResponse response, GatewayRoute route) {
        for (int i = filters.size() - 1; i >= 0; i--) {
            filters.get(i).postFilter(request, response, route);
        }
    }

    public List<GatewayFilter> getFilters() {
        return new ArrayList<>(filters);
    }
}
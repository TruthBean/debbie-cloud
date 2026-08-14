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

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway route definition.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class GatewayRoute {

    private String id;
    private String uri;
    private List<GatewayPredicate> predicates = new ArrayList<>();
    private List<GatewayFilter> filters = new ArrayList<>();
    private int order = 0;

    public GatewayRoute() {
    }

    public GatewayRoute(String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public String getId() { return id; }
    public GatewayRoute setId(String id) { this.id = id; return this; }

    public String getUri() { return uri; }
    public GatewayRoute setUri(String uri) { this.uri = uri; return this; }

    public List<GatewayPredicate> getPredicates() { return predicates; }
    public GatewayRoute setPredicates(List<GatewayPredicate> predicates) { this.predicates = predicates; return this; }
    public GatewayRoute addPredicate(GatewayPredicate predicate) { this.predicates.add(predicate); return this; }

    public List<GatewayFilter> getFilters() { return filters; }
    public GatewayRoute setFilters(List<GatewayFilter> filters) { this.filters = filters; return this; }
    public GatewayRoute addFilter(GatewayFilter filter) { this.filters.add(filter); return this; }

    public int getOrder() { return order; }
    public GatewayRoute setOrder(int order) { this.order = order; return this; }

    @Override
    public String toString() {
        return "GatewayRoute{id='" + id + "', uri='" + uri + "', order=" + order + '}';
    }
}
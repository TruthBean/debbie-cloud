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

/**
 * Removes a prefix from the request path before proxying.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class StripPrefixFilter implements GatewayFilter {

    private final int parts;
    private final int order;

    public StripPrefixFilter(int parts) {
        this(parts, 0);
    }

    public StripPrefixFilter(int parts, int order) {
        this.parts = parts;
        this.order = order;
    }

    @Override
    public boolean preFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
        // The strip prefix transformation is handled by the proxy handler
        // when building the target URI
        return true;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public int getParts() {
        return parts;
    }
}
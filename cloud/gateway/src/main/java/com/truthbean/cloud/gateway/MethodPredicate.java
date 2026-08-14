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

/**
 * HTTP method-based predicate.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MethodPredicate implements GatewayPredicate {

    private final String method;

    public MethodPredicate(String method) {
        this.method = method.toUpperCase();
    }

    @Override
    public boolean test(RouterRequest request) {
        return method.equalsIgnoreCase(request.getMethod().name());
    }

    @Override
    public String toString() {
        return "MethodPredicate{method=" + method + '}';
    }
}
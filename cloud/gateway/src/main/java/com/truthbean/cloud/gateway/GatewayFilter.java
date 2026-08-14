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
 * Gateway filter for pre/post request processing.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface GatewayFilter {

    /**
     * Process the request before proxying to backend.
     *
     * @param request  the incoming request
     * @param response the response (may be modified)
     * @param route    the matched route
     * @return true to continue the chain, false to abort
     */
    default boolean preFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
        return true;
    }

    /**
     * Process the response after proxying from backend.
     *
     * @param request  the incoming request
     * @param response the response from backend
     * @param route    the matched route
     */
    default void postFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
    }

    /**
     * The order of this filter. Lower values have higher priority.
     */
    default int getOrder() {
        return 0;
    }
}
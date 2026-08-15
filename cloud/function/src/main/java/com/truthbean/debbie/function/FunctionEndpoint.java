/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.function.binding.HttpFunctionBinding;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.router.GetRouter;
import com.truthbean.debbie.mvc.router.PostRouter;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.mvc.response.provider.JsonResponseHandler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP endpoint that exposes registered functions as REST routes.
 * <p>
 * Routes (prefix configurable via {@code debbie.function.http.prefix}):
 * <ul>
 *   <li>{@code GET  /{prefix}} — list all registered function names</li>
 *   <li>{@code GET  /{prefix}/{name}} — invoke a supplier by name</li>
 *   <li>{@code POST /{prefix}/{name}} — invoke a function/consumer with request body</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@Router
public class FunctionEndpoint {

    private final HttpFunctionBinding binding;
    private final String prefix;

    public FunctionEndpoint(HttpFunctionBinding binding, String prefix) {
        this.binding = binding;
        this.prefix = prefix;
    }

    @GetRouter(value = {"", "/"},
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Collection<String> listFunctions() {
        var registry = binding.getRegistry();
        if (registry == null) {
            return java.util.List.of();
        }
        return registry.getFunctionNames();
    }

    @GetRouter(value = "/{name}",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Object invokeSupplier(String name) {
        try {
            var result = binding.invokeSupplier(name);
            LOGGER.debug(() -> "invoked supplier [" + name + "] via HTTP GET");
            return result;
        } catch (FunctionException e) {
            LOGGER.warn("failed to invoke supplier [" + name + "]: " + e.getMessage());
            return errorResponse(name, e.getMessage());
        }
    }

    @PostRouter(value = "/{name}",
            responseType = MediaType.APPLICATION_JSON_UTF8,
            handlerClass = JsonResponseHandler.class)
    public Object invokeFunction(String name, Object body) {
        try {
            var result = binding.invoke(name, body);
            LOGGER.debug(() -> "invoked function [" + name + "] via HTTP POST");
            return result;
        } catch (FunctionException e) {
            LOGGER.warn("failed to invoke function [" + name + "]: " + e.getMessage());
            return errorResponse(name, e.getMessage());
        }
    }

    private Map<String, Object> errorResponse(String functionName, String error) {
        var map = new LinkedHashMap<String, Object>();
        map.put("function", functionName);
        map.put("error", error);
        return map;
    }

    public String getPrefix() {
        return prefix;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionEndpoint.class);
}
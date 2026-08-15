/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function.routing;

import com.truthbean.debbie.function.FunctionException;
import com.truthbean.debbie.function.FunctionMessage;
import com.truthbean.debbie.function.FunctionRegistry;
import com.truthbean.debbie.function.FunctionWrapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Routes {@link FunctionMessage} instances to the appropriate function
 * based on the {@code function.name} header or a custom routing function.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FunctionRouter {

    private final FunctionRegistry registry;
    private Function<FunctionMessage<?>, String> routingFunction;
    private final Map<String, String> routingRules = new LinkedHashMap<>();

    public FunctionRouter(FunctionRegistry registry) {
        this.registry = registry;
    }

    public FunctionRouter withRoutingFunction(Function<FunctionMessage<?>, String> routingFunction) {
        this.routingFunction = routingFunction;
        return this;
    }

    public FunctionRouter addRoutingRule(String condition, String functionName) {
        routingRules.put(condition, functionName);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <I, O> FunctionMessage<O> route(FunctionMessage<I> message) {
        String functionName = resolveFunctionName(message);
        if (functionName == null) {
            throw new FunctionException("could not resolve function name for message: " + message);
        }

        var wrapper = (FunctionWrapper<I, O>) registry.lookup(functionName);
        if (wrapper == null) {
            throw new FunctionException("function [" + functionName + "] not found in registry");
        }

        O result = wrapper.invoke(message.getPayload());
        var response = message.withPayload(result);
        response.setFunctionName(functionName);
        return response;
    }

    @SuppressWarnings("unchecked")
    public <I, O> O routeAndApply(String functionName, I input) {
        var wrapper = (FunctionWrapper<I, O>) registry.lookup(functionName);
        if (wrapper == null) {
            throw new FunctionException("function [" + functionName + "] not found in registry");
        }
        return wrapper.invoke(input);
    }

    public <I, O> O route(FunctionMessage<I> message, String defaultFunction) {
        String name = resolveFunctionName(message);
        if (name == null) {
            name = defaultFunction;
        }
        return routeAndApply(name, message.getPayload());
    }

    private String resolveFunctionName(FunctionMessage<?> message) {
        if (routingFunction != null) {
            return routingFunction.apply(message);
        }

        var name = message.getFunctionName();
        if (name != null) {
            return name;
        }

        for (var entry : routingRules.entrySet()) {
            var header = message.getHeader(entry.getKey());
            if (header != null && String.valueOf(header).equals(entry.getValue())) {
                var condition = entry.getKey();
                return routingRules.get(condition);
            }
        }

        return null;
    }
}
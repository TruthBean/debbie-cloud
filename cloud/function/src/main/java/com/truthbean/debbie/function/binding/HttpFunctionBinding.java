/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function.binding;

import com.truthbean.debbie.function.FunctionException;
import com.truthbean.debbie.function.FunctionMessage;
import com.truthbean.debbie.function.FunctionRegistry;
import com.truthbean.debbie.function.FunctionWrapper;

/**
 * Default {@link FunctionBinding} implementation that invokes functions
 * directly from the registry. Used as the HTTP binding backend.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class HttpFunctionBinding implements FunctionBinding {

    private volatile FunctionRegistry registry;

    @Override
    public String getName() {
        return "http";
    }

    @Override
    public void bind(FunctionRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I, O> O invoke(String functionName, I input) {
        ensureBound();
        var wrapper = (FunctionWrapper<I, O>) registry.lookup(functionName);
        if (wrapper == null) {
            throw new FunctionException("function [" + functionName + "] not found");
        }
        return wrapper.invoke(input);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <O> O invokeSupplier(String functionName) {
        ensureBound();
        var wrapper = (FunctionWrapper<Void, O>) (FunctionWrapper<?, ?>) registry.lookup(functionName);
        if (wrapper == null) {
            throw new FunctionException("supplier [" + functionName + "] not found");
        }
        return wrapper.invoke();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I, O> FunctionMessage<O> process(FunctionMessage<I> message) {
        ensureBound();
        var name = message.getFunctionName();
        if (name == null) {
            throw new FunctionException("message has no function name header");
        }
        var wrapper = (FunctionWrapper<I, O>) registry.lookup(name);
        if (wrapper == null) {
            throw new FunctionException("function [" + name + "] not found");
        }
        O result = wrapper.invoke(message.getPayload());
        return message.withPayload(result);
    }

    @Override
    public void unbind() {
        registry = null;
    }

    public boolean isBound() {
        return registry != null;
    }

    public FunctionRegistry getRegistry() {
        return registry;
    }

    private void ensureBound() {
        if (registry == null) {
            throw new FunctionException("binding is not bound to a registry");
        }
    }
}
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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A thread-safe registry that manages named {@link FunctionWrapper} instances.
 * <p>
 * Functions can be registered as {@link Function}, {@link Consumer}, or
 * {@link Supplier} and looked up by name.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FunctionRegistry {

    private final Map<String, FunctionWrapper<?, ?>> functions = new ConcurrentHashMap<>();

    public <I, O> FunctionWrapper<I, O> register(String name, Function<I, O> function) {
        var wrapper = FunctionWrapper.ofFunction(name, function);
        functions.put(name, wrapper);
        return wrapper;
    }

    public <I> FunctionWrapper<I, Void> registerConsumer(String name, Consumer<I> consumer) {
        var wrapper = FunctionWrapper.ofConsumer(name, consumer);
        functions.put(name, wrapper);
        return wrapper;
    }

    public <O> FunctionWrapper<Void, O> registerSupplier(String name, Supplier<O> supplier) {
        var wrapper = FunctionWrapper.ofSupplier(name, supplier);
        functions.put(name, wrapper);
        return wrapper;
    }

    public <I, O> FunctionWrapper<I, O> register(FunctionWrapper<I, O> wrapper) {
        functions.put(wrapper.getName(), wrapper);
        return wrapper;
    }

    @SuppressWarnings("unchecked")
    public <I, O> FunctionWrapper<I, O> lookup(String name) {
        return (FunctionWrapper<I, O>) functions.get(name);
    }

    public boolean contains(String name) {
        return functions.containsKey(name);
    }

    public Collection<String> getFunctionNames() {
        return functions.keySet();
    }

    public Collection<FunctionWrapper<?, ?>> getAll() {
        return functions.values();
    }

    public int size() {
        return functions.size();
    }

    public FunctionWrapper<?, ?> remove(String name) {
        return functions.remove(name);
    }

    public void clear() {
        functions.clear();
    }

    @SuppressWarnings("unchecked")
    public <I, O> O invoke(String name, I input) {
        var wrapper = (FunctionWrapper<I, O>) functions.get(name);
        if (wrapper == null) {
            throw new FunctionException("function [" + name + "] not found in registry");
        }
        return wrapper.invoke(input);
    }

    @SuppressWarnings("unchecked")
    public <O> O invokeSupplier(String name) {
        var wrapper = (FunctionWrapper<Void, O>) functions.get(name);
        if (wrapper == null) {
            throw new FunctionException("supplier [" + name + "] not found in registry");
        }
        return wrapper.invoke();
    }
}
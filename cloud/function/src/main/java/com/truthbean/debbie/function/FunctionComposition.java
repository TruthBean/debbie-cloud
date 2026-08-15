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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Composes multiple named functions from a {@link FunctionRegistry} into a
 * single pipeline.
 * <p>
 * Composition expression syntax: {@code fn1|fn2|fn3} — the output of
 * each function feeds into the next.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FunctionComposition {

    private final FunctionRegistry registry;

    public FunctionComposition(FunctionRegistry registry) {
        this.registry = registry;
    }

    public List<String> parse(String expression) {
        if (expression == null || expression.isBlank()) {
            return List.of();
        }
        var parts = expression.split("\\|");
        var names = new ArrayList<String>();
        for (var part : parts) {
            var trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                names.add(trimmed);
            }
        }
        return names;
    }

    @SuppressWarnings("unchecked")
    public <I, O> O compose(String expression, I input) {
        var names = parse(expression);
        if (names.isEmpty()) {
            throw new FunctionException("empty composition expression");
        }
        Object current = input;
        for (var name : names) {
            var wrapper = registry.lookup(name);
            if (wrapper == null) {
                throw new FunctionException("function [" + name + "] not found in registry");
            }
            current = wrapper.invoke(current);
        }
        return (O) current;
    }

    @SuppressWarnings("unchecked")
    public <I, O> Function<I, O> composeAsFunction(String expression) {
        var names = parse(expression);
        if (names.isEmpty()) {
            throw new FunctionException("empty composition expression");
        }

        Function<Object, Object> result = null;
        for (var name : names) {
            var wrapper = registry.lookup(name);
            if (wrapper == null) {
                throw new FunctionException("function [" + name + "] not found in registry");
            }
            var fn = (Function<Object, Object>) wrapper.asFunction();
            result = result == null ? fn : result.andThen(fn);
        }
        return (Function<I, O>) result;
    }

    @SuppressWarnings("unchecked")
    public <I, O> FunctionWrapper<I, O> composeAsWrapper(String composedName, String expression) {
        Function<I, O> fn = (Function<I, O>) (Function<?, ?>) composeAsFunction(expression);
        return FunctionWrapper.ofFunction(composedName, fn);
    }

    public String buildExpression(String... names) {
        return String.join("|", Arrays.asList(names));
    }
}
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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Unified wrapper around {@link Function}, {@link Consumer} and {@link Supplier}.
 * <p>
 * This allows the {@link FunctionRegistry} to treat all three uniformly.
 *
 * @param <I> input type (Void for Supplier)
 * @param <O> output type (Void for Consumer)
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class FunctionWrapper<I, O> {

    public enum FunctionType {
        FUNCTION,
        CONSUMER,
        SUPPLIER
    }

    private final String name;
    private final FunctionType type;
    private final Function<I, O> function;
    private final Consumer<I> consumer;
    private final Supplier<O> supplier;

    private FunctionWrapper(String name, FunctionType type,
                            Function<I, O> function, Consumer<I> consumer, Supplier<O> supplier) {
        this.name = name;
        this.type = type;
        this.function = function;
        this.consumer = consumer;
        this.supplier = supplier;
    }

    public static <I, O> FunctionWrapper<I, O> ofFunction(String name, Function<I, O> fn) {
        return new FunctionWrapper<>(name, FunctionType.FUNCTION, fn, null, null);
    }

    public static <I> FunctionWrapper<I, Void> ofConsumer(String name, Consumer<I> cn) {
        return new FunctionWrapper<>(name, FunctionType.CONSUMER, null, cn, null);
    }

    public static <O> FunctionWrapper<Void, O> ofSupplier(String name, Supplier<O> sp) {
        return new FunctionWrapper<>(name, FunctionType.SUPPLIER, null, null, sp);
    }

    public String getName() {
        return name;
    }

    public FunctionType getType() {
        return type;
    }

    public boolean isFunction() {
        return type == FunctionType.FUNCTION;
    }

    public boolean isConsumer() {
        return type == FunctionType.CONSUMER;
    }

    public boolean isSupplier() {
        return type == FunctionType.SUPPLIER;
    }

    @SuppressWarnings("unchecked")
    public O invoke(I input) {
        return switch (type) {
            case FUNCTION -> function.apply(input);
            case CONSUMER -> {
                consumer.accept(input);
                yield null;
            }
            case SUPPLIER -> supplier.get();
        };
    }

    public O invoke() {
        if (type != FunctionType.SUPPLIER) {
            throw new FunctionException("invoke() can only be called on supplier [" + name + "]");
        }
        return supplier.get();
    }

    @SuppressWarnings("unchecked")
    public <R> FunctionWrapper<I, R> andThen(Function<O, R> after) {
        if (type == FunctionType.FUNCTION) {
            return ofFunction(name, function.andThen(after));
        }
        if (type == FunctionType.SUPPLIER) {
            return (FunctionWrapper<I, R>) ofSupplier(name, () -> after.apply(supplier.get()));
        }
        throw new FunctionException("cannot compose consumer [" + name + "] with andThen");
    }

    @SuppressWarnings("unchecked")
    public <R> FunctionWrapper<R, O> compose(Function<R, I> before) {
        if (type == FunctionType.FUNCTION) {
            return ofFunction(name, function.compose(before));
        }
        if (type == FunctionType.CONSUMER) {
            return (FunctionWrapper<R, O>) ofConsumer(name, (R r) -> consumer.accept(before.apply(r)));
        }
        throw new FunctionException("cannot compose supplier [" + name + "] with compose");
    }

    public Function<I, O> asFunction() {
        return switch (type) {
            case FUNCTION -> function;
            case CONSUMER -> i -> {
                consumer.accept(i);
                return null;
            };
            case SUPPLIER -> i -> supplier.get();
        };
    }

    @Override
    public String toString() {
        return "FunctionWrapper{name='" + name + "', type=" + type + '}';
    }
}
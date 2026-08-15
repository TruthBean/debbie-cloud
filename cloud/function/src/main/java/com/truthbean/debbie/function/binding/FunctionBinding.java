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
 * Binding that exposes registered functions as invokable endpoints.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface FunctionBinding {

    /**
     * @return the name of this binding (e.g. "http", "kafka", "rabbitmq")
     */
    String getName();

    /**
     * Bind the registry to this binding.
     *
     * @param registry the function registry
     */
    void bind(FunctionRegistry registry);

    /**
     * Invoke a registered function by name with the given input.
     *
     * @param functionName the function name
     * @param input        the input
     * @param <I>          input type
     * @param <O>          output type
     * @return the result
     */
    <I, O> O invoke(String functionName, I input);

    /**
     * Invoke a registered supplier by name.
     *
     * @param functionName the supplier name
     * @param <O>          output type
     * @return the result
     */
    <O> O invokeSupplier(String functionName);

    /**
     * Process a function message through the binding.
     *
     * @param message the message
     * @param <I>     input type
     * @param <O>     output type
     * @return the output message
     */
    <I, O> FunctionMessage<O> process(FunctionMessage<I> message);

    /**
     * Unbind and release resources.
     */
    void unbind();
}
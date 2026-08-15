/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function.test;

import com.truthbean.debbie.function.FunctionComposition;
import com.truthbean.debbie.function.FunctionConfiguration;
import com.truthbean.debbie.function.FunctionException;
import com.truthbean.debbie.function.FunctionMessage;
import com.truthbean.debbie.function.FunctionRegistry;
import com.truthbean.debbie.function.FunctionWrapper;
import com.truthbean.debbie.function.binding.HttpFunctionBinding;
import com.truthbean.debbie.function.routing.FunctionRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FunctionTest {

    private FunctionRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new FunctionRegistry();
    }

    // ---- FunctionWrapper tests ----

    @Test
    public void functionWrapperShouldInvokeFunction() {
        var wrapper = FunctionWrapper.ofFunction("uppercase", (String s) -> s.toUpperCase());
        assertEquals("HELLO", wrapper.invoke("hello"));
        assertTrue(wrapper.isFunction());
        assertFalse(wrapper.isConsumer());
        assertFalse(wrapper.isSupplier());
    }

    @Test
    public void functionWrapperShouldInvokeConsumer() {
        var result = new StringBuilder();
        var wrapper = FunctionWrapper.ofConsumer("printer", (String s) -> result.append(s));
        wrapper.invoke("test");
        assertEquals("test", result.toString());
        assertTrue(wrapper.isConsumer());
    }

    @Test
    public void functionWrapperShouldInvokeSupplier() {
        var wrapper = FunctionWrapper.ofSupplier("timestamp", () -> System.currentTimeMillis());
        var value = wrapper.invoke();
        assertTrue(value > 0);
        assertTrue(wrapper.isSupplier());
    }

    @Test
    public void functionWrapperAndThenShouldCompose() {
        var wrapper = FunctionWrapper.ofFunction("double", (Integer x) -> x * 2);
        var composed = wrapper.andThen((Integer x) -> x + 1);
        assertEquals(11, composed.invoke(5));
    }

    @Test
    public void functionWrapperComposeShouldPrepend() {
        var wrapper = FunctionWrapper.ofFunction("addOne", (Integer x) -> x + 1);
        var composed = wrapper.compose((Integer x) -> x * 2);
        assertEquals(11, composed.invoke(5));
    }

    @Test
    public void functionWrapperAsFunctionShouldConvert() {
        var supplier = FunctionWrapper.ofSupplier("constant", () -> 42);
        var fn = supplier.asFunction();
        assertEquals(42, fn.apply(null));
        assertEquals(42, fn.apply(null));
    }

    // ---- FunctionRegistry tests ----

    @Test
    public void registryShouldRegisterAndLookupFunction() {
        registry.register("uppercase", (String s) -> s.toUpperCase());
        var wrapper = registry.<String, String>lookup("uppercase");
        assertNotNull(wrapper);
        assertEquals("HELLO", wrapper.invoke("hello"));
    }

    @Test
    public void registryShouldRegisterConsumer() {
        var called = new boolean[]{false};
        registry.registerConsumer("log", (String s) -> called[0] = true);
        registry.invoke("log", "test");
        assertTrue(called[0]);
    }

    @Test
    public void registryShouldRegisterSupplier() {
        registry.registerSupplier("answer", () -> 42);
        var result = registry.<Integer>invokeSupplier("answer");
        assertEquals(42, result);
    }

    @Test
    public void registryShouldCheckContains() {
        registry.register("fn1", (Object x) -> x);
        assertTrue(registry.contains("fn1"));
        assertFalse(registry.contains("fn2"));
    }

    @Test
    public void registryShouldListFunctionNames() {
        registry.register("fn1", (Object x) -> x);
        registry.register("fn2", (Object x) -> x);
        registry.registerSupplier("sp1", () -> "hello");
        var names = registry.getFunctionNames();
        assertEquals(3, names.size());
        assertTrue(names.contains("fn1"));
        assertTrue(names.contains("fn2"));
        assertTrue(names.contains("sp1"));
    }

    @Test
    public void registryShouldRemoveFunction() {
        registry.register("fn1", (Object x) -> x);
        registry.remove("fn1");
        assertFalse(registry.contains("fn1"));
    }

    @Test
    public void registryShouldThrowWhenFunctionNotFound() {
        assertThrows(FunctionException.class, () -> registry.invoke("nonexistent", "input"));
    }

    @Test
    public void registryShouldInvokeByName() {
        registry.register("reverse", (String s) -> new StringBuilder(s).reverse().toString());
        assertEquals("olleh", registry.invoke("reverse", "hello"));
    }

    // ---- FunctionMessage tests ----

    @Test
    public void messageShouldCarryPayloadAndHeaders() {
        var msg = FunctionMessage.of("hello").setFunctionName("uppercase");
        assertEquals("hello", msg.getPayload());
        assertEquals("uppercase", msg.getFunctionName());
    }

    @Test
    public void messageShouldAddHeaders() {
        var msg = FunctionMessage.of(42)
                .addHeader("source", "test")
                .addHeader("version", "1.0");
        assertEquals("test", msg.getHeaderAsString("source"));
        assertEquals("1.0", msg.getHeaderAsString("version"));
    }

    @Test
    public void messageWithPayloadShouldPreserveHeaders() {
        var msg = FunctionMessage.of("input").addHeader("key", "value");
        var newMsg = msg.withPayload(42);
        assertEquals(42, newMsg.getPayload());
        assertEquals("value", newMsg.getHeaderAsString("key"));
    }

    @Test
    public void messageOfWithFunctionNameShouldSetHeader() {
        var msg = FunctionMessage.of("data", "myFunction");
        assertEquals("myFunction", msg.getFunctionName());
    }

    // ---- FunctionComposition tests ----

    @Test
    public void compositionShouldParseExpression() {
        var composition = new FunctionComposition(registry);
        var names = composition.parse("fn1|fn2|fn3");
        assertEquals(List.of("fn1", "fn2", "fn3"), names);
    }

    @Test
    public void compositionShouldParseWithSpaces() {
        var composition = new FunctionComposition(registry);
        var names = composition.parse(" fn1 | fn2 | fn3 ");
        assertEquals(List.of("fn1", "fn2", "fn3"), names);
    }

    @Test
    public void compositionShouldChainFunctions() {
        registry.register("uppercase", (String s) -> s.toUpperCase());
        registry.register("reverse", (String s) -> new StringBuilder(s).reverse().toString());

        var composition = new FunctionComposition(registry);
        var result = composition.<String, String>compose("uppercase|reverse", "hello");
        assertEquals("OLLEH", result);
    }

    @Test
    public void compositionShouldCreateComposedFunction() {
        registry.register("double", (Integer x) -> x * 2);
        registry.register("addOne", (Integer x) -> x + 1);

        var composition = new FunctionComposition(registry);
        Function<Integer, Integer> fn = composition.composeAsFunction("double|addOne");
        assertEquals(11, fn.apply(5));
    }

    @Test
    public void compositionShouldCreateWrapper() {
        registry.register("double", (Integer x) -> x * 2);
        registry.register("addOne", (Integer x) -> x + 1);

        var composition = new FunctionComposition(registry);
        var wrapper = composition.composeAsWrapper("pipeline", "double|addOne");
        assertEquals("pipeline", wrapper.getName());
        assertEquals(11, wrapper.invoke(5));
    }

    @Test
    public void compositionShouldThrowOnEmptyExpression() {
        var composition = new FunctionComposition(registry);
        assertThrows(FunctionException.class, () -> composition.compose("", "input"));
    }

    @Test
    public void compositionShouldThrowOnMissingFunction() {
        var composition = new FunctionComposition(registry);
        assertThrows(FunctionException.class, () -> composition.compose("nonexistent", "input"));
    }

    // ---- FunctionRouter tests ----

    @Test
    public void routerShouldRouteByFunctionNameHeader() {
        registry.register("uppercase", (String s) -> s.toUpperCase());
        var router = new FunctionRouter(registry);

        var msg = FunctionMessage.of("hello", "uppercase");
        var result = router.route(msg);
        assertEquals("HELLO", result.getPayload());
    }

    @Test
    public void routerShouldRouteWithCustomRoutingFunction() {
        registry.register("fn1", (String s) -> s + " from fn1");
        registry.register("fn2", (String s) -> s + " from fn2");
        var router = new FunctionRouter(registry)
                .withRoutingFunction(msg -> String.valueOf(msg.getPayload()).length() > 5 ? "fn1" : "fn2");

        var result1 = router.route(FunctionMessage.of("hello world"));
        assertEquals("hello world from fn1", result1.getPayload());

        var result2 = router.route(FunctionMessage.of("hi"));
        assertEquals("hi from fn2", result2.getPayload());
    }

    @Test
    public void routerShouldRouteByRoutingRules() {
        registry.register("processA", (String s) -> "A:" + s);
        registry.register("processB", (String s) -> "B:" + s);
        var router = new FunctionRouter(registry)
                .addRoutingRule("type", "processA")
                .addRoutingRule("category", "processB");

        var msg1 = FunctionMessage.of("data").addHeader("type", "processA");
        var result1 = router.route(msg1);
        assertEquals("A:data", result1.getPayload());
    }

    @Test
    public void routerShouldUseDefaultFunction() {
        registry.register("default", (String s) -> "default:" + s);
        var router = new FunctionRouter(registry);

        var msg = FunctionMessage.of("data");
        var result = router.route(msg, "default");
        assertEquals("default:data", result);
    }

    @Test
    public void routerShouldThrowWhenNameNotResolvable() {
        var router = new FunctionRouter(registry);
        assertThrows(FunctionException.class, () -> router.route(FunctionMessage.of("data")));
    }

    // ---- HttpFunctionBinding tests ----

    @Test
    public void bindingShouldInvokeFunction() {
        registry.register("echo", (String s) -> "echo:" + s);
        var binding = new HttpFunctionBinding();
        binding.bind(registry);

        var result = binding.<String, String>invoke("echo", "hello");
        assertEquals("echo:hello", result);
    }

    @Test
    public void bindingShouldInvokeSupplier() {
        registry.registerSupplier("time", () -> 12345L);
        var binding = new HttpFunctionBinding();
        binding.bind(registry);

        var result = binding.<Long>invokeSupplier("time");
        assertEquals(12345L, result);
    }

    @Test
    public void bindingShouldProcessMessage() {
        registry.register("uppercase", (String s) -> s.toUpperCase());
        var binding = new HttpFunctionBinding();
        binding.bind(registry);

        var msg = FunctionMessage.of("hello", "uppercase");
        var result = binding.<String, String>process(msg);
        assertEquals("HELLO", result.getPayload());
    }

    @Test
    public void bindingShouldThrowWhenNotBound() {
        var binding = new HttpFunctionBinding();
        assertThrows(FunctionException.class, () -> binding.invoke("fn", "input"));
    }

    @Test
    public void bindingShouldUnbind() {
        registry.register("fn", (Object x) -> x);
        var binding = new HttpFunctionBinding();
        binding.bind(registry);
        assertTrue(binding.isBound());
        binding.unbind();
        assertFalse(binding.isBound());
    }

    // ---- Configuration tests ----

    @Test
    public void configurationDefaults() {
        var config = new FunctionConfiguration();
        assertTrue(config.isEnable());
        assertTrue(config.isHttpEnable());
        assertEquals("/function", config.getHttpPrefix());
        assertTrue(config.isRoutingEnable());
        assertTrue(config.isCompositionEnable());
        assertFalse(config.isAutoScan());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new FunctionConfiguration();
        config.setHttpPrefix("/api/fn");
        config.setAutoScan(true);

        var copy = config.<FunctionConfiguration>copy();
        assertEquals("/api/fn", copy.getHttpPrefix());
        assertTrue(copy.isAutoScan());
    }

    // ---- Exception tests ----

    @Test
    public void exceptionShouldStoreMessage() {
        var ex = new FunctionException("test error");
        assertEquals("test error", ex.getMessage());
    }

    @Test
    public void exceptionShouldStoreCause() {
        var cause = new RuntimeException("root cause");
        var ex = new FunctionException("wrapper", cause);
        assertSame(cause, ex.getCause());
    }
}
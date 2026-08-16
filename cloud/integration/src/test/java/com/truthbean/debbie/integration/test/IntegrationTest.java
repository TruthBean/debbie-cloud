/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration.test;

import com.truthbean.debbie.integration.IntegrationConfiguration;
import com.truthbean.debbie.integration.IntegrationFlow;
import com.truthbean.debbie.integration.IntegrationFlowContext;
import com.truthbean.debbie.integration.Message;
import com.truthbean.debbie.integration.MessageChannel;
import com.truthbean.debbie.integration.MessageHandler;
import com.truthbean.debbie.integration.channel.DirectChannel;
import com.truthbean.debbie.integration.channel.PublishSubscribeChannel;
import com.truthbean.debbie.integration.channel.QueueChannel;
import com.truthbean.debbie.integration.endpoint.Aggregator;
import com.truthbean.debbie.integration.endpoint.Filter;
import com.truthbean.debbie.integration.endpoint.Router;
import com.truthbean.debbie.integration.endpoint.ServiceActivator;
import com.truthbean.debbie.integration.endpoint.Splitter;
import com.truthbean.debbie.integration.endpoint.Transformer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class IntegrationTest {

    // ---- Message tests ----

    @Test
    public void messageShouldCreateWithPayload() {
        var msg = Message.of("hello");
        assertNotNull(msg.getPayload());
        assertEquals("hello", msg.getPayload());
        assertNotNull(msg.getId());
        assertNotNull(msg.getHeaders());
    }

    @Test
    public void messageShouldHaveIdAndTimestampHeaders() {
        var msg = Message.of("test");
        assertNotNull(msg.getHeader("id"));
        assertNotNull(msg.getHeader("timestamp"));
        assertEquals(msg.getId(), msg.getHeader("id"));
    }

    @Test
    public void messageShouldSetAndGetHeader() {
        var msg = Message.of("data");
        msg.setHeader("custom", "value");
        assertEquals("value", msg.getHeader("custom"));
        assertEquals("value", msg.getHeader("custom", String.class));
    }

    @Test
    public void messageShouldRemoveHeader() {
        var msg = Message.of("data");
        msg.setHeader("key", "val");
        msg.removeHeader("key");
        assertNull(msg.getHeader("key"));
    }

    @Test
    public void messageHeadersShouldBeUnmodifiable() {
        var msg = Message.of("data");
        var headers = msg.getHeaders();
        assertThrows(UnsupportedOperationException.class, () -> headers.put("x", "y"));
    }

    @Test
    public void messageEqualsShouldUseIdAndPayload() {
        var msg1 = Message.of("hello");
        var msg2 = Message.of("hello");
        assertNotEquals(msg1, msg2);
        assertEquals(msg1, msg1);
    }

    @Test
    public void messageToStringShouldContainId() {
        var msg = Message.of("payload");
        var str = msg.toString();
        assertTrue(str.contains(msg.getId()));
        assertTrue(str.contains("payload"));
    }

    // ---- DirectChannel tests ----

    @Test
    public void directChannelShouldDeliverToSubscriber() {
        var channel = new DirectChannel("test-direct");
        var received = new AtomicReference<Message<?>>();
        channel.subscribe(received::set);

        var msg = Message.of("hello");
        boolean sent = channel.send(msg);

        assertTrue(sent);
        assertEquals(msg, received.get());
    }

    @Test
    public void directChannelShouldReturnFalseWhenNoSubscribers() {
        var channel = new DirectChannel("empty");
        assertFalse(channel.send(Message.of("x")));
    }

    @Test
    public void directChannelShouldDeliverToAllSubscribers() {
        var channel = new DirectChannel("multi");
        var count = new AtomicInteger(0);
        channel.subscribe(msg -> count.incrementAndGet());
        channel.subscribe(msg -> count.incrementAndGet());

        channel.send(Message.of("x"));
        assertEquals(2, count.get());
    }

    @Test
    public void directChannelShouldUnsubscribe() {
        var channel = new DirectChannel("unsub");
        var count = new AtomicInteger(0);
        MessageHandler handler = msg -> count.incrementAndGet();
        channel.subscribe(handler);
        channel.unsubscribe(handler);

        channel.send(Message.of("x"));
        assertEquals(0, count.get());
    }

    @Test
    public void directChannelShouldHaveName() {
        var channel = new DirectChannel("named");
        assertEquals("named", channel.getName());
    }

    @Test
    public void directChannelDefaultNameShouldNotBeNull() {
        var channel = new DirectChannel();
        assertNotNull(channel.getName());
    }

    // ---- QueueChannel tests ----

    @Test
    public void queueChannelShouldBufferMessages() {
        var channel = new QueueChannel("q1", 10);
        channel.send(Message.of("a"));
        channel.send(Message.of("b"));

        assertEquals(2, channel.size());
        assertFalse(channel.isEmpty());
    }

    @Test
    public void queueChannelShouldReceiveMessages() {
        var channel = new QueueChannel("q2", 10);
        channel.send(Message.of("first"));
        channel.send(Message.of("second"));

        var msg1 = channel.receive();
        var msg2 = channel.receive();

        assertEquals("first", msg1.getPayload());
        assertEquals("second", msg2.getPayload());
        assertTrue(channel.isEmpty());
    }

    @Test
    public void queueChannelReceiveEmptyShouldReturnNull() {
        var channel = new QueueChannel("q3");
        assertNull(channel.receive());
    }

    @Test
    public void queueChannelShouldClear() {
        var channel = new QueueChannel("q4", 10);
        channel.send(Message.of("x"));
        channel.clear();
        assertTrue(channel.isEmpty());
    }

    @Test
    public void queueChannelReceiveBlockingShouldWait() throws InterruptedException {
        var channel = new QueueChannel("q5", 10);
        channel.send(Message.of("data"));
        var msg = channel.receiveBlocking();
        assertEquals("data", msg.getPayload());
    }

    @Test
    public void queueChannelReceiveWithTimeoutShouldReturnNullOnTimeout() throws InterruptedException {
        var channel = new QueueChannel("q6", 10);
        var msg = channel.receive(100, TimeUnit.MILLISECONDS);
        assertNull(msg);
    }

    // ---- PublishSubscribeChannel tests ----

    @Test
    public void pubSubChannelShouldDeliverToAllSubscribers() {
        var channel = new PublishSubscribeChannel("pubsub1");
        var count = new AtomicInteger(0);
        channel.subscribe(msg -> count.incrementAndGet());
        channel.subscribe(msg -> count.incrementAndGet());
        channel.subscribe(msg -> count.incrementAndGet());

        channel.send(Message.of("broadcast"));
        assertEquals(3, count.get());
    }

    @Test
    public void pubSubChannelShouldReturnFalseWhenNoSubscribers() {
        var channel = new PublishSubscribeChannel("pubsub2");
        assertFalse(channel.send(Message.of("x")));
    }

    @Test
    public void pubSubChannelShouldUnsubscribe() {
        var channel = new PublishSubscribeChannel("pubsub3");
        var count = new AtomicInteger(0);
        MessageHandler handler = msg -> count.incrementAndGet();
        channel.subscribe(handler);
        channel.unsubscribe(handler);
        channel.send(Message.of("x"));
        assertEquals(0, count.get());
    }

    // ---- Transformer tests ----

    @Test
    public void transformerShouldTransformPayload() {
        var transformer = new Transformer(obj -> String.valueOf(obj).toUpperCase());
        var msg = Message.of("hello");
        var result = transformer.apply(msg);
        assertEquals("HELLO", result.getPayload());
    }

    @Test
    public void transformerShouldPreserveHeaders() {
        var transformer = new Transformer(obj -> obj);
        var msg = Message.of("data");
        msg.setHeader("key", "value");
        var result = transformer.apply(msg);
        assertEquals("value", result.getHeader("key"));
    }

    // ---- Filter tests ----

    @Test
    public void filterShouldPassMatchingMessages() {
        var filter = new Filter(obj -> obj instanceof String s && s.length() > 3);
        assertTrue(filter.test(Message.of("hello")));
    }

    @Test
    public void filterShouldRejectNonMatchingMessages() {
        var filter = new Filter(obj -> obj instanceof String s && s.length() > 3);
        assertFalse(filter.test(Message.of("hi")));
    }

    // ---- Router tests ----

    @Test
    public void routerShouldRouteToCorrectChannel() {
        var channelA = new DirectChannel("a");
        var channelB = new DirectChannel("b");
        var receivedA = new AtomicReference<Message<?>>();
        var receivedB = new AtomicReference<Message<?>>();
        channelA.subscribe(receivedA::set);
        channelB.subscribe(receivedB::set);

        var router = new Router(
                obj -> obj.toString().startsWith("a") ? "a" : "b",
                Map.of("a", channelA, "b", channelB)
        );

        router.route(Message.of("apple"));
        router.route(Message.of("banana"));

        assertEquals("apple", receivedA.get().getPayload());
        assertEquals("banana", receivedB.get().getPayload());
    }

    @Test
    public void routerShouldReturnFalseForUnknownChannel() {
        var router = new Router(
                obj -> "unknown",
                Map.of()
        );
        assertFalse(router.route(Message.of("x")));
    }

    // ---- Splitter tests ----

    @Test
    public void splitterShouldSplitPayload() {
        var splitter = new Splitter(obj -> List.of(obj + "-1", obj + "-2", obj + "-3"));
        var result = splitter.apply(Message.of("part"));
        assertEquals(3, result.size());
    }

    @Test
    public void splitterShouldPreserveHeadersInSplitMessages() {
        var splitter = new Splitter(obj -> List.of(obj, obj));
        var msg = Message.of("x");
        msg.setHeader("trace", "123");
        var result = splitter.apply(msg);
        for (var m : result) {
            assertEquals("123", m.getHeader("trace"));
        }
    }

    // ---- Aggregator tests ----

    @Test
    public void aggregatorShouldCombineMessages() {
        var aggregator = new Aggregator(payloads -> String.join("-", payloads.stream().map(Object::toString).toList()));
        List<Message<?>> messages = List.of(Message.of("a"), Message.of("b"), Message.of("c"));
        var result = aggregator.apply(messages);
        assertEquals("a-b-c", result.getPayload());
    }

    // ---- ServiceActivator tests ----

    @Test
    public void serviceActivatorShouldInvokeHandler() {
        var invoked = new AtomicInteger(0);
        var activator = new ServiceActivator(msg -> invoked.incrementAndGet());
        activator.handleMessage(Message.of("x"));
        assertEquals(1, invoked.get());
    }

    @Test
    public void serviceActivatorShouldAcceptRunnable() {
        var invoked = new AtomicInteger(0);
        var activator = new ServiceActivator(() -> invoked.incrementAndGet());
        activator.handleMessage(Message.of("x"));
        assertEquals(1, invoked.get());
    }

    // ---- IntegrationFlow tests ----

    @Test
    public void flowShouldProcessInlineTransformAndFilter() {
        var result = new AtomicReference<Object>();
        var flow = new IntegrationFlow("test-flow")
                .transform(obj -> String.valueOf(obj).toUpperCase())
                .filter(obj -> obj instanceof String s && s.length() > 2)
                .handle(msg -> result.set(msg.getPayload()));

        flow.send(Message.of("hello"));
        assertEquals("HELLO", result.get());
    }

    @Test
    public void flowShouldStopWhenFilterRejects() {
        var result = new AtomicReference<Object>();
        var flow = new IntegrationFlow("filter-flow")
                .filter(obj -> obj instanceof String s && s.length() > 10)
                .handle(msg -> result.set(msg.getPayload()));

        flow.send(Message.of("short"));
        assertNull(result.get());
    }

    @Test
    public void flowShouldChainMultipleTransforms() {
        var result = new AtomicReference<Object>();
        var flow = new IntegrationFlow("chain-flow")
                .transform(obj -> String.valueOf(obj).toUpperCase())
                .transform(obj -> obj + "!")
                .handle(msg -> result.set(msg.getPayload()));

        flow.send(Message.of("hi"));
        assertEquals("HI!", result.get());
    }

    @Test
    public void flowBuilderShouldBuildFlow() {
        var result = new AtomicReference<Object>();
        var flow = IntegrationFlow.builder()
                .name("builder-flow")
                .transform(obj -> String.valueOf(obj).toLowerCase())
                .handle(msg -> result.set(msg.getPayload()))
                .build();

        flow.send(Message.of("HELLO"));
        assertEquals("hello", result.get());
    }

    @Test
    public void flowShouldHaveName() {
        var flow = new IntegrationFlow("named-flow");
        assertEquals("named-flow", flow.getName());
    }

    @Test
    public void flowShouldRecordSteps() {
        var flow = new IntegrationFlow("steps-flow")
                .transform(obj -> obj)
                .filter(obj -> true)
                .handle(msg -> {});

        assertEquals(3, flow.getSteps().size());
    }

    // ---- IntegrationFlowContext tests ----

    @Test
    public void flowContextShouldRegisterFlow() {
        var context = new IntegrationFlowContext();
        var flow = new IntegrationFlow("ctx-flow");
        context.register(flow);
        assertEquals(1, context.getFlowCount());
        assertSame(flow, context.getFlow("ctx-flow"));
    }

    @Test
    public void flowContextShouldUnregisterFlow() {
        var context = new IntegrationFlowContext();
        var flow = new IntegrationFlow("removable");
        context.register(flow);
        assertTrue(context.unregister("removable"));
        assertEquals(0, context.getFlowCount());
    }

    @Test
    public void flowContextShouldReturnFalseForUnknownUnregister() {
        var context = new IntegrationFlowContext();
        assertFalse(context.unregister("nonexistent"));
    }

    @Test
    public void flowContextShouldRegisterChannel() {
        var context = new IntegrationFlowContext();
        var channel = new DirectChannel("ctx-channel");
        context.registerChannel(channel);
        assertSame(channel, context.getChannel("ctx-channel"));
    }

    @Test
    public void flowContextShouldStartAndStop() {
        var context = new IntegrationFlowContext();
        assertFalse(context.isRunning());
        context.start();
        assertTrue(context.isRunning());
        context.stop();
        assertFalse(context.isRunning());
    }

    @Test
    public void flowContextShouldSendToFlow() {
        var result = new AtomicReference<Object>();
        var flow = new IntegrationFlow("send-flow")
                .handle(msg -> result.set(msg.getPayload()));
        var context = new IntegrationFlowContext();
        context.register(flow);

        context.send("send-flow", Message.of("data"));
        assertEquals("data", result.get());
    }

    @Test
    public void flowContextSendShouldReturnFalseForUnknownFlow() {
        var context = new IntegrationFlowContext();
        assertFalse(context.send("unknown", Message.of("x")));
    }

    @Test
    public void flowContextShouldClear() {
        var context = new IntegrationFlowContext();
        context.register(new IntegrationFlow("f1"));
        context.registerChannel(new DirectChannel("c1"));
        context.start();
        context.clear();
        assertEquals(0, context.getFlowCount());
        assertEquals(0, context.getChannelCount());
        assertFalse(context.isRunning());
    }

    @Test
    public void flowContextShouldAutoRegisterFlowChannels() {
        var context = new IntegrationFlowContext();
        var input = new DirectChannel("flow-input");
        var output = new DirectChannel("flow-output");
        var flow = new IntegrationFlow("auto-channels")
                .from(input)
                .to(output);
        context.register(flow);
        assertNotNull(context.getChannel("flow-input"));
        assertNotNull(context.getChannel("flow-output"));
    }

    // ---- IntegrationConfiguration tests ----

    @Test
    public void configurationShouldHaveDefaults() {
        var config = new IntegrationConfiguration();
        assertTrue(config.isEnable());
        assertEquals("direct", config.getDefaultChannelType());
        assertEquals(1024, config.getQueueCapacity());
        assertTrue(config.isAutoStart());
        assertEquals(30000, config.getGlobalTimeout());
    }

    @Test
    public void configurationCopyShouldEqualOriginal() {
        var config = new IntegrationConfiguration();
        config.setDefaultChannelType("queue");
        config.setQueueCapacity(2048);
        config.setAutoStart(false);
        config.setGlobalTimeout(5000);
        config.setErrorChannel("error-ch");

        var copy = config.<IntegrationConfiguration>copy();
        assertEquals(config.getDefaultChannelType(), copy.getDefaultChannelType());
        assertEquals(config.getQueueCapacity(), copy.getQueueCapacity());
        assertEquals(config.isAutoStart(), copy.isAutoStart());
        assertEquals(config.getGlobalTimeout(), copy.getGlobalTimeout());
        assertEquals(config.getErrorChannel(), copy.getErrorChannel());
    }

    @Test
    public void configurationCopyShouldBeIndependent() {
        var config = new IntegrationConfiguration();
        var copy = config.<IntegrationConfiguration>copy();
        copy.setQueueCapacity(999);
        assertNotEquals(config.getQueueCapacity(), copy.getQueueCapacity());
    }

    @Test
    public void configurationShouldReturnDefaultProfileAndCategory() {
        var config = new IntegrationConfiguration();
        assertNotNull(config.getProfile());
        assertNotNull(config.getCategory());
    }
}
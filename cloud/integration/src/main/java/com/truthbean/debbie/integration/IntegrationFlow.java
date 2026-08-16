/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration;

import com.truthbean.debbie.integration.channel.DirectChannel;
import com.truthbean.debbie.integration.channel.PublishSubscribeChannel;
import com.truthbean.debbie.integration.channel.QueueChannel;
import com.truthbean.debbie.integration.endpoint.Aggregator;
import com.truthbean.debbie.integration.endpoint.Filter;
import com.truthbean.debbie.integration.endpoint.Router;
import com.truthbean.debbie.integration.endpoint.ServiceActivator;
import com.truthbean.debbie.integration.endpoint.Splitter;
import com.truthbean.debbie.integration.endpoint.Transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A fluent API for building integration flows.
 * <p>
 * An integration flow is a sequence of steps that process messages.
 * Each step can be a channel, a transformer, a filter, a router, a splitter,
 * an aggregator, or a service activator.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class IntegrationFlow {

    private final String name;
    private final List<FlowStep> steps = new ArrayList<>();
    private MessageChannel inputChannel;
    private MessageChannel outputChannel;

    public IntegrationFlow(String name) {
        this.name = name;
    }

    public IntegrationFlow() {
        this("flow-" + System.nanoTime());
    }

    public String getName() {
        return name;
    }

    public MessageChannel getInputChannel() {
        return inputChannel;
    }

    public MessageChannel getOutputChannel() {
        return outputChannel;
    }

    public List<FlowStep> getSteps() {
        return List.copyOf(steps);
    }

    public IntegrationFlow from(MessageChannel channel) {
        this.inputChannel = channel;
        steps.add(new FlowStep(StepType.INPUT_CHANNEL, channel));
        return this;
    }

    public IntegrationFlow fromDirect(String channelName) {
        return from(new DirectChannel(channelName));
    }

    public IntegrationFlow fromQueue(String channelName) {
        return from(new QueueChannel(channelName));
    }

    public IntegrationFlow fromPublishSubscribe(String channelName) {
        return from(new PublishSubscribeChannel(channelName));
    }

    public IntegrationFlow channel(MessageChannel channel) {
        steps.add(new FlowStep(StepType.CHANNEL, channel));
        return this;
    }

    public IntegrationFlow channel(String channelName) {
        return channel(new DirectChannel(channelName));
    }

    public IntegrationFlow transform(Function<Object, Object> transformFunction) {
        var transformer = new Transformer(transformFunction);
        steps.add(new FlowStep(StepType.TRANSFORMER, transformer));
        return this;
    }

    public IntegrationFlow filter(Predicate<Object> filterPredicate) {
        var filter = new Filter(filterPredicate);
        steps.add(new FlowStep(StepType.FILTER, filter));
        return this;
    }

    public IntegrationFlow route(Function<Object, String> routingFunction, Map<String, MessageChannel> channelMap) {
        var router = new Router(routingFunction, channelMap);
        steps.add(new FlowStep(StepType.ROUTER, router));
        return this;
    }

    public IntegrationFlow split(Function<Object, Collection<?>> splitFunction) {
        var splitter = new Splitter(splitFunction);
        steps.add(new FlowStep(StepType.SPLITTER, splitter));
        return this;
    }

    public IntegrationFlow aggregate(Function<List<Object>, Object> aggregateFunction) {
        var aggregator = new Aggregator(aggregateFunction);
        steps.add(new FlowStep(StepType.AGGREGATOR, aggregator));
        return this;
    }

    public IntegrationFlow handle(MessageHandler handler) {
        var activator = new ServiceActivator(handler);
        steps.add(new FlowStep(StepType.SERVICE_ACTIVATOR, activator));
        return this;
    }


    public IntegrationFlow to(MessageChannel channel) {
        this.outputChannel = channel;
        steps.add(new FlowStep(StepType.OUTPUT_CHANNEL, channel));
        return this;
    }

    public IntegrationFlow toDirect(String channelName) {
        return to(new DirectChannel(channelName));
    }

    public IntegrationFlow toQueue(String channelName) {
        return to(new QueueChannel(channelName));
    }

    public boolean send(Message<?> message) {
        if (inputChannel != null) {
            return inputChannel.send(message);
        }
        return processInline(message);
    }

    private boolean processInline(Message<?> message) {
        Message<?> current = message;
        for (var step : steps) {
            current = step.process(current);
            if (current == null) {
                return false;
            }
        }
        if (outputChannel != null) {
            return outputChannel.send(current);
        }
        return true;
    }

    public enum StepType {
        INPUT_CHANNEL,
        CHANNEL,
        TRANSFORMER,
        FILTER,
        ROUTER,
        SPLITTER,
        AGGREGATOR,
        SERVICE_ACTIVATOR,
        OUTPUT_CHANNEL
    }

    public static final class FlowStep {
        private final StepType type;
        private final Object component;

        FlowStep(StepType type, Object component) {
            this.type = type;
            this.component = component;
        }

        public StepType getType() {
            return type;
        }

        public Object getComponent() {
            return component;
        }

        Message<?> process(Message<?> message) {
            return switch (type) {
                case INPUT_CHANNEL, CHANNEL, OUTPUT_CHANNEL -> message;
                case TRANSFORMER -> ((Transformer) component).apply(message);
                case FILTER -> ((Filter) component).test(message) ? message : null;
                case ROUTER -> {
                    var router = (Router) component;
                    router.route(message);
                    yield message;
                }
                case SPLITTER -> {
                    var splitter = (Splitter) component;
                    var parts = splitter.apply(message);
                    yield parts.isEmpty() ? null : parts.iterator().next();
                }
                case AGGREGATOR -> {
                    var aggregator = (Aggregator) component;
                    yield aggregator.apply(List.of(message));
                }
                case SERVICE_ACTIVATOR -> {
                    ((ServiceActivator) component).handleMessage(message);
                    yield message;
                }
            };
        }
    }

    @Override
    public String toString() {
        return "IntegrationFlow{name='" + name + "', steps=" + steps.size() + "}";
    }

    public static IntegrationFlowBuilder builder() {
        return new IntegrationFlowBuilder();
    }

    public static final class IntegrationFlowBuilder {
        private String name;
        private MessageChannel inputChannel;
        private MessageChannel outputChannel;
        private final List<FlowStep> steps = new ArrayList<>();

        public IntegrationFlowBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IntegrationFlowBuilder from(MessageChannel channel) {
            this.inputChannel = channel;
            steps.add(new FlowStep(StepType.INPUT_CHANNEL, channel));
            return this;
        }

        public IntegrationFlowBuilder fromDirect(String channelName) {
            return from(new DirectChannel(channelName));
        }

        public IntegrationFlowBuilder transform(Function<Object, Object> fn) {
            steps.add(new FlowStep(StepType.TRANSFORMER, new Transformer(fn)));
            return this;
        }

        public IntegrationFlowBuilder filter(Predicate<Object> predicate) {
            steps.add(new FlowStep(StepType.FILTER, new Filter(predicate)));
            return this;
        }

        public IntegrationFlowBuilder handle(MessageHandler handler) {
            steps.add(new FlowStep(StepType.SERVICE_ACTIVATOR, new ServiceActivator(handler)));
            return this;
        }

        public IntegrationFlowBuilder to(MessageChannel channel) {
            this.outputChannel = channel;
            steps.add(new FlowStep(StepType.OUTPUT_CHANNEL, channel));
            return this;
        }

        public IntegrationFlow build() {
            var flow = new IntegrationFlow(name != null ? name : "flow-" + System.nanoTime());
            flow.inputChannel = this.inputChannel;
            flow.outputChannel = this.outputChannel;
            flow.steps.addAll(this.steps);
            return flow;
        }
    }
}
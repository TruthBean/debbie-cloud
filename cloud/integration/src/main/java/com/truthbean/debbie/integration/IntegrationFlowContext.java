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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context for managing integration flow lifecycle.
 * <p>
 * Acts as a registry for flows and channels, allowing flows to be
 * registered, looked up, started, and stopped.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class IntegrationFlowContext {

    private final Map<String, IntegrationFlow> flows = new ConcurrentHashMap<>();
    private final Map<String, MessageChannel> channels = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    public IntegrationFlow registration(IntegrationFlow flow) {
        flows.put(flow.getName(), flow);
        if (flow.getInputChannel() != null) {
            channels.putIfAbsent(flow.getInputChannel().getName(), flow.getInputChannel());
        }
        if (flow.getOutputChannel() != null) {
            channels.putIfAbsent(flow.getOutputChannel().getName(), flow.getOutputChannel());
        }
        return flow;
    }

    public IntegrationFlow register(IntegrationFlow flow) {
        return registration(flow);
    }

    public IntegrationFlow getFlow(String name) {
        return flows.get(name);
    }

    public MessageChannel getChannel(String name) {
        return channels.get(name);
    }

    public void registerChannel(MessageChannel channel) {
        channels.put(channel.getName(), channel);
    }

    public boolean unregister(String flowName) {
        var removed = flows.remove(flowName);
        if (removed != null) {
            if (removed.getInputChannel() != null) {
                channels.remove(removed.getInputChannel().getName());
            }
            if (removed.getOutputChannel() != null) {
                channels.remove(removed.getOutputChannel().getName());
            }
            return true;
        }
        return false;
    }

    public Map<String, IntegrationFlow> getFlows() {
        return Map.copyOf(flows);
    }

    public Map<String, MessageChannel> getChannels() {
        return Map.copyOf(channels);
    }

    public int getFlowCount() {
        return flows.size();
    }

    public int getChannelCount() {
        return channels.size();
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean send(String flowName, Message<?> message) {
        var flow = flows.get(flowName);
        if (flow == null) {
            return false;
        }
        return flow.send(message);
    }

    public void clear() {
        flows.clear();
        channels.clear();
        running = false;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * configuration of debbie-bus.
 * <p>
 * properties prefix: {@code debbie.bus}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.bus")
public class BusConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "id", defaultValue = "default")
    private String id;

    @PropertyInject(value = "destination", defaultValue = "**")
    private String destination;

    @PropertyInject(value = "topic", defaultValue = "debbie-bus")
    private String topic;

    @PropertyInject(value = "broker", defaultValue = "simple")
    private String broker;

    @PropertyInject(value = "ack", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean ack;

    @PropertyInject(value = "trace", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean trace;

    @PropertyInject(value = "publish-timeout", transformer = IntegerTransformer.class, defaultValue = "5000")
    private int publishTimeout;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    public int getPublishTimeout() {
        return publishTimeout;
    }

    public void setPublishTimeout(int publishTimeout) {
        this.publishTimeout = publishTimeout;
    }

    @Override
    public String getProfile() {
        return EnvironmentDepositoryHolder.DEFAULT_PROFILE;
    }

    @Override
    public String getCategory() {
        return EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var copy = new BusConfiguration();
        copy.enable = this.enable;
        copy.id = this.id;
        copy.destination = this.destination;
        copy.topic = this.topic;
        copy.broker = this.broker;
        copy.ack = this.ack;
        copy.trace = this.trace;
        copy.publishTimeout = this.publishTimeout;
        return (T) copy;
    }

    @Override
    public void close() {
    }
}
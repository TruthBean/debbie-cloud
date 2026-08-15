/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul.discovery;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.consul.ConsulClient;
import com.truthbean.debbie.consul.ConsulException;

import java.util.List;
import java.util.Map;

/**
 * High-level service registry that wraps {@link ConsulClient} for
 * registering and deregistering the current application instance.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulServiceRegistry {

    private final ConsulClient client;
    private volatile ConsulServiceRegistration currentRegistration;

    public ConsulServiceRegistry(ConsulClient client) {
        this.client = client;
    }

    public void register(ConsulServiceRegistration registration) {
        client.registerService(registration);
        this.currentRegistration = registration;
        LOGGER.info(() -> "registered service [" + registration.getName()
                + "] id=" + registration.getId()
                + " at " + registration.getAddress() + ":" + registration.getPort());
    }

    public void deregister() {
        var reg = currentRegistration;
        if (reg == null) {
            return;
        }
        try {
            client.deregisterService(reg.getId());
            LOGGER.info(() -> "deregistered service [" + reg.getName() + "] id=" + reg.getId());
        } catch (ConsulException e) {
            LOGGER.warn("failed to deregister service: " + e.getMessage());
        }
        currentRegistration = null;
    }

    public void deregister(String serviceId) {
        client.deregisterService(serviceId);
    }

    public ConsulServiceRegistration getCurrentRegistration() {
        return currentRegistration;
    }

    public boolean isRegistered() {
        return currentRegistration != null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulServiceRegistry.class);
}
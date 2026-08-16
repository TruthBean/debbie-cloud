/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.integration.endpoint;

import com.truthbean.debbie.integration.Message;
import com.truthbean.debbie.integration.MessageHandler;

/**
 * A service activator that invokes a service method for each message.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ServiceActivator implements MessageHandler {

    private final MessageHandler service;

    public ServiceActivator(MessageHandler service) {
        this.service = service;
    }

    public ServiceActivator(Runnable runnable) {
        this.service = msg -> runnable.run();
    }

    @Override
    public void handleMessage(Message<?> message) {
        service.handleMessage(message);
    }
}
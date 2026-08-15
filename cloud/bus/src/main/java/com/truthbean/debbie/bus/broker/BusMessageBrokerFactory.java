/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus.broker;

import com.truthbean.debbie.bus.BusConfiguration;

/**
 * spi factory to create a {@link BusMessageBroker}.
 * <p>
 * implementations are discovered via {@link java.util.ServiceLoader}
 * under {@code META-INF/services/com.truthbean.debbie.bus.broker.BusMessageBrokerFactory}.
 * the one whose {@link #name()} matches {@code debbie.bus.broker} (default {@code "simple"})
 * and reports {@link #support(BusConfiguration)} will be used.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface BusMessageBrokerFactory {

    String name();

    boolean support(BusConfiguration configuration);

    BusMessageBroker create(BusConfiguration configuration);
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bus;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.bus.broker.BusMessageBroker;
import com.truthbean.debbie.bus.broker.BusMessageBrokerFactory;
import com.truthbean.debbie.bus.identity.BusDestination;
import com.truthbean.debbie.bus.identity.BusIdentity;
import com.truthbean.debbie.bus.refresh.EnvironmentRefreshHandler;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.event.DebbieEventPublisher;

import java.util.ServiceLoader;

/**
 * debbie-bus module starter.
 * <p>
 * it wires up the {@link BusConfiguration}, resolves a {@link BusMessageBroker}
 * via spi, builds the {@link BusEventPublisher} / {@link BusEventListener},
 * and registers an {@link EnvironmentRefreshHandler} so that incoming
 * {@link com.truthbean.debbie.bus.event.RefreshBusEvent}s refresh the local environment.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class BusModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.bus.enable";

    private volatile BusMessageBroker broker;
    private volatile BusEventListener eventListener;

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(BusConfiguration.class);
        configBeanInfo.addBeanName("busConfiguration", BusConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);

        var identityBeanInfo = new DebbieReflectionBeanFactory<>(BusIdentity.class);
        identityBeanInfo.addBeanName("busIdentity", BusIdentity.class.getName());
        beanInfoManager.registerBeanInfo(identityBeanInfo);

        var destinationBeanInfo = new DebbieReflectionBeanFactory<>(BusDestination.class);
        destinationBeanInfo.addBeanName("busDestination", BusDestination.class.getName());
        beanInfoManager.registerBeanInfo(destinationBeanInfo);

        var publisherBeanInfo = new DebbieReflectionBeanFactory<>(BusEventPublisher.class);
        publisherBeanInfo.addBeanName("busEventPublisher", BusEventPublisher.class.getName());
        beanInfoManager.registerBeanInfo(publisherBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        BusConfiguration configuration = factory.factory(BusConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        BusIdentity self = new BusIdentity(configuration.getId());
        var identityBeanFactory = new SimpleBeanFactory<>(self, BusIdentity.class, "busIdentity");
        beanInfoManager.registerBeanInfo(identityBeanFactory);

        BusDestination defaultDestination = BusDestination.of(configuration.getDestination());
        var destinationBeanFactory = new SimpleBeanFactory<>(defaultDestination, BusDestination.class, "busDestination");
        beanInfoManager.registerBeanInfo(destinationBeanFactory);

        BusMessageBroker resolved = resolveBroker(configuration);
        if (resolved == null) {
            LOGGER.warn("no BusMessageBrokerFactory matched debbie.bus.broker=" + configuration.getBroker()
                    + ", bus events will only be dispatched in-process");
            return;
        }
        resolved.start();
        this.broker = resolved;
        var brokerBeanFactory = new SimpleBeanFactory<>(resolved, BusMessageBroker.class, "busMessageBroker");
        beanInfoManager.registerBeanInfo(brokerBeanFactory);

        DebbieEventPublisher eventPublisher = factory.factory(DebbieEventPublisher.class);
        BusEventPublisher busEventPublisher = new BusEventPublisher(self, resolved, eventPublisher, configuration.isAck());
        var publisherBeanFactory = new SimpleBeanFactory<>(busEventPublisher, BusEventPublisher.class, "busEventPublisher");
        beanInfoManager.registerBeanInfo(publisherBeanFactory);

        BusEventListener listener = new BusEventListener(self, eventPublisher, busEventPublisher, configuration.isAck());
        var environmentHolder = applicationContext.getEnvironmentHolder();
        listener.addRefreshHandler(new EnvironmentRefreshHandler(environmentHolder));
        resolved.subscribe(listener.asBrokerListener());
        this.eventListener = listener;

        LOGGER.info(() -> "debbie-bus started, self=" + self + ", broker=" + resolved.name()
                + ", destination=" + defaultDestination);
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 1010000;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        if (eventListener != null) {
            eventListener.removeRefreshHandler(null);
        }
        if (broker != null) {
            try {
                broker.close();
            } catch (Exception e) {
                LOGGER.error("failed to close bus broker", e);
            }
        }
        LOGGER.info(() -> "debbie-bus released");
    }

    private BusMessageBroker resolveBroker(BusConfiguration configuration) {
        var loader = ServiceLoader.load(BusMessageBrokerFactory.class);
        BusMessageBrokerFactory fallback = null;
        for (var f : loader) {
            try {
                if (f.support(configuration)) {
                    return f.create(configuration);
                }
                if (fallback == null && "simple".equalsIgnoreCase(f.name())) {
                    fallback = f;
                }
            } catch (Exception e) {
                LOGGER.error("bus broker factory " + f + " failed", e);
            }
        }
        if (fallback != null) {
            return fallback.create(configuration);
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BusModuleStarter.class);
}
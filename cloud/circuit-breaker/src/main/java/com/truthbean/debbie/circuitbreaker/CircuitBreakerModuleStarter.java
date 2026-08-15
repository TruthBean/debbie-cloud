/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.circuitbreaker;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

/**
 * debbie-circuit-breaker module starter.
 * <p>
 * It creates a {@link CircuitBreakerRegistry} with the resolved
 * {@link CircuitBreakerConfiguration} and registers it as a singleton bean
 * so that other modules can obtain named {@link CircuitBreaker} and
 * {@link com.truthbean.debbie.circuitbreaker.ratelimit.RateLimiter} instances.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class CircuitBreakerModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.circuit-breaker.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(CircuitBreakerConfiguration.class);
        configBeanInfo.addBeanName("circuitBreakerConfiguration", CircuitBreakerConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        CircuitBreakerConfiguration configuration = factory.factory(CircuitBreakerConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var cbConfig = configuration.toCircuitBreakerConfig();
        var rlConfig = configuration.toRateLimiterConfig();
        var registry = new CircuitBreakerRegistry(cbConfig, rlConfig);

        var registryBean = new SimpleBeanFactory<>(registry, CircuitBreakerRegistry.class,
                "circuitBreakerRegistry");
        beanInfoManager.registerBeanInfo(registryBean);

        LOGGER.info(() -> "debbie-circuit-breaker started, default failureRateThreshold="
                + cbConfig.getFailureRateThreshold() + "%, slidingWindowSize=" + cbConfig.getSlidingWindowSize()
                + ", rateLimiter=" + configuration.isRateLimiterEnable());
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var registry = factory.factory(CircuitBreakerRegistry.class);
        if (registry != null) {
            registry.resetAll();
        }
        LOGGER.info(() -> "debbie-circuit-breaker released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerModuleStarter.class);
}
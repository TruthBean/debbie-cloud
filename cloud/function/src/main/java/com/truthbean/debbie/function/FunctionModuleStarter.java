/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.function.binding.HttpFunctionBinding;
import com.truthbean.debbie.function.routing.FunctionRouter;

/**
 * debbie-function module starter.
 * <p>
 * Creates and registers:
 * <ul>
 *   <li>{@link FunctionRegistry} — the central function registry</li>
 *   <li>{@link FunctionComposition} — for composing function pipelines</li>
 *   <li>{@link FunctionRouter} — for routing messages to functions</li>
 *   <li>{@link HttpFunctionBinding} — HTTP binding</li>
 *   <li>{@link FunctionEndpoint} — HTTP endpoint (if {@code debbie.function.http.enable=true})</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FunctionModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.function.enable";

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(FunctionConfiguration.class);
        configBeanInfo.addBeanName("functionConfiguration", FunctionConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        FunctionConfiguration configuration = factory.factory(FunctionConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var registry = new FunctionRegistry();
        var registryBean = new SimpleBeanFactory<>(registry, FunctionRegistry.class,
                "functionRegistry");
        beanInfoManager.registerBeanInfo(registryBean);

        if (configuration.isCompositionEnable()) {
            var composition = new FunctionComposition(registry);
            var compositionBean = new SimpleBeanFactory<>(composition,
                    FunctionComposition.class, "functionComposition");
            beanInfoManager.registerBeanInfo(compositionBean);
        }

        if (configuration.isRoutingEnable()) {
            var router = new FunctionRouter(registry);
            var routerBean = new SimpleBeanFactory<>(router,
                    FunctionRouter.class, "functionRouter");
            beanInfoManager.registerBeanInfo(routerBean);
        }

        var httpBinding = new HttpFunctionBinding();
        httpBinding.bind(registry);
        var bindingBean = new SimpleBeanFactory<>(httpBinding,
                HttpFunctionBinding.class, "httpFunctionBinding");
        beanInfoManager.registerBeanInfo(bindingBean);

        if (configuration.isHttpEnable()) {
            var endpoint = new FunctionEndpoint(httpBinding, configuration.getHttpPrefix());
            var endpointBean = new SimpleBeanFactory<>(endpoint,
                    FunctionEndpoint.class, "functionEndpoint");
            beanInfoManager.registerBeanInfo(endpointBean);
        }

        LOGGER.info(() -> "debbie-function started, http=" + configuration.isHttpEnable()
                + ", prefix=" + configuration.getHttpPrefix()
                + ", routing=" + configuration.isRoutingEnable()
                + ", composition=" + configuration.isCompositionEnable());
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 1010012;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        LOGGER.info(() -> "debbie-function released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionModuleStarter.class);
}
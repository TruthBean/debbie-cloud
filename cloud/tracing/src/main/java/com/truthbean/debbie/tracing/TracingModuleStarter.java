/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tracing;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.tracing.reporter.HttpReporter;
import com.truthbean.debbie.tracing.reporter.LoggingReporter;
import com.truthbean.debbie.tracing.reporter.TraceReporter;
import com.truthbean.debbie.tracing.sampler.AlwaysSampler;
import com.truthbean.debbie.tracing.sampler.ProbabilitySampler;
import com.truthbean.debbie.tracing.sampler.Sampler;
import com.truthbean.debbie.tracing.sampler.SamplerFactory;

/**
 * debbie-tracing module starter.
 * <p>
 * Creates a {@link Tracer} with the configured sampler and reporter,
 * and registers it as a bean for other modules to use.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class TracingModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.tracing.enable";

    private Tracer tracer;

    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment)
                && environment.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var configBeanInfo = new DebbieReflectionBeanFactory<>(TracingConfiguration.class);
        configBeanInfo.addBeanName("tracingConfiguration", TracingConfiguration.class.getName());
        beanInfoManager.registerBeanInfo(configBeanInfo);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        var factory = applicationContext.getGlobalBeanFactory();
        var beanInfoManager = applicationContext.getBeanInfoManager();

        TracingConfiguration configuration = factory.factory(TracingConfiguration.class);
        if (configuration == null || !configuration.isEnable()) {
            return;
        }

        var sampler = createSampler(configuration);
        var reporter = createReporter(configuration);
        tracer = new Tracer(configuration.getServiceName(), sampler, reporter);

        var tracerBean = new SimpleBeanFactory<>(tracer,
                Tracer.class, "tracer");
        beanInfoManager.registerBeanInfo(tracerBean);

        LOGGER.info(() -> "debbie-tracing started, service=" + configuration.getServiceName()
                + ", sampler=" + sampler.name()
                + ", reporter=" + reporter.name());
    }

    private Sampler createSampler(TracingConfiguration config) {
        var strategy = config.getSamplerStrategy();
        if ("probability".equalsIgnoreCase(strategy)) {
            return new ProbabilitySampler(config.getSamplerRate());
        }
        return SamplerFactory.create(strategy);
    }

    private TraceReporter createReporter(TracingConfiguration config) {
        var type = config.getReporterType();
        if (type == null || type.isBlank()) {
            return new LoggingReporter();
        }
        return switch (type.trim().toLowerCase()) {
            case "logging" -> new LoggingReporter();
            case "http" -> {
                var endpoint = config.getReporterEndpoint();
                if (endpoint == null || endpoint.isBlank()) {
                    LOGGER.warn("http reporter configured but no endpoint set, falling back to logging");
                    yield new LoggingReporter();
                }
                yield new HttpReporter(endpoint, config.getReporterBatchSize(),
                        config.getReporterConnectTimeout());
            }
            default -> new LoggingReporter();
        };
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 1010008;
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        if (tracer != null) {
            tracer.close();
        }
        LOGGER.info(() -> "debbie-tracing released");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TracingModuleStarter.class);
}
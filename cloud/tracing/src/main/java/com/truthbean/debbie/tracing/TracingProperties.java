/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tracing;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-tracing.
 * <p>
 * Reads the {@link TracingConfiguration} from the environment using the prefix
 * {@code debbie.tracing.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class TracingProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<TracingConfiguration> {

    /**
     * key prefix of the tracing properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.tracing.service-name}
     */
    private static final String KEY_PREFIX = "debbie.tracing.";

    private final Map<String, TracingConfiguration> map = new HashMap<>();
    private TracingConfiguration configuration;

    public TracingProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, TracingConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return Map.of();
    }

    @Override
    public Set<String> getCategories(String profile) {
        return Set.of();
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return false;
    }

    @Override
    public TracingConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public TracingConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new TracingConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setServiceName(getStringValue(KEY_PREFIX + "service-name", "application"));

        // sampler
        configuration.setSamplerStrategy(getStringValue(KEY_PREFIX + "sampler.strategy", "always"));
        configuration.setSamplerRate(getDoubleValue(KEY_PREFIX + "sampler.rate", 0.1));

        // reporter
        configuration.setReporterType(getStringValue(KEY_PREFIX + "reporter.type", "logging"));
        configuration.setReporterEndpoint(getValue(KEY_PREFIX + "reporter.endpoint"));
        configuration.setReporterBatchSize(getIntegerValue(KEY_PREFIX + "reporter.batch-size", 100));
        configuration.setReporterConnectTimeout(getIntegerValue(KEY_PREFIX + "reporter.connect-timeout", 5000));

        // propagation
        configuration.setPropagationType(getStringValue(KEY_PREFIX + "propagation.type", "w3c"));
        configuration.setPropagationIncludeTags(getBooleanValue(KEY_PREFIX + "propagation.include-tags", false));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
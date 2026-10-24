/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.dubbo;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-dubbo.
 * <p>
 * Reads the {@link DubboConfiguration} from the environment using the prefix
 * {@code debbie.dubbo.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class DubboProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<DubboConfiguration> {

    /**
     * key prefix of the dubbo properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.dubbo.protocol.port}
     */
    private static final String KEY_PREFIX = "debbie.dubbo.";

    private final Map<String, DubboConfiguration> map = new HashMap<>();
    private DubboConfiguration configuration;

    public DubboProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, DubboConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public DubboConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public DubboConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new DubboConfiguration();

        // application
        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setApplicationName(getStringValue(KEY_PREFIX + "application.name", "debbie-dubbo"));
        configuration.setApplicationOwner(getValue(KEY_PREFIX + "application.owner"));
        configuration.setApplicationOrganization(getValue(KEY_PREFIX + "application.organization"));

        // protocol
        configuration.setProtocolName(getStringValue(KEY_PREFIX + "protocol.name", "dubbo"));
        configuration.setProtocolPort(getIntegerValue(KEY_PREFIX + "protocol.port", -1));
        configuration.setProtocolHost(getValue(KEY_PREFIX + "protocol.host"));
        configuration.setProtocolThreadpool(getStringValue(KEY_PREFIX + "protocol.threadpool", "fixed"));
        configuration.setProtocolThreads(getIntegerValue(KEY_PREFIX + "protocol.threads", 200));

        // registry
        configuration.setRegistryAddress(getValue(KEY_PREFIX + "registry.address"));
        configuration.setRegistryProtocol(getStringValue(KEY_PREFIX + "registry.protocol", "zookeeper"));
        configuration.setRegistryGroup(getValue(KEY_PREFIX + "registry.group"));
        configuration.setRegistryCheck(getBooleanValue(KEY_PREFIX + "registry.check", true));
        configuration.setRegistryRegister(getBooleanValue(KEY_PREFIX + "registry.register", true));

        // provider
        configuration.setProviderTimeout(getIntegerValue(KEY_PREFIX + "provider.timeout", 1000));
        configuration.setProviderRetries(getIntegerValue(KEY_PREFIX + "provider.retries", 2));
        configuration.setProviderDelay(getIntegerValue(KEY_PREFIX + "provider.delay", -1));

        // consumer
        configuration.setConsumerTimeout(getIntegerValue(KEY_PREFIX + "consumer.timeout", 1000));
        configuration.setConsumerRetries(getIntegerValue(KEY_PREFIX + "consumer.retries", 2));
        configuration.setConsumerCheck(getBooleanValue(KEY_PREFIX + "consumer.check", true));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
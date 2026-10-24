/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-consul.
 * <p>
 * Reads the {@link ConsulConfiguration} from the environment using the prefix
 * {@code debbie.consul.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<ConsulConfiguration> {

    /**
     * key prefix of the consul properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.consul.host}
     */
    private static final String KEY_PREFIX = "debbie.consul.";

    private final Map<String, ConsulConfiguration> map = new HashMap<>();
    private ConsulConfiguration configuration;

    public ConsulProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, ConsulConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public ConsulConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public ConsulConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ConsulConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 8500));
        configuration.setScheme(getStringValue(KEY_PREFIX + "scheme", "http"));
        configuration.setToken(getValue(KEY_PREFIX + "token"));
        configuration.setConnectTimeout(getIntegerValue(KEY_PREFIX + "connect-timeout", 5000));
        configuration.setReadTimeout(getIntegerValue(KEY_PREFIX + "read-timeout", 10000));

        // service discovery
        configuration.setDiscoveryEnable(getBooleanValue(KEY_PREFIX + "discovery.enable", true));
        configuration.setDiscoveryRegister(getBooleanValue(KEY_PREFIX + "discovery.register", true));
        configuration.setDiscoveryPreferIpAddress(getBooleanValue(KEY_PREFIX + "discovery.prefer-ip-address", false));
        configuration.setDiscoveryHeartbeatInterval(getStringValue(KEY_PREFIX + "discovery.heartbeat-interval", "10s"));
        configuration.setDiscoveryDeregisterCriticalAfter(getStringValue(KEY_PREFIX + "discovery.deregister-critical-after", "30s"));
        configuration.setDiscoveryHealthCheckPath(getStringValue(KEY_PREFIX + "discovery.health-check-path", "/actuator/health"));

        // config
        configuration.setConfigEnable(getBooleanValue(KEY_PREFIX + "config.enable", false));
        configuration.setConfigPrefix(getStringValue(KEY_PREFIX + "config.prefix", "config"));
        configuration.setConfigProfileSeparator(getStringValue(KEY_PREFIX + "config.profile-separator", "/"));
        configuration.setConfigWatch(getBooleanValue(KEY_PREFIX + "config.watch", false));
        configuration.setConfigWatchInterval(getIntegerValue(KEY_PREFIX + "config.watch-interval", 5000));
        configuration.setConfigFailFast(getBooleanValue(KEY_PREFIX + "config.fail-fast", false));

        // health and service identity
        configuration.setHealthEnable(getBooleanValue(KEY_PREFIX + "health.enable", true));
        configuration.setServiceName(getValue(KEY_PREFIX + "service.name"));
        configuration.setServiceAddress(getValue(KEY_PREFIX + "service.address"));
        configuration.setServicePort(getIntegerValue(KEY_PREFIX + "service.port", 8080));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
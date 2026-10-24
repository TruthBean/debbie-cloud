/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.sentinel;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-sentinel.
 * <p>
 * Reads the {@link SentinelConfiguration} from the environment using the prefix
 * {@code debbie.sentinel.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SentinelProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<SentinelConfiguration> {

    /**
     * key prefix of the sentinel properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.sentinel.app-name}
     */
    private static final String KEY_PREFIX = "debbie.sentinel.";

    private final Map<String, SentinelConfiguration> map = new HashMap<>();
    private SentinelConfiguration configuration;

    public SentinelProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, SentinelConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public SentinelConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public SentinelConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new SentinelConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setAppName(getStringValue(KEY_PREFIX + "app-name", ""));
        configuration.setEager(getBooleanValue(KEY_PREFIX + "eager", false));

        // log
        configuration.setLogDir(getStringValue(KEY_PREFIX + "log-dir", ""));
        configuration.setLogNamePrefix(getStringValue(KEY_PREFIX + "log-name-prefix", "sentinel-record"));
        configuration.setLogUsePid(getBooleanValue(KEY_PREFIX + "log-use-pid", false));
        configuration.setCharset(getStringValue(KEY_PREFIX + "charset", "UTF-8"));

        // transport / dashboard
        configuration.setDashboard(getStringValue(KEY_PREFIX + "transport.dashboard", ""));
        configuration.setTransportPort(getIntegerValue(KEY_PREFIX + "transport.port", 8719));
        configuration.setHeartbeatIntervalMs(getLongValue(KEY_PREFIX + "transport.heartbeat-interval-ms", 10000));
        configuration.setClientIp(getStringValue(KEY_PREFIX + "transport.client-ip", ""));

        // metric
        configuration.setMetricFileSize(getLongValue(KEY_PREFIX + "metric.file-size", 52428800));
        configuration.setMetricFileCount(getIntegerValue(KEY_PREFIX + "metric.file-count", 6));
        configuration.setMetricFlushInterval(getLongValue(KEY_PREFIX + "metric.flush-interval", 1));

        // statistic
        configuration.setStatisticMaxRt(getIntegerValue(KEY_PREFIX + "statistic.max-rt", 4900));
        configuration.setColdFactor(getIntegerValue(KEY_PREFIX + "cold-factor", 3));

        // system rule defaults
        configuration.setSystemLoad(getDoubleValue(KEY_PREFIX + "system.load", -1));
        configuration.setSystemCpuUsage(getDoubleValue(KEY_PREFIX + "system.cpu-usage", -1));
        configuration.setSystemAvgRt(getLongValue(KEY_PREFIX + "system.avg-rt", -1));
        configuration.setSystemMaxThread(getLongValue(KEY_PREFIX + "system.max-thread", -1));
        configuration.setSystemQps(getDoubleValue(KEY_PREFIX + "system.qps", -1));

        // web filter
        configuration.setBlockPage(getStringValue(KEY_PREFIX + "block-page", ""));
        configuration.setFilterEnabled(getBooleanValue(KEY_PREFIX + "filter.enabled", true));
        configuration.setFilterUrlPatterns(getStringValue(KEY_PREFIX + "filter.url-patterns", "/*"));
        configuration.setFilterOrder(getIntegerValue(KEY_PREFIX + "filter.order", -2147483648));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mongo;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-mongo.
 * <p>
 * Reads the {@link MongoConfiguration} from the environment using the prefix
 * {@code debbie.mongo.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class MongoProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<MongoConfiguration> {

    /**
     * key prefix of the mongo properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.mongo.host}
     */
    private static final String KEY_PREFIX = "debbie.mongo.";

    private final Map<String, MongoConfiguration> map = new HashMap<>();
    private MongoConfiguration configuration;

    public MongoProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, MongoConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public MongoConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public MongoConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new MongoConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setUri(getValue(KEY_PREFIX + "uri"));
        configuration.setHost(getStringValue(KEY_PREFIX + "host", "localhost"));
        configuration.setPort(getIntegerValue(KEY_PREFIX + "port", 27017));
        configuration.setDatabase(getValue(KEY_PREFIX + "database"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));
        configuration.setAuthSource(getValue(KEY_PREFIX + "auth-source"));
        configuration.setConnectTimeout(getIntegerValue(KEY_PREFIX + "connect-timeout", 10000));
        configuration.setSocketTimeout(getIntegerValue(KEY_PREFIX + "socket-timeout", 0));
        configuration.setServerSelectionTimeout(getIntegerValue(KEY_PREFIX + "server-selection-timeout", 30000));
        configuration.setMaxPoolSize(getIntegerValue(KEY_PREFIX + "max-pool-size", 100));
        configuration.setMinPoolSize(getIntegerValue(KEY_PREFIX + "min-pool-size", 0));
        configuration.setMaxIdleTime(getLongValue(KEY_PREFIX + "max-idle-time", 60000));
        configuration.setMaxWaitTime(getLongValue(KEY_PREFIX + "max-wait-time", 120000));
        configuration.setSsl(getBooleanValue(KEY_PREFIX + "ssl", false));
        configuration.setSslInvalidHostAllowed(getBooleanValue(KEY_PREFIX + "ssl-invalid-host-allowed", false));
        configuration.setRetryWrites(getBooleanValue(KEY_PREFIX + "retry-writes", true));
        configuration.setRetryReads(getBooleanValue(KEY_PREFIX + "retry-reads", true));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
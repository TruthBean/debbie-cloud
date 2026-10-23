/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.dbcp2;

import com.truthbean.core.util.StringUtils;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.6.3
 */
public class Dbcp2Properties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<Dbcp2Configuration> {

    private final Map<String, Map<String, Dbcp2Configuration>> map = new HashMap<>();
    private Dbcp2Configuration configuration;

    private static final String DBCP2_X_KEY_PREFIX = "debbie.datasource.dbcp2.x.";
    private static final int DBCP2_X_KEY_PREFIX_LENGTH = 28;

    /**
     * key prefix of the dbcp2 properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.datasource.dbcp2.max-total}
     */
    private static final String KEY_PREFIX = "debbie.datasource.dbcp2.";

    public Dbcp2Properties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, Dbcp2Configuration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return map;
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        getConfiguration(applicationContext);
        return map.containsKey(profile) && map.get(profile).containsKey(category);
    }

    @Override
    public Set<String> getCategories(String profile) {
        return map.getOrDefault(profile, new HashMap<>()).keySet();
    }

    @Override
    public Dbcp2Configuration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public Dbcp2Configuration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new Dbcp2Configuration(applicationContext);

        // basic connection
        configuration.setDbcp2Url(getValue(KEY_PREFIX + "url"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setDbcp2Password(getValue(KEY_PREFIX + "password"));
        configuration.setDriverClassName(getValue(KEY_PREFIX + "driver-class-name"));

        // pool capacity
        configuration.setInitialSize(getIntegerValue(KEY_PREFIX + "initial-size", 0));
        configuration.setMaxTotal(getIntegerValue(KEY_PREFIX + "max-total", 8));
        configuration.setMaxIdle(getIntegerValue(KEY_PREFIX + "max-idle", 8));
        configuration.setMinIdle(getIntegerValue(KEY_PREFIX + "min-idle", 0));
        configuration.setMaxWaitMillis(getLongValue(KEY_PREFIX + "max-wait-millis", -1));

        // connection lifecycle
        configuration.setMaxConnLifetimeMillis(getLongValue(KEY_PREFIX + "max-conn-lifetime-millis", -1));
        configuration.setTimeBetweenEvictionRunsMillis(getLongValue(KEY_PREFIX + "time-between-eviction-runs-millis", -1));
        configuration.setMinEvictableIdleTimeMillis(getLongValue(KEY_PREFIX + "min-evictable-idle-time-millis", 1800000));
        configuration.setSoftMinEvictableIdleTimeMillis(getLongValue(KEY_PREFIX + "soft-min-evictable-idle-time-millis", -1));
        configuration.setNumTestsPerEvictionRun(getIntegerValue(KEY_PREFIX + "num-tests-per-eviction-run", 3));

        // validation
        configuration.setValidationQuery(getValue(KEY_PREFIX + "validation-query"));
        configuration.setValidationQueryTimeout(getIntegerValue(KEY_PREFIX + "validation-query-timeout", -1));
        configuration.setTestOnCreate(getBooleanValue(KEY_PREFIX + "test-on-create", false));
        configuration.setTestOnBorrow(getBooleanValue(KEY_PREFIX + "test-on-borrow", true));
        configuration.setTestOnReturn(getBooleanValue(KEY_PREFIX + "test-on-return", false));
        configuration.setTestWhileIdle(getBooleanValue(KEY_PREFIX + "test-while-idle", false));

        // prepared statement cache
        configuration.setPoolPreparedStatements(getBooleanValue(KEY_PREFIX + "pool-prepared-statements", false));
        configuration.setMaxOpenPreparedStatements(getIntegerValue(KEY_PREFIX + "max-open-prepared-statements", -1));

        // transaction
        configuration.setDefaultAutoCommit(getBooleanValue(KEY_PREFIX + "default-auto-commit", true));
        configuration.setDefaultReadOnly(getBooleanValue(KEY_PREFIX + "default-read-only", false));
        configuration.setDefaultTransactionIsolation(getIntegerValue(KEY_PREFIX + "default-transaction-isolation", -1));
        configuration.setDefaultCatalog(getValue(KEY_PREFIX + "default-catalog"));
        configuration.setDefaultSchema(getValue(KEY_PREFIX + "default-schema"));

        // connection behaviour
        configuration.setLifo(getBooleanValue(KEY_PREFIX + "lifo", true));
        configuration.setCacheState(getBooleanValue(KEY_PREFIX + "cache-state", true));
        configuration.setFastFailValidation(getBooleanValue(KEY_PREFIX + "fast-fail-validation", false));

        // abandoned connection handling
        configuration.setAbandonedUsageTracking(getBooleanValue(KEY_PREFIX + "abandoned-usage-tracking", false));
        configuration.setRemoveAbandonedOnBorrow(getBooleanValue(KEY_PREFIX + "remove-abandoned-on-borrow", false));
        configuration.setRemoveAbandonedOnMaintenance(getBooleanValue(KEY_PREFIX + "remove-abandoned-on-maintenance", false));
        configuration.setRemoveAbandonedTimeout(getIntegerValue(KEY_PREFIX + "remove-abandoned-timeout", 300));
        configuration.setLogAbandoned(getBooleanValue(KEY_PREFIX + "log-abandoned", false));

        // logging and monitoring
        configuration.setLogExpiredConnections(getBooleanValue(KEY_PREFIX + "log-expired-connections", true));

        // others
        configuration.setEvictionPolicyClassName(getValue(KEY_PREFIX + "eviction-policy-class-name"));
        configuration.setJmxName(getValue(KEY_PREFIX + "jmx-name"));
        configuration.setEnableAutoCommitOnReturn(getBooleanValue(KEY_PREFIX + "enable-auto-commit-on-return", true));
        configuration.setRollbackOnReturn(getBooleanValue(KEY_PREFIX + "rollback-on-return", false));
        var connectionInitSqls = getStringListValue(KEY_PREFIX + "connection-init-sqls", ";");
        if (connectionInitSqls != null) {
            configuration.setConnectionInitSqls(connectionInitSqls);
        }

        final Map<String, String> matchedKey = getMatchedKey(DBCP2_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(DBCP2_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getBasicDataSource().addConnectionProperty(k, value);
        });
        Map<String, Dbcp2Configuration> configurationMap = new HashMap<>();
        configurationMap.put(DEFAULT_CATEGORY, configuration);
        map.put(DEFAULT_PROFILE, configurationMap);
        return configuration;
    }

    @Override
    public void close() {
        map.forEach((k, m) -> m.clear());
        map.clear();
        configuration = null;
    }
}
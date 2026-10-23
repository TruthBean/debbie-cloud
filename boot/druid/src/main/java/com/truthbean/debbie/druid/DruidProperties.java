/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.druid;

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
public class DruidProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<DruidConfiguration> {

    private final Map<String, Map<String, DruidConfiguration>> map = new HashMap<>();
    private DruidConfiguration configuration;

    //=================================================================================================================
    /**
     * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE
     *
     * key name is snake case
     */
    private static final String DRUID_X_KEY_PREFIX = "debbie.datasource.druid.x.";
    private static final int DRUID_X_KEY_PREFIX_LENGTH = 27;

    /**
     * key prefix of the druid properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.datasource.druid.max-active}
     */
    private static final String KEY_PREFIX = "debbie.datasource.druid.";
    //=================================================================================================================

    public DruidProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, DruidConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public DruidConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public DruidConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new DruidConfiguration(applicationContext);

        // basic connection
        configuration.setDruidUrl(getValue(KEY_PREFIX + "url"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setDruidPassword(getValue(KEY_PREFIX + "password"));
        configuration.setDriverClassName(getValue(KEY_PREFIX + "driver-class-name"));

        // pool capacity and lifecycle
        configuration.setInitialSize(getIntegerValue(KEY_PREFIX + "initial-size", 0));
        configuration.setMinIdle(getIntegerValue(KEY_PREFIX + "min-idle", 0));
        configuration.setMaxActive(getIntegerValue(KEY_PREFIX + "max-active", 8));
        configuration.setMaxWait(getLongValue(KEY_PREFIX + "max-wait", -1));
        configuration.setTimeBetweenEvictionRunsMillis(getLongValue(KEY_PREFIX + "time-between-eviction-runs-millis", 60000));
        configuration.setMinEvictableIdleTimeMillis(getLongValue(KEY_PREFIX + "min-evictable-idle-time-millis", 1800000));

        // validation
        configuration.setValidationQuery(getValue(KEY_PREFIX + "validation-query"));
        configuration.setValidationQueryTimeout(getIntegerValue(KEY_PREFIX + "validation-query-timeout", -1));
        configuration.setTestWhileIdle(getBooleanValue(KEY_PREFIX + "test-while-idle", true));
        configuration.setTestOnBorrow(getBooleanValue(KEY_PREFIX + "test-on-borrow", true));
        configuration.setTestOnReturn(getBooleanValue(KEY_PREFIX + "test-on-return", false));

        // prepared statement cache
        configuration.setPoolPreparedStatements(getBooleanValue(KEY_PREFIX + "pool-prepared-statements", false));
        configuration.setMaxPoolPreparedStatementPerConnectionSize(getIntegerValue(KEY_PREFIX + "max-pool-prepared-statement-per-connection-size", 10));

        // filters and connection properties
        configuration.setFilters(getValue(KEY_PREFIX + "filters"));
        configuration.setConnectionProperties(getValue(KEY_PREFIX + "connection-properties"));

        // transaction and abandoned connection
        configuration.setDefaultAutoCommit(getBooleanValue(KEY_PREFIX + "default-auto-commit", true));
        configuration.setRemoveAbandoned(getBooleanValue(KEY_PREFIX + "remove-abandoned", false));
        configuration.setRemoveAbandonedTimeoutMillis(getLongValue(KEY_PREFIX + "remove-abandoned-timeout-millis", 300000));
        configuration.setLogAbandoned(getBooleanValue(KEY_PREFIX + "log-abandoned", false));

        // keep alive
        configuration.setKeepAlive(getBooleanValue(KEY_PREFIX + "keep-alive", false));
        configuration.setKeepAliveBetweenTimeMillis(getLongValue(KEY_PREFIX + "keep-alive-between-time-millis", 120000));
        configuration.setMaxEvictableIdleTimeMillis(getLongValue(KEY_PREFIX + "max-evictable-idle-time-millis", 25200000));
        configuration.setTimeBetweenConnectErrorMillis(getLongValue(KEY_PREFIX + "time-between-connect-error-millis", 500));

        // more transaction settings
        configuration.setDefaultTransactionIsolation(getIntegerValue(KEY_PREFIX + "default-transaction-isolation", -1));
        configuration.setDefaultReadOnly(getBooleanValue(KEY_PREFIX + "default-read-only", false));
        configuration.setTransactionQueryTimeout(getIntegerValue(KEY_PREFIX + "transaction-query-timeout", 0));
        configuration.setTransactionThresholdMillis(getLongValue(KEY_PREFIX + "transaction-threshold-millis", 0));

        // initialization
        configuration.setAsyncInit(getBooleanValue(KEY_PREFIX + "async-init", false));
        configuration.setInitVariants(getBooleanValue(KEY_PREFIX + "init-variants", false));
        configuration.setInitGlobalVariants(getBooleanValue(KEY_PREFIX + "init-global-variants", false));

        // locking and retry
        configuration.setUseUnfairLock(getBooleanValue(KEY_PREFIX + "use-unfair-lock", false));
        configuration.setMaxWaitThreadCount(getIntegerValue(KEY_PREFIX + "max-wait-thread-count", -1));
        configuration.setNotFullTimeoutRetryCount(getIntegerValue(KEY_PREFIX + "not-full-timeout-retry-count", 0));
        configuration.setMaxCreateTaskCount(getIntegerValue(KEY_PREFIX + "max-create-task-count", 3));
        configuration.setConnectionErrorRetryAttempts(getIntegerValue(KEY_PREFIX + "connection-error-retry-attempts", 1));
        configuration.setBreakAfterAcquireFailure(getBooleanValue(KEY_PREFIX + "break-after-acquire-failure", false));
        configuration.setExceptionSorter(getValue(KEY_PREFIX + "exception-sorter"));

        // statistics
        configuration.setUseGlobalDataSourceStat(getBooleanValue(KEY_PREFIX + "use-global-data-source-stat", false));
        configuration.setTimeBetweenLogStatsMillis(getLongValue(KEY_PREFIX + "time-between-log-stats-millis", 0));
        configuration.setResetStatEnable(getBooleanValue(KEY_PREFIX + "reset-stat-enable", true));

        // others
        configuration.setName(getValue(KEY_PREFIX + "name"));
        configuration.setPhyTimeoutMillis(getLongValue(KEY_PREFIX + "phy-timeout-millis", -1));
        configuration.setPhyMaxUseCount(getLongValue(KEY_PREFIX + "phy-max-use-count", -1));
        configuration.setDupCloseLogEnable(getBooleanValue(KEY_PREFIX + "dup-close-log-enable", false));
        configuration.setAccessToUnderlyingConnectionAllowed(getBooleanValue(KEY_PREFIX + "access-to-underlying-connection-allowed", false));
        configuration.setRemoveAbandonedTimeout(getIntegerValue(KEY_PREFIX + "remove-abandoned-timeout", 300));
        configuration.setSharePreparedStatements(getBooleanValue(KEY_PREFIX + "share-prepared-statements", false));
        configuration.setClearFiltersEnable(getBooleanValue(KEY_PREFIX + "clear-filters-enable", true));
        var connectionInitSqls = getStringListValue(KEY_PREFIX + "connection-init-sqls", ";");
        if (connectionInitSqls != null) {
            configuration.setConnectionInitSqls(connectionInitSqls);
        }

        final Map<String, String> matchedKey = getMatchedKey(DRUID_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(DRUID_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getDruidDataSource().addConnectionProperty(k, value);
        });
        Map<String, DruidConfiguration> configurationMap = new HashMap<>();
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
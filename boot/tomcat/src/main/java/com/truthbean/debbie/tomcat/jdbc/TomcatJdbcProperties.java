/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tomcat.jdbc;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class TomcatJdbcProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<TomcatJdbcConfiguration> {

    private final Map<String, Map<String, TomcatJdbcConfiguration>> map = new HashMap<>();
    private TomcatJdbcConfiguration configuration;

    //=================================================================================================================
    /**
     * key name is snake case
     */
    private static final String TOMCAT_JDBC_X_KEY_PREFIX = "debbie.datasource.tomcat-jdbc.x.";
    private static final int TOMCAT_JDBC_X_KEY_PREFIX_LENGTH = 37;

    /**
     * key prefix of the tomcat jdbc connection pool properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.datasource.tomcat-jdbc.initial-size}
     */
    private static final String KEY_PREFIX = "debbie.datasource.tomcat-jdbc.";
    //=================================================================================================================

    public TomcatJdbcProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, TomcatJdbcConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public TomcatJdbcConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public TomcatJdbcConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new TomcatJdbcConfiguration(applicationContext);

        // driver and connection
        configuration.setDriverClassName(getValue(KEY_PREFIX + "driver-class-name"));
        configuration.setJdbcUrl(getValue(KEY_PREFIX + "jdbc-url"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setPassword(getValue(KEY_PREFIX + "password"));

        // pool size and timeout
        configuration.setInitialSize(getIntegerValue(KEY_PREFIX + "initial-size", 10));
        configuration.setMaxActive(getIntegerValue(KEY_PREFIX + "max-active", 100));
        configuration.setMaxIdle(getIntegerValue(KEY_PREFIX + "max-idle", 100));
        configuration.setMinIdle(getIntegerValue(KEY_PREFIX + "min-idle", 10));
        configuration.setMaxWait(getIntegerValue(KEY_PREFIX + "max-wait", 30000));

        // validation
        configuration.setValidationQuery(getValue(KEY_PREFIX + "validation-query"));
        configuration.setValidationQueryTimeout(getIntegerValue(KEY_PREFIX + "validation-query-timeout", 3000));
        configuration.setValidationInterval(getLongValue(KEY_PREFIX + "validation-interval", 30000));
        configuration.setTestOnBorrow(getBooleanValue(KEY_PREFIX + "test-on-borrow", false));
        configuration.setTestOnReturn(getBooleanValue(KEY_PREFIX + "test-on-return", false));
        configuration.setTestWhileIdle(getBooleanValue(KEY_PREFIX + "test-while-idle", false));
        configuration.setTestOnConnect(getBooleanValue(KEY_PREFIX + "test-on-connect", false));
        configuration.setTimeBetweenEvictionRunsMillis(getIntegerValue(KEY_PREFIX + "time-between-eviction-runs-millis", 5000));
        configuration.setMinEvictableIdleTimeMillis(getIntegerValue(KEY_PREFIX + "min-evictable-idle-time-millis", 60000));
        configuration.setNumTestsPerEvictionRun(getIntegerValue(KEY_PREFIX + "num-tests-per-eviction-run", 3));

        // nullable connection properties, keep null to fall back to the driver default
        String defaultAutoCommit = getValue(KEY_PREFIX + "default-auto-commit");
        if (defaultAutoCommit != null) {
            configuration.setDefaultAutoCommit(Boolean.parseBoolean(defaultAutoCommit));
        }
        String defaultReadOnly = getValue(KEY_PREFIX + "default-read-only");
        if (defaultReadOnly != null) {
            configuration.setDefaultReadOnly(Boolean.parseBoolean(defaultReadOnly));
        }
        String defaultTransactionIsolation = getValue(KEY_PREFIX + "default-transaction-isolation");
        if (defaultTransactionIsolation != null && !defaultTransactionIsolation.isBlank()) {
            configuration.setDefaultTransactionIsolation(TransactionIsolationLevel.valueOf(defaultTransactionIsolation.trim().toUpperCase()));
        }
        configuration.setDefaultCatalog(getValue(KEY_PREFIX + "default-catalog"));

        // abandoned connection handling
        configuration.setRemoveAbandoned(getBooleanValue(KEY_PREFIX + "remove-abandoned", false));
        configuration.setRemoveAbandonedTimeout(getIntegerValue(KEY_PREFIX + "remove-abandoned-timeout", 60));
        configuration.setLogAbandoned(getBooleanValue(KEY_PREFIX + "log-abandoned", false));
        configuration.setAbandonWhenPercentageFull(getIntegerValue(KEY_PREFIX + "abandon-when-percentage-full", 0));

        // interceptors and custom connection settings
        configuration.setJdbcInterceptors(getValue(KEY_PREFIX + "jdbc-interceptors"));
        configuration.setConnectionProperties(getValue(KEY_PREFIX + "connection-properties"));
        configuration.setInitSQL(getValue(KEY_PREFIX + "init-sql"));
        configuration.setName(getValue(KEY_PREFIX + "name"));
        configuration.setJmxEnabled(getBooleanValue(KEY_PREFIX + "jmx-enabled", true));
        configuration.setFairQueue(getBooleanValue(KEY_PREFIX + "fair-queue", true));
        configuration.setAccessToUnderlyingConnectionAllowed(getBooleanValue(KEY_PREFIX + "access-to-underlying-connection-allowed", false));
        configuration.setSuspectTimeout(getIntegerValue(KEY_PREFIX + "suspect-timeout", 300));
        configuration.setCommitOnReturn(getBooleanValue(KEY_PREFIX + "commit-on-return", false));
        configuration.setRollbackOnReturn(getBooleanValue(KEY_PREFIX + "rollback-on-return", false));
        configuration.setValidatorClassName(getValue(KEY_PREFIX + "validator-class-name"));
        // the validator instance and the wrapped data source can only be set programmatically, not from properties
        configuration.setUseEquals(getBooleanValue(KEY_PREFIX + "use-equals", true));
        configuration.setMaxAge(getLongValue(KEY_PREFIX + "max-age", 0));
        configuration.setUseLock(getBooleanValue(KEY_PREFIX + "use-lock", true));
        configuration.setDataSourceJNDI(getValue(KEY_PREFIX + "data-source-jndi"));
        configuration.setAlternateUsernameAllowed(getBooleanValue(KEY_PREFIX + "alternate-username-allowed", false));
        configuration.setUseDisposableConnectionFacade(getBooleanValue(KEY_PREFIX + "use-disposable-connection-facade", true));
        configuration.setLogValidationErrors(getBooleanValue(KEY_PREFIX + "log-validation-errors", false));
        configuration.setPropagateInterruptState(getBooleanValue(KEY_PREFIX + "propagate-interrupt-state", false));
        configuration.setIgnoreExceptionOnPreLoad(getBooleanValue(KEY_PREFIX + "ignore-exception-on-pre-load", false));
        configuration.setUseStatementFacade(getBooleanValue(KEY_PREFIX + "use-statement-facade", true));

        final Map<String, String> matchedKey = getMatchedKey(TOMCAT_JDBC_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(TOMCAT_JDBC_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getPoolProperties().setDbProperties(mergeProperty(configuration.getPoolProperties().getDbProperties(), k, value));
        });
        Map<String, TomcatJdbcConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(DEFAULT_CATEGORY, configuration);
        map.put(DEFAULT_PROFILE, configurationMap);
        return configuration;
    }

    private java.util.Properties mergeProperty(java.util.Properties properties, String key, String value) {
        if (properties == null) {
            properties = new java.util.Properties();
        }
        properties.setProperty(key, value);
        return properties;
    }

    @Override
    public void close() {
        map.forEach((k, m) -> m.clear());
        map.clear();
        configuration = null;
    }
}
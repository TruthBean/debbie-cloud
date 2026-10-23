/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.hikari;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 22:46.
 */
public class HikariProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<HikariConfiguration> {

    private final Map<String, Map<String, HikariConfiguration>> map = new HashMap<>();
    private HikariConfiguration configuration;

    //=================================================================================================================
    /**
     * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md
     *
     *  key name is snake case
     */
    private static final String HIKARI_X_KEY_PREFIX = "debbie.datasource.hikari.x.";
    private static final int HIKARI_X_KEY_PREFIX_LENGTH = 27;

    /**
     * key prefix of the hikari properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.datasource.hikari.jdbc-url}
     */
    private static final String KEY_PREFIX = "debbie.datasource.hikari.";
    //=================================================================================================================

    public HikariProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, HikariConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public HikariConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public HikariConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new HikariConfiguration(applicationContext);

        // datasource and connection
        configuration.setDataSourceClassName(getValue(KEY_PREFIX + "datasource-class-name"));
        configuration.setJdbcUrl(getValue(KEY_PREFIX + "jdbc-url"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setHikariPassword(getValue(KEY_PREFIX + "password"));
        configuration.setDriverClassName(getValue(KEY_PREFIX + "driver-class-name"));

        // pool capacity and timeout
        configuration.setHikariAutoCommit(Boolean.valueOf(getBooleanValue(KEY_PREFIX + "auto-commit", false)));
        configuration.setConnectionTimeout(getLongValue(KEY_PREFIX + "connection-timeout", 30000));
        configuration.setIdleTimeout(getLongValue(KEY_PREFIX + "idle-timeout", 600000));
        configuration.setKeepaliveTime(getLongValue(KEY_PREFIX + "keepalive-time", 600000));
        configuration.setMaxLifetime(getLongValue(KEY_PREFIX + "max-lifetime", 1800000));
        configuration.setMaximumPoolSize(getIntegerValue(KEY_PREFIX + "max-pool-size", 10));
        configuration.setMinimumIdle(getIntegerValue(KEY_PREFIX + "min-idle", 10));
        configuration.setInitializationFailTimeout(getLongValue(KEY_PREFIX + "init-fail-timeout", 1));
        configuration.setValidationTimeout(getLongValue(KEY_PREFIX + "validate-timeout", 5000));
        configuration.setLeakDetectionThreshold(getLongValue(KEY_PREFIX + "leak-detection-threshold", 0));

        // validation and transaction
        configuration.setConnectionTestQuery(getValue(KEY_PREFIX + "connection-test-query"));
        configuration.setConnectionInitSql(getValue(KEY_PREFIX + "connection-init-sql"));
        String transactionIsolation = getValue(KEY_PREFIX + "transaction-isolation");
        if (transactionIsolation != null && !transactionIsolation.isBlank()) {
            configuration.setTransactionIsolation(TransactionIsolationLevel.valueOf(transactionIsolation.trim().toUpperCase()));
        }

        // catalog and schema
        configuration.setCatalog(getValue(KEY_PREFIX + "catalog"));
        configuration.setSchema(getValue(KEY_PREFIX + "schema"));

        // pool behaviour
        configuration.setReadOnly(getBooleanValue(KEY_PREFIX + "read-only", false));
        configuration.setRegisterMbeans(getBooleanValue(KEY_PREFIX + "register-mbeans", false));
        configuration.setIsolateInternalQueries(getBooleanValue(KEY_PREFIX + "isolate-internal-queries", false));
        configuration.setAllowPoolSuspension(getBooleanValue(KEY_PREFIX + "allow-pool-suspension", false));
        configuration.setPoolName(getValue(KEY_PREFIX + "pool-name"));
        // the metric registry and the health check registry are read as their string names
        configuration.setMetricRegistry(getValue(KEY_PREFIX + "metric-registry"));
        configuration.setHealthCheckRegistry(getValue(KEY_PREFIX + "health-check-registry"));
        // dataSource, threadFactory, scheduledExecutor, exceptionOverride and
        // exceptionOverrideClassName can only be set programmatically, not from properties

        final Map<String, String> matchedKey = getMatchedKey(HIKARI_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(HIKARI_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getHikariConfig().addDataSourceProperty(k, value);
        });
        Map<String, HikariConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(DEFAULT_CATEGORY, configuration);
        map.put(DEFAULT_PROFILE, configurationMap);
        // configuration.getDriverProperties().put("dataSource.logWriter", new PrintWriter(Console));
        return configuration;
    }

    @Override
    public void close() {
        map.forEach((k, m) -> m.clear());
        map.clear();
        configuration = null;
    }
}

/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.c3p0;

import com.truthbean.core.util.StringUtils;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class C3p0Properties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<C3p0Configuration> {

    private final Map<String, Map<String, C3p0Configuration>> map = new HashMap<>();
    private C3p0Configuration configuration;

    //=================================================================================================================
    /**
     * https://www.mchange.com/projects/c3p0/#configuration_properties
     *
     * key name is snake case
     */
    private static final String C3P0_X_KEY_PREFIX = "debbie.datasource.c3p0.x.";
    private static final int C3P0_X_KEY_PREFIX_LENGTH = 25;

    /**
     * key prefix of the c3p0 properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.datasource.c3p0.initial-pool-size}
     */
    private static final String KEY_PREFIX = "debbie.datasource.c3p0.";
    //=================================================================================================================

    public C3p0Properties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, C3p0Configuration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
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
    public C3p0Configuration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return configuration;
    }

    @Override
    public C3p0Configuration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new C3p0Configuration(applicationContext);

        // basic connection
        configuration.setDriverClassName(getValue(KEY_PREFIX + "driver-class-name"));
        configuration.setC3p0Url(getValue(KEY_PREFIX + "jdbc-url"));
        configuration.setUsername(getValue(KEY_PREFIX + "username"));
        configuration.setC3p0Password(getValue(KEY_PREFIX + "password"));
        configuration.setDescription(getValue(KEY_PREFIX + "description"));
        configuration.setForceUseNamedDriverClass(getBooleanValue(KEY_PREFIX + "force-use-named-driver-class", false));
        configuration.setOverrideDefaultUser(getValue(KEY_PREFIX + "override-default-user"));
        configuration.setOverrideDefaultPassword(getValue(KEY_PREFIX + "override-default-password"));

        // pool capacity
        configuration.setInitialPoolSize(getIntegerValue(KEY_PREFIX + "initial-pool-size", 3));
        configuration.setMinPoolSize(getIntegerValue(KEY_PREFIX + "min-pool-size", 3));
        configuration.setMaxPoolSize(getIntegerValue(KEY_PREFIX + "max-pool-size", 15));
        configuration.setAcquireIncrement(getIntegerValue(KEY_PREFIX + "acquire-increment", 3));
        configuration.setMaxIdleTime(getIntegerValue(KEY_PREFIX + "max-idle-time", 0));
        configuration.setMaxIdleTimeExcessConnections(getIntegerValue(KEY_PREFIX + "max-idle-time-excess-connections", 0));
        configuration.setMaxConnectionAge(getIntegerValue(KEY_PREFIX + "max-connection-age", 0));
        configuration.setMaxStatements(getIntegerValue(KEY_PREFIX + "max-statements", 0));
        configuration.setMaxStatementsPerConnection(getIntegerValue(KEY_PREFIX + "max-statements-per-connection", 0));
        configuration.setPropertyCycle(getIntegerValue(KEY_PREFIX + "property-cycle", 0));
        configuration.setNumHelperThreads(getIntegerValue(KEY_PREFIX + "num-helper-threads", 3));
        configuration.setMaxAdministrativeTaskTime(getIntegerValue(KEY_PREFIX + "max-administrative-task-time", 0));

        // acquire a connection
        configuration.setCheckoutTimeout(getIntegerValue(KEY_PREFIX + "checkout-timeout", 0));
        configuration.setAcquireRetryAttempts(getIntegerValue(KEY_PREFIX + "acquire-retry-attempts", 30));
        configuration.setAcquireRetryDelay(getIntegerValue(KEY_PREFIX + "acquire-retry-delay", 1000));
        configuration.setBreakAfterAcquireFailure(getBooleanValue(KEY_PREFIX + "break-after-acquire-failure", false));

        // connection testing
        configuration.setPreferredTestQuery(getValue(KEY_PREFIX + "preferred-test-query"));
        configuration.setAutomaticTestTable(getValue(KEY_PREFIX + "automatic-test-table"));
        configuration.setTestConnectionOnCheckout(getBooleanValue(KEY_PREFIX + "test-connection-on-checkout", false));
        configuration.setTestConnectionOnCheckin(getBooleanValue(KEY_PREFIX + "test-connection-on-checkin", false));
        configuration.setIdleConnectionTestPeriod(getIntegerValue(KEY_PREFIX + "idle-connection-test-period", 0));
        configuration.setConnectionTesterClassName(getValue(KEY_PREFIX + "connection-tester-class-name"));
        configuration.setConnectionCustomizerClassName(getValue(KEY_PREFIX + "connection-customizer-class-name"));

        // lifecycle and transaction
        configuration.setAutoCommitOnClose(getBooleanValue(KEY_PREFIX + "auto-commit-on-close", false));
        configuration.setForceIgnoreUnresolvedTransactions(getBooleanValue(KEY_PREFIX + "force-ignore-unresolved-transactions", false));
        configuration.setUnreturnedConnectionTimeout(getIntegerValue(KEY_PREFIX + "unreturned-connection-timeout", 0));
        configuration.setDebugUnreturnedConnectionStackTraces(getBooleanValue(KEY_PREFIX + "debug-unreturned-connection-stack-traces", false));

        // others
        configuration.setPrivilegeSpawnedThreads(getBooleanValue(KEY_PREFIX + "privilege-spawned-threads", false));
        configuration.setForceSynchronousCheckins(getBooleanValue(KEY_PREFIX + "force-synchronous-checkins", false));
        configuration.setDataSourceName(getValue(KEY_PREFIX + "data-source-name"));
        configuration.setContextClassLoaderSource(getValue(KEY_PREFIX + "context-class-loader-source"));
        configuration.setFactoryClassLocation(getValue(KEY_PREFIX + "factory-class-location"));
        configuration.setUserOverridesAsString(getValue(KEY_PREFIX + "user-overrides-as-string"));
        configuration.setLoginTimeout(getIntegerValue(KEY_PREFIX + "login-timeout", 0));

        final Map<String, String> matchedKey = getMatchedKey(C3P0_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(C3P0_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            var dataSource = configuration.getComboPooledDataSource();
            Properties properties = dataSource.getProperties();
            if (properties == null) {
                properties = new Properties();
            }
            properties.setProperty(k, value);
            dataSource.setProperties(properties);
        });
        Map<String, C3p0Configuration> configurationMap = new HashMap<>();
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

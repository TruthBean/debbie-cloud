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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

import java.beans.PropertyVetoException;

/**
 * c3p0 connection pool configuration.
 * <p>
 * see: https://www.mchange.com/projects/c3p0/#configuration_properties
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.datasource.c3p0.")
public class C3p0Configuration extends DataSourceConfiguration implements DebbieConfiguration {

    private final ComboPooledDataSource comboPooledDataSource;

    // ======================== 基础连接配置 ========================

    /**
     * The fully qualified Java class name of the JDBC driver to be used.
     */
    @PropertyInject(value = "driver-class-name")
    private String driverClassName;

    /**
     * The JDBC url of the database.
     */
    @PropertyInject(value = "jdbc-url")
    private String c3p0Url;

    /**
     * The username of the database.
     */
    @PropertyInject(value = "username")
    private String username;

    /**
     * The password of the database.
     */
    @PropertyInject(value = "password")
    private String c3p0Password;

    /**
     * The description of the data source.
     */
    @PropertyInject(value = "description")
    private String description;

    /**
     * Whether to force the use of the named driver class.
     * Default: false
     */
    @PropertyInject(value = "force-use-named-driver-class", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean forceUseNamedDriverClass;

    /**
     * The default user to be used when creating new connections,
     * which overrides the user obtained from the DataSource.
     */
    @PropertyInject(value = "override-default-user")
    private String overrideDefaultUser;

    /**
     * The default password to be used when creating new connections,
     * which overrides the password obtained from the DataSource.
     */
    @PropertyInject(value = "override-default-password")
    private String overrideDefaultPassword;

    // ======================== 连接池容量 ========================

    /**
     * The initial number of connections created when the pool is started.
     * Default: 3
     */
    @PropertyInject(value = "initial-pool-size", transformer = IntegerTransformer.class, defaultValue = "3")
    private int initialPoolSize;

    /**
     * The minimum number of connections kept in the pool at all times.
     * Default: 3
     */
    @PropertyInject(value = "min-pool-size", transformer = IntegerTransformer.class, defaultValue = "3")
    private int minPoolSize;

    /**
     * The maximum number of connections kept in the pool.
     * Default: 15
     */
    @PropertyInject(value = "max-pool-size", transformer = IntegerTransformer.class, defaultValue = "15")
    private int maxPoolSize;

    /**
     * The number of connections created at a time when the pool runs out.
     * Default: 3
     */
    @PropertyInject(value = "acquire-increment", transformer = IntegerTransformer.class, defaultValue = "3")
    private int acquireIncrement;

    /**
     * The maximum idle time of a connection, in seconds. 0 means no limit.
     * Default: 0
     */
    @PropertyInject(value = "max-idle-time", transformer = IntegerTransformer.class, defaultValue = "0")
    private int maxIdleTime;

    /**
     * The maximum idle time for connections over the minimum pool size, in seconds.
     * Default: 0
     */
    @PropertyInject(value = "max-idle-time-excess-connections", transformer = IntegerTransformer.class, defaultValue = "0")
    private int maxIdleTimeExcessConnections;

    /**
     * The maximum age of a connection, in seconds. 0 means no limit.
     * Default: 0
     */
    @PropertyInject(value = "max-connection-age", transformer = IntegerTransformer.class, defaultValue = "0")
    private int maxConnectionAge;

    /**
     * The total number of PreparedStatements cached for the pool. 0 means no statement caching.
     * Default: 0
     */
    @PropertyInject(value = "max-statements", transformer = IntegerTransformer.class, defaultValue = "0")
    private int maxStatements;

    /**
     * The number of PreparedStatements cached per connection. 0 means no statement caching.
     * Default: 0
     */
    @PropertyInject(value = "max-statements-per-connection", transformer = IntegerTransformer.class, defaultValue = "0")
    private int maxStatementsPerConnection;

    /**
     * The time interval in seconds after which the pool should recycle connections
     * when the configuration properties change. 0 means no recycle.
     * Default: 0
     */
    @PropertyInject(value = "property-cycle", transformer = IntegerTransformer.class, defaultValue = "0")
    private int propertyCycle;

    /**
     * The number of helper threads the pool should maintain for handling asynchronous tasks.
     * Default: 3
     */
    @PropertyInject(value = "num-helper-threads", transformer = IntegerTransformer.class, defaultValue = "3")
    private int numHelperThreads;

    /**
     * The maximum time in seconds the pool will spend on administrative tasks
     * before giving up. 0 means no timeout.
     * Default: 0
     */
    @PropertyInject(value = "max-administrative-task-time", transformer = IntegerTransformer.class, defaultValue = "0")
    private int maxAdministrativeTaskTime;

    // ======================== 获取连接 ========================

    /**
     * The timeout in milliseconds that clients will wait for a connection
     * from the pool. 0 means no timeout.
     * Default: 0
     */
    @PropertyInject(value = "checkout-timeout", transformer = IntegerTransformer.class, defaultValue = "0")
    private int checkoutTimeout;

    /**
     * The number of times the pool will try to acquire a new Connection
     * before giving up.
     * Default: 30
     */
    @PropertyInject(value = "acquire-retry-attempts", transformer = IntegerTransformer.class, defaultValue = "30")
    private int acquireRetryAttempts;

    /**
     * The delay in milliseconds between successive acquisition attempts.
     * Default: 1000
     */
    @PropertyInject(value = "acquire-retry-delay", transformer = IntegerTransformer.class, defaultValue = "1000")
    private int acquireRetryDelay;

    /**
     * Whether the pool should break after a failed acquisition attempt.
     * Default: false
     */
    @PropertyInject(value = "break-after-acquire-failure", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean breakAfterAcquireFailure;

    // ======================== 连接测试 ========================

    /**
     * The query used to test connections. If not set, a driver-specific
     * test will be used if possible.
     */
    @PropertyInject(value = "preferred-test-query")
    private String preferredTestQuery;

    /**
     * The name of a table the pool can create and use to test connections.
     */
    @PropertyInject(value = "automatic-test-table")
    private String automaticTestTable;

    /**
     * Whether to test each connection when it is checked out of the pool.
     * Default: false
     */
    @PropertyInject(value = "test-connection-on-checkout", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testConnectionOnCheckout;

    /**
     * Whether to test each connection when it is checked back into the pool.
     * Default: false
     */
    @PropertyInject(value = "test-connection-on-checkin", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testConnectionOnCheckin;

    /**
     * The time interval in seconds between idle connection tests. 0 means no testing.
     * Default: 0
     */
    @PropertyInject(value = "idle-connection-test-period", transformer = IntegerTransformer.class, defaultValue = "0")
    private int idleConnectionTestPeriod;

    /**
     * The fully qualified class name of the ConnectionTester used to test connections.
     */
    @PropertyInject(value = "connection-tester-class-name")
    private String connectionTesterClassName;

    /**
     * The fully qualified class name of the ConnectionCustomizer used
     * to customize connections as they are created and checked in/out.
     */
    @PropertyInject(value = "connection-customizer-class-name")
    private String connectionCustomizerClassName;

    // ======================== 生命周期与事务 ========================

    /**
     * Whether to commit or rollback pending transactions when a connection is closed.
     * Default: false (rollback)
     */
    @PropertyInject(value = "auto-commit-on-close", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean autoCommitOnClose;

    /**
     * Whether to force ignoring unresolved transactions when a connection is closed.
     * Default: false
     */
    @PropertyInject(value = "force-ignore-unresolved-transactions", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean forceIgnoreUnresolvedTransactions;

    /**
     * The time in seconds after which an unreturned connection is forcibly closed.
     * 0 means no limit.
     * Default: 0
     */
    @PropertyInject(value = "unreturned-connection-timeout", transformer = IntegerTransformer.class, defaultValue = "0")
    private int unreturnedConnectionTimeout;

    /**
     * Whether to log the stack traces of unreturned connections when they are forcibly closed.
     * Default: false
     */
    @PropertyInject(value = "debug-unreturned-connection-stack-traces", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean debugUnreturnedConnectionStackTraces;

    // ======================== 其他 ========================

    /**
     * Whether to spawn threads with the privileges of the pool's owner.
     * Default: false
     */
    @PropertyInject(value = "privilege-spawned-threads", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean privilegeSpawnedThreads;

    /**
     * Whether to force synchronous checkins.
     * Default: false
     */
    @PropertyInject(value = "force-synchronous-checkins", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean forceSynchronousCheckins;

    /**
     * The name of the data source.
     */
    @PropertyInject(value = "data-source-name")
    private String dataSourceName;

    /**
     * The class loader source used to load classes. "library", "thread" or "none".
     */
    @PropertyInject(value = "context-class-loader-source")
    private String contextClassLoaderSource;

    /**
     * The location of the c3p0 class files, used to load c3p0 classes from a custom location.
     */
    @PropertyInject(value = "factory-class-location")
    private String factoryClassLocation;

    /**
     * A comma-separated list of per-user overrides.
     */
    @PropertyInject(value = "user-overrides-as-string")
    private String userOverridesAsString;

    /**
     * The login timeout in seconds for the underlying driver.
     * Default: 0
     */
    @PropertyInject(value = "login-timeout", transformer = IntegerTransformer.class, defaultValue = "0")
    private int loginTimeout;

    // ================================================================

    public C3p0Configuration(ApplicationContext applicationContext) {
        super(new DataSourceProperties(applicationContext).getConfiguration(applicationContext));
        this.comboPooledDataSource = new ComboPooledDataSource();
        super.setDataSourceFactoryClass(C3p0DataSourceFactory.class);
    }

    public ComboPooledDataSource getComboPooledDataSource() {
        return comboPooledDataSource;
    }

    // ======================== Getter/Setter ========================

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        if (driverClassName != null && !driverClassName.isBlank()) {
            try {
                this.comboPooledDataSource.setDriverClass(driverClassName);
            } catch (PropertyVetoException e) {
                throw new IllegalArgumentException("invalid driver class name: " + driverClassName, e);
            }
        }
    }

    public String getC3p0Url() {
        return c3p0Url;
    }

    public void setC3p0Url(String c3p0Url) {
        this.c3p0Url = c3p0Url;
        if (c3p0Url != null && !c3p0Url.isBlank())
            this.comboPooledDataSource.setJdbcUrl(c3p0Url);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null && !username.isBlank())
            this.comboPooledDataSource.setUser(username);
    }

    public String getC3p0Password() {
        return c3p0Password;
    }

    public void setC3p0Password(String c3p0Password) {
        this.c3p0Password = c3p0Password;
        if (c3p0Password != null && !c3p0Password.isBlank())
            this.comboPooledDataSource.setPassword(c3p0Password);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (description != null && !description.isBlank())
            this.comboPooledDataSource.setDescription(description);
    }

    public boolean isForceUseNamedDriverClass() {
        return forceUseNamedDriverClass;
    }

    public void setForceUseNamedDriverClass(boolean forceUseNamedDriverClass) {
        this.forceUseNamedDriverClass = forceUseNamedDriverClass;
        this.comboPooledDataSource.setForceUseNamedDriverClass(forceUseNamedDriverClass);
    }

    public String getOverrideDefaultUser() {
        return overrideDefaultUser;
    }

    public void setOverrideDefaultUser(String overrideDefaultUser) {
        this.overrideDefaultUser = overrideDefaultUser;
        if (overrideDefaultUser != null && !overrideDefaultUser.isBlank())
            this.comboPooledDataSource.setOverrideDefaultUser(overrideDefaultUser);
    }

    public String getOverrideDefaultPassword() {
        return overrideDefaultPassword;
    }

    public void setOverrideDefaultPassword(String overrideDefaultPassword) {
        this.overrideDefaultPassword = overrideDefaultPassword;
        if (overrideDefaultPassword != null && !overrideDefaultPassword.isBlank())
            this.comboPooledDataSource.setOverrideDefaultPassword(overrideDefaultPassword);
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
        this.comboPooledDataSource.setInitialPoolSize(initialPoolSize);
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
        this.comboPooledDataSource.setMinPoolSize(minPoolSize);
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        this.comboPooledDataSource.setMaxPoolSize(maxPoolSize);
    }

    public int getAcquireIncrement() {
        return acquireIncrement;
    }

    public void setAcquireIncrement(int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
        this.comboPooledDataSource.setAcquireIncrement(acquireIncrement);
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        this.comboPooledDataSource.setMaxIdleTime(maxIdleTime);
    }

    public int getMaxIdleTimeExcessConnections() {
        return maxIdleTimeExcessConnections;
    }

    public void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) {
        this.maxIdleTimeExcessConnections = maxIdleTimeExcessConnections;
        this.comboPooledDataSource.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
    }

    public int getMaxConnectionAge() {
        return maxConnectionAge;
    }

    public void setMaxConnectionAge(int maxConnectionAge) {
        this.maxConnectionAge = maxConnectionAge;
        this.comboPooledDataSource.setMaxConnectionAge(maxConnectionAge);
    }

    public int getMaxStatements() {
        return maxStatements;
    }

    public void setMaxStatements(int maxStatements) {
        this.maxStatements = maxStatements;
        this.comboPooledDataSource.setMaxStatements(maxStatements);
    }

    public int getMaxStatementsPerConnection() {
        return maxStatementsPerConnection;
    }

    public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
        this.maxStatementsPerConnection = maxStatementsPerConnection;
        this.comboPooledDataSource.setMaxStatementsPerConnection(maxStatementsPerConnection);
    }

    public int getPropertyCycle() {
        return propertyCycle;
    }

    public void setPropertyCycle(int propertyCycle) {
        this.propertyCycle = propertyCycle;
        this.comboPooledDataSource.setPropertyCycle(propertyCycle);
    }

    public int getNumHelperThreads() {
        return numHelperThreads;
    }

    public void setNumHelperThreads(int numHelperThreads) {
        this.numHelperThreads = numHelperThreads;
        this.comboPooledDataSource.setNumHelperThreads(numHelperThreads);
    }

    public int getMaxAdministrativeTaskTime() {
        return maxAdministrativeTaskTime;
    }

    public void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) {
        this.maxAdministrativeTaskTime = maxAdministrativeTaskTime;
        this.comboPooledDataSource.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);
    }

    public int getCheckoutTimeout() {
        return checkoutTimeout;
    }

    public void setCheckoutTimeout(int checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
        this.comboPooledDataSource.setCheckoutTimeout(checkoutTimeout);
    }

    public int getAcquireRetryAttempts() {
        return acquireRetryAttempts;
    }

    public void setAcquireRetryAttempts(int acquireRetryAttempts) {
        this.acquireRetryAttempts = acquireRetryAttempts;
        this.comboPooledDataSource.setAcquireRetryAttempts(acquireRetryAttempts);
    }

    public int getAcquireRetryDelay() {
        return acquireRetryDelay;
    }

    public void setAcquireRetryDelay(int acquireRetryDelay) {
        this.acquireRetryDelay = acquireRetryDelay;
        this.comboPooledDataSource.setAcquireRetryDelay(acquireRetryDelay);
    }

    public boolean isBreakAfterAcquireFailure() {
        return breakAfterAcquireFailure;
    }

    public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
        this.comboPooledDataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
    }

    public String getPreferredTestQuery() {
        return preferredTestQuery;
    }

    public void setPreferredTestQuery(String preferredTestQuery) {
        this.preferredTestQuery = preferredTestQuery;
        if (preferredTestQuery != null && !preferredTestQuery.isBlank())
            this.comboPooledDataSource.setPreferredTestQuery(preferredTestQuery);
    }

    public String getAutomaticTestTable() {
        return automaticTestTable;
    }

    public void setAutomaticTestTable(String automaticTestTable) {
        this.automaticTestTable = automaticTestTable;
        if (automaticTestTable != null && !automaticTestTable.isBlank())
            this.comboPooledDataSource.setAutomaticTestTable(automaticTestTable);
    }

    public boolean isTestConnectionOnCheckout() {
        return testConnectionOnCheckout;
    }

    public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) {
        this.testConnectionOnCheckout = testConnectionOnCheckout;
        this.comboPooledDataSource.setTestConnectionOnCheckout(testConnectionOnCheckout);
    }

    public boolean isTestConnectionOnCheckin() {
        return testConnectionOnCheckin;
    }

    public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
        this.testConnectionOnCheckin = testConnectionOnCheckin;
        this.comboPooledDataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);
    }

    public int getIdleConnectionTestPeriod() {
        return idleConnectionTestPeriod;
    }

    public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
        this.idleConnectionTestPeriod = idleConnectionTestPeriod;
        this.comboPooledDataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
    }

    public String getConnectionTesterClassName() {
        return connectionTesterClassName;
    }

    public void setConnectionTesterClassName(String connectionTesterClassName) {
        this.connectionTesterClassName = connectionTesterClassName;
        if (connectionTesterClassName != null && !connectionTesterClassName.isBlank()) {
            try {
                this.comboPooledDataSource.setConnectionTesterClassName(connectionTesterClassName);
            } catch (PropertyVetoException e) {
                throw new IllegalArgumentException("invalid connection tester class name: " + connectionTesterClassName, e);
            }
        }
    }

    public String getConnectionCustomizerClassName() {
        return connectionCustomizerClassName;
    }

    public void setConnectionCustomizerClassName(String connectionCustomizerClassName) {
        this.connectionCustomizerClassName = connectionCustomizerClassName;
        if (connectionCustomizerClassName != null && !connectionCustomizerClassName.isBlank())
            this.comboPooledDataSource.setConnectionCustomizerClassName(connectionCustomizerClassName);
    }

    public boolean isAutoCommitOnClose() {
        return autoCommitOnClose;
    }

    public void setAutoCommitOnClose(boolean autoCommitOnClose) {
        this.autoCommitOnClose = autoCommitOnClose;
        this.comboPooledDataSource.setAutoCommitOnClose(autoCommitOnClose);
    }

    public boolean isForceIgnoreUnresolvedTransactions() {
        return forceIgnoreUnresolvedTransactions;
    }

    public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) {
        this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
        this.comboPooledDataSource.setForceIgnoreUnresolvedTransactions(forceIgnoreUnresolvedTransactions);
    }

    public int getUnreturnedConnectionTimeout() {
        return unreturnedConnectionTimeout;
    }

    public void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) {
        this.unreturnedConnectionTimeout = unreturnedConnectionTimeout;
        this.comboPooledDataSource.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
    }

    public boolean isDebugUnreturnedConnectionStackTraces() {
        return debugUnreturnedConnectionStackTraces;
    }

    public void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) {
        this.debugUnreturnedConnectionStackTraces = debugUnreturnedConnectionStackTraces;
        this.comboPooledDataSource.setDebugUnreturnedConnectionStackTraces(debugUnreturnedConnectionStackTraces);
    }

    public boolean isPrivilegeSpawnedThreads() {
        return privilegeSpawnedThreads;
    }

    public void setPrivilegeSpawnedThreads(boolean privilegeSpawnedThreads) {
        this.privilegeSpawnedThreads = privilegeSpawnedThreads;
        this.comboPooledDataSource.setPrivilegeSpawnedThreads(privilegeSpawnedThreads);
    }

    public boolean isForceSynchronousCheckins() {
        return forceSynchronousCheckins;
    }

    public void setForceSynchronousCheckins(boolean forceSynchronousCheckins) {
        this.forceSynchronousCheckins = forceSynchronousCheckins;
        this.comboPooledDataSource.setForceSynchronousCheckins(forceSynchronousCheckins);
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        if (dataSourceName != null && !dataSourceName.isBlank())
            this.comboPooledDataSource.setDataSourceName(dataSourceName);
    }

    public String getContextClassLoaderSource() {
        return contextClassLoaderSource;
    }

    public void setContextClassLoaderSource(String contextClassLoaderSource) {
        this.contextClassLoaderSource = contextClassLoaderSource;
        if (contextClassLoaderSource != null && !contextClassLoaderSource.isBlank()) {
            try {
                this.comboPooledDataSource.setContextClassLoaderSource(contextClassLoaderSource);
            } catch (PropertyVetoException e) {
                throw new IllegalArgumentException("invalid context class loader source: " + contextClassLoaderSource, e);
            }
        }
    }

    public String getFactoryClassLocation() {
        return factoryClassLocation;
    }

    public void setFactoryClassLocation(String factoryClassLocation) {
        this.factoryClassLocation = factoryClassLocation;
        if (factoryClassLocation != null && !factoryClassLocation.isBlank())
            this.comboPooledDataSource.setFactoryClassLocation(factoryClassLocation);
    }

    public String getUserOverridesAsString() {
        return userOverridesAsString;
    }

    public void setUserOverridesAsString(String userOverridesAsString) {
        this.userOverridesAsString = userOverridesAsString;
        if (userOverridesAsString != null && !userOverridesAsString.isBlank()) {
            try {
                this.comboPooledDataSource.setUserOverridesAsString(userOverridesAsString);
            } catch (PropertyVetoException e) {
                throw new IllegalArgumentException("invalid user overrides: " + userOverridesAsString, e);
            }
        }
    }

    public int getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
        try {
            this.comboPooledDataSource.setLoginTimeout(loginTimeout);
        } catch (java.sql.SQLException e) {
            throw new IllegalArgumentException("failed to set login timeout: " + loginTimeout, e);
        }
    }
}

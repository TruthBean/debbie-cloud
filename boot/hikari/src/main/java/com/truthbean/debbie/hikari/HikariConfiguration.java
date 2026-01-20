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

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.LongTransformer;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.SQLExceptionOverride;

import javax.sql.DataSource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 22:28.
 */
@PropertiesConfiguration(keyPrefix = "debbie.datasource.hikari.")
public class HikariConfiguration extends DataSourceConfiguration implements DebbieConfiguration {

    private final HikariConfig hikariConfig;

    /**
     * This is the name of the DataSource class provided by the JDBC driver.
     * Consult the documentation for your specific JDBC driver to get this class name, or see the table below.
     * Note XA data sources are not supported.
     * XA requires a real transaction manager like bitronix.
     * Note that you do not need this property if you are using jdbcUrl for "old-school" DriverManager-based JDBC driver configuration.
     * Default: none
     */
    @PropertyInject(value = "datasource-class-name")
    private String dataSourceClassName;

    /**
     * This property directs HikariCP to use "DriverManager-based" configuration.
     * We feel that DataSource-based configuration (above) is superior for a variety of reasons (see below), but for many deployments there is little significant difference.
     * When using this property with "old" drivers, you may also need to set the driverClassName property, but try it first without.
     * Note that if this property is used, you may still use DataSource properties to configure your driver and is in fact recommended over driver parameters specified in the URL itself.
     * Default: none
     */
    @PropertyInject(value = "jdbc-url")
    private String jdbcUrl;

    /**
     * This property sets the default authentication username used when obtaining Connections from the underlying driver.
     * Note that for DataSources this works in a very deterministic fashion by calling DataSource.getConnection(*username*, password) on the underlying DataSource.
     * However, for Driver-based configurations, every driver is different.
     * In the case of Driver-based, HikariCP will use this username property to set a user property in the Properties passed to the driver's DriverManager.getConnection(jdbcUrl, props) call.
     * If this is not what you need, skip this method entirely and call addDataSourceProperty("username", ...), for example.
     * Default: none
     */
    @PropertyInject(value = "username")
    private String username;

    /**
     * This property sets the default authentication password used when obtaining Connections from the underlying driver.
     * Note that for DataSources this works in a very deterministic fashion by calling DataSource.getConnection(username, *password*) on the underlying DataSource.
     * However, for Driver-based configurations, every driver is different.
     * In the case of Driver-based, HikariCP will use this password property to set a password property in the Properties passed to the driver's DriverManager.getConnection(jdbcUrl, props) call.
     * If this is not what you need, skip this method entirely and call addDataSourceProperty("pass", ...), for example.
     * Default: none
     */
    @PropertyInject(value = "password")
    private String hikariPassword;

    /**
     * This property controls the default auto-commit behavior of connections returned from the pool.
     * It is a boolean value.
     * Default: true
     */
    @PropertyInject(value = "auto-commit", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean hikariAutoCommit = true;

    /**
     *  This property controls the maximum number of milliseconds that a client (that's you) will wait for a connection from the pool.
     *  If this time is exceeded without a connection becoming available, a SQLException will be thrown.
     *  Lowest acceptable connection timeout is 250 ms.
     *  Default: 30000 (30 seconds)
     */
    @PropertyInject(value = "connection-timeout", transformer = LongTransformer.class, defaultValue = "30000")
    private long connectionTimeout = 30000;

    /**
     *  This property controls the maximum amount of time that a connection is allowed to sit idle in the pool.
     *  This setting only applies when minimumIdle is defined to be less than maximumPoolSize.
     *  Idle connections will not be retired once the pool reaches minimumIdle connections.
     *  Whether a connection is retired as idle or not is subject to a maximum variation of +30 seconds, and average variation of +15 seconds.
     *  A connection will never be retired as idle before this timeout.
     *  A value of 0 means that idle connections are never removed from the pool.
     *  The minimum allowed value is 10000ms (10 seconds).
     *  Default: 600000 (10 minutes)
     */
    @PropertyInject(value = "idle-timeout", defaultValue = "600000")
    private long idleTimeout = 600000;

    /**
     *  This property controls how frequently HikariCP will attempt to keep a connection alive, in order to prevent it from being timed out by the database or network infrastructure.
     *  This value must be less than the maxLifetime value.
     *  A "keepalive" will only occur on an idle connection.
     *  When the time arrives for a "keepalive" against a given connection, that connection will be removed from the pool, "pinged", and then returned to the pool.
     *  The 'ping' is one of either: invocation of the JDBC4 isValid() method, or execution of the connectionTestQuery.
     *  Typically, the duration out-of-the-pool should be measured in single digit milliseconds or even sub-millisecond, and therefore should have little or no noticeable performance impact.
     *  The minimum allowed value is 30000ms (30 seconds), but a value in the range of minutes is most desirable.
     *  Default: 120000 (2 minutes)
     */
    @PropertyInject(value = "keepalive-time", defaultValue = "600000")
    private long keepaliveTime = 600000;

    /**
     *  This property controls the maximum lifetime of a connection in the pool.
     *  An in-use connection will never be retired, only when it is closed will it then be removed.
     *  On a connection-by-connection basis, minor negative attenuation is applied to avoid mass-extinction in the pool.
     *  We strongly recommend setting this value, and it should be several seconds shorter than any database or infrastructure imposed connection time limit.
     *  A value of 0 indicates no maximum lifetime (infinite lifetime), subject of course to the idleTimeout setting.
     *  The minimum allowed value is 30000ms (30 seconds).
     *  Default: 1800000 (30 minutes)
     */
    @PropertyInject(value = "max-lifetime", defaultValue = "1800000")
    private long maxLifetime = 1800000;

    /**
     * If your driver supports JDBC4 we strongly recommend not setting this property.
     * This is for "legacy" drivers that do not support the JDBC4 Connection.isValid() API.
     * This is the query that will be executed just before a connection is given to you from the pool to validate that the connection to the database is still alive.
     * Again, try running the pool without this property, HikariCP will log an error if your driver is not JDBC4 compliant to let you know.
     * Default: none
     */
    @PropertyInject(value = "connection-test-query")
    private String connectionTestQuery;

    /**
     * This property controls the maximum size that the pool is allowed to reach, including both idle and in-use connections.
     * Basically this value will determine the maximum number of actual connections to the database backend.
     * A reasonable value for this is best determined by your execution environment.
     * When the pool reaches this size, and no idle connections are available, calls to getConnection() will block for up to connectionTimeout milliseconds before timing out.
     * Please read about pool sizing.
     * Default: 10
     */
    @PropertyInject(value = "max-pool-size", defaultValue = "10")
    private int maximumPoolSize = 10;

    /**
     * This property controls the minimum number of idle connections that HikariCP tries to maintain in the pool.
     * If the idle connections dip below this value and total connections in the pool are less than maximumPoolSize, HikariCP will make a best effort to add additional connections quickly and efficiently.
     * However, for maximum performance and responsiveness to spike demands, we recommend not setting this value and instead allowing HikariCP to act as a fixed size connection pool.
     * Default: same as maximumPoolSize
     */
    @PropertyInject(value = "min-idle", defaultValue = "10")
    private int minimumIdle = maximumPoolSize;

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/Dropwizard-Metrics
     *
     * This property is only available via programmatic configuration or IoC container.
     * This property allows you to specify an instance of a Codahale/Dropwizard MetricRegistry to be used by the pool to record various metrics.
     * See the Metrics wiki page for details.
     * Default: none
     */
    @PropertyInject(value = "metric-registry")
    private String metricRegistry;

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/Dropwizard-HealthChecks
     *
     * This property is only available via programmatic configuration or IoC container.
     * This property allows you to specify an instance of a Codahale/Dropwizard HealthCheckRegistry to be used by the pool to report current health information.
     * See the Health Checks wiki page for details.
     * Default: none
     */
    @PropertyInject(value = "health-check-registry")
    private String healthCheckRegistry;

    /**
     * This property represents a user-defined name for the connection pool and appears mainly in logging and JMX management consoles to identify pools and pool configurations.
     * Default: auto-generated
     */
    @PropertyInject(value = "pool-name")
    private String poolName;

    /**
     * ⏳initializationFailTimeout
     * This property controls whether the pool will "fail fast" if the pool cannot be seeded with an initial connection successfully.
     * Any positive number is taken to be the number of milliseconds to attempt to acquire an initial connection; the application thread will be blocked during this period.
     * If a connection cannot be acquired before this timeout occurs, an exception will be thrown.
     * This timeout is applied after the connectionTimeout period.
     * If the value is zero (0), HikariCP will attempt to obtain and validate a connection.
     * If a connection is obtained, but fails validation, an exception will be thrown and the pool not started.
     * However, if a connection cannot be obtained, the pool will start, but later efforts to obtain a connection may fail.
     * A value less than zero will bypass any initial connection attempt, and the pool will start immediately while trying to obtain connections in the background.
     * Consequently, later efforts to obtain a connection may fail.
     * Default: 1
     */
    @PropertyInject(value = "init-fail-timeout", defaultValue = "1")
    private long initializationFailTimeout = 1;

    /**
     * ❎isolateInternalQueries
     * This property determines whether HikariCP isolates internal pool queries, such as the connection alive test, in their own transaction.
     * Since these are typically read-only queries, it is rarely necessary to encapsulate them in their own transaction.
     * This property only applies if autoCommit is disabled.
     * Default: false
     */
    @PropertyInject(value = "isolate-internal-queries", defaultValue = "false")
    private boolean isolateInternalQueries = false;

    /**
     * This property controls whether the pool can be suspended and resumed through JMX.
     * This is useful for certain failover automation scenarios.
     * When the pool is suspended, calls to getConnection() will not timeout and will be held until the pool is resumed.
     * Default: false
     */
    @PropertyInject(value = "allow-pool-suspension", defaultValue = "false")
    private boolean allowPoolSuspension = false;

    /**
     * This property controls whether Connections obtained from the pool are in read-only mode by default.
     * Note some databases do not support the concept of read-only mode, while others provide query optimizations when the Connection is set to read-only.
     * Whether you need this property or not will depend largely on your application and database.
     * Default: false
     */
    @PropertyInject(value = "read-only", defaultValue = "false")
    private boolean readOnly = false;

    /**
     * This property controls whether or not JMX Management Beans ("MBeans") are registered or not.
     * Default: false
     */
    @PropertyInject(value = "register-mbeans", defaultValue = "false")
    private boolean registerMbeans = false;

    /**
     * This property sets the default catalog for databases that support the concept of catalogs.
     * If this property is not specified, the default catalog defined by the JDBC driver is used.
     * Default: driver default
     */
    @PropertyInject(value = "catalog")
    private String catalog;

    /**
     * This property sets a SQL statement that will be executed after every new connection creation before adding it to the pool.
     * If this SQL is not valid or throws an exception, it will be treated as a connection failure and the standard retry logic will be followed.
     * Default: none
     */
    @PropertyInject(value = "connection-init-sql")
    private String connectionInitSql;

    /**
     * HikariCP will attempt to resolve a driver through the DriverManager based solely on the jdbcUrl, but for some older drivers the driverClassName must also be specified.
     * Omit this property unless you get an obvious error message indicating that the driver was not found.
     * Default: none
     */
    @PropertyInject(value = "driver-class-name")
    private String driverClassName;

    /**
     * This property controls the default transaction isolation level of connections returned from the pool.
     * If this property is not specified, the default transaction isolation level defined by the JDBC driver is used.
     * Only use this property if you have specific isolation requirements that are common for all queries.
     * The value of this property is the constant name from the Connection class such as TRANSACTION_READ_COMMITTED, TRANSACTION_REPEATABLE_READ, etc.
     * Default: driver default
     */
    @PropertyInject(value = "transaction-isolation")
    private TransactionIsolationLevel transactionIsolation;

    /**
     *  This property controls the maximum amount of time that a connection will be tested for aliveness.
     *  This value must be less than the connectionTimeout.
     *  Lowest acceptable validation timeout is 250 ms.
     *  Default: 5000
     */
    @PropertyInject(value = "validate-timeout", defaultValue = "5000")
    private long validationTimeout = 5000;

    /**
     * This property controls the amount of time that a connection can be out of the pool before a message is logged indicating a possible connection leak.
     * A value of 0 means leak detection is disabled. Lowest acceptable value for enabling leak detection is 2000 (2 seconds).
     * Default: 0
     */
    @PropertyInject(value = "leak-detection-threshold", defaultValue = "0")
    private long leakDetectionThreshold = 0;

    /**
     * This property sets the default schema for databases that support the concept of schemas.
     * If this property is not specified, the default schema defined by the JDBC driver is used.
     * Default: driver default
     */
    @PropertyInject(value = "schema")
    private String schema;

    /**
     * This property is only available via programmatic configuration or IoC container.
     * This property allows you to directly set the instance of the DataSource to be wrapped by the pool, rather than having HikariCP construct it via reflection.
     * This can be useful in some dependency injection frameworks.
     * When this property is specified, the dataSourceClassName property and all DataSource-specific properties will be ignored.
     * Default: none
     */
    @PropertyInject(value = "datasource")
    private DataSource dataSource;

    /**
     * This property is only available via programmatic configuration or IoC container.
     * This property allows you to set the instance of the java.util.concurrent.ThreadFactory that will be used for creating all threads used by the pool.
     * It is needed in some restricted execution environments where threads can only be created through a ThreadFactory provided by the application container.
     * Default: none
     */
    @PropertyInject(value = "thread-factory")
    private ThreadFactory threadFactory;

    /**
     * This property is only available via programmatic configuration or IoC container.
     * This property allows you to set the instance of the java.util.concurrent.ScheduledExecutorService that will be used for various internally scheduled tasks.
     * If supplying HikariCP with a ScheduledThreadPoolExecutor instance, it is recommended that setRemoveOnCancelPolicy(true) is used.
     * Default: none
     */
    @PropertyInject(value = "scheduled-executor")
    private ScheduledExecutorService scheduledExecutor;

    /**
     * This property is only available via programmatic configuration or IoC container.
     * This property allows you to set an instance of a class, implementing the com.zaxxer.hikari.SQLExceptionOverride interface, that will be called before a connection is evicted from the pool due to specific exception conditions.
     * Typically, when a SQLException is thrown, connections are evicted from the pool when specific SQLStates or ErrorCodes are present.
     * The adjudicate() method will be called on the SQLExceptionOverride instance, which may return one of: Override.CONTINUE_EVICT. Override.DO_NOT_EVICT or Override.MUST_EVICT.
     * Except in very specific cases Override.CONTINUE_EVICT should be returned, allowing the default evict/no-evict logic to execute.
     * Default: none
     */
    @PropertyInject(value = "exception-override")
    private SQLExceptionOverride.Override exceptionOverride;

    /**
     * This property allows you to specify the name of a user-supplied class implementing the com.zaxxer.hikari.SQLExceptionOverride interface.
     * An instance of the class will be instantiated by the pool to adjudicate connection evictions.
     * See the above property exceptionOverride for a full description.
     * Default: none
     */
    @PropertyInject(value = "exception-override-class-name")
    private Class<? extends SQLExceptionOverride> exceptionOverrideClassName;

    public HikariConfiguration(ApplicationContext applicationContext) {
        super(new DataSourceProperties(applicationContext).getConfiguration(applicationContext));
        this.hikariConfig = new HikariConfig();
        super.setDataSourceFactoryClass(HikariDataSourceFactory.class);
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
        if (dataSourceClassName != null && !dataSourceClassName.isBlank())
            this.hikariConfig.setDataSourceClassName(dataSourceClassName);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        if (jdbcUrl != null && !jdbcUrl.isBlank())
            this.hikariConfig.setJdbcUrl(jdbcUrl);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null && !username.isBlank())
            this.hikariConfig.setUsername(username);
    }

    public String getHikariPassword() {
        return hikariPassword;
    }

    public void setHikariPassword(String password) {
        this.hikariPassword = password;
        if (password != null && !password.isBlank())
            this.hikariConfig.setPassword(password);
    }

    public Boolean getHikariAutoCommit() {
        return hikariAutoCommit;
    }

    public void setHikariAutoCommit(Boolean autoCommit) {
        this.hikariAutoCommit = autoCommit;
        if (autoCommit != null)
            this.hikariConfig.setAutoCommit(autoCommit);
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        if (driverClassName != null && !driverClassName.isBlank())
            this.hikariConfig.setDriverClassName(driverClassName);
    }

    public TransactionIsolationLevel getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(TransactionIsolationLevel transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
        if (transactionIsolation != null)
            this.hikariConfig.setTransactionIsolation(transactionIsolation.name());
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
        if (schema != null)
            this.hikariConfig.setSchema(schema);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        if (dataSource != null)
            this.hikariConfig.setDataSource(dataSource);
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.hikariConfig.setConnectionTimeout(connectionTimeout);
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        this.hikariConfig.setIdleTimeout(idleTimeout);
    }

    public long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
        this.hikariConfig.setMaxLifetime(maxLifetime);
    }

    public String getConnectionTestQuery() {
        return connectionTestQuery;
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
        if (connectionTestQuery != null && !connectionTestQuery.isBlank())
            this.hikariConfig.setConnectionTestQuery(connectionTestQuery);
    }

    public long getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
        this.hikariConfig.setMinimumIdle(minimumIdle);
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        this.hikariConfig.setMaximumPoolSize(maximumPoolSize);
    }

    public String getMetricRegistry() {
        return metricRegistry;
    }

    public void setMetricRegistry(String metricRegistry) {
        this.metricRegistry = metricRegistry;
        if (metricRegistry != null && !metricRegistry.isBlank())
            this.hikariConfig.setMetricRegistry(metricRegistry);
    }

    public String getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    public void setHealthCheckRegistry(String healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
        if (healthCheckRegistry != null && !healthCheckRegistry.isBlank())
            this.hikariConfig.setHealthCheckRegistry(healthCheckRegistry);
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
        if (poolName != null && !poolName.isBlank())
            this.hikariConfig.setPoolName(poolName);
    }

    public long getInitializationFailTimeout() {
        return initializationFailTimeout;
    }

    public void setInitializationFailTimeout(long initializationFailTimeout) {
        this.initializationFailTimeout = initializationFailTimeout;
        this.hikariConfig.setInitializationFailTimeout(initializationFailTimeout);
    }

    public boolean isIsolateInternalQueries() {
        return isolateInternalQueries;
    }

    public void setIsolateInternalQueries(boolean isolateInternalQueries) {
        this.isolateInternalQueries = isolateInternalQueries;
        this.hikariConfig.setIsolateInternalQueries(isolateInternalQueries);
    }

    public boolean isAllowPoolSuspension() {
        return allowPoolSuspension;
    }

    public void setAllowPoolSuspension(boolean allowPoolSuspension) {
        this.allowPoolSuspension = allowPoolSuspension;
        this.hikariConfig.setAllowPoolSuspension(allowPoolSuspension);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        this.hikariConfig.setReadOnly(readOnly);
    }

    public boolean isRegisterMbeans() {
        return registerMbeans;
    }

    public void setRegisterMbeans(boolean registerMbeans) {
        this.registerMbeans = registerMbeans;
        this.hikariConfig.setRegisterMbeans(registerMbeans);
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
        if (catalog != null && !catalog.isBlank())
            this.hikariConfig.setCatalog(catalog);
    }

    public String getConnectionInitSql() {
        return connectionInitSql;
    }

    public void setConnectionInitSql(String connectionInitSql) {
        this.connectionInitSql = connectionInitSql;
        if (connectionInitSql != null && !connectionInitSql.isBlank())
            this.hikariConfig.setConnectionInitSql(connectionInitSql);
    }

    public long getValidationTimeout() {
        return validationTimeout;
    }

    public void setValidationTimeout(long validationTimeout) {
        this.validationTimeout = validationTimeout;
        this.hikariConfig.setValidationTimeout(validationTimeout);
    }

    public long getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public void setLeakDetectionThreshold(long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
        this.hikariConfig.setLeakDetectionThreshold(leakDetectionThreshold);
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        if (threadFactory != null)
            this.hikariConfig.setThreadFactory(threadFactory);
    }

    public boolean isHikariAutoCommit() {
        return hikariAutoCommit;
    }

    public void setHikariAutoCommit(boolean hikariAutoCommit) {
        this.hikariAutoCommit = hikariAutoCommit;
    }

    public long getKeepaliveTime() {
        return keepaliveTime;
    }

    public void setKeepaliveTime(long keepaliveTime) {
        this.keepaliveTime = keepaliveTime;
    }

    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    public void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }

    public SQLExceptionOverride.Override getExceptionOverride() {
        return exceptionOverride;
    }

    public void setExceptionOverride(SQLExceptionOverride.Override exceptionOverride) {
        this.exceptionOverride = exceptionOverride;
    }

    public Class<? extends SQLExceptionOverride> getExceptionOverrideClassName() {
        return exceptionOverrideClassName;
    }

    public void setExceptionOverrideClassName(Class<? extends SQLExceptionOverride> exceptionOverrideClassName) {
        this.exceptionOverrideClassName = exceptionOverrideClassName;
    }
}

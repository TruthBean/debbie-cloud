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

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.Validator;

/**
 * Tomcat JDBC connection pool configuration.
 * <p>
 * see: https://tomcat.apache.org/tomcat-10.1-doc/jdbc-pool.html
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.datasource.tomcat-jdbc.")
public class TomcatJdbcConfiguration extends DataSourceConfiguration implements DebbieConfiguration {

    private final PoolProperties poolProperties;

    /**
     * The fully qualified Java class name of the JDBC driver to be used.
     * Default: none
     */
    @PropertyInject("driver-class-name")
    private String driverClassName;

    /**
     * The connection URL to be passed to our JDBC driver.
     * Default: none
     */
    @PropertyInject("jdbc-url")
    private String url;

    /**
     * The connection username to be passed to our JDBC driver.
     * Default: none
     */
    @PropertyInject("username")
    private String username;

    /**
     * The connection password to be passed to our JDBC driver.
     * Default: none
     */
    @PropertyInject("password")
    private String password;

    /**
     * The initial number of connections that are created when the pool is started.
     * Default: 10
     */
    @PropertyInject(value = "initial-size", transformer = IntegerTransformer.class, defaultValue = "10")
    private int initialSize;

    /**
     * The maximum number of active connections that can be allocated from this pool at the same time.
     * Default: 100
     */
    @PropertyInject(value = "max-active", transformer = IntegerTransformer.class, defaultValue = "100")
    private int maxActive;

    /**
     * The maximum number of connections that should be kept in the pool at all times.
     * Default: 100
     */
    @PropertyInject(value = "max-idle", transformer = IntegerTransformer.class, defaultValue = "100")
    private int maxIdle;

    /**
     * The minimum number of established connections that should be kept in the pool at all times.
     * Default: 10
     */
    @PropertyInject(value = "min-idle", transformer = IntegerTransformer.class, defaultValue = "10")
    private int minIdle;

    /**
     * The maximum number of milliseconds that the pool will wait for a connection to be returned.
     * Default: 30000
     */
    @PropertyInject(value = "max-wait", transformer = IntegerTransformer.class, defaultValue = "30000")
    private int maxWait;

    /**
     * The validation query used to validate a connection.
     * Default: null
     */
    @PropertyInject("validation-query")
    private String validationQuery;

    /**
     * The timeout in seconds before a connection validation queries fail.
     * Default: 3000
     */
    @PropertyInject(value = "validation-query-timeout", transformer = IntegerTransformer.class, defaultValue = "3000")
    private int validationQueryTimeout;

    /**
     * The amount of time in milliseconds between validation runs.
     * Default: 30000
     */
    @PropertyInject(value = "validation-interval", transformer = LongTransformer.class, defaultValue = "30000")
    private long validationInterval;

    /**
     * Indicator whether connections should be validated before being borrowed from the pool.
     * Default: false
     */
    @PropertyInject(value = "test-on-borrow", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testOnBorrow;

    /**
     * Indicator whether connections should be validated after being returned to the pool.
     * Default: false
     */
    @PropertyInject(value = "test-on-return", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testOnReturn;

    /**
     * Indicator whether connections should be validated by the idle object evictor.
     * Default: false
     */
    @PropertyInject(value = "test-while-idle", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testWhileIdle;

    /**
     * Indicator whether connections should be validated when the pool is initialized.
     * Default: false
     */
    @PropertyInject(value = "test-on-connect", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testOnConnect;

    /**
     * The number of milliseconds to sleep between runs of the idle connection validation/cleaner thread.
     * Default: 5000
     */
    @PropertyInject(value = "time-between-eviction-runs-millis", transformer = IntegerTransformer.class, defaultValue = "5000")
    private int timeBetweenEvictionRunsMillis;

    /**
     * The minimum amount of time an object may sit idle in the pool before it is eligible for eviction.
     * Default: 60000
     */
    @PropertyInject(value = "min-evictable-idle-time-millis", transformer = IntegerTransformer.class, defaultValue = "60000")
    private int minEvictableIdleTimeMillis;

    /**
     * The number of connections to examine during each run of the idle object evictor thread.
     * Default: 3
     */
    @PropertyInject(value = "num-tests-per-eviction-run", transformer = IntegerTransformer.class, defaultValue = "3")
    private int numTestsPerEvictionRun;

    /**
     * The default auto-commit state of connections created by this pool.
     * Default: driver default
     */
    @PropertyInject(value = "default-auto-commit", transformer = BooleanTransformer.class)
    private Boolean defaultAutoCommit;

    /**
     * The default read-only state of connections created by this pool.
     * Default: driver default
     */
    @PropertyInject(value = "default-read-only", transformer = BooleanTransformer.class)
    private Boolean defaultReadOnly;

    /**
     * The default transaction isolation level of connections created by this pool.
     * Default: driver default
     */
    @PropertyInject(value = "default-transaction-isolation")
    private TransactionIsolationLevel defaultTransactionIsolation;

    /**
     * The default catalog of connections created by this pool.
     * Default: driver default
     */
    @PropertyInject("default-catalog")
    private String defaultCatalog;

    /**
     * Flag to remove abandoned connections if they exceed the removeAbandonedTimeout.
     * Default: false
     */
    @PropertyInject(value = "remove-abandoned", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean removeAbandoned;

    /**
     * The timeout in seconds before an abandoned connection can be removed.
     * Default: 60
     */
    @PropertyInject(value = "remove-abandoned-timeout", transformer = IntegerTransformer.class, defaultValue = "60")
    private int removeAbandonedTimeout;

    /**
     * Flag to log stack traces for application code which abandoned a Connection.
     * Default: false
     */
    @PropertyInject(value = "log-abandoned", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean logAbandoned;

    /**
     * Connections that have been abandoned (timed out) won't get closed and reported up to abandonWhenPercentageFull.
     * Default: 0
     */
    @PropertyInject(value = "abandon-when-percentage-full", transformer = IntegerTransformer.class, defaultValue = "0")
    private int abandonWhenPercentageFull;

    /**
     * A semicolon separated list of classnames extending JdbcInterceptor.
     * Default: null
     */
    @PropertyInject("jdbc-interceptors")
    private String jdbcInterceptors;

    /**
     * The connection properties that will be sent to our JDBC driver.
     * Default: null
     */
    @PropertyInject("connection-properties")
    private String connectionProperties;

    /**
     * A custom query to be run when a connection is first created.
     * Default: null
     */
    @PropertyInject("init-sql")
    private String initSQL;

    /**
     * The name of the connection pool.
     * Default: auto-generated
     */
    @PropertyInject("name")
    private String name;

    /**
     * Flag to enable JMX.
     * Default: true
     */
    @PropertyInject(value = "jmx-enabled", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean jmxEnabled;

    /**
     * Flag to enable fair queueing.
     * Default: true
     */
    @PropertyInject(value = "fair-queue", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean fairQueue;

    /**
     * Flag to allow access to the underlying connection.
     * Default: false
     */
    @PropertyInject(value = "access-to-underlying-connection-allowed", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean accessToUnderlyingConnectionAllowed;

    /**
     * Timeout in seconds for suspect connections.
     * Default: 300
     */
    @PropertyInject(value = "suspect-timeout", transformer = IntegerTransformer.class, defaultValue = "300")
    private int suspectTimeout;

    /**
     * Whether connections should be committed when returned to the pool.
     * Default: false
     */
    @PropertyInject(value = "commit-on-return", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean commitOnReturn;

    /**
     * Whether connections should be rolled back when returned to the pool.
     * Default: false
     */
    @PropertyInject(value = "rollback-on-return", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean rollbackOnReturn;

    /**
     * The fully qualified Java class name of a custom {@link org.apache.tomcat.jdbc.pool.Validator} implementation
     * used to validate connections.
     * Default: none
     */
    @PropertyInject("validator-class-name")
    private String validatorClassName;

    /**
     * A custom {@link org.apache.tomcat.jdbc.pool.Validator} instance used to validate connections.
     * Only available via programmatic configuration.
     * Default: none
     */
    @PropertyInject("validator")
    private Validator validator;

    /**
     * Whether the pool should use {@code equals()} instead of {@code ==} to compare connections.
     * Default: true
     */
    @PropertyInject(value = "use-equals", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean useEquals;

    /**
     * The maximum age of a connection in milliseconds, after which the connection is closed.
     * A value of 0 means no maximum age.
     * Default: 0
     */
    @PropertyInject(value = "max-age", transformer = LongTransformer.class, defaultValue = "0")
    private long maxAge;

    /**
     * Whether the pool should use a {@code ReentrantLock} instead of a synchronized block.
     * Default: true
     */
    @PropertyInject(value = "use-lock", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean useLock;

    /**
     * The underlying {@link javax.sql.DataSource} instance wrapped by the pool.
     * Only available via programmatic configuration.
     * Default: none
     */
    @PropertyInject("data-source")
    private Object dataSource;

    /**
     * The JNDI name of the underlying data source.
     * Default: none
     */
    @PropertyInject("data-source-jndi")
    private String dataSourceJNDI;

    /**
     * Whether the username can be overridden on a per-connection basis.
     * Default: false
     */
    @PropertyInject(value = "alternate-username-allowed", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean alternateUsernameAllowed;

    /**
     * Whether connections should be wrapped in a disposable connection facade.
     * Default: true
     */
    @PropertyInject(value = "use-disposable-connection-facade", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean useDisposableConnectionFacade;

    /**
     * Whether validation errors should be logged.
     * Default: false
     */
    @PropertyInject(value = "log-validation-errors", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean logValidationErrors;

    /**
     * Whether the interrupt state of a thread should be propagated when a connection is released.
     * Default: false
     */
    @PropertyInject(value = "propagate-interrupt-state", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean propagateInterruptState;

    /**
     * Whether exceptions thrown while pre-loading the pool should be ignored.
     * Default: false
     */
    @PropertyInject(value = "ignore-exception-on-pre-load", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean ignoreExceptionOnPreLoad;

    /**
     * Whether a statement facade should be used to wrap statements.
     * Default: true
     */
    @PropertyInject(value = "use-statement-facade", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean useStatementFacade;

    public TomcatJdbcConfiguration(ApplicationContext applicationContext) {
        super(new DataSourceProperties(applicationContext).getConfiguration(applicationContext));
        this.poolProperties = new PoolProperties();
        super.setDataSourceFactoryClass(TomcatJdbcDataSourceFactory.class);
    }

    public PoolProperties getPoolProperties() {
        return poolProperties;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        if (driverClassName != null && !driverClassName.isBlank())
            this.poolProperties.setDriverClassName(driverClassName);
    }

    public String getJdbcUrl() {
        return url;
    }

    public void setJdbcUrl(String url) {
        this.url = url;
        if (url != null && !url.isBlank())
            this.poolProperties.setUrl(url);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null && !username.isBlank())
            this.poolProperties.setUsername(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        if (password != null && !password.isBlank())
            this.poolProperties.setPassword(password);
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
        this.poolProperties.setInitialSize(initialSize);
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
        this.poolProperties.setMaxActive(maxActive);
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        this.poolProperties.setMaxIdle(maxIdle);
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        this.poolProperties.setMinIdle(minIdle);
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
        this.poolProperties.setMaxWait(maxWait);
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
        if (validationQuery != null && !validationQuery.isBlank())
            this.poolProperties.setValidationQuery(validationQuery);
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
        this.poolProperties.setValidationQueryTimeout(validationQueryTimeout);
    }

    public long getValidationInterval() {
        return validationInterval;
    }

    public void setValidationInterval(long validationInterval) {
        this.validationInterval = validationInterval;
        this.poolProperties.setValidationInterval(validationInterval);
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        this.poolProperties.setTestOnBorrow(testOnBorrow);
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        this.poolProperties.setTestOnReturn(testOnReturn);
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        this.poolProperties.setTestWhileIdle(testWhileIdle);
    }

    public boolean isTestOnConnect() {
        return testOnConnect;
    }

    public void setTestOnConnect(boolean testOnConnect) {
        this.testOnConnect = testOnConnect;
        this.poolProperties.setTestOnConnect(testOnConnect);
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.poolProperties.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this.poolProperties.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        this.poolProperties.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    public Boolean getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
        if (defaultAutoCommit != null)
            this.poolProperties.setDefaultAutoCommit(defaultAutoCommit);
    }

    public Boolean getDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
        if (defaultReadOnly != null)
            this.poolProperties.setDefaultReadOnly(defaultReadOnly);
    }

    public TransactionIsolationLevel getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(TransactionIsolationLevel defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        if (defaultTransactionIsolation != null)
            this.poolProperties.setDefaultTransactionIsolation(defaultTransactionIsolation.getLevel());
    }

    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
        if (defaultCatalog != null && !defaultCatalog.isBlank())
            this.poolProperties.setDefaultCatalog(defaultCatalog);
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
        this.poolProperties.setRemoveAbandoned(removeAbandoned);
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
        this.poolProperties.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
        this.poolProperties.setLogAbandoned(logAbandoned);
    }

    public int getAbandonWhenPercentageFull() {
        return abandonWhenPercentageFull;
    }

    public void setAbandonWhenPercentageFull(int abandonWhenPercentageFull) {
        this.abandonWhenPercentageFull = abandonWhenPercentageFull;
        this.poolProperties.setAbandonWhenPercentageFull(abandonWhenPercentageFull);
    }

    public String getJdbcInterceptors() {
        return jdbcInterceptors;
    }

    public void setJdbcInterceptors(String jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
        if (jdbcInterceptors != null && !jdbcInterceptors.isBlank())
            this.poolProperties.setJdbcInterceptors(jdbcInterceptors);
    }

    public String getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
        if (connectionProperties != null && !connectionProperties.isBlank())
            this.poolProperties.setConnectionProperties(connectionProperties);
    }

    public String getInitSQL() {
        return initSQL;
    }

    public void setInitSQL(String initSQL) {
        this.initSQL = initSQL;
        if (initSQL != null && !initSQL.isBlank())
            this.poolProperties.setInitSQL(initSQL);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (name != null && !name.isBlank())
            this.poolProperties.setName(name);
    }

    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
        this.poolProperties.setJmxEnabled(jmxEnabled);
    }

    public boolean isFairQueue() {
        return fairQueue;
    }

    public void setFairQueue(boolean fairQueue) {
        this.fairQueue = fairQueue;
        this.poolProperties.setFairQueue(fairQueue);
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
        this.poolProperties.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
    }

    public int getSuspectTimeout() {
        return suspectTimeout;
    }

    public void setSuspectTimeout(int suspectTimeout) {
        this.suspectTimeout = suspectTimeout;
        this.poolProperties.setSuspectTimeout(suspectTimeout);
    }

    public boolean isCommitOnReturn() {
        return commitOnReturn;
    }

    public void setCommitOnReturn(boolean commitOnReturn) {
        this.commitOnReturn = commitOnReturn;
        this.poolProperties.setCommitOnReturn(commitOnReturn);
    }

    public boolean isRollbackOnReturn() {
        return rollbackOnReturn;
    }

    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
        this.poolProperties.setRollbackOnReturn(rollbackOnReturn);
    }

    public String getValidatorClassName() {
        return validatorClassName;
    }

    public void setValidatorClassName(String validatorClassName) {
        this.validatorClassName = validatorClassName;
        if (validatorClassName != null && !validatorClassName.isBlank())
            this.poolProperties.setValidatorClassName(validatorClassName);
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
        if (validator != null)
            this.poolProperties.setValidator(validator);
    }

    public boolean isUseEquals() {
        return useEquals;
    }

    public void setUseEquals(boolean useEquals) {
        this.useEquals = useEquals;
        this.poolProperties.setUseEquals(useEquals);
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
        this.poolProperties.setMaxAge(maxAge);
    }

    public boolean isUseLock() {
        return useLock;
    }

    public void setUseLock(boolean useLock) {
        this.useLock = useLock;
        this.poolProperties.setUseLock(useLock);
    }

    public Object getDataSource() {
        return dataSource;
    }

    public void setDataSource(Object dataSource) {
        this.dataSource = dataSource;
        if (dataSource != null)
            this.poolProperties.setDataSource(dataSource);
    }

    public String getDataSourceJNDI() {
        return dataSourceJNDI;
    }

    public void setDataSourceJNDI(String dataSourceJNDI) {
        this.dataSourceJNDI = dataSourceJNDI;
        if (dataSourceJNDI != null && !dataSourceJNDI.isBlank())
            this.poolProperties.setDataSourceJNDI(dataSourceJNDI);
    }

    public boolean isAlternateUsernameAllowed() {
        return alternateUsernameAllowed;
    }

    public void setAlternateUsernameAllowed(boolean alternateUsernameAllowed) {
        this.alternateUsernameAllowed = alternateUsernameAllowed;
        this.poolProperties.setAlternateUsernameAllowed(alternateUsernameAllowed);
    }

    public boolean isUseDisposableConnectionFacade() {
        return useDisposableConnectionFacade;
    }

    public void setUseDisposableConnectionFacade(boolean useDisposableConnectionFacade) {
        this.useDisposableConnectionFacade = useDisposableConnectionFacade;
        this.poolProperties.setUseDisposableConnectionFacade(useDisposableConnectionFacade);
    }

    public boolean isLogValidationErrors() {
        return logValidationErrors;
    }

    public void setLogValidationErrors(boolean logValidationErrors) {
        this.logValidationErrors = logValidationErrors;
        this.poolProperties.setLogValidationErrors(logValidationErrors);
    }

    public boolean isPropagateInterruptState() {
        return propagateInterruptState;
    }

    public void setPropagateInterruptState(boolean propagateInterruptState) {
        this.propagateInterruptState = propagateInterruptState;
        this.poolProperties.setPropagateInterruptState(propagateInterruptState);
    }

    public boolean isIgnoreExceptionOnPreLoad() {
        return ignoreExceptionOnPreLoad;
    }

    public void setIgnoreExceptionOnPreLoad(boolean ignoreExceptionOnPreLoad) {
        this.ignoreExceptionOnPreLoad = ignoreExceptionOnPreLoad;
        this.poolProperties.setIgnoreExceptionOnPreLoad(ignoreExceptionOnPreLoad);
    }

    public boolean isUseStatementFacade() {
        return useStatementFacade;
    }

    public void setUseStatementFacade(boolean useStatementFacade) {
        this.useStatementFacade = useStatementFacade;
        this.poolProperties.setUseStatementFacade(useStatementFacade);
    }
}
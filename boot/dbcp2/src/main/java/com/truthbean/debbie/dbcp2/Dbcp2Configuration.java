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

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Collection;

/**
 * @author truthbean
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.datasource.dbcp2.")
public class Dbcp2Configuration extends DataSourceConfiguration implements DebbieConfiguration {

    private final BasicDataSource basicDataSource;

    // ======================== 基础连接配置 ========================

    @PropertyInject(value = "url")
    private String dbcp2Url;

    @PropertyInject(value = "username")
    private String username;

    @PropertyInject(value = "password")
    private String dbcp2Password;

    @PropertyInject(value = "driver-class-name")
    private String driverClassName;

    // ======================== 连接池容量 ========================

    /**
     * The initial number of connections created when the pool is started.
     * Default: 0
     */
    @PropertyInject(value = "initial-size", transformer = IntegerTransformer.class, defaultValue = "0")
    private int initialSize;

    /**
     * The maximum number of active connections in the pool.
     * Default: 8
     */
    @PropertyInject(value = "max-total", transformer = IntegerTransformer.class, defaultValue = "8")
    private int maxTotal;

    /**
     * The maximum number of idle connections in the pool.
     * Default: 8
     */
    @PropertyInject(value = "max-idle", transformer = IntegerTransformer.class, defaultValue = "8")
    private int maxIdle;

    /**
     * The minimum number of idle connections in the pool.
     * Default: 0
     */
    @PropertyInject(value = "min-idle", transformer = IntegerTransformer.class, defaultValue = "0")
    private int minIdle;

    /**
     * The maximum time to wait for a connection from the pool, in milliseconds.
     * Default: -1 (indefinitely)
     */
    @PropertyInject(value = "max-wait-millis", transformer = LongTransformer.class, defaultValue = "-1")
    private long maxWaitMillis;

    // ======================== 连接生命周期 ========================

    /**
     * The maximum lifetime of a connection, in milliseconds.
     * Default: -1 (no limit)
     */
    @PropertyInject(value = "max-conn-lifetime-millis", transformer = LongTransformer.class, defaultValue = "-1")
    private long maxConnLifetimeMillis;

    /**
     * The time between eviction runs, in milliseconds.
     * Default: -1 (no eviction)
     */
    @PropertyInject(value = "time-between-eviction-runs-millis", transformer = LongTransformer.class, defaultValue = "-1")
    private long timeBetweenEvictionRunsMillis;

    /**
     * The minimum time an idle connection can stay in the pool before being evicted, in milliseconds.
     * Default: 1800000 (30 minutes)
     */
    @PropertyInject(value = "min-evictable-idle-time-millis", transformer = LongTransformer.class, defaultValue = "1800000")
    private long minEvictableIdleTimeMillis;

    /**
     * The soft minimum evictable idle time, in milliseconds.
     * Default: -1 (no limit)
     */
    @PropertyInject(value = "soft-min-evictable-idle-time-millis", transformer = LongTransformer.class, defaultValue = "-1")
    private long softMinEvictableIdleTimeMillis;

    /**
     * The number of connections to test per eviction run.
     * Default: 3
     */
    @PropertyInject(value = "num-tests-per-eviction-run", transformer = IntegerTransformer.class, defaultValue = "3")
    private int numTestsPerEvictionRun;

    // ======================== 验证 ========================

    /**
     * The SQL query to validate connections.
     */
    @PropertyInject(value = "validation-query")
    private String validationQuery;

    /**
     * The validation query timeout, in seconds.
     * Default: -1 (no timeout)
     */
    @PropertyInject(value = "validation-query-timeout", transformer = IntegerTransformer.class, defaultValue = "-1")
    private int validationQueryTimeout;

    /**
     * Whether to test connections when created.
     * Default: false
     */
    @PropertyInject(value = "test-on-create", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testOnCreate;

    /**
     * Whether to test connections when borrowing from the pool.
     * Default: true
     */
    @PropertyInject(value = "test-on-borrow", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean testOnBorrow;

    /**
     * Whether to test connections when returning to the pool.
     * Default: false
     */
    @PropertyInject(value = "test-on-return", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testOnReturn;

    /**
     * Whether to test connections while idle.
     * Default: false
     */
    @PropertyInject(value = "test-while-idle", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testWhileIdle;

    // ======================== Prepared Statement 缓存 ========================

    /**
     * Whether to pool prepared statements.
     * Default: false
     */
    @PropertyInject(value = "pool-prepared-statements", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean poolPreparedStatements;

    /**
     * The maximum number of open prepared statements.
     * Default: -1 (no limit)
     */
    @PropertyInject(value = "max-open-prepared-statements", transformer = IntegerTransformer.class, defaultValue = "-1")
    private int maxOpenPreparedStatements;

    // ======================== 事务配置 ========================

    /**
     * Whether to enable auto-commit by default.
     */
    @PropertyInject(value = "default-auto-commit", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean defaultAutoCommit;

    /**
     * Whether the connection is read-only by default.
     */
    @PropertyInject(value = "default-read-only", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean defaultReadOnly;

    /**
     * The default transaction isolation level.
     * Default: -1 (driver default)
     */
    @PropertyInject(value = "default-transaction-isolation", transformer = IntegerTransformer.class, defaultValue = "-1")
    private int defaultTransactionIsolation;

    /**
     * The default catalog.
     */
    @PropertyInject(value = "default-catalog")
    private String defaultCatalog;

    /**
     * The default schema.
     */
    @PropertyInject(value = "default-schema")
    private String defaultSchema;

    // ======================== 连接行为 ========================

    /**
     * Whether to use LIFO (last-in-first-out) for the pool.
     * Default: true
     */
    @PropertyInject(value = "lifo", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean lifo;

    /**
     * Whether to cache state.
     * Default: true
     */
    @PropertyInject(value = "cache-state", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean cacheState;

    /**
     * Whether to fast-fail validation.
     * Default: false
     */
    @PropertyInject(value = "fast-fail-validation", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean fastFailValidation;

    // ======================== 废弃连接处理 ========================

    /**
     * Whether to track abandoned usage.
     * Default: false
     */
    @PropertyInject(value = "abandoned-usage-tracking", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean abandonedUsageTracking;

    /**
     * Whether to remove abandoned connections when borrowing.
     * Default: false
     */
    @PropertyInject(value = "remove-abandoned-on-borrow", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean removeAbandonedOnBorrow;

    /**
     * Whether to remove abandoned connections during maintenance.
     * Default: false
     */
    @PropertyInject(value = "remove-abandoned-on-maintenance", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean removeAbandonedOnMaintenance;

    /**
     * The timeout for removing abandoned connections, in seconds.
     * Default: 300 (5 minutes)
     */
    @PropertyInject(value = "remove-abandoned-timeout", transformer = IntegerTransformer.class, defaultValue = "300")
    private int removeAbandonedTimeout;

    /**
     * Whether to log abandoned connections.
     * Default: false
     */
    @PropertyInject(value = "log-abandoned", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean logAbandoned;

    // ======================== 日志和监控 ========================

    /**
     * Whether to log expired connections.
     * Default: true
     */
    @PropertyInject(value = "log-expired-connections", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean logExpiredConnections;

    // ======================== 其他 ========================

    /**
     * The eviction policy class name.
     */
    @PropertyInject(value = "eviction-policy-class-name")
    private String evictionPolicyClassName;

    /**
     * The JMX name.
     */
    @PropertyInject(value = "jmx-name")
    private String jmxName;

    /**
     * Whether to enable auto-commit on return.
     * Default: true
     */
    @PropertyInject(value = "enable-auto-commit-on-return", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean enableAutoCommitOnReturn;

    /**
     * Whether to rollback on return.
     * Default: false
     */
    @PropertyInject(value = "rollback-on-return", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean rollbackOnReturn;

    /**
     * The SQL statements to execute after creating a new connection.
     */
    @PropertyInject(value = "connection-init-sqls")
    private Collection<String> connectionInitSqls;

    // ================================================================

    public Dbcp2Configuration(ApplicationContext applicationContext) {
        super(new DataSourceProperties(applicationContext).getConfiguration(applicationContext));
        this.basicDataSource = new BasicDataSource();
        super.setDataSourceFactoryClass(Dbcp2DataSourceFactory.class);
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    // ======================== Getter/Setter ========================

    public String getDbcp2Url() {
        return dbcp2Url;
    }

    public void setDbcp2Url(String dbcp2Url) {
        this.dbcp2Url = dbcp2Url;
        if (dbcp2Url != null && !dbcp2Url.isBlank())
            this.basicDataSource.setUrl(dbcp2Url);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null && !username.isBlank())
            this.basicDataSource.setUsername(username);
    }

    public String getDbcp2Password() {
        return dbcp2Password;
    }

    public void setDbcp2Password(String dbcp2Password) {
        this.dbcp2Password = dbcp2Password;
        if (dbcp2Password != null && !dbcp2Password.isBlank())
            this.basicDataSource.setPassword(dbcp2Password);
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        if (driverClassName != null && !driverClassName.isBlank())
            this.basicDataSource.setDriverClassName(driverClassName);
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
        this.basicDataSource.setInitialSize(initialSize);
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        this.basicDataSource.setMaxTotal(maxTotal);
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        this.basicDataSource.setMaxIdle(maxIdle);
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        this.basicDataSource.setMinIdle(minIdle);
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
        this.basicDataSource.setMaxWaitMillis(maxWaitMillis);
    }

    public long getMaxConnLifetimeMillis() {
        return maxConnLifetimeMillis;
    }

    public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
        this.maxConnLifetimeMillis = maxConnLifetimeMillis;
        this.basicDataSource.setMaxConnLifetimeMillis(maxConnLifetimeMillis);
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.basicDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this.basicDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
        this.basicDataSource.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        this.basicDataSource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
        if (validationQuery != null && !validationQuery.isBlank())
            this.basicDataSource.setValidationQuery(validationQuery);
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
        this.basicDataSource.setValidationQueryTimeout(validationQueryTimeout);
    }

    public boolean isTestOnCreate() {
        return testOnCreate;
    }

    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
        this.basicDataSource.setTestOnCreate(testOnCreate);
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        this.basicDataSource.setTestOnBorrow(testOnBorrow);
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        this.basicDataSource.setTestOnReturn(testOnReturn);
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        this.basicDataSource.setTestWhileIdle(testWhileIdle);
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
        this.basicDataSource.setPoolPreparedStatements(poolPreparedStatements);
    }

    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
        this.basicDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
        this.basicDataSource.setDefaultAutoCommit(defaultAutoCommit);
    }

    public boolean isDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
        this.basicDataSource.setDefaultReadOnly(defaultReadOnly);
    }

    public int getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        this.basicDataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
    }

    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
        if (defaultCatalog != null && !defaultCatalog.isBlank())
            this.basicDataSource.setDefaultCatalog(defaultCatalog);
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
        if (defaultSchema != null && !defaultSchema.isBlank())
            this.basicDataSource.setDefaultSchema(defaultSchema);
    }

    public boolean isLifo() {
        return lifo;
    }

    public void setLifo(boolean lifo) {
        this.lifo = lifo;
        this.basicDataSource.setLifo(lifo);
    }

    public boolean isCacheState() {
        return cacheState;
    }

    public void setCacheState(boolean cacheState) {
        this.cacheState = cacheState;
        this.basicDataSource.setCacheState(cacheState);
    }

    public boolean isFastFailValidation() {
        return fastFailValidation;
    }

    public void setFastFailValidation(boolean fastFailValidation) {
        this.fastFailValidation = fastFailValidation;
        this.basicDataSource.setFastFailValidation(fastFailValidation);
    }

    public boolean isAbandonedUsageTracking() {
        return abandonedUsageTracking;
    }

    public void setAbandonedUsageTracking(boolean abandonedUsageTracking) {
        this.abandonedUsageTracking = abandonedUsageTracking;
        this.basicDataSource.setAbandonedUsageTracking(abandonedUsageTracking);
    }

    public boolean isRemoveAbandonedOnBorrow() {
        return removeAbandonedOnBorrow;
    }

    public void setRemoveAbandonedOnBorrow(boolean removeAbandonedOnBorrow) {
        this.removeAbandonedOnBorrow = removeAbandonedOnBorrow;
        this.basicDataSource.setRemoveAbandonedOnBorrow(removeAbandonedOnBorrow);
    }

    public boolean isRemoveAbandonedOnMaintenance() {
        return removeAbandonedOnMaintenance;
    }

    public void setRemoveAbandonedOnMaintenance(boolean removeAbandonedOnMaintenance) {
        this.removeAbandonedOnMaintenance = removeAbandonedOnMaintenance;
        this.basicDataSource.setRemoveAbandonedOnMaintenance(removeAbandonedOnMaintenance);
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
        this.basicDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
        this.basicDataSource.setLogAbandoned(logAbandoned);
    }

    public boolean isLogExpiredConnections() {
        return logExpiredConnections;
    }

    public void setLogExpiredConnections(boolean logExpiredConnections) {
        this.logExpiredConnections = logExpiredConnections;
        this.basicDataSource.setLogExpiredConnections(logExpiredConnections);
    }

    public String getEvictionPolicyClassName() {
        return evictionPolicyClassName;
    }

    public void setEvictionPolicyClassName(String evictionPolicyClassName) {
        this.evictionPolicyClassName = evictionPolicyClassName;
        if (evictionPolicyClassName != null && !evictionPolicyClassName.isBlank())
            this.basicDataSource.setEvictionPolicyClassName(evictionPolicyClassName);
    }

    public String getJmxName() {
        return jmxName;
    }

    public void setJmxName(String jmxName) {
        this.jmxName = jmxName;
        if (jmxName != null && !jmxName.isBlank())
            this.basicDataSource.setJmxName(jmxName);
    }

    public boolean isEnableAutoCommitOnReturn() {
        return enableAutoCommitOnReturn;
    }

    public void setEnableAutoCommitOnReturn(boolean enableAutoCommitOnReturn) {
        this.enableAutoCommitOnReturn = enableAutoCommitOnReturn;
        this.basicDataSource.setEnableAutoCommitOnReturn(enableAutoCommitOnReturn);
    }

    public boolean isRollbackOnReturn() {
        return rollbackOnReturn;
    }

    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
        this.basicDataSource.setRollbackOnReturn(rollbackOnReturn);
    }

    public Collection<String> getConnectionInitSqls() {
        return connectionInitSqls;
    }

    public void setConnectionInitSqls(Collection<String> connectionInitSqls) {
        this.connectionInitSqls = connectionInitSqls;
        if (connectionInitSqls != null && !connectionInitSqls.isEmpty())
            this.basicDataSource.setConnectionInitSqls(connectionInitSqls);
    }
}
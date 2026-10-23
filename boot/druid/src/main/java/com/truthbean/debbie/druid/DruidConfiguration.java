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

import com.alibaba.druid.pool.DruidDataSource;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;

import java.sql.SQLException;
import java.util.Collection;

/**
 * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE
 *
 * @author truthbean
 * @since 0.6.3
 */
public class DruidConfiguration extends DataSourceConfiguration {

    private final DruidDataSource druidDataSource;

    /**
     * The JDBC URL for the database connection.
     */
    private String druidUrl;

    /**
     * The username for the database connection.
     */
    private String username;

    /**
     * The password for the database connection.
     */
    private String druidPassword;

    /**
     * The fully qualified class name of the JDBC driver.
     */
    private String driverClassName;

    /**
     * The initial size of the connection pool.
     * Default: 0
     */
    private int initialSize;

    /**
     * The minimum number of idle connections in the pool.
     * Default: 0
     */
    private int minIdle;

    /**
     * The maximum number of active connections in the pool.
     * Default: 8
     */
    private int maxActive;

    /**
     * The maximum time to wait for a connection from the pool, in milliseconds.
     * Default: -1 (no wait)
     */
    private long maxWait;

    /**
     * The time between eviction runs, in milliseconds.
     * Default: 60000 (60 seconds)
     */
    private long timeBetweenEvictionRunsMillis;

    /**
     * The minimum time an idle connection can stay in the pool before being evicted, in milliseconds.
     * Default: 1800000 (30 minutes)
     */
    private long minEvictableIdleTimeMillis;

    /**
     * The SQL query to validate connections.
     */
    private String validationQuery;

    /**
     * Whether to test connections while idle.
     * Default: true
     */
    private boolean testWhileIdle;

    /**
     * Whether to test connections when borrowing from the pool.
     * Default: true
     */
    private boolean testOnBorrow;

    /**
     * Whether to test connections when returning to the pool.
     * Default: false
     */
    private boolean testOnReturn;

    /**
     * Whether to cache prepared statements.
     * Default: false
     */
    private boolean poolPreparedStatements;

    /**
     * The maximum number of prepared statements per connection.
     * Default: 10
     */
    private int maxPoolPreparedStatementPerConnectionSize;

    /**
     * The filters to use (e.g., "stat,wall,log4j").
     */
    private String filters;

    /**
     * The connection properties.
     */
    private String connectionProperties;

    /**
     * Whether to enable auto-commit.
     * Default: true
     */
    private boolean defaultAutoCommit = true;

    /**
     * Whether to remove abandoned connections.
     * Default: false
     */
    private boolean removeAbandoned;

    /**
     * The timeout for removing abandoned connections, in milliseconds.
     * Default: 300000 (5 minutes)
     */
    private long removeAbandonedTimeoutMillis;

    /**
     * Whether to log abandoned connections.
     * Default: false
     */
    private boolean logAbandoned;

    /**
     * The SQL statements to execute after creating a new connection.
     */
    private Collection<String> connectionInitSqls;

    // ======================== 新增缺失配置属性 ========================

    /**
     * The validation query timeout, in seconds.
     * Default: -1 (no timeout)
     */
    private int validationQueryTimeout;

    /**
     * Whether to keep connections alive.
     * Default: false
     */
    private boolean keepAlive;

    /**
     * The time between keep-alive checks, in milliseconds.
     * Default: 120000 (2 minutes)
     */
    private long keepAliveBetweenTimeMillis;

    /**
     * The maximum time an idle connection can stay in the pool before being evicted, in milliseconds.
     * Default: 25200000 (7 hours)
     */
    private long maxEvictableIdleTimeMillis;

    /**
     * The time between connect error retry attempts, in milliseconds.
     * Default: 500
     */
    private long timeBetweenConnectErrorMillis;

    /**
     * The default transaction isolation level.
     * Default: -1 (driver default)
     */
    private int defaultTransactionIsolation;

    /**
     * Whether the connection is read-only by default.
     * Default: false
     */
    private boolean defaultReadOnly;

    /**
     * The transaction query timeout, in seconds.
     * Default: 0 (no timeout)
     */
    private int transactionQueryTimeout;

    /**
     * The transaction threshold, in milliseconds.
     * Default: 0
     */
    private long transactionThresholdMillis;

    /**
     * Whether to initialize the data source asynchronously.
     * Default: false
     */
    private boolean asyncInit;

    /**
     * Whether to initialize variants.
     * Default: false
     */
    private boolean initVariants;

    /**
     * Whether to initialize global variants.
     * Default: false
     */
    private boolean initGlobalVariants;

    /**
     * Whether to use unfair lock for maxWait.
     * Default: false
     */
    private boolean useUnfairLock;

    /**
     * The maximum number of threads waiting for a connection.
     * Default: -1 (no limit)
     */
    private int maxWaitThreadCount;

    /**
     * The number of retry attempts when the pool is not full.
     * Default: 0
     */
    private int notFullTimeoutRetryCount;

    /**
     * The maximum number of create task.
     * Default: 3
     */
    private int maxCreateTaskCount;

    /**
     * The exception sorter class name.
     */
    private String exceptionSorter;

    /**
     * The number of connection error retry attempts.
     * Default: 1
     */
    private int connectionErrorRetryAttempts;

    /**
     * Whether to break after acquire failure.
     * Default: false
     */
    private boolean breakAfterAcquireFailure;

    /**
     * Whether to use global data source statistics.
     * Default: false
     */
    private boolean useGlobalDataSourceStat;

    /**
     * The time between logging stats, in milliseconds.
     * Default: 0 (disabled)
     */
    private long timeBetweenLogStatsMillis;

    /**
     * Whether to reset statistics.
     * Default: true
     */
    private boolean resetStatEnable;

    /**
     * The connection pool name.
     */
    private String name;

    /**
     * The physical connection timeout, in milliseconds.
     * Default: -1 (no timeout)
     */
    private long phyTimeoutMillis;

    /**
     * The maximum number of physical connection uses.
     * Default: -1 (no limit)
     */
    private long phyMaxUseCount;

    /**
     * Whether to enable duplicate close logging.
     * Default: false
     */
    private boolean dupCloseLogEnable;

    /**
     * Whether to allow access to the underlying connection.
     * Default: false
     */
    private boolean accessToUnderlyingConnectionAllowed;

    /**
     * The remove abandoned timeout, in seconds.
     * Default: 300 (5 minutes)
     */
    private int removeAbandonedTimeout;

    /**
     * Whether to share prepared statements.
     * Default: false
     */
    private boolean sharePreparedStatements;

    /**
     * Whether to enable clearing filters.
     * Default: true
     */
    private boolean clearFiltersEnable;

    public DruidConfiguration(ApplicationContext applicationContext) {
        super(new DataSourceProperties(applicationContext).getConfiguration(applicationContext));
        this.druidDataSource = new DruidDataSource();
        super.setDataSourceFactoryClass(DruidDataSourceFactory.class);
    }

    public DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }

    public String getDruidUrl() {
        return druidUrl;
    }

    public void setDruidUrl(String druidUrl) {
        this.druidUrl = druidUrl;
        if (druidUrl != null && !druidUrl.isBlank())
            this.druidDataSource.setUrl(druidUrl);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null && !username.isBlank())
            this.druidDataSource.setUsername(username);
    }

    public String getDruidPassword() {
        return druidPassword;
    }

    public void setDruidPassword(String druidPassword) {
        this.druidPassword = druidPassword;
        if (druidPassword != null && !druidPassword.isBlank())
            this.druidDataSource.setPassword(druidPassword);
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        if (driverClassName != null && !driverClassName.isBlank())
            this.druidDataSource.setDriverClassName(driverClassName);
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
        this.druidDataSource.setInitialSize(initialSize);
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        this.druidDataSource.setMinIdle(minIdle);
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
        this.druidDataSource.setMaxActive(maxActive);
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
        this.druidDataSource.setMaxWait(maxWait);
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this.druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
        if (validationQuery != null && !validationQuery.isBlank())
            this.druidDataSource.setValidationQuery(validationQuery);
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        this.druidDataSource.setTestWhileIdle(testWhileIdle);
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        this.druidDataSource.setTestOnBorrow(testOnBorrow);
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        this.druidDataSource.setTestOnReturn(testOnReturn);
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
        this.druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
    }

    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
        this.druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
        if (filters != null && !filters.isBlank()) {
            try {
                this.druidDataSource.setFilters(filters);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to set Druid filters: " + filters, e);
            }
        }
    }

    public String getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
        if (connectionProperties != null && !connectionProperties.isBlank())
            this.druidDataSource.setConnectionProperties(connectionProperties);
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
        this.druidDataSource.setDefaultAutoCommit(defaultAutoCommit);
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
        this.druidDataSource.setRemoveAbandoned(removeAbandoned);
    }

    public long getRemoveAbandonedTimeoutMillis() {
        return removeAbandonedTimeoutMillis;
    }

    public void setRemoveAbandonedTimeoutMillis(long removeAbandonedTimeoutMillis) {
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
        this.druidDataSource.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
        this.druidDataSource.setLogAbandoned(logAbandoned);
    }

    public Collection<String> getConnectionInitSqls() {
        return connectionInitSqls;
    }

    public void setConnectionInitSqls(Collection<String> connectionInitSqls) {
        this.connectionInitSqls = connectionInitSqls;
        if (connectionInitSqls != null && !connectionInitSqls.isEmpty())
            this.druidDataSource.setConnectionInitSqls(connectionInitSqls);
    }

    // ======================== 新增缺失配置属性 getter/setter ========================

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
        this.druidDataSource.setValidationQueryTimeout(validationQueryTimeout);
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        this.druidDataSource.setKeepAlive(keepAlive);
    }

    public long getKeepAliveBetweenTimeMillis() {
        return keepAliveBetweenTimeMillis;
    }

    public void setKeepAliveBetweenTimeMillis(long keepAliveBetweenTimeMillis) {
        this.keepAliveBetweenTimeMillis = keepAliveBetweenTimeMillis;
        this.druidDataSource.setKeepAliveBetweenTimeMillis(keepAliveBetweenTimeMillis);
    }

    public long getMaxEvictableIdleTimeMillis() {
        return maxEvictableIdleTimeMillis;
    }

    public void setMaxEvictableIdleTimeMillis(long maxEvictableIdleTimeMillis) {
        this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
        this.druidDataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
    }

    public long getTimeBetweenConnectErrorMillis() {
        return timeBetweenConnectErrorMillis;
    }

    public void setTimeBetweenConnectErrorMillis(long timeBetweenConnectErrorMillis) {
        this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
        this.druidDataSource.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
    }

    public int getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        this.druidDataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
    }

    public boolean isDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
        this.druidDataSource.setDefaultReadOnly(defaultReadOnly);
    }

    public int getTransactionQueryTimeout() {
        return transactionQueryTimeout;
    }

    public void setTransactionQueryTimeout(int transactionQueryTimeout) {
        this.transactionQueryTimeout = transactionQueryTimeout;
        this.druidDataSource.setTransactionQueryTimeout(transactionQueryTimeout);
    }

    public long getTransactionThresholdMillis() {
        return transactionThresholdMillis;
    }

    public void setTransactionThresholdMillis(long transactionThresholdMillis) {
        this.transactionThresholdMillis = transactionThresholdMillis;
        this.druidDataSource.setTransactionThresholdMillis(transactionThresholdMillis);
    }

    public boolean isAsyncInit() {
        return asyncInit;
    }

    public void setAsyncInit(boolean asyncInit) {
        this.asyncInit = asyncInit;
        this.druidDataSource.setAsyncInit(asyncInit);
    }

    public boolean isInitVariants() {
        return initVariants;
    }

    public void setInitVariants(boolean initVariants) {
        this.initVariants = initVariants;
        this.druidDataSource.setInitVariants(initVariants);
    }

    public boolean isInitGlobalVariants() {
        return initGlobalVariants;
    }

    public void setInitGlobalVariants(boolean initGlobalVariants) {
        this.initGlobalVariants = initGlobalVariants;
        this.druidDataSource.setInitGlobalVariants(initGlobalVariants);
    }

    public boolean isUseUnfairLock() {
        return useUnfairLock;
    }

    public void setUseUnfairLock(boolean useUnfairLock) {
        this.useUnfairLock = useUnfairLock;
        this.druidDataSource.setUseUnfairLock(useUnfairLock);
    }

    public int getMaxWaitThreadCount() {
        return maxWaitThreadCount;
    }

    public void setMaxWaitThreadCount(int maxWaitThreadCount) {
        this.maxWaitThreadCount = maxWaitThreadCount;
        this.druidDataSource.setMaxWaitThreadCount(maxWaitThreadCount);
    }

    public int getNotFullTimeoutRetryCount() {
        return notFullTimeoutRetryCount;
    }

    public void setNotFullTimeoutRetryCount(int notFullTimeoutRetryCount) {
        this.notFullTimeoutRetryCount = notFullTimeoutRetryCount;
        this.druidDataSource.setNotFullTimeoutRetryCount(notFullTimeoutRetryCount);
    }

    public int getMaxCreateTaskCount() {
        return maxCreateTaskCount;
    }

    public void setMaxCreateTaskCount(int maxCreateTaskCount) {
        this.maxCreateTaskCount = maxCreateTaskCount;
        this.druidDataSource.setMaxCreateTaskCount(maxCreateTaskCount);
    }

    public String getExceptionSorter() {
        return exceptionSorter;
    }

    public void setExceptionSorter(String exceptionSorter) {
        this.exceptionSorter = exceptionSorter;
        if (exceptionSorter != null && !exceptionSorter.isBlank()) {
            try {
                this.druidDataSource.setExceptionSorter(exceptionSorter);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to set Druid exception sorter: " + exceptionSorter, e);
            }
        }
    }

    public int getConnectionErrorRetryAttempts() {
        return connectionErrorRetryAttempts;
    }

    public void setConnectionErrorRetryAttempts(int connectionErrorRetryAttempts) {
        this.connectionErrorRetryAttempts = connectionErrorRetryAttempts;
        this.druidDataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
    }

    public boolean isBreakAfterAcquireFailure() {
        return breakAfterAcquireFailure;
    }

    public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
        this.druidDataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
    }

    public boolean isUseGlobalDataSourceStat() {
        return useGlobalDataSourceStat;
    }

    public void setUseGlobalDataSourceStat(boolean useGlobalDataSourceStat) {
        this.useGlobalDataSourceStat = useGlobalDataSourceStat;
        this.druidDataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
    }

    public long getTimeBetweenLogStatsMillis() {
        return timeBetweenLogStatsMillis;
    }

    public void setTimeBetweenLogStatsMillis(long timeBetweenLogStatsMillis) {
        this.timeBetweenLogStatsMillis = timeBetweenLogStatsMillis;
        this.druidDataSource.setTimeBetweenLogStatsMillis(timeBetweenLogStatsMillis);
    }

    public boolean isResetStatEnable() {
        return resetStatEnable;
    }

    public void setResetStatEnable(boolean resetStatEnable) {
        this.resetStatEnable = resetStatEnable;
        this.druidDataSource.setResetStatEnable(resetStatEnable);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (name != null && !name.isBlank())
            this.druidDataSource.setName(name);
    }

    public long getPhyTimeoutMillis() {
        return phyTimeoutMillis;
    }

    public void setPhyTimeoutMillis(long phyTimeoutMillis) {
        this.phyTimeoutMillis = phyTimeoutMillis;
        this.druidDataSource.setPhyTimeoutMillis(phyTimeoutMillis);
    }

    public long getPhyMaxUseCount() {
        return phyMaxUseCount;
    }

    public void setPhyMaxUseCount(long phyMaxUseCount) {
        this.phyMaxUseCount = phyMaxUseCount;
        this.druidDataSource.setPhyMaxUseCount(phyMaxUseCount);
    }

    public boolean isDupCloseLogEnable() {
        return dupCloseLogEnable;
    }

    public void setDupCloseLogEnable(boolean dupCloseLogEnable) {
        this.dupCloseLogEnable = dupCloseLogEnable;
        this.druidDataSource.setDupCloseLogEnable(dupCloseLogEnable);
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
        this.druidDataSource.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
        this.druidDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    }

    public boolean isSharePreparedStatements() {
        return sharePreparedStatements;
    }

    public void setSharePreparedStatements(boolean sharePreparedStatements) {
        this.sharePreparedStatements = sharePreparedStatements;
        this.druidDataSource.setSharePreparedStatements(sharePreparedStatements);
    }

    public boolean isClearFiltersEnable() {
        return clearFiltersEnable;
    }

    public void setClearFiltersEnable(boolean clearFiltersEnable) {
        this.clearFiltersEnable = clearFiltersEnable;
        this.druidDataSource.setClearFiltersEnable(clearFiltersEnable);
    }
}
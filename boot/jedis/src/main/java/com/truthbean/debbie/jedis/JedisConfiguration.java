/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jedis;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.jedis")
public class JedisConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Redis server host.
     * Default: localhost
     */
    @PropertyInject("host")
    private String host = "localhost";

    /**
     * Redis server port.
     * Default: 6379
     */
    @PropertyInject(value = "port", transformer = IntegerTransformer.class, defaultValue = "6379")
    private int port;

    /**
     * Optional Redis password (ACL user password).
     */
    @PropertyInject("password")
    private String password;

    /**
     * Redis database index.
     * Default: 0
     */
    @PropertyInject(value = "database", transformer = IntegerTransformer.class, defaultValue = "0")
    private int database;

    /**
     * Connection / socket / blocking socket timeout in milliseconds.
     * Default: 2000
     */
    @PropertyInject(value = "timeout", transformer = IntegerTransformer.class, defaultValue = "2000")
    private int timeout;

    // ======================== JedisPool settings ========================

    /**
     * Maximum number of connections in the pool.
     * Default: 8
     */
    @PropertyInject(value = "max-total", transformer = IntegerTransformer.class, defaultValue = "8")
    private int maxTotal;

    /**
     * Maximum number of idle connections in the pool.
     * Default: 8
     */
    @PropertyInject(value = "max-idle", transformer = IntegerTransformer.class, defaultValue = "8")
    private int maxIdle;

    /**
     * Minimum number of idle connections in the pool.
     * Default: 0
     */
    @PropertyInject(value = "min-idle", transformer = IntegerTransformer.class, defaultValue = "0")
    private int minIdle;

    /**
     * Maximum time (milliseconds) to wait for a connection to become available.
     * A value of -1 means wait indefinitely.
     * Default: -1
     */
    @PropertyInject(value = "max-wait-millis", transformer = LongTransformer.class, defaultValue = "-1")
    private long maxWaitMillis;

    /**
     * Whether to block when the pool is exhausted.
     * Default: true
     */
    @PropertyInject(value = "block-when-exhausted", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean blockWhenExhausted;

    /**
     * Whether connections should be validated before being borrowed.
     * Default: false
     */
    @PropertyInject(value = "test-on-borrow", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testOnBorrow;

    /**
     * Whether idle connections should be validated by the eviction thread.
     * Default: false
     */
    @PropertyInject(value = "test-while-idle", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testWhileIdle;

    /**
     * Whether connections should be validated when returned to the pool.
     * Default: false
     */
    @PropertyInject(value = "test-on-return", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean testOnReturn;

    /**
     * Time (milliseconds) between eviction runs.
     * Default: 30000
     */
    @PropertyInject(value = "time-between-eviction-runs-millis", transformer = LongTransformer.class, defaultValue = "30000")
    private long timeBetweenEvictionRunsMillis;

    /**
     * Minimum idle time (milliseconds) before an idle connection may be evicted.
     * Default: 60000
     */
    @PropertyInject(value = "min-evictable-idle-time-millis", transformer = LongTransformer.class, defaultValue = "60000")
    private long minEvictableIdleTimeMillis;

    /**
     * Number of connections to examine per eviction run.
     * Default: 3
     */
    @PropertyInject(value = "num-tests-per-eviction-run", transformer = IntegerTransformer.class, defaultValue = "3")
    private int numTestsPerEvictionRun;

    // ======================== Getter/Setter ========================

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    // ======================== DebbieConfiguration ========================

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getProfile() {
        return EnvironmentDepositoryHolder.DEFAULT_PROFILE;
    }

    @Override
    public String getCategory() {
        return EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        return (T) this;
    }

    @Override
    public void close() {
    }
}

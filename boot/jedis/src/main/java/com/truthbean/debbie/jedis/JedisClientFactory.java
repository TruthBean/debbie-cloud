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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class JedisClientFactory {

    private final JedisConfiguration configuration;
    private final JedisPool jedisPool;

    public JedisClientFactory(JedisConfiguration configuration) {
        this.configuration = configuration;
        this.jedisPool = createPool(configuration);
    }

    private JedisPool createPool(JedisConfiguration configuration) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(configuration.getMaxTotal());
        poolConfig.setMaxIdle(configuration.getMaxIdle());
        poolConfig.setMinIdle(configuration.getMinIdle());
        poolConfig.setMaxWaitMillis(configuration.getMaxWaitMillis());
        poolConfig.setBlockWhenExhausted(configuration.isBlockWhenExhausted());
        poolConfig.setTestOnBorrow(configuration.isTestOnBorrow());
        poolConfig.setTestWhileIdle(configuration.isTestWhileIdle());
        poolConfig.setTestOnReturn(configuration.isTestOnReturn());
        poolConfig.setTimeBetweenEvictionRunsMillis(configuration.getTimeBetweenEvictionRunsMillis());
        poolConfig.setMinEvictableIdleTimeMillis(configuration.getMinEvictableIdleTimeMillis());
        poolConfig.setNumTestsPerEvictionRun(configuration.getNumTestsPerEvictionRun());

        DefaultJedisClientConfig.Builder configBuilder = DefaultJedisClientConfig.builder();
        configBuilder.database(configuration.getDatabase());
        if (configuration.getPassword() != null && !configuration.getPassword().isBlank()) {
            configBuilder.password(configuration.getPassword());
        }
        int timeout = configuration.getTimeout();
        if (timeout > 0) {
            configBuilder.connectionTimeoutMillis(timeout);
            configBuilder.socketTimeoutMillis(timeout);
            configBuilder.blockingSocketTimeoutMillis(timeout);
        }
        JedisClientConfig clientConfig = configBuilder.build();
        return new JedisPool(poolConfig, new HostAndPort(configuration.getHost(), configuration.getPort()), clientConfig);
    }

    /**
     * Borrow a Jedis connection from the pool.
     * Remember to close the returned Jedis instance to return it to the pool.
     */
    public Jedis createClient() {
        return jedisPool.getResource();
    }

    public JedisPool getPool() {
        return jedisPool;
    }

    public JedisConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        jedisPool.close();
        LOGGER.info("Jedis pool closed");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisClientFactory.class);
}

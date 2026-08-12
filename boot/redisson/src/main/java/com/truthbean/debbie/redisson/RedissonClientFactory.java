/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.redisson;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class RedissonClientFactory {

    private final RedissonConfiguration configuration;
    private volatile RedissonClient redissonClient;

    public RedissonClientFactory(RedissonConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Create and return the Redisson client (lazy init).
     */
    public synchronized RedissonClient getClient() {
        if (redissonClient == null) {
            redissonClient = createClient(configuration);
        }
        return redissonClient;
    }

    private RedissonClient createClient(RedissonConfiguration configuration) {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + configuration.getHost() + ":" + configuration.getPort())
                .setDatabase(configuration.getDatabase())
                .setConnectTimeout(configuration.getConnectTimeout())
                .setTimeout(configuration.getTimeout())
                .setRetryAttempts(configuration.getRetryAttempts())
                .setRetryInterval(configuration.getRetryInterval())
                .setConnectionPoolSize(configuration.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(configuration.getConnectionMinIdleSize())
                .setIdleConnectionTimeout(configuration.getIdleConnectionTimeout())
                .setPingConnectionInterval(configuration.getPingConnectionInterval())
                .setKeepAlive(configuration.isKeepAlive())
                .setDnsMonitoringInterval(configuration.getDnsMonitoringInterval());

        if (configuration.getPassword() != null && !configuration.getPassword().isBlank()) {
            serverConfig.setPassword(configuration.getPassword());
        }

        return Redisson.create(config);
    }

    public RedissonConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        if (redissonClient != null) {
            redissonClient.shutdown();
            LOGGER.info("Redisson client shutdown");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonClientFactory.class);
}
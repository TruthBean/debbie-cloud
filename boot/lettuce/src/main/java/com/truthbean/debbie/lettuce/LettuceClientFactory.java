/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lettuce;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

import java.time.Duration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LettuceClientFactory {

    private final LettuceConfiguration configuration;
    private final RedisClient redisClient;
    private final ClientResources clientResources;

    public LettuceClientFactory(LettuceConfiguration configuration) {
        this.configuration = configuration;
        this.clientResources = DefaultClientResources.create();
        this.redisClient = createClient(configuration);
    }

    private RedisClient createClient(LettuceConfiguration configuration) {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(configuration.getHost())
                .withPort(configuration.getPort())
                .withDatabase(configuration.getDatabase())
                .withTimeout(Duration.ofMillis(configuration.getTimeout()));

        if (configuration.getPassword() != null && !configuration.getPassword().isBlank()) {
            builder.withPassword(configuration.getPassword().toCharArray());
        }
        if (configuration.getClientName() != null && !configuration.getClientName().isBlank()) {
            builder.withClientName(configuration.getClientName());
        }
        if (configuration.isSsl()) {
            builder.withSsl(true);
        }

        RedisURI redisUri = builder.build();
        return RedisClient.create(clientResources, redisUri);
    }

    /**
     * Create a new stateful connection.
     */
    public StatefulRedisConnection<String, String> connect() {
        return redisClient.connect();
    }

    /**
     * Get sync commands (auto-connect).
     */
    public RedisCommands<String, String> sync() {
        return connect().sync();
    }

    public RedisClient getClient() {
        return redisClient;
    }

    public LettuceConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        redisClient.shutdown();
        clientResources.shutdown();
        LOGGER.info("Lettuce client shutdown");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LettuceClientFactory.class);
}
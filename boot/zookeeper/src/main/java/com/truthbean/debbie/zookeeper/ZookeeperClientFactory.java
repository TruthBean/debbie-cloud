/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.zookeeper;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ZookeeperClientFactory {

    private final ZookeeperConfiguration configuration;
    private ZooKeeper zooKeeper;

    public ZookeeperClientFactory(ZookeeperConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Create a ZooKeeper client with the configured connection string and timeout.
     * Uses a default watcher that logs connection events.
     */
    public ZooKeeper createClient() {
        return createClient(null);
    }

    /**
     * Create a ZooKeeper client with a custom watcher.
     */
    public ZooKeeper createClient(Watcher watcher) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Watcher connectionWatcher = watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                    LOGGER.info("ZooKeeper connection established: " + watchedEvent);
                } else if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired) {
                    LOGGER.warn("ZooKeeper session expired: " + watchedEvent);
                } else if (watchedEvent.getState() == Watcher.Event.KeeperState.Disconnected) {
                    LOGGER.warn("ZooKeeper disconnected: " + watchedEvent);
                }
                if (watcher != null) {
                    watcher.process(watchedEvent);
                }
            };

            ZooKeeper zk = new ZooKeeper(
                    configuration.getConnectionString(),
                    configuration.getSessionTimeout(),
                    connectionWatcher
            );

            boolean connected = latch.await(configuration.getConnectionTimeout(), TimeUnit.MILLISECONDS);
            if (!connected) {
                LOGGER.warn("ZooKeeper connection timed out after " + configuration.getConnectionTimeout() + "ms");
            }

            // Apply digest authentication if configured
            if (configuration.getDigest() != null && !configuration.getDigest().isBlank()) {
                zk.addAuthInfo("digest", configuration.getDigest().getBytes());
            }

            this.zooKeeper = zk;
            return zk;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZooKeeper client", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while creating ZooKeeper client", e);
        }
    }

    /**
     * Get the existing ZooKeeper client, or create a new one if not yet created.
     */
    public ZooKeeper getClient() {
        if (zooKeeper == null || !zooKeeper.getState().isAlive()) {
            return createClient();
        }
        return zooKeeper;
    }

    /**
     * Close the ZooKeeper client.
     */
    public void close() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
                LOGGER.info("ZooKeeper client closed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("Interrupted while closing ZooKeeper client", e);
            }
        }
    }

    public ZookeeperConfiguration getConfiguration() {
        return configuration;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperClientFactory.class);
}
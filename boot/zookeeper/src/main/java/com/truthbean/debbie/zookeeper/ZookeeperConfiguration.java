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

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.zookeeper")
public class ZookeeperConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * ZooKeeper connection string, e.g. "localhost:2181".
     */
    @PropertyInject("connection-string")
    private String connectionString = "localhost:2181";

    /**
     * Session timeout in milliseconds.
     * Default: 30000
     */
    @PropertyInject(value = "session-timeout", transformer = IntegerTransformer.class, defaultValue = "30000")
    private int sessionTimeout;

    /**
     * Connection timeout in milliseconds.
     * Default: 15000
     */
    @PropertyInject(value = "connection-timeout", transformer = IntegerTransformer.class, defaultValue = "15000")
    private int connectionTimeout;

    /**
     * Base sleep time for retry in milliseconds.
     * Default: 1000
     */
    @PropertyInject(value = "base-sleep-time-ms", transformer = IntegerTransformer.class, defaultValue = "1000")
    private int baseSleepTimeMs;

    /**
     * Maximum number of retry attempts.
     * Default: 3
     */
    @PropertyInject(value = "max-retries", transformer = IntegerTransformer.class, defaultValue = "3")
    private int maxRetries;

    /**
     * Optional ZooKeeper namespace.
     */
    @PropertyInject("namespace")
    private String namespace;

    /**
     * Optional digest authentication, e.g. "user:password".
     */
    @PropertyInject("digest")
    private String digest;

    // ======================== Getter/Setter ========================

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
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
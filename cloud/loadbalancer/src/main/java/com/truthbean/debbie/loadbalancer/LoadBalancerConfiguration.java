/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.loadbalancer;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * Configuration of debbie-loadbalancer.
 * <p>
 * properties prefix: {@code debbie.loadbalancer}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.loadbalancer")
public class LoadBalancerConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "default-strategy", defaultValue = "round-robin")
    private String defaultStrategy = "round-robin";

    @PropertyInject(value = "health-check.enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean healthCheckEnable = false;

    @PropertyInject(value = "health-check.interval", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int healthCheckInterval = 10000;

    @PropertyInject(value = "retry.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean retryEnable = true;

    @PropertyInject(value = "retry.max-attempts", transformer = IntegerTransformer.class, defaultValue = "3")
    private int retryMaxAttempts = 3;

    @PropertyInject(value = "sticky.enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean stickyEnable = false;

    @PropertyInject(value = "cache.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean cacheEnable = true;

    @PropertyInject(value = "cache.ttl", transformer = IntegerTransformer.class, defaultValue = "30000")
    private int cacheTtl = 30000;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public String getDefaultStrategy() { return defaultStrategy; }
    public void setDefaultStrategy(String defaultStrategy) { this.defaultStrategy = defaultStrategy; }

    public boolean isHealthCheckEnable() { return healthCheckEnable; }
    public void setHealthCheckEnable(boolean v) { this.healthCheckEnable = v; }

    public int getHealthCheckInterval() { return healthCheckInterval; }
    public void setHealthCheckInterval(int v) { this.healthCheckInterval = v; }

    public boolean isRetryEnable() { return retryEnable; }
    public void setRetryEnable(boolean v) { this.retryEnable = v; }

    public int getRetryMaxAttempts() { return retryMaxAttempts; }
    public void setRetryMaxAttempts(int v) { this.retryMaxAttempts = v; }

    public boolean isStickyEnable() { return stickyEnable; }
    public void setStickyEnable(boolean v) { this.stickyEnable = v; }

    public boolean isCacheEnable() { return cacheEnable; }
    public void setCacheEnable(boolean v) { this.cacheEnable = v; }

    public int getCacheTtl() { return cacheTtl; }
    public void setCacheTtl(int v) { this.cacheTtl = v; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new LoadBalancerConfiguration();
        c.enable = this.enable;
        c.defaultStrategy = this.defaultStrategy;
        c.healthCheckEnable = this.healthCheckEnable;
        c.healthCheckInterval = this.healthCheckInterval;
        c.retryEnable = this.retryEnable;
        c.retryMaxAttempts = this.retryMaxAttempts;
        c.stickyEnable = this.stickyEnable;
        c.cacheEnable = this.cacheEnable;
        c.cacheTtl = this.cacheTtl;
        return (T) c;
    }

    @Override
    public void close() {}
}
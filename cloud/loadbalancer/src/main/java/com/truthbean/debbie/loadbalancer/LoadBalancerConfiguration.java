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

/**
 * Configuration of debbie-loadbalancer.
 * <p>
 * properties prefix: {@code debbie.loadbalancer}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LoadBalancerConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    private String defaultStrategy = "round-robin";

    private boolean healthCheckEnable = false;

    private int healthCheckInterval = 10000;

    private boolean retryEnable = true;

    private int retryMaxAttempts = 3;

    private boolean stickyEnable = false;

    private boolean cacheEnable = true;

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
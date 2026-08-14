/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.sentinel;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.sentinel")
public class SentinelConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Sentinel log directory.
     * Default: ${user.home}/logs/csp/
     */
    @PropertyInject(value = "log-dir", defaultValue = "")
    private String logDir;

    /**
     * Sentinel log file name prefix.
     * Default: sentinel-record
     */
    @PropertyInject(value = "log-name-prefix", defaultValue = "sentinel-record")
    private String logNamePrefix;

    /**
     * Charset for log output.
     * Default: UTF-8
     */
    @PropertyInject(value = "charset", defaultValue = "UTF-8")
    private String charset;

    // ======================== Getter/Setter ========================

    public String getLogDir() { return logDir; }
    public void setLogDir(String logDir) { this.logDir = logDir; }

    public String getLogNamePrefix() { return logNamePrefix; }
    public void setLogNamePrefix(String logNamePrefix) { this.logNamePrefix = logNamePrefix; }

    public String getCharset() { return charset; }
    public void setCharset(String charset) { this.charset = charset; }

    // ======================== DebbieConfiguration ========================

    @Override
    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() { return (T) this; }

    @Override
    public void close() {}
}
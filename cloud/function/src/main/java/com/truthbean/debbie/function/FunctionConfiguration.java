/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.function;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;

/**
 * Configuration of debbie-function.
 * <p>
 * properties prefix: {@code debbie.function}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.function")
public class FunctionConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "http.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean httpEnable = true;

    @PropertyInject(value = "http.prefix", defaultValue = "/function")
    private String httpPrefix = "/function";

    @PropertyInject(value = "routing.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean routingEnable = true;

    @PropertyInject(value = "composition.enable", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean compositionEnable = true;

    @PropertyInject(value = "auto-scan", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean autoScan = false;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public boolean isHttpEnable() { return httpEnable; }
    public void setHttpEnable(boolean httpEnable) { this.httpEnable = httpEnable; }

    public String getHttpPrefix() { return httpPrefix; }
    public void setHttpPrefix(String httpPrefix) { this.httpPrefix = httpPrefix; }

    public boolean isRoutingEnable() { return routingEnable; }
    public void setRoutingEnable(boolean routingEnable) { this.routingEnable = routingEnable; }

    public boolean isCompositionEnable() { return compositionEnable; }
    public void setCompositionEnable(boolean compositionEnable) { this.compositionEnable = compositionEnable; }

    public boolean isAutoScan() { return autoScan; }
    public void setAutoScan(boolean autoScan) { this.autoScan = autoScan; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new FunctionConfiguration();
        c.enable = this.enable;
        c.httpEnable = this.httpEnable;
        c.httpPrefix = this.httpPrefix;
        c.routingEnable = this.routingEnable;
        c.compositionEnable = this.compositionEnable;
        c.autoScan = this.autoScan;
        return (T) c;
    }

    @Override
    public void close() {}
}
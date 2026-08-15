/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tracing;

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.DoubleTransformer;
import com.truthbean.transformer.text.IntegerTransformer;

/**
 * Configuration of debbie-tracing.
 * <p>
 * properties prefix: {@code debbie.tracing}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.tracing")
public class TracingConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    @PropertyInject(value = "service-name", defaultValue = "application")
    private String serviceName = "application";

    @PropertyInject(value = "sampler.strategy", defaultValue = "always")
    private String samplerStrategy = "always";

    @PropertyInject(value = "sampler.rate", transformer = DoubleTransformer.class, defaultValue = "0.1")
    private double samplerRate = 0.1;

    @PropertyInject(value = "reporter.type", defaultValue = "logging")
    private String reporterType = "logging";

    @PropertyInject(value = "reporter.endpoint")
    private String reporterEndpoint;

    @PropertyInject(value = "reporter.batch-size", transformer = IntegerTransformer.class, defaultValue = "100")
    private int reporterBatchSize = 100;

    @PropertyInject(value = "reporter.connect-timeout", transformer = IntegerTransformer.class, defaultValue = "5000")
    private int reporterConnectTimeout = 5000;

    @PropertyInject(value = "propagation.type", defaultValue = "w3c")
    private String propagationType = "w3c";

    @PropertyInject(value = "propagation.include-tags", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean propagationIncludeTags = false;

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getSamplerStrategy() { return samplerStrategy; }
    public void setSamplerStrategy(String samplerStrategy) { this.samplerStrategy = samplerStrategy; }

    public double getSamplerRate() { return samplerRate; }
    public void setSamplerRate(double samplerRate) { this.samplerRate = samplerRate; }

    public String getReporterType() { return reporterType; }
    public void setReporterType(String reporterType) { this.reporterType = reporterType; }

    public String getReporterEndpoint() { return reporterEndpoint; }
    public void setReporterEndpoint(String reporterEndpoint) { this.reporterEndpoint = reporterEndpoint; }

    public int getReporterBatchSize() { return reporterBatchSize; }
    public void setReporterBatchSize(int v) { this.reporterBatchSize = v; }

    public int getReporterConnectTimeout() { return reporterConnectTimeout; }
    public void setReporterConnectTimeout(int v) { this.reporterConnectTimeout = v; }

    public String getPropagationType() { return propagationType; }
    public void setPropagationType(String propagationType) { this.propagationType = propagationType; }

    public boolean isPropagationIncludeTags() { return propagationIncludeTags; }
    public void setPropagationIncludeTags(boolean v) { this.propagationIncludeTags = v; }

    @Override
    public String getProfile() { return EnvironmentDepositoryHolder.DEFAULT_PROFILE; }

    @Override
    public String getCategory() { return EnvironmentDepositoryHolder.DEFAULT_CATEGORY; }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DebbieConfiguration> T copy() {
        var c = new TracingConfiguration();
        c.enable = this.enable;
        c.serviceName = this.serviceName;
        c.samplerStrategy = this.samplerStrategy;
        c.samplerRate = this.samplerRate;
        c.reporterType = this.reporterType;
        c.reporterEndpoint = this.reporterEndpoint;
        c.reporterBatchSize = this.reporterBatchSize;
        c.reporterConnectTimeout = this.reporterConnectTimeout;
        c.propagationType = this.propagationType;
        c.propagationIncludeTags = this.propagationIncludeTags;
        return (T) c;
    }

    @Override
    public void close() {}
}
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
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;

/**
 * Configuration of debbie-sentinel.
 * <p>
 * properties prefix: {@code debbie.sentinel}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.sentinel")
public class SentinelConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Application / project name registered to Sentinel dashboard.
     */
    @PropertyInject(value = "app-name", defaultValue = "")
    private String appName = "";

    /**
     * Whether to eagerly initialize Sentinel transport on startup.
     * Default: false
     */
    @PropertyInject(value = "eager", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean eager = false;

    // ======================== Log ========================

    /**
     * Sentinel log directory.
     * Default: ${user.home}/logs/csp/
     */
    @PropertyInject(value = "log-dir", defaultValue = "")
    private String logDir = "";

    /**
     * Sentinel log file name prefix.
     * Default: sentinel-record
     */
    @PropertyInject(value = "log-name-prefix", defaultValue = "sentinel-record")
    private String logNamePrefix = "sentinel-record";

    /**
     * Whether to use PID in log file name.
     * Default: false
     */
    @PropertyInject(value = "log-use-pid", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean logUsePid = false;

    /**
     * Charset for log output.
     * Default: UTF-8
     */
    @PropertyInject(value = "charset", defaultValue = "UTF-8")
    private String charset = "UTF-8";

    // ======================== Transport / Dashboard ========================

    /**
     * Sentinel dashboard server address (e.g. localhost:8080).
     * If set, transport will be initialized to communicate with dashboard.
     */
    @PropertyInject(value = "transport.dashboard", defaultValue = "")
    private String dashboard = "";

    /**
     * Client port that Sentinel transport listens on.
     * Default: 8719
     */
    @PropertyInject(value = "transport.port", transformer = IntegerTransformer.class, defaultValue = "8719")
    private int transportPort = 8719;

    /**
     * Heartbeat interval in milliseconds.
     * Default: 10000 (10 seconds)
     */
    @PropertyInject(value = "transport.heartbeat-interval-ms", transformer = LongTransformer.class, defaultValue = "10000")
    private long heartbeatIntervalMs = 10000;

    /**
     * Client IP reported to dashboard. Auto-detected if empty.
     */
    @PropertyInject(value = "transport.client-ip", defaultValue = "")
    private String clientIp = "";

    // ======================== Metric ========================

    /**
     * Single metric log file size in bytes.
     * Default: 52428800 (50 MB)
     */
    @PropertyInject(value = "metric.file-size", transformer = LongTransformer.class, defaultValue = "52428800")
    private long metricFileSize = 52428800L;

    /**
     * Total metric log file count.
     * Default: 6
     */
    @PropertyInject(value = "metric.file-count", transformer = IntegerTransformer.class, defaultValue = "6")
    private int metricFileCount = 6;

    /**
     * Metric log flush interval in seconds.
     * Default: 1
     */
    @PropertyInject(value = "metric.flush-interval", transformer = LongTransformer.class, defaultValue = "1")
    private long metricFlushInterval = 1L;

    // ======================== Statistic ========================

    /**
     * Max RT (response time) in milliseconds. Any RT exceeding this will be truncated.
     * Default: 4900
     */
    @PropertyInject(value = "statistic.max-rt", transformer = IntegerTransformer.class, defaultValue = "4900")
    private int statisticMaxRt = 4900;

    /**
     * Cold factor for warm up.
     * Default: 3
     */
    @PropertyInject(value = "cold-factor", transformer = IntegerTransformer.class, defaultValue = "3")
    private int coldFactor = 3;

    // ======================== System Rule Defaults ========================

    /**
     * Highest system load threshold. -1 means not set.
     * Default: -1
     */
    @PropertyInject(value = "system.load", defaultValue = "-1")
    private double systemLoad = -1;

    /**
     * Highest CPU usage threshold (0.0 ~ 1.0). -1 means not set.
     * Default: -1
     */
    @PropertyInject(value = "system.cpu-usage", defaultValue = "-1")
    private double systemCpuUsage = -1;

    /**
     * Average RT threshold in ms. -1 means not set.
     * Default: -1
     */
    @PropertyInject(value = "system.avg-rt", transformer = LongTransformer.class, defaultValue = "-1")
    private long systemAvgRt = -1;

    /**
     * Max thread threshold. -1 means not set.
     * Default: -1
     */
    @PropertyInject(value = "system.max-thread", transformer = LongTransformer.class, defaultValue = "-1")
    private long systemMaxThread = -1;

    /**
     * Inbound QPS threshold. -1 means not set.
     * Default: -1
     */
    @PropertyInject(value = "system.qps", defaultValue = "-1")
    private double systemQps = -1;

    // ======================== Web Filter ========================

    /**
     * Block page URL for web integration. When a request is blocked, redirect to this URL.
     */
    @PropertyInject(value = "block-page", defaultValue = "")
    private String blockPage = "";

    /**
     * Whether to enable Sentinel web filter.
     * Default: true
     */
    @PropertyInject(value = "filter.enabled", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean filterEnabled = true;

    /**
     * URL patterns for Sentinel web filter, comma-separated.
     * Default: /*
     */
    @PropertyInject(value = "filter.url-patterns", defaultValue = "/*")
    private String filterUrlPatterns = "/*";

    /**
     * Order of Sentinel web filter.
     * Default: -2147483648 (Integer.MIN_VALUE + 1, highest priority)
     */
    @PropertyInject(value = "filter.order", transformer = IntegerTransformer.class, defaultValue = "-2147483648")
    private int filterOrder = -2147483648;

    // ======================== Getter/Setter ========================

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public boolean isEager() { return eager; }
    public void setEager(boolean eager) { this.eager = eager; }

    public String getLogDir() { return logDir; }
    public void setLogDir(String logDir) { this.logDir = logDir; }

    public String getLogNamePrefix() { return logNamePrefix; }
    public void setLogNamePrefix(String logNamePrefix) { this.logNamePrefix = logNamePrefix; }

    public boolean isLogUsePid() { return logUsePid; }
    public void setLogUsePid(boolean logUsePid) { this.logUsePid = logUsePid; }

    public String getCharset() { return charset; }
    public void setCharset(String charset) { this.charset = charset; }

    public String getDashboard() { return dashboard; }
    public void setDashboard(String dashboard) { this.dashboard = dashboard; }

    public int getTransportPort() { return transportPort; }
    public void setTransportPort(int transportPort) { this.transportPort = transportPort; }

    public long getHeartbeatIntervalMs() { return heartbeatIntervalMs; }
    public void setHeartbeatIntervalMs(long heartbeatIntervalMs) { this.heartbeatIntervalMs = heartbeatIntervalMs; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public long getMetricFileSize() { return metricFileSize; }
    public void setMetricFileSize(long metricFileSize) { this.metricFileSize = metricFileSize; }

    public int getMetricFileCount() { return metricFileCount; }
    public void setMetricFileCount(int metricFileCount) { this.metricFileCount = metricFileCount; }

    public long getMetricFlushInterval() { return metricFlushInterval; }
    public void setMetricFlushInterval(long metricFlushInterval) { this.metricFlushInterval = metricFlushInterval; }

    public int getStatisticMaxRt() { return statisticMaxRt; }
    public void setStatisticMaxRt(int statisticMaxRt) { this.statisticMaxRt = statisticMaxRt; }

    public int getColdFactor() { return coldFactor; }
    public void setColdFactor(int coldFactor) { this.coldFactor = coldFactor; }

    public double getSystemLoad() { return systemLoad; }
    public void setSystemLoad(double systemLoad) { this.systemLoad = systemLoad; }

    public double getSystemCpuUsage() { return systemCpuUsage; }
    public void setSystemCpuUsage(double systemCpuUsage) { this.systemCpuUsage = systemCpuUsage; }

    public long getSystemAvgRt() { return systemAvgRt; }
    public void setSystemAvgRt(long systemAvgRt) { this.systemAvgRt = systemAvgRt; }

    public long getSystemMaxThread() { return systemMaxThread; }
    public void setSystemMaxThread(long systemMaxThread) { this.systemMaxThread = systemMaxThread; }

    public double getSystemQps() { return systemQps; }
    public void setSystemQps(double systemQps) { this.systemQps = systemQps; }

    public String getBlockPage() { return blockPage; }
    public void setBlockPage(String blockPage) { this.blockPage = blockPage; }

    public boolean isFilterEnabled() { return filterEnabled; }
    public void setFilterEnabled(boolean filterEnabled) { this.filterEnabled = filterEnabled; }

    public String getFilterUrlPatterns() { return filterUrlPatterns; }
    public void setFilterUrlPatterns(String filterUrlPatterns) { this.filterUrlPatterns = filterUrlPatterns; }

    public int getFilterOrder() { return filterOrder; }
    public void setFilterOrder(int filterOrder) { this.filterOrder = filterOrder; }

    // ======================== Helpers ========================

    public boolean hasDashboard() {
        return dashboard != null && !dashboard.isBlank();
    }

    public boolean hasAppName() {
        return appName != null && !appName.isBlank();
    }

    public boolean hasClientIp() {
        return clientIp != null && !clientIp.isBlank();
    }

    public boolean hasLogDir() {
        return logDir != null && !logDir.isBlank();
    }

    public boolean hasBlockPage() {
        return blockPage != null && !blockPage.isBlank();
    }

    public boolean hasSystemRuleConfig() {
        return systemLoad >= 0 || systemCpuUsage >= 0 || systemAvgRt >= 0
                || systemMaxThread >= 0 || systemQps >= 0;
    }

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
    public <T extends DebbieConfiguration> T copy() {
        var c = new SentinelConfiguration();
        c.enable = this.enable;
        c.appName = this.appName;
        c.eager = this.eager;
        c.logDir = this.logDir;
        c.logNamePrefix = this.logNamePrefix;
        c.logUsePid = this.logUsePid;
        c.charset = this.charset;
        c.dashboard = this.dashboard;
        c.transportPort = this.transportPort;
        c.heartbeatIntervalMs = this.heartbeatIntervalMs;
        c.clientIp = this.clientIp;
        c.metricFileSize = this.metricFileSize;
        c.metricFileCount = this.metricFileCount;
        c.metricFlushInterval = this.metricFlushInterval;
        c.statisticMaxRt = this.statisticMaxRt;
        c.coldFactor = this.coldFactor;
        c.systemLoad = this.systemLoad;
        c.systemCpuUsage = this.systemCpuUsage;
        c.systemAvgRt = this.systemAvgRt;
        c.systemMaxThread = this.systemMaxThread;
        c.systemQps = this.systemQps;
        c.blockPage = this.blockPage;
        c.filterEnabled = this.filterEnabled;
        c.filterUrlPatterns = this.filterUrlPatterns;
        c.filterOrder = this.filterOrder;
        return (T) c;
    }

    @Override
    public void close() {}

    @Override
    public String toString() {
        return "SentinelConfiguration{appName=" + appName
                + ", eager=" + eager
                + ", dashboard=" + dashboard
                + ", transportPort=" + transportPort
                + ", heartbeatIntervalMs=" + heartbeatIntervalMs
                + ", metricFileSize=" + metricFileSize
                + ", metricFileCount=" + metricFileCount
                + ", statisticMaxRt=" + statisticMaxRt
                + ", systemLoad=" + systemLoad
                + ", systemCpuUsage=" + systemCpuUsage
                + ", filterEnabled=" + filterEnabled + "}";
    }
}

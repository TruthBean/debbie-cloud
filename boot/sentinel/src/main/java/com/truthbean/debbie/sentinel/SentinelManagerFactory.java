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

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sentinel manager factory. Handles Sentinel configuration initialization,
 * rule management (flow, degrade, system, authority), and transport setup.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SentinelManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentinelManagerFactory.class);

    private final SentinelConfiguration configuration;

    private boolean initialized = false;

    public SentinelManagerFactory(SentinelConfiguration configuration) {
        this.configuration = configuration;
    }

    // ======================== Initialization ========================

    /**
     * Initialize Sentinel configuration: app name, charset, metric, log, transport.
     */
    public void init() {
        if (initialized) {
            LOGGER.warn("Sentinel manager already initialized, skipping");
            return;
        }
        initSentinelConfig();
        initLogConfig();
        initTransportConfig();
        initSystemRules();
        initialized = true;
        LOGGER.info("Sentinel manager initialized");
    }

    /**
     * Configure Sentinel core config: app name, charset, metric, statistic, cold factor.
     */
    private void initSentinelConfig() {
        if (configuration.hasAppName()) {
            SentinelConfig.setConfig(SentinelConfig.APP_NAME_PROP_KEY, configuration.getAppName());
            SentinelConfig.setConfig(SentinelConfig.PROJECT_NAME_PROP_KEY, configuration.getAppName());
            LOGGER.info("Sentinel app name: " + configuration.getAppName());
        }

        SentinelConfig.setConfig(SentinelConfig.CHARSET, configuration.getCharset());

        SentinelConfig.setConfig(SentinelConfig.SINGLE_METRIC_FILE_SIZE,
                String.valueOf(configuration.getMetricFileSize()));
        SentinelConfig.setConfig(SentinelConfig.TOTAL_METRIC_FILE_COUNT,
                String.valueOf(configuration.getMetricFileCount()));
        SentinelConfig.setConfig(SentinelConfig.METRIC_FLUSH_INTERVAL,
                String.valueOf(configuration.getMetricFlushInterval()));

        SentinelConfig.setConfig(SentinelConfig.STATISTIC_MAX_RT,
                String.valueOf(configuration.getStatisticMaxRt()));
        SentinelConfig.setConfig(SentinelConfig.COLD_FACTOR,
                String.valueOf(configuration.getColdFactor()));

        LOGGER.info("Sentinel core config applied: charset=" + configuration.getCharset()
                + ", metricFileSize=" + configuration.getMetricFileSize()
                + ", metricFileCount=" + configuration.getMetricFileCount()
                + ", statisticMaxRt=" + configuration.getStatisticMaxRt()
                + ", coldFactor=" + configuration.getColdFactor());
    }

    /**
     * Configure Sentinel log directory and PID usage via system properties.
     */
    private void initLogConfig() {
        if (configuration.hasLogDir()) {
            System.setProperty(LogBase.LOG_DIR, configuration.getLogDir());
            LOGGER.info("Sentinel log dir: " + configuration.getLogDir());
        }
        if (configuration.isLogUsePid()) {
            System.setProperty(LogBase.LOG_NAME_USE_PID, "true");
        }
        System.setProperty(LogBase.LOG_CHARSET, configuration.getCharset());
    }

    /**
     * Configure Sentinel transport (dashboard communication) via system properties.
     * These properties are read by sentinel-transport module if present on classpath.
     */
    private void initTransportConfig() {
        if (!configuration.hasDashboard()) {
            LOGGER.info("Sentinel dashboard not configured, transport will not be initialized");
            return;
        }

        System.setProperty("csp.sentinel.dashboard.server", configuration.getDashboard());
        LOGGER.info("Sentinel dashboard: " + configuration.getDashboard());

        System.setProperty("csp.sentinel.heartbeat.interval.ms",
                String.valueOf(configuration.getHeartbeatIntervalMs()));

        if (configuration.hasClientIp()) {
            System.setProperty("csp.sentinel.api.port", String.valueOf(configuration.getTransportPort()));
        }

        if (configuration.hasAppName()) {
            System.setProperty("project.name", configuration.getAppName());
        }

        LOGGER.info("Sentinel transport config applied: dashboard=" + configuration.getDashboard()
                + ", port=" + configuration.getTransportPort()
                + ", heartbeatIntervalMs=" + configuration.getHeartbeatIntervalMs());
    }

    /**
     * Load default system rules from configuration if any system threshold is set.
     */
    private void initSystemRules() {
        if (!configuration.hasSystemRuleConfig()) {
            return;
        }

        var rule = new SystemRule();
        if (configuration.getSystemLoad() >= 0) {
            rule.setHighestSystemLoad(configuration.getSystemLoad());
        }
        if (configuration.getSystemCpuUsage() >= 0) {
            rule.setHighestCpuUsage(configuration.getSystemCpuUsage());
        }
        if (configuration.getSystemAvgRt() >= 0) {
            rule.setAvgRt(configuration.getSystemAvgRt());
        }
        if (configuration.getSystemMaxThread() >= 0) {
            rule.setMaxThread(configuration.getSystemMaxThread());
        }
        if (configuration.getSystemQps() >= 0) {
            rule.setQps(configuration.getSystemQps());
        }

        SystemRuleManager.loadRules(Collections.singletonList(rule));
        LOGGER.info("Sentinel default system rule loaded: " + rule);
    }

    // ======================== Flow Rules ========================

    /**
     * Load flow rules.
     */
    public void loadFlowRules(List<FlowRule> rules) {
        FlowRuleManager.loadRules(rules);
        LOGGER.info("Sentinel flow rules loaded: " + rules.size());
    }

    /**
     * Get current flow rules.
     */
    public List<FlowRule> getFlowRules() {
        return new ArrayList<>(FlowRuleManager.getRules());
    }

    // ======================== Degrade Rules ========================

    /**
     * Load degrade rules.
     */
    public void loadDegradeRules(List<DegradeRule> rules) {
        DegradeRuleManager.loadRules(rules);
        LOGGER.info("Sentinel degrade rules loaded: " + rules.size());
    }

    /**
     * Get current degrade rules.
     */
    public List<DegradeRule> getDegradeRules() {
        return new ArrayList<>(DegradeRuleManager.getRules());
    }

    // ======================== System Rules ========================

    /**
     * Load system rules.
     */
    public void loadSystemRules(List<SystemRule> rules) {
        SystemRuleManager.loadRules(rules);
        LOGGER.info("Sentinel system rules loaded: " + rules.size());
    }

    /**
     * Get current system rules.
     */
    public List<SystemRule> getSystemRules() {
        return new ArrayList<>(SystemRuleManager.getRules());
    }

    // ======================== Authority Rules ========================

    /**
     * Load authority rules.
     */
    public void loadAuthorityRules(List<AuthorityRule> rules) {
        AuthorityRuleManager.loadRules(rules);
        LOGGER.info("Sentinel authority rules loaded: " + rules.size());
    }

    /**
     * Get current authority rules.
     */
    public List<AuthorityRule> getAuthorityRules() {
        return new ArrayList<>(AuthorityRuleManager.getRules());
    }

    // ======================== Misc ========================

    public SentinelConfiguration getConfiguration() {
        return configuration;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void close() {
        LOGGER.info("Sentinel manager closed");
    }
}

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

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SentinelManagerFactory {

    private final SentinelConfiguration configuration;

    public SentinelManagerFactory(SentinelConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Load flow rules.
     */
    public void loadFlowRules(List<FlowRule> rules) {
        FlowRuleManager.loadRules(rules);
        LOGGER.info("Sentinel flow rules loaded: " + rules.size());
    }

    /**
     * Load degrade rules.
     */
    public void loadDegradeRules(List<DegradeRule> rules) {
        DegradeRuleManager.loadRules(rules);
        LOGGER.info("Sentinel degrade rules loaded: " + rules.size());
    }

    /**
     * Get current flow rules.
     */
    public List<FlowRule> getFlowRules() {
        return new ArrayList<>(FlowRuleManager.getRules());
    }

    /**
     * Get current degrade rules.
     */
    public List<DegradeRule> getDegradeRules() {
        return new ArrayList<>(DegradeRuleManager.getRules());
    }

    public SentinelConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        LOGGER.info("Sentinel manager closed");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SentinelManagerFactory.class);
}
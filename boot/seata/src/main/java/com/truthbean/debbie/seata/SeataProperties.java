/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.seata;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties reader of debbie-seata.
 * <p>
 * Reads the {@link SeataConfiguration} from the environment using the prefix
 * {@code debbie.seata.} and fills a plain configuration object, instead of
 * relying on annotations.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SeataProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<SeataConfiguration> {

    /**
     * key prefix of the seata properties,
     * the full key is this prefix plus the property name, e.g. {@code debbie.seata.application-id}
     */
    private static final String KEY_PREFIX = "debbie.seata.";

    private final Map<String, SeataConfiguration> map = new HashMap<>();
    private SeataConfiguration configuration;

    public SeataProperties() {
    }

    @Override
    public Set<String> getProfiles() {
        return map.keySet();
    }

    @Override
    public Map<String, Map<String, SeataConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return Map.of();
    }

    @Override
    public Set<String> getCategories(String profile) {
        return Set.of();
    }

    @Override
    public boolean containConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return false;
    }

    @Override
    public SeataConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        return getConfiguration(applicationContext);
    }

    @Override
    public SeataConfiguration getConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new SeataConfiguration();

        configuration.setEnable(getBooleanValue(KEY_PREFIX + "enable", true));
        configuration.setApplicationId(getStringValue(KEY_PREFIX + "application-id", "debbie-app"));
        configuration.setTxServiceGroup(getStringValue(KEY_PREFIX + "tx-service-group", "default"));
        configuration.setServerAddr(getStringValue(KEY_PREFIX + "server-addr", "localhost:8091"));
        configuration.setEnableAutoDataSourceProxy(getBooleanValue(KEY_PREFIX + "enable-auto-data-source-proxy", true));
        configuration.setDataSourceProxyMode(getStringValue(KEY_PREFIX + "data-source-proxy-mode", "AT"));
        configuration.setUseJdkProxy(getBooleanValue(KEY_PREFIX + "use-jdk-proxy", false));
        configuration.setDisableGlobalTransaction(getBooleanValue(KEY_PREFIX + "disable-global-transaction", false));

        // transport
        configuration.setTransportType(getStringValue(KEY_PREFIX + "transport.type", "TCP"));
        configuration.setTransportServer(getStringValue(KEY_PREFIX + "transport.server", "NIO"));
        configuration.setTransportSerialization(getStringValue(KEY_PREFIX + "transport.serialization", "seata"));
        configuration.setTransportCompressor(getStringValue(KEY_PREFIX + "transport.compressor", "none"));
        configuration.setTransportHeartbeat(getBooleanValue(KEY_PREFIX + "transport.heartbeat", true));
        configuration.setTransportShutdownWait(getIntegerValue(KEY_PREFIX + "transport.shutdown.wait", 3));
        configuration.setTransportShutdownWaitPeriod(getLongValue(KEY_PREFIX + "transport.shutdown.wait-period", 1000));

        // client rm
        configuration.setRmAsyncCommitBufferLimit(getIntegerValue(KEY_PREFIX + "client.rm.async-commit-buffer-limit", 10000));
        configuration.setRmReportRetryCount(getIntegerValue(KEY_PREFIX + "client.rm.report-retry-count", 5));
        configuration.setRmTableMetaCheckEnable(getBooleanValue(KEY_PREFIX + "client.rm.table-meta-check-enable", false));
        configuration.setRmTableMetaCheckerInterval(getLongValue(KEY_PREFIX + "client.rm.table-meta-checker-interval", 60000));
        configuration.setRmSqlParserType(getStringValue(KEY_PREFIX + "client.rm.sql-parser-type", "druid"));
        configuration.setRmLockRetryInterval(getIntegerValue(KEY_PREFIX + "client.rm.lock.retry-interval", 10));
        configuration.setRmLockRetryTimes(getIntegerValue(KEY_PREFIX + "client.rm.lock.retry-times", 30));
        configuration.setRmLockRetryPolicyBranchRollbackOnConflict(getBooleanValue(KEY_PREFIX + "client.rm.lock.retry-policy-branch-rollback-on-conflict", true));

        // client tm
        configuration.setTmCommitRetryCount(getIntegerValue(KEY_PREFIX + "client.tm.commit-retry-count", 5));
        configuration.setTmRollbackRetryCount(getIntegerValue(KEY_PREFIX + "client.tm.rollback-retry-count", 5));
        configuration.setTmDefaultGlobalTransactionTimeout(getIntegerValue(KEY_PREFIX + "client.tm.default-global-transaction-timeout", 60000));

        // client undo
        configuration.setUndoDataValidation(getBooleanValue(KEY_PREFIX + "client.undo.data-validation", true));
        configuration.setUndoLogSerialization(getStringValue(KEY_PREFIX + "client.undo.log-serialization", "jackson"));
        configuration.setUndoOnlyCareUpdateColumns(getBooleanValue(KEY_PREFIX + "client.undo.only-care-update-columns", false));

        // registry
        configuration.setRegistryType(getStringValue(KEY_PREFIX + "registry.type", "file"));
        configuration.setRegistryNacosServerAddr(getStringValue(KEY_PREFIX + "registry.nacos.server-addr", ""));
        configuration.setRegistryNacosNamespace(getStringValue(KEY_PREFIX + "registry.nacos.namespace", ""));
        configuration.setRegistryNacosGroup(getStringValue(KEY_PREFIX + "registry.nacos.group", "SEATA_GROUP"));
        configuration.setRegistryNacosCluster(getStringValue(KEY_PREFIX + "registry.nacos.cluster", "default"));

        // config
        configuration.setConfigType(getStringValue(KEY_PREFIX + "config.type", "file"));
        configuration.setConfigNacosServerAddr(getStringValue(KEY_PREFIX + "config.nacos.server-addr", ""));
        configuration.setConfigNacosNamespace(getStringValue(KEY_PREFIX + "config.nacos.namespace", ""));
        configuration.setConfigNacosGroup(getStringValue(KEY_PREFIX + "config.nacos.group", "SEATA_GROUP"));

        // load balance
        configuration.setLoadBalanceType(getStringValue(KEY_PREFIX + "load-balance.type", "RandomLoadBalance"));
        configuration.setLoadBalanceVirtualNodes(getIntegerValue(KEY_PREFIX + "load-balance.virtual-nodes", 10));

        // log
        configuration.setLogExceptionRate(getIntegerValue(KEY_PREFIX + "log.exception-rate", 100));

        // tcc fence
        configuration.setTccFenceLogTableName(getStringValue(KEY_PREFIX + "tcc.fence.log-table-name", "tcc_fence_log"));
        configuration.setTccFenceCleanPeriod(getLongValue(KEY_PREFIX + "tcc.fence.clean-period", 3600000));

        map.put(DEFAULT_PROFILE, configuration);

        return configuration;
    }

    @Override
    public void close() {
        map.clear();
        configuration = null;
    }
}
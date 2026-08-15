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

import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.transformer.text.BooleanTransformer;
import com.truthbean.transformer.text.IntegerTransformer;
import com.truthbean.transformer.text.LongTransformer;

/**
 * Configuration of debbie-seata.
 * <p>
 * properties prefix: {@code debbie.seata}
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@PropertiesConfiguration(keyPrefix = "debbie.seata")
public class SeataConfiguration implements DebbieConfiguration {

    private boolean enable = true;

    /**
     * Seata application id.
     * Default: debbie-app
     */
    @PropertyInject(value = "application-id", defaultValue = "debbie-app")
    private String applicationId = "debbie-app";

    /**
     * Seata transaction service group.
     * Default: default
     */
    @PropertyInject(value = "tx-service-group", defaultValue = "default")
    private String txServiceGroup = "default";

    /**
     * Seata server address (for file registry mode).
     * Default: localhost:8091
     */
    @PropertyInject(value = "server-addr", defaultValue = "localhost:8091")
    private String serverAddr = "localhost:8091";

    /**
     * Whether to enable auto data source proxy.
     * Default: true
     */
    @PropertyInject(value = "enable-auto-data-source-proxy", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean enableAutoDataSourceProxy = true;

    /**
     * Data source proxy mode: AT or XA.
     * Default: AT
     */
    @PropertyInject(value = "data-source-proxy-mode", defaultValue = "AT")
    private String dataSourceProxyMode = "AT";

    /**
     * Whether to use JDK proxy instead of CGLIB.
     * Default: false
     */
    @PropertyInject(value = "use-jdk-proxy", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean useJdkProxy = false;

    /**
     * Whether to disable global transaction.
     * Default: false
     */
    @PropertyInject(value = "disable-global-transaction", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean disableGlobalTransaction = false;

    // ======================== Transport ========================

    /**
     * Transport type: TCP or NATIVE.
     * Default: TCP
     */
    @PropertyInject(value = "transport.type", defaultValue = "TCP")
    private String transportType = "TCP";

    /**
     * Transport server: NIO or NATIVE.
     * Default: NIO
     */
    @PropertyInject(value = "transport.server", defaultValue = "NIO")
    private String transportServer = "NIO";

    /**
     * Transport serialization: seata, protobuf, etc.
     * Default: seata
     */
    @PropertyInject(value = "transport.serialization", defaultValue = "seata")
    private String transportSerialization = "seata";

    /**
     * Transport compressor: none, gzip, etc.
     * Default: none
     */
    @PropertyInject(value = "transport.compressor", defaultValue = "none")
    private String transportCompressor = "none";

    /**
     * Heartbeat interval in seconds.
     * Default: 30
     */
    @PropertyInject(value = "transport.heartbeat", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean transportHeartbeat = true;

    /**
     * Transport shutdown wait.
     * Default: 3
     */
    @PropertyInject(value = "transport.shutdown.wait", transformer = IntegerTransformer.class, defaultValue = "3")
    private int transportShutdownWait = 3;

    /**
     * Transport shutdown wait period in milliseconds.
     * Default: 1000
     */
    @PropertyInject(value = "transport.shutdown.wait-period", transformer = LongTransformer.class, defaultValue = "1000")
    private long transportShutdownWaitPeriod = 1000L;

    // ======================== Client RM ========================

    /**
     * Async commit buffer limit.
     * Default: 10000
     */
    @PropertyInject(value = "client.rm.async-commit-buffer-limit", transformer = IntegerTransformer.class, defaultValue = "10000")
    private int rmAsyncCommitBufferLimit = 10000;

    /**
     * Report retry count.
     * Default: 5
     */
    @PropertyInject(value = "client.rm.report-retry-count", transformer = IntegerTransformer.class, defaultValue = "5")
    private int rmReportRetryCount = 5;

    /**
     * Whether to enable table meta check.
     * Default: false
     */
    @PropertyInject(value = "client.rm.table-meta-check-enable", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean rmTableMetaCheckEnable = false;

    /**
     * Table meta checker interval in milliseconds.
     * Default: 60000
     */
    @PropertyInject(value = "client.rm.table-meta-checker-interval", transformer = LongTransformer.class, defaultValue = "60000")
    private long rmTableMetaCheckerInterval = 60000L;

    /**
     * SQL parser type: druid, antlr.
     * Default: druid
     */
    @PropertyInject(value = "client.rm.sql-parser-type", defaultValue = "druid")
    private String rmSqlParserType = "druid";

    /**
     * Lock retry interval in milliseconds.
     * Default: 10
     */
    @PropertyInject(value = "client.rm.lock.retry-interval", transformer = IntegerTransformer.class, defaultValue = "10")
    private int rmLockRetryInterval = 10;

    /**
     * Lock retry times.
     * Default: 30
     */
    @PropertyInject(value = "client.rm.lock.retry-times", transformer = IntegerTransformer.class, defaultValue = "30")
    private int rmLockRetryTimes = 30;

    /**
     * Lock retry policy branch rollback on conflict.
     * Default: true
     */
    @PropertyInject(value = "client.rm.lock.retry-policy-branch-rollback-on-conflict", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean rmLockRetryPolicyBranchRollbackOnConflict = true;

    // ======================== Client TM ========================

    /**
     * TM commit retry count.
     * Default: 5
     */
    @PropertyInject(value = "client.tm.commit-retry-count", transformer = IntegerTransformer.class, defaultValue = "5")
    private int tmCommitRetryCount = 5;

    /**
     * TM rollback retry count.
     * Default: 5
     */
    @PropertyInject(value = "client.tm.rollback-retry-count", transformer = IntegerTransformer.class, defaultValue = "5")
    private int tmRollbackRetryCount = 5;

    /**
     * Default global transaction timeout in milliseconds.
     * Default: 60000
     */
    @PropertyInject(value = "client.tm.default-global-transaction-timeout", transformer = IntegerTransformer.class, defaultValue = "60000")
    private int tmDefaultGlobalTransactionTimeout = 60000;

    // ======================== Client Undo ========================

    /**
     * Whether to enable undo data validation.
     * Default: true
     */
    @PropertyInject(value = "client.undo.data-validation", transformer = BooleanTransformer.class, defaultValue = "true")
    private boolean undoDataValidation = true;

    /**
     * Undo log serialization: jackson, fastjson, etc.
     * Default: jackson
     */
    @PropertyInject(value = "client.undo.log-serialization", defaultValue = "jackson")
    private String undoLogSerialization = "jackson";

    /**
     * Whether undo only cares update columns.
     * Default: false
     */
    @PropertyInject(value = "client.undo.only-care-update-columns", transformer = BooleanTransformer.class, defaultValue = "false")
    private boolean undoOnlyCareUpdateColumns = false;

    // ======================== Registry ========================

    /**
     * Registry type: file, nacos, eureka, redis, zk, consul, etcd3, sofa.
     * Default: file
     */
    @PropertyInject(value = "registry.type", defaultValue = "file")
    private String registryType = "file";

    /**
     * Nacos registry server address.
     */
    @PropertyInject(value = "registry.nacos.server-addr", defaultValue = "")
    private String registryNacosServerAddr = "";

    /**
     * Nacos registry namespace.
     */
    @PropertyInject(value = "registry.nacos.namespace", defaultValue = "")
    private String registryNacosNamespace = "";

    /**
     * Nacos registry group.
     * Default: SEATA_GROUP
     */
    @PropertyInject(value = "registry.nacos.group", defaultValue = "SEATA_GROUP")
    private String registryNacosGroup = "SEATA_GROUP";

    /**
     * Nacos registry cluster.
     * Default: default
     */
    @PropertyInject(value = "registry.nacos.cluster", defaultValue = "default")
    private String registryNacosCluster = "default";

    // ======================== Config ========================

    /**
     * Config type: file, nacos, apollo, zk, consul, etcd3.
     * Default: file
     */
    @PropertyInject(value = "config.type", defaultValue = "file")
    private String configType = "file";

    /**
     * Nacos config server address.
     */
    @PropertyInject(value = "config.nacos.server-addr", defaultValue = "")
    private String configNacosServerAddr = "";

    /**
     * Nacos config namespace.
     */
    @PropertyInject(value = "config.nacos.namespace", defaultValue = "")
    private String configNacosNamespace = "";

    /**
     * Nacos config group.
     * Default: SEATA_GROUP
     */
    @PropertyInject(value = "config.nacos.group", defaultValue = "SEATA_GROUP")
    private String configNacosGroup = "SEATA_GROUP";

    // ======================== Load Balance ========================

    /**
     * Load balance type: RandomLoadBalance, RoundRobinLoadBalance, ConsistentHashLoadBalance.
     * Default: RandomLoadBalance
     */
    @PropertyInject(value = "load-balance.type", defaultValue = "RandomLoadBalance")
    private String loadBalanceType = "RandomLoadBalance";

    /**
     * Load balance virtual nodes.
     * Default: 10
     */
    @PropertyInject(value = "load-balance.virtual-nodes", transformer = IntegerTransformer.class, defaultValue = "10")
    private int loadBalanceVirtualNodes = 10;

    // ======================== Log ========================

    /**
     * Log exception rate (0-100).
     * Default: 100
     */
    @PropertyInject(value = "log.exception-rate", transformer = IntegerTransformer.class, defaultValue = "100")
    private int logExceptionRate = 100;

    // ======================== TCC Fence ========================

    /**
     * TCC fence log table name.
     * Default: tcc_fence_log
     */
    @PropertyInject(value = "tcc.fence.log-table-name", defaultValue = "tcc_fence_log")
    private String tccFenceLogTableName = "tcc_fence_log";

    /**
     * TCC fence clean period in milliseconds.
     * Default: 3600000 (1 hour)
     */
    @PropertyInject(value = "tcc.fence.clean-period", transformer = LongTransformer.class, defaultValue = "3600000")
    private long tccFenceCleanPeriod = 3600000L;

    // ======================== Getter/Setter ========================

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getTxServiceGroup() { return txServiceGroup; }
    public void setTxServiceGroup(String txServiceGroup) { this.txServiceGroup = txServiceGroup; }

    public String getServerAddr() { return serverAddr; }
    public void setServerAddr(String serverAddr) { this.serverAddr = serverAddr; }

    public boolean isEnableAutoDataSourceProxy() { return enableAutoDataSourceProxy; }
    public void setEnableAutoDataSourceProxy(boolean enableAutoDataSourceProxy) { this.enableAutoDataSourceProxy = enableAutoDataSourceProxy; }

    public String getDataSourceProxyMode() { return dataSourceProxyMode; }
    public void setDataSourceProxyMode(String dataSourceProxyMode) { this.dataSourceProxyMode = dataSourceProxyMode; }

    public boolean isUseJdkProxy() { return useJdkProxy; }
    public void setUseJdkProxy(boolean useJdkProxy) { this.useJdkProxy = useJdkProxy; }

    public boolean isDisableGlobalTransaction() { return disableGlobalTransaction; }
    public void setDisableGlobalTransaction(boolean disableGlobalTransaction) { this.disableGlobalTransaction = disableGlobalTransaction; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public String getTransportServer() { return transportServer; }
    public void setTransportServer(String transportServer) { this.transportServer = transportServer; }

    public String getTransportSerialization() { return transportSerialization; }
    public void setTransportSerialization(String transportSerialization) { this.transportSerialization = transportSerialization; }

    public String getTransportCompressor() { return transportCompressor; }
    public void setTransportCompressor(String transportCompressor) { this.transportCompressor = transportCompressor; }

    public boolean isTransportHeartbeat() { return transportHeartbeat; }
    public void setTransportHeartbeat(boolean transportHeartbeat) { this.transportHeartbeat = transportHeartbeat; }

    public int getTransportShutdownWait() { return transportShutdownWait; }
    public void setTransportShutdownWait(int transportShutdownWait) { this.transportShutdownWait = transportShutdownWait; }

    public long getTransportShutdownWaitPeriod() { return transportShutdownWaitPeriod; }
    public void setTransportShutdownWaitPeriod(long transportShutdownWaitPeriod) { this.transportShutdownWaitPeriod = transportShutdownWaitPeriod; }

    public int getRmAsyncCommitBufferLimit() { return rmAsyncCommitBufferLimit; }
    public void setRmAsyncCommitBufferLimit(int rmAsyncCommitBufferLimit) { this.rmAsyncCommitBufferLimit = rmAsyncCommitBufferLimit; }

    public int getRmReportRetryCount() { return rmReportRetryCount; }
    public void setRmReportRetryCount(int rmReportRetryCount) { this.rmReportRetryCount = rmReportRetryCount; }

    public boolean isRmTableMetaCheckEnable() { return rmTableMetaCheckEnable; }
    public void setRmTableMetaCheckEnable(boolean rmTableMetaCheckEnable) { this.rmTableMetaCheckEnable = rmTableMetaCheckEnable; }

    public long getRmTableMetaCheckerInterval() { return rmTableMetaCheckerInterval; }
    public void setRmTableMetaCheckerInterval(long rmTableMetaCheckerInterval) { this.rmTableMetaCheckerInterval = rmTableMetaCheckerInterval; }

    public String getRmSqlParserType() { return rmSqlParserType; }
    public void setRmSqlParserType(String rmSqlParserType) { this.rmSqlParserType = rmSqlParserType; }

    public int getRmLockRetryInterval() { return rmLockRetryInterval; }
    public void setRmLockRetryInterval(int rmLockRetryInterval) { this.rmLockRetryInterval = rmLockRetryInterval; }

    public int getRmLockRetryTimes() { return rmLockRetryTimes; }
    public void setRmLockRetryTimes(int rmLockRetryTimes) { this.rmLockRetryTimes = rmLockRetryTimes; }

    public boolean isRmLockRetryPolicyBranchRollbackOnConflict() { return rmLockRetryPolicyBranchRollbackOnConflict; }
    public void setRmLockRetryPolicyBranchRollbackOnConflict(boolean rmLockRetryPolicyBranchRollbackOnConflict) { this.rmLockRetryPolicyBranchRollbackOnConflict = rmLockRetryPolicyBranchRollbackOnConflict; }

    public int getTmCommitRetryCount() { return tmCommitRetryCount; }
    public void setTmCommitRetryCount(int tmCommitRetryCount) { this.tmCommitRetryCount = tmCommitRetryCount; }

    public int getTmRollbackRetryCount() { return tmRollbackRetryCount; }
    public void setTmRollbackRetryCount(int tmRollbackRetryCount) { this.tmRollbackRetryCount = tmRollbackRetryCount; }

    public int getTmDefaultGlobalTransactionTimeout() { return tmDefaultGlobalTransactionTimeout; }
    public void setTmDefaultGlobalTransactionTimeout(int tmDefaultGlobalTransactionTimeout) { this.tmDefaultGlobalTransactionTimeout = tmDefaultGlobalTransactionTimeout; }

    public boolean isUndoDataValidation() { return undoDataValidation; }
    public void setUndoDataValidation(boolean undoDataValidation) { this.undoDataValidation = undoDataValidation; }

    public String getUndoLogSerialization() { return undoLogSerialization; }
    public void setUndoLogSerialization(String undoLogSerialization) { this.undoLogSerialization = undoLogSerialization; }

    public boolean isUndoOnlyCareUpdateColumns() { return undoOnlyCareUpdateColumns; }
    public void setUndoOnlyCareUpdateColumns(boolean undoOnlyCareUpdateColumns) { this.undoOnlyCareUpdateColumns = undoOnlyCareUpdateColumns; }

    public String getRegistryType() { return registryType; }
    public void setRegistryType(String registryType) { this.registryType = registryType; }

    public String getRegistryNacosServerAddr() { return registryNacosServerAddr; }
    public void setRegistryNacosServerAddr(String registryNacosServerAddr) { this.registryNacosServerAddr = registryNacosServerAddr; }

    public String getRegistryNacosNamespace() { return registryNacosNamespace; }
    public void setRegistryNacosNamespace(String registryNacosNamespace) { this.registryNacosNamespace = registryNacosNamespace; }

    public String getRegistryNacosGroup() { return registryNacosGroup; }
    public void setRegistryNacosGroup(String registryNacosGroup) { this.registryNacosGroup = registryNacosGroup; }

    public String getRegistryNacosCluster() { return registryNacosCluster; }
    public void setRegistryNacosCluster(String registryNacosCluster) { this.registryNacosCluster = registryNacosCluster; }

    public String getConfigType() { return configType; }
    public void setConfigType(String configType) { this.configType = configType; }

    public String getConfigNacosServerAddr() { return configNacosServerAddr; }
    public void setConfigNacosServerAddr(String configNacosServerAddr) { this.configNacosServerAddr = configNacosServerAddr; }

    public String getConfigNacosNamespace() { return configNacosNamespace; }
    public void setConfigNacosNamespace(String configNacosNamespace) { this.configNacosNamespace = configNacosNamespace; }

    public String getConfigNacosGroup() { return configNacosGroup; }
    public void setConfigNacosGroup(String configNacosGroup) { this.configNacosGroup = configNacosGroup; }

    public String getLoadBalanceType() { return loadBalanceType; }
    public void setLoadBalanceType(String loadBalanceType) { this.loadBalanceType = loadBalanceType; }

    public int getLoadBalanceVirtualNodes() { return loadBalanceVirtualNodes; }
    public void setLoadBalanceVirtualNodes(int loadBalanceVirtualNodes) { this.loadBalanceVirtualNodes = loadBalanceVirtualNodes; }

    public int getLogExceptionRate() { return logExceptionRate; }
    public void setLogExceptionRate(int logExceptionRate) { this.logExceptionRate = logExceptionRate; }

    public String getTccFenceLogTableName() { return tccFenceLogTableName; }
    public void setTccFenceLogTableName(String tccFenceLogTableName) { this.tccFenceLogTableName = tccFenceLogTableName; }

    public long getTccFenceCleanPeriod() { return tccFenceCleanPeriod; }
    public void setTccFenceCleanPeriod(long tccFenceCleanPeriod) { this.tccFenceCleanPeriod = tccFenceCleanPeriod; }

    // ======================== Helpers ========================

    public boolean isFileRegistry() {
        return "file".equalsIgnoreCase(registryType);
    }

    public boolean isNacosRegistry() {
        return "nacos".equalsIgnoreCase(registryType);
    }

    public boolean isFileConfig() {
        return "file".equalsIgnoreCase(configType);
    }

    public boolean isNacosConfig() {
        return "nacos".equalsIgnoreCase(configType);
    }

    public boolean isAtMode() {
        return "AT".equalsIgnoreCase(dataSourceProxyMode);
    }

    public boolean isXaMode() {
        return "XA".equalsIgnoreCase(dataSourceProxyMode);
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
        var c = new SeataConfiguration();
        c.enable = this.enable;
        c.applicationId = this.applicationId;
        c.txServiceGroup = this.txServiceGroup;
        c.serverAddr = this.serverAddr;
        c.enableAutoDataSourceProxy = this.enableAutoDataSourceProxy;
        c.dataSourceProxyMode = this.dataSourceProxyMode;
        c.useJdkProxy = this.useJdkProxy;
        c.disableGlobalTransaction = this.disableGlobalTransaction;
        c.transportType = this.transportType;
        c.transportServer = this.transportServer;
        c.transportSerialization = this.transportSerialization;
        c.transportCompressor = this.transportCompressor;
        c.transportHeartbeat = this.transportHeartbeat;
        c.transportShutdownWait = this.transportShutdownWait;
        c.transportShutdownWaitPeriod = this.transportShutdownWaitPeriod;
        c.rmAsyncCommitBufferLimit = this.rmAsyncCommitBufferLimit;
        c.rmReportRetryCount = this.rmReportRetryCount;
        c.rmTableMetaCheckEnable = this.rmTableMetaCheckEnable;
        c.rmTableMetaCheckerInterval = this.rmTableMetaCheckerInterval;
        c.rmSqlParserType = this.rmSqlParserType;
        c.rmLockRetryInterval = this.rmLockRetryInterval;
        c.rmLockRetryTimes = this.rmLockRetryTimes;
        c.rmLockRetryPolicyBranchRollbackOnConflict = this.rmLockRetryPolicyBranchRollbackOnConflict;
        c.tmCommitRetryCount = this.tmCommitRetryCount;
        c.tmRollbackRetryCount = this.tmRollbackRetryCount;
        c.tmDefaultGlobalTransactionTimeout = this.tmDefaultGlobalTransactionTimeout;
        c.undoDataValidation = this.undoDataValidation;
        c.undoLogSerialization = this.undoLogSerialization;
        c.undoOnlyCareUpdateColumns = this.undoOnlyCareUpdateColumns;
        c.registryType = this.registryType;
        c.registryNacosServerAddr = this.registryNacosServerAddr;
        c.registryNacosNamespace = this.registryNacosNamespace;
        c.registryNacosGroup = this.registryNacosGroup;
        c.registryNacosCluster = this.registryNacosCluster;
        c.configType = this.configType;
        c.configNacosServerAddr = this.configNacosServerAddr;
        c.configNacosNamespace = this.configNacosNamespace;
        c.configNacosGroup = this.configNacosGroup;
        c.loadBalanceType = this.loadBalanceType;
        c.loadBalanceVirtualNodes = this.loadBalanceVirtualNodes;
        c.logExceptionRate = this.logExceptionRate;
        c.tccFenceLogTableName = this.tccFenceLogTableName;
        c.tccFenceCleanPeriod = this.tccFenceCleanPeriod;
        return (T) c;
    }

    @Override
    public void close() {}

    @Override
    public String toString() {
        return "SeataConfiguration{applicationId=" + applicationId
                + ", txServiceGroup=" + txServiceGroup
                + ", serverAddr=" + serverAddr
                + ", dataSourceProxyMode=" + dataSourceProxyMode
                + ", enableAutoDataSourceProxy=" + enableAutoDataSourceProxy
                + ", transportType=" + transportType
                + ", transportSerialization=" + transportSerialization
                + ", registryType=" + registryType
                + ", configType=" + configType
                + ", tmDefaultGlobalTransactionTimeout=" + tmDefaultGlobalTransactionTimeout
                + ", loadBalanceType=" + loadBalanceType + "}";
    }
}

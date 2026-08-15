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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.model.BranchType;
import io.seata.core.model.GlobalStatus;
import io.seata.rm.RMClient;
import io.seata.tm.TMClient;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;

import java.util.concurrent.Callable;

/**
 * Seata transaction factory. Handles Seata client initialization (TM/RM),
 * global transaction management, and XID propagation.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SeataTransactionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeataTransactionFactory.class);

    private final SeataConfiguration configuration;

    private boolean initialized = false;

    public SeataTransactionFactory(SeataConfiguration configuration) {
        this.configuration = configuration;
    }

    // ======================== Initialization ========================

    /**
     * Initialize Seata clients: set system properties, then init TMClient and RMClient.
     */
    public void init() {
        if (initialized) {
            LOGGER.warn("Seata transaction factory already initialized, skipping");
            return;
        }

        initSystemProperties();

        if (!configuration.isDisableGlobalTransaction()) {
            try {
                TMClient.init(configuration.getApplicationId(), configuration.getTxServiceGroup());
                LOGGER.info("Seata TMClient initialized: applicationId=" + configuration.getApplicationId()
                        + ", txServiceGroup=" + configuration.getTxServiceGroup());

                RMClient.init(configuration.getApplicationId(), configuration.getTxServiceGroup());
                LOGGER.info("Seata RMClient initialized: applicationId=" + configuration.getApplicationId()
                        + ", txServiceGroup=" + configuration.getTxServiceGroup());
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Seata clients", e);
            }
        } else {
            LOGGER.warn("Seata global transaction is disabled, skipping TM/RM client initialization");
        }

        initialized = true;
        LOGGER.info("Seata transaction factory initialized");
    }

    /**
     * Set Seata configuration via system properties.
     * Seata's FileConfiguration reads these before loading from file.
     */
    private void initSystemProperties() {
        var prefix = "seata.";

        System.setProperty(prefix + "application.id", configuration.getApplicationId());
        System.setProperty(prefix + "tx-service-group", configuration.getTxServiceGroup());

        if (configuration.isFileRegistry()) {
            var cluster = "default";
            System.setProperty(prefix + "service.vgroupMapping." + configuration.getTxServiceGroup(), cluster);
            System.setProperty(prefix + "service.grouplist." + cluster, configuration.getServerAddr());
        }

        System.setProperty(prefix + "transport.type", configuration.getTransportType());
        System.setProperty(prefix + "transport.server", configuration.getTransportServer());
        System.setProperty(prefix + "transport.serialization", configuration.getTransportSerialization());
        System.setProperty(prefix + "transport.compressor", configuration.getTransportCompressor());

        System.setProperty(prefix + "client.rm.asyncCommitBufferLimit",
                String.valueOf(configuration.getRmAsyncCommitBufferLimit()));
        System.setProperty(prefix + "client.rm.reportRetryCount",
                String.valueOf(configuration.getRmReportRetryCount()));
        System.setProperty(prefix + "client.rm.tableMetaCheckEnable",
                String.valueOf(configuration.isRmTableMetaCheckEnable()));
        System.setProperty(prefix + "client.rm.sqlParserType", configuration.getRmSqlParserType());
        System.setProperty(prefix + "client.rm.lock.retryInterval",
                String.valueOf(configuration.getRmLockRetryInterval()));
        System.setProperty(prefix + "client.rm.lock.retryTimes",
                String.valueOf(configuration.getRmLockRetryTimes()));
        System.setProperty(prefix + "client.rm.lock.retryPolicyBranchRollbackOnConflict",
                String.valueOf(configuration.isRmLockRetryPolicyBranchRollbackOnConflict()));

        System.setProperty(prefix + "client.tm.commitRetryCount",
                String.valueOf(configuration.getTmCommitRetryCount()));
        System.setProperty(prefix + "client.tm.rollbackRetryCount",
                String.valueOf(configuration.getTmRollbackRetryCount()));
        System.setProperty(prefix + "client.tm.defaultGlobalTransactionTimeout",
                String.valueOf(configuration.getTmDefaultGlobalTransactionTimeout()));

        System.setProperty(prefix + "client.undo.dataValidation",
                String.valueOf(configuration.isUndoDataValidation()));
        System.setProperty(prefix + "client.undo.logSerialization", configuration.getUndoLogSerialization());
        System.setProperty(prefix + "client.undo.onlyCareUpdateColumns",
                String.valueOf(configuration.isUndoOnlyCareUpdateColumns()));

        System.setProperty(prefix + "registry.type", configuration.getRegistryType());
        System.setProperty(prefix + "config.type", configuration.getConfigType());

        System.setProperty(prefix + "loadBalanceType", configuration.getLoadBalanceType());
        System.setProperty(prefix + "loadBalanceVirtualNodes",
                String.valueOf(configuration.getLoadBalanceVirtualNodes()));

        System.setProperty(prefix + "log.exceptionRate", String.valueOf(configuration.getLogExceptionRate()));

        if (configuration.isNacosRegistry()) {
            if (!configuration.getRegistryNacosServerAddr().isEmpty()) {
                System.setProperty(prefix + "registry.nacos.serverAddr", configuration.getRegistryNacosServerAddr());
            }
            if (!configuration.getRegistryNacosNamespace().isEmpty()) {
                System.setProperty(prefix + "registry.nacos.namespace", configuration.getRegistryNacosNamespace());
            }
            System.setProperty(prefix + "registry.nacos.group", configuration.getRegistryNacosGroup());
            System.setProperty(prefix + "registry.nacos.cluster", configuration.getRegistryNacosCluster());
        }

        if (configuration.isNacosConfig()) {
            if (!configuration.getConfigNacosServerAddr().isEmpty()) {
                System.setProperty(prefix + "config.nacos.serverAddr", configuration.getConfigNacosServerAddr());
            }
            if (!configuration.getConfigNacosNamespace().isEmpty()) {
                System.setProperty(prefix + "config.nacos.namespace", configuration.getConfigNacosNamespace());
            }
            System.setProperty(prefix + "config.nacos.group", configuration.getConfigNacosGroup());
        }

        LOGGER.info("Seata system properties configured: registryType=" + configuration.getRegistryType()
                + ", configType=" + configuration.getConfigType());
    }

    // ======================== XID Management ========================

    /**
     * Get the current XID (global transaction id).
     */
    public String getXid() {
        return RootContext.getXID();
    }

    /**
     * Bind a XID to current thread.
     */
    public void bind(String xid) {
        RootContext.bind(xid);
    }

    /**
     * Unbind the XID from current thread.
     */
    public String unbind() {
        return RootContext.unbind();
    }

    /**
     * Check if current thread is in a global transaction.
     */
    public boolean inGlobalTransaction() {
        return RootContext.inGlobalTransaction();
    }

    /**
     * Assert that current thread is NOT in a global transaction.
     */
    public void assertNotInGlobalTransaction() {
        RootContext.assertNotInGlobalTransaction();
    }

    /**
     * Get the current branch type.
     */
    public BranchType getBranchType() {
        return RootContext.getBranchType();
    }

    /**
     * Set the default branch type.
     */
    public void setDefaultBranchType(BranchType branchType) {
        RootContext.setDefaultBranchType(branchType);
    }

    // ======================== Global Transaction ========================

    /**
     * Create a new GlobalTransaction.
     */
    public GlobalTransaction createGlobalTransaction() {
        return GlobalTransactionContext.getCurrentOrCreate();
    }

    /**
     * Create a brand new GlobalTransaction.
     */
    public GlobalTransaction createNewGlobalTransaction() {
        return GlobalTransactionContext.createNew();
    }

    /**
     * Begin a global transaction with default timeout.
     *
     * @return the XID of the global transaction
     * @throws TransactionException if begin fails
     */
    public String beginGlobalTransaction() throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.begin();
        return tx.getXid();
    }

    /**
     * Begin a global transaction with specified timeout.
     *
     * @param timeout timeout in milliseconds
     * @return the XID of the global transaction
     * @throws TransactionException if begin fails
     */
    public String beginGlobalTransaction(int timeout) throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.begin(timeout);
        return tx.getXid();
    }

    /**
     * Begin a global transaction with specified timeout and name.
     *
     * @param timeout timeout in milliseconds
     * @param name    transaction name
     * @return the XID of the global transaction
     * @throws TransactionException if begin fails
     */
    public String beginGlobalTransaction(int timeout, String name) throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.begin(timeout, name);
        return tx.getXid();
    }

    /**
     * Commit the current global transaction.
     *
     * @throws TransactionException if commit fails
     */
    public void commitGlobalTransaction() throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.commit();
    }

    /**
     * Rollback the current global transaction.
     *
     * @throws TransactionException if rollback fails
     */
    public void rollbackGlobalTransaction() throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.rollback();
    }

    /**
     * Get the status of the current global transaction.
     *
     * @return the global status
     * @throws TransactionException if getting status fails
     */
    public GlobalStatus getGlobalTransactionStatus() throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        return tx.getStatus();
    }

    // ======================== Template Methods ========================

    /**
     * Execute a Runnable within a global transaction.
     * Begins a global transaction, runs the task, commits on success, rollbacks on failure.
     *
     * @param task the task to execute
     * @throws TransactionException if transaction operations fail
     */
    public void executeInGlobalTransaction(Runnable task) throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.begin();
        try {
            task.run();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            if (e instanceof TransactionException te) {
                throw te;
            }
            throw new RuntimeException("Global transaction failed", e);
        }
    }

    /**
     * Execute a Callable within a global transaction and return the result.
     * Begins a global transaction, runs the task, commits on success, rollbacks on failure.
     *
     * @param task the task to execute
     * @return the result of the task
     * @throws TransactionException if transaction operations fail
     */
    public <T> T executeInGlobalTransaction(Callable<T> task) throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.begin();
        try {
            var result = task.call();
            tx.commit();
            return result;
        } catch (Exception e) {
            tx.rollback();
            if (e instanceof TransactionException te) {
                throw te;
            }
            throw new RuntimeException("Global transaction failed", e);
        }
    }

    /**
     * Execute a Runnable within a global transaction with specified timeout.
     *
     * @param task    the task to execute
     * @param timeout timeout in milliseconds
     * @throws TransactionException if transaction operations fail
     */
    public void executeInGlobalTransaction(Runnable task, int timeout) throws TransactionException {
        var tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.begin(timeout);
        try {
            task.run();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            if (e instanceof TransactionException te) {
                throw te;
            }
            throw new RuntimeException("Global transaction failed", e);
        }
    }

    // ======================== Misc ========================

    public SeataConfiguration getConfiguration() {
        return configuration;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void close() {
        LOGGER.info("Seata transaction factory closed");
    }
}

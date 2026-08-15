/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.seata.check;

import com.truthbean.debbie.seata.SeataConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SeataConfigurationTest {

    @Test
    public void configurationDefaults() {
        var config = new SeataConfiguration();
        assertTrue(config.isEnable());
        assertEquals("debbie-app", config.getApplicationId());
        assertEquals("default", config.getTxServiceGroup());
        assertEquals("localhost:8091", config.getServerAddr());
        assertTrue(config.isEnableAutoDataSourceProxy());
        assertEquals("AT", config.getDataSourceProxyMode());
        assertFalse(config.isUseJdkProxy());
        assertFalse(config.isDisableGlobalTransaction());
        assertEquals("TCP", config.getTransportType());
        assertEquals("NIO", config.getTransportServer());
        assertEquals("seata", config.getTransportSerialization());
        assertEquals("none", config.getTransportCompressor());
        assertTrue(config.isTransportHeartbeat());
        assertEquals(3, config.getTransportShutdownWait());
        assertEquals(10000, config.getRmAsyncCommitBufferLimit());
        assertEquals(5, config.getRmReportRetryCount());
        assertFalse(config.isRmTableMetaCheckEnable());
        assertEquals(60000L, config.getRmTableMetaCheckerInterval());
        assertEquals("druid", config.getRmSqlParserType());
        assertEquals(10, config.getRmLockRetryInterval());
        assertEquals(30, config.getRmLockRetryTimes());
        assertTrue(config.isRmLockRetryPolicyBranchRollbackOnConflict());
        assertEquals(5, config.getTmCommitRetryCount());
        assertEquals(5, config.getTmRollbackRetryCount());
        assertEquals(60000, config.getTmDefaultGlobalTransactionTimeout());
        assertTrue(config.isUndoDataValidation());
        assertEquals("jackson", config.getUndoLogSerialization());
        assertFalse(config.isUndoOnlyCareUpdateColumns());
        assertEquals("file", config.getRegistryType());
        assertEquals("SEATA_GROUP", config.getRegistryNacosGroup());
        assertEquals("default", config.getRegistryNacosCluster());
        assertEquals("file", config.getConfigType());
        assertEquals("SEATA_GROUP", config.getConfigNacosGroup());
        assertEquals("RandomLoadBalance", config.getLoadBalanceType());
        assertEquals(10, config.getLoadBalanceVirtualNodes());
        assertEquals(100, config.getLogExceptionRate());
        assertEquals("tcc_fence_log", config.getTccFenceLogTableName());
        assertEquals(3600000L, config.getTccFenceCleanPeriod());
    }

    @Test
    public void configurationHelpers() {
        var config = new SeataConfiguration();
        assertTrue(config.isFileRegistry());
        assertFalse(config.isNacosRegistry());
        assertTrue(config.isFileConfig());
        assertFalse(config.isNacosConfig());
        assertTrue(config.isAtMode());
        assertFalse(config.isXaMode());

        config.setRegistryType("nacos");
        assertFalse(config.isFileRegistry());
        assertTrue(config.isNacosRegistry());

        config.setConfigType("nacos");
        assertFalse(config.isFileConfig());
        assertTrue(config.isNacosConfig());

        config.setDataSourceProxyMode("XA");
        assertFalse(config.isAtMode());
        assertTrue(config.isXaMode());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new SeataConfiguration();
        config.setApplicationId("order-service");
        config.setTxServiceGroup("order-tx-group");
        config.setServerAddr("seata-server:8091");
        config.setEnableAutoDataSourceProxy(false);
        config.setDataSourceProxyMode("XA");
        config.setUseJdkProxy(true);
        config.setTransportType("NATIVE");
        config.setTransportSerialization("protobuf");
        config.setRmAsyncCommitBufferLimit(20000);
        config.setRmReportRetryCount(10);
        config.setTmCommitRetryCount(3);
        config.setTmDefaultGlobalTransactionTimeout(120000);
        config.setUndoDataValidation(false);
        config.setUndoLogSerialization("fastjson");
        config.setRegistryType("nacos");
        config.setRegistryNacosServerAddr("nacos:8848");
        config.setRegistryNacosNamespace("seata-ns");
        config.setConfigType("nacos");
        config.setConfigNacosServerAddr("nacos:8848");
        config.setLoadBalanceType("RoundRobinLoadBalance");
        config.setLoadBalanceVirtualNodes(20);
        config.setLogExceptionRate(50);
        config.setTccFenceLogTableName("custom_fence_log");
        config.setTccFenceCleanPeriod(7200000L);

        var copy = config.<SeataConfiguration>copy();
        assertEquals("order-service", copy.getApplicationId());
        assertEquals("order-tx-group", copy.getTxServiceGroup());
        assertEquals("seata-server:8091", copy.getServerAddr());
        assertFalse(copy.isEnableAutoDataSourceProxy());
        assertEquals("XA", copy.getDataSourceProxyMode());
        assertTrue(copy.isUseJdkProxy());
        assertEquals("NATIVE", copy.getTransportType());
        assertEquals("protobuf", copy.getTransportSerialization());
        assertEquals(20000, copy.getRmAsyncCommitBufferLimit());
        assertEquals(10, copy.getRmReportRetryCount());
        assertEquals(3, copy.getTmCommitRetryCount());
        assertEquals(120000, copy.getTmDefaultGlobalTransactionTimeout());
        assertFalse(copy.isUndoDataValidation());
        assertEquals("fastjson", copy.getUndoLogSerialization());
        assertEquals("nacos", copy.getRegistryType());
        assertEquals("nacos:8848", copy.getRegistryNacosServerAddr());
        assertEquals("seata-ns", copy.getRegistryNacosNamespace());
        assertEquals("nacos", copy.getConfigType());
        assertEquals("nacos:8848", copy.getConfigNacosServerAddr());
        assertEquals("RoundRobinLoadBalance", copy.getLoadBalanceType());
        assertEquals(20, copy.getLoadBalanceVirtualNodes());
        assertEquals(50, copy.getLogExceptionRate());
        assertEquals("custom_fence_log", copy.getTccFenceLogTableName());
        assertEquals(7200000L, copy.getTccFenceCleanPeriod());
    }

    @Test
    public void configurationCopyShouldNotAffectOriginal() {
        var config = new SeataConfiguration();
        config.setServerAddr("original:8091");
        var copy = config.<SeataConfiguration>copy();
        copy.setServerAddr("copy:8092");
        assertEquals("original:8091", config.getServerAddr());
        assertEquals("copy:8092", copy.getServerAddr());
    }

    @Test
    public void configurationToStringShouldContainInfo() {
        var config = new SeataConfiguration();
        config.setApplicationId("my-app");
        config.setTxServiceGroup("my-tx-group");
        var str = config.toString();
        assertTrue(str.contains("my-app"));
        assertTrue(str.contains("my-tx-group"));
        assertTrue(str.startsWith("SeataConfiguration{"));
    }

    @Test
    public void configurationProfileAndCategoryShouldBeDefault() {
        var config = new SeataConfiguration();
        assertNotNull(config.getProfile());
        assertNotNull(config.getCategory());
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new SeataConfiguration();
        config.setEnable(false);
        config.setApplicationId("prod-app");
        config.setTxServiceGroup("prod-tx-group");
        config.setServerAddr("seata-prod:8091");
        config.setEnableAutoDataSourceProxy(false);
        config.setDataSourceProxyMode("XA");
        config.setUseJdkProxy(true);
        config.setDisableGlobalTransaction(true);
        config.setTransportType("NATIVE");
        config.setTransportServer("NATIVE");
        config.setTransportSerialization("protobuf");
        config.setTransportCompressor("gzip");
        config.setTransportHeartbeat(false);
        config.setTransportShutdownWait(5);
        config.setTransportShutdownWaitPeriod(2000);
        config.setRmAsyncCommitBufferLimit(50000);
        config.setRmReportRetryCount(10);
        config.setRmTableMetaCheckEnable(true);
        config.setRmTableMetaCheckerInterval(30000);
        config.setRmSqlParserType("antlr");
        config.setRmLockRetryInterval(20);
        config.setRmLockRetryTimes(50);
        config.setRmLockRetryPolicyBranchRollbackOnConflict(false);
        config.setTmCommitRetryCount(3);
        config.setTmRollbackRetryCount(3);
        config.setTmDefaultGlobalTransactionTimeout(120000);
        config.setUndoDataValidation(false);
        config.setUndoLogSerialization("fastjson");
        config.setUndoOnlyCareUpdateColumns(true);
        config.setRegistryType("eureka");
        config.setRegistryNacosServerAddr("eureka:8761");
        config.setRegistryNacosNamespace("ns1");
        config.setRegistryNacosGroup("MY_GROUP");
        config.setRegistryNacosCluster("my-cluster");
        config.setConfigType("apollo");
        config.setConfigNacosServerAddr("apollo:8070");
        config.setConfigNacosNamespace("apollo-ns");
        config.setConfigNacosGroup("APOLLO_GROUP");
        config.setLoadBalanceType("ConsistentHashLoadBalance");
        config.setLoadBalanceVirtualNodes(100);
        config.setLogExceptionRate(30);
        config.setTccFenceLogTableName("my_fence_log");
        config.setTccFenceCleanPeriod(1800000);

        assertFalse(config.isEnable());
        assertEquals("prod-app", config.getApplicationId());
        assertEquals("prod-tx-group", config.getTxServiceGroup());
        assertEquals("seata-prod:8091", config.getServerAddr());
        assertFalse(config.isEnableAutoDataSourceProxy());
        assertEquals("XA", config.getDataSourceProxyMode());
        assertTrue(config.isUseJdkProxy());
        assertTrue(config.isDisableGlobalTransaction());
        assertEquals("NATIVE", config.getTransportType());
        assertEquals("NATIVE", config.getTransportServer());
        assertEquals("protobuf", config.getTransportSerialization());
        assertEquals("gzip", config.getTransportCompressor());
        assertFalse(config.isTransportHeartbeat());
        assertEquals(5, config.getTransportShutdownWait());
        assertEquals(2000L, config.getTransportShutdownWaitPeriod());
        assertEquals(50000, config.getRmAsyncCommitBufferLimit());
        assertEquals(10, config.getRmReportRetryCount());
        assertTrue(config.isRmTableMetaCheckEnable());
        assertEquals(30000L, config.getRmTableMetaCheckerInterval());
        assertEquals("antlr", config.getRmSqlParserType());
        assertEquals(20, config.getRmLockRetryInterval());
        assertEquals(50, config.getRmLockRetryTimes());
        assertFalse(config.isRmLockRetryPolicyBranchRollbackOnConflict());
        assertEquals(3, config.getTmCommitRetryCount());
        assertEquals(3, config.getTmRollbackRetryCount());
        assertEquals(120000, config.getTmDefaultGlobalTransactionTimeout());
        assertFalse(config.isUndoDataValidation());
        assertEquals("fastjson", config.getUndoLogSerialization());
        assertTrue(config.isUndoOnlyCareUpdateColumns());
        assertEquals("eureka", config.getRegistryType());
        assertEquals("eureka:8761", config.getRegistryNacosServerAddr());
        assertEquals("ns1", config.getRegistryNacosNamespace());
        assertEquals("MY_GROUP", config.getRegistryNacosGroup());
        assertEquals("my-cluster", config.getRegistryNacosCluster());
        assertEquals("apollo", config.getConfigType());
        assertEquals("apollo:8070", config.getConfigNacosServerAddr());
        assertEquals("apollo-ns", config.getConfigNacosNamespace());
        assertEquals("APOLLO_GROUP", config.getConfigNacosGroup());
        assertEquals("ConsistentHashLoadBalance", config.getLoadBalanceType());
        assertEquals(100, config.getLoadBalanceVirtualNodes());
        assertEquals(30, config.getLogExceptionRate());
        assertEquals("my_fence_log", config.getTccFenceLogTableName());
        assertEquals(1800000L, config.getTccFenceCleanPeriod());
    }
}

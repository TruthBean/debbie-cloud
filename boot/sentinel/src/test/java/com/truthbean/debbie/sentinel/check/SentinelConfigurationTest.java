/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.sentinel.check;

import com.truthbean.debbie.sentinel.SentinelConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class SentinelConfigurationTest {

    @Test
    public void configurationDefaults() {
        var config = new SentinelConfiguration();
        assertTrue(config.isEnable());
        assertFalse(config.isEager());
        assertEquals("", config.getAppName());
        assertEquals("", config.getLogDir());
        assertEquals("sentinel-record", config.getLogNamePrefix());
        assertFalse(config.isLogUsePid());
        assertEquals("UTF-8", config.getCharset());
        assertEquals("", config.getDashboard());
        assertEquals(8719, config.getTransportPort());
        assertEquals(10000L, config.getHeartbeatIntervalMs());
        assertEquals("", config.getClientIp());
        assertEquals(52428800L, config.getMetricFileSize());
        assertEquals(6, config.getMetricFileCount());
        assertEquals(1L, config.getMetricFlushInterval());
        assertEquals(4900, config.getStatisticMaxRt());
        assertEquals(3, config.getColdFactor());
        assertEquals(-1.0, config.getSystemLoad());
        assertEquals(-1.0, config.getSystemCpuUsage());
        assertEquals(-1L, config.getSystemAvgRt());
        assertEquals(-1L, config.getSystemMaxThread());
        assertEquals(-1.0, config.getSystemQps());
        assertEquals("", config.getBlockPage());
        assertTrue(config.isFilterEnabled());
        assertEquals("/*", config.getFilterUrlPatterns());
        assertEquals(-2147483648, config.getFilterOrder());
    }

    @Test
    public void configurationHelpers() {
        var config = new SentinelConfiguration();
        assertFalse(config.hasAppName());
        assertFalse(config.hasDashboard());
        assertFalse(config.hasClientIp());
        assertFalse(config.hasLogDir());
        assertFalse(config.hasBlockPage());
        assertFalse(config.hasSystemRuleConfig());

        config.setAppName("my-app");
        assertTrue(config.hasAppName());

        config.setDashboard("localhost:8080");
        assertTrue(config.hasDashboard());

        config.setClientIp("10.0.0.1");
        assertTrue(config.hasClientIp());

        config.setLogDir("/var/log/sentinel");
        assertTrue(config.hasLogDir());

        config.setBlockPage("/blocked");
        assertTrue(config.hasBlockPage());

        config.setSystemLoad(2.0);
        assertTrue(config.hasSystemRuleConfig());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new SentinelConfiguration();
        config.setAppName("test-app");
        config.setEager(true);
        config.setDashboard("localhost:8080");
        config.setTransportPort(9000);
        config.setHeartbeatIntervalMs(5000);
        config.setMetricFileSize(10485760);
        config.setMetricFileCount(10);
        config.setStatisticMaxRt(10000);
        config.setColdFactor(5);
        config.setSystemLoad(3.0);
        config.setSystemCpuUsage(0.8);
        config.setSystemAvgRt(100);
        config.setSystemMaxThread(500);
        config.setSystemQps(1000);
        config.setBlockPage("/error");
        config.setFilterEnabled(false);
        config.setFilterUrlPatterns("/api/*");
        config.setFilterOrder(100);

        var copy = config.<SentinelConfiguration>copy();
        assertEquals("test-app", copy.getAppName());
        assertTrue(copy.isEager());
        assertEquals("localhost:8080", copy.getDashboard());
        assertEquals(9000, copy.getTransportPort());
        assertEquals(5000L, copy.getHeartbeatIntervalMs());
        assertEquals(10485760L, copy.getMetricFileSize());
        assertEquals(10, copy.getMetricFileCount());
        assertEquals(10000, copy.getStatisticMaxRt());
        assertEquals(5, copy.getColdFactor());
        assertEquals(3.0, copy.getSystemLoad());
        assertEquals(0.8, copy.getSystemCpuUsage());
        assertEquals(100L, copy.getSystemAvgRt());
        assertEquals(500L, copy.getSystemMaxThread());
        assertEquals(1000.0, copy.getSystemQps());
        assertEquals("/error", copy.getBlockPage());
        assertFalse(copy.isFilterEnabled());
        assertEquals("/api/*", copy.getFilterUrlPatterns());
        assertEquals(100, copy.getFilterOrder());
    }

    @Test
    public void configurationCopyShouldNotAffectOriginal() {
        var config = new SentinelConfiguration();
        config.setDashboard("localhost:8080");
        var copy = config.<SentinelConfiguration>copy();
        copy.setDashboard("remote:9090");
        assertEquals("localhost:8080", config.getDashboard());
        assertEquals("remote:9090", copy.getDashboard());
    }

    @Test
    public void configurationToStringShouldContainInfo() {
        var config = new SentinelConfiguration();
        config.setAppName("my-app");
        config.setDashboard("localhost:8080");
        var str = config.toString();
        assertTrue(str.contains("my-app"));
        assertTrue(str.contains("localhost:8080"));
        assertTrue(str.startsWith("SentinelConfiguration{"));
    }

    @Test
    public void configurationProfileAndCategoryShouldBeDefault() {
        var config = new SentinelConfiguration();
        assertNotNull(config.getProfile());
        assertNotNull(config.getCategory());
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new SentinelConfiguration();
        config.setEnable(false);
        config.setAppName("prod-app");
        config.setEager(true);
        config.setLogDir("/tmp/sentinel");
        config.setLogNamePrefix("my-sentinel");
        config.setLogUsePid(true);
        config.setCharset("GBK");
        config.setDashboard("sentinel-dashboard:8080");
        config.setTransportPort(8720);
        config.setHeartbeatIntervalMs(30000);
        config.setClientIp("192.168.1.100");
        config.setMetricFileSize(20971520);
        config.setMetricFileCount(12);
        config.setMetricFlushInterval(5);
        config.setStatisticMaxRt(10000);
        config.setColdFactor(4);
        config.setSystemLoad(5.0);
        config.setSystemCpuUsage(0.9);
        config.setSystemAvgRt(200);
        config.setSystemMaxThread(1000);
        config.setSystemQps(5000);
        config.setBlockPage("/sentinel-block");
        config.setFilterEnabled(false);
        config.setFilterUrlPatterns("/api/v1/*,/api/v2/*");
        config.setFilterOrder(Integer.MAX_VALUE);

        assertFalse(config.isEnable());
        assertEquals("prod-app", config.getAppName());
        assertTrue(config.isEager());
        assertEquals("/tmp/sentinel", config.getLogDir());
        assertEquals("my-sentinel", config.getLogNamePrefix());
        assertTrue(config.isLogUsePid());
        assertEquals("GBK", config.getCharset());
        assertEquals("sentinel-dashboard:8080", config.getDashboard());
        assertEquals(8720, config.getTransportPort());
        assertEquals(30000L, config.getHeartbeatIntervalMs());
        assertEquals("192.168.1.100", config.getClientIp());
        assertEquals(20971520L, config.getMetricFileSize());
        assertEquals(12, config.getMetricFileCount());
        assertEquals(5L, config.getMetricFlushInterval());
        assertEquals(10000, config.getStatisticMaxRt());
        assertEquals(4, config.getColdFactor());
        assertEquals(5.0, config.getSystemLoad());
        assertEquals(0.9, config.getSystemCpuUsage());
        assertEquals(200L, config.getSystemAvgRt());
        assertEquals(1000L, config.getSystemMaxThread());
        assertEquals(5000.0, config.getSystemQps());
        assertEquals("/sentinel-block", config.getBlockPage());
        assertFalse(config.isFilterEnabled());
        assertEquals("/api/v1/*,/api/v2/*", config.getFilterUrlPatterns());
        assertEquals(Integer.MAX_VALUE, config.getFilterOrder());
    }
}

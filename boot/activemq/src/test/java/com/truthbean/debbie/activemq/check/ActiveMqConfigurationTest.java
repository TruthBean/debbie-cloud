/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.activemq.check;

import com.truthbean.debbie.activemq.ActiveMqConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ActiveMqConfigurationTest {

    @Test
    public void configurationDefaults() {
        var config = new ActiveMqConfiguration();
        assertTrue(config.isEnable());
        assertEquals("tcp://localhost:61616", config.getBrokerUrl());
        assertEquals(1, config.getMaxConnections());
        assertTrue(config.isUseAsyncSend());
        assertFalse(config.isAlwaysSyncSend());
        assertEquals(15000, config.getCloseTimeout());
        assertEquals(0, config.getProducerWindowSize());
        assertTrue(config.isDispatchAsync());
        assertEquals(6, config.getRedeliveryMaxRedeliveries());
        assertEquals(1000, config.getRedeliveryInitialDelay());
        assertEquals(2.0, config.getRedeliveryBackOffMultiplier());
        assertTrue(config.isRedeliveryUseExponentialBackOff());
    }

    @Test
    public void configurationShouldDetectCredentials() {
        var config = new ActiveMqConfiguration();
        assertFalse(config.hasCredentials());
        config.setUsername("user");
        config.setPassword("pass");
        assertTrue(config.hasCredentials());
    }

    @Test
    public void configurationShouldNotDetectCredentialsWithOnlyUsername() {
        var config = new ActiveMqConfiguration();
        config.setUsername("user");
        assertFalse(config.hasCredentials());
    }

    @Test
    public void configurationShouldNotDetectCredentialsWithBlankUsername() {
        var config = new ActiveMqConfiguration();
        config.setUsername("  ");
        config.setPassword("pass");
        assertFalse(config.hasCredentials());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new ActiveMqConfiguration();
        config.setBrokerUrl("tcp://broker:61617");
        config.setUsername("admin");
        config.setPassword("secret");
        config.setMaxConnections(10);
        config.setUseAsyncSend(false);
        config.setAlwaysSyncSend(true);
        config.setCloseTimeout(30000);
        config.setProducerWindowSize(1024);
        config.setDispatchAsync(false);
        config.setRedeliveryMaxRedeliveries(3);
        config.setRedeliveryInitialDelay(500);
        config.setRedeliveryBackOffMultiplier(1.5);
        config.setRedeliveryUseExponentialBackOff(false);

        var copy = config.<ActiveMqConfiguration>copy();
        assertEquals("tcp://broker:61617", copy.getBrokerUrl());
        assertEquals("admin", copy.getUsername());
        assertEquals("secret", copy.getPassword());
        assertEquals(10, copy.getMaxConnections());
        assertFalse(copy.isUseAsyncSend());
        assertTrue(copy.isAlwaysSyncSend());
        assertEquals(30000, copy.getCloseTimeout());
        assertEquals(1024, copy.getProducerWindowSize());
        assertFalse(copy.isDispatchAsync());
        assertEquals(3, copy.getRedeliveryMaxRedeliveries());
        assertEquals(500, copy.getRedeliveryInitialDelay());
        assertEquals(1.5, copy.getRedeliveryBackOffMultiplier());
        assertFalse(copy.isRedeliveryUseExponentialBackOff());
    }

    @Test
    public void configurationCopyShouldNotAffectOriginal() {
        var config = new ActiveMqConfiguration();
        config.setBrokerUrl("tcp://original:61616");
        var copy = config.<ActiveMqConfiguration>copy();
        copy.setBrokerUrl("tcp://copy:61617");
        assertEquals("tcp://original:61616", config.getBrokerUrl());
        assertEquals("tcp://copy:61617", copy.getBrokerUrl());
    }

    @Test
    public void configurationToStringShouldContainInfo() {
        var config = new ActiveMqConfiguration();
        config.setBrokerUrl("tcp://broker:61617");
        config.setMaxConnections(5);
        var str = config.toString();
        assertTrue(str.contains("tcp://broker:61617"));
        assertTrue(str.contains("5"));
        assertTrue(str.startsWith("ActiveMqConfiguration{"));
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new ActiveMqConfiguration();
        config.setEnable(false);
        config.setBrokerUrl("failover:(tcp://host1:61616,tcp://host2:61616)");
        config.setUsername("admin");
        config.setPassword("password");
        config.setMaxConnections(20);
        config.setUseAsyncSend(false);
        config.setAlwaysSyncSend(true);
        config.setCloseTimeout(60000);
        config.setProducerWindowSize(4096);
        config.setDispatchAsync(false);
        config.setRedeliveryMaxRedeliveries(0);
        config.setRedeliveryInitialDelay(2000);
        config.setRedeliveryBackOffMultiplier(3.0);
        config.setRedeliveryUseExponentialBackOff(false);

        assertFalse(config.isEnable());
        assertEquals("failover:(tcp://host1:61616,tcp://host2:61616)", config.getBrokerUrl());
        assertEquals("admin", config.getUsername());
        assertEquals("password", config.getPassword());
        assertEquals(20, config.getMaxConnections());
        assertFalse(config.isUseAsyncSend());
        assertTrue(config.isAlwaysSyncSend());
        assertEquals(60000, config.getCloseTimeout());
        assertEquals(4096, config.getProducerWindowSize());
        assertFalse(config.isDispatchAsync());
        assertEquals(0, config.getRedeliveryMaxRedeliveries());
        assertEquals(2000, config.getRedeliveryInitialDelay());
        assertEquals(3.0, config.getRedeliveryBackOffMultiplier());
        assertFalse(config.isRedeliveryUseExponentialBackOff());
    }

    @Test
    public void configurationProfileAndCategoryShouldBeDefault() {
        var config = new ActiveMqConfiguration();
        assertNotNull(config.getProfile());
        assertNotNull(config.getCategory());
    }
}

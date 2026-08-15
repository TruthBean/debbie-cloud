/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.eureka.test;

import com.truthbean.debbie.eureka.EurekaConfiguration;
import com.truthbean.debbie.eureka.EurekaException;
import com.truthbean.debbie.eureka.EurekaJson;
import com.truthbean.debbie.eureka.client.EurekaClient;
import com.truthbean.debbie.eureka.client.EurekaHeartbeatScheduler;
import com.truthbean.debbie.eureka.model.ApplicationInfo;
import com.truthbean.debbie.eureka.model.InstanceInfo;
import com.truthbean.debbie.eureka.server.EurekaServerRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EurekaTest {

    // ---- EurekaJson tests ----

    @Test
    public void jsonShouldParseObject() {
        var result = EurekaJson.parseObject("{\"name\":\"test\",\"port\":8080}");
        assertEquals("test", result.get("name"));
        assertEquals(8080, ((Number) result.get("port")).intValue());
    }

    @Test
    public void jsonShouldParseNested() {
        var json = "{\"instance\":{\"appName\":\"ORDER\",\"port\":8080}}";
        var result = EurekaJson.parseObject(json);
        var instance = (Map<String, Object>) result.get("instance");
        assertEquals("ORDER", instance.get("appName"));
    }

    @Test
    public void jsonShouldParseEmpty() {
        assertTrue(EurekaJson.parseObject("{}").isEmpty());
    }

    @Test
    public void jsonShouldHandleNull() {
        assertEquals("null", EurekaJson.toJson(null));
    }

    @Test
    public void jsonShouldSerializeString() {
        assertEquals("\"hello\"", EurekaJson.toJson("hello"));
    }

    @Test
    public void jsonShouldSerializeNumber() {
        assertEquals("42", EurekaJson.toJson(42));
    }

    @Test
    public void jsonShouldSerializeBoolean() {
        assertEquals("true", EurekaJson.toJson(true));
    }

    @Test
    public void jsonShouldSerializeMap() {
        var json = EurekaJson.toJson(Map.of("a", "1", "b", "2"));
        var parsed = EurekaJson.parseObject(json);
        assertEquals("1", parsed.get("a"));
        assertEquals("2", parsed.get("b"));
    }

    @Test
    public void jsonShouldSerializeList() {
        var json = EurekaJson.toJson(List.of(1, 2, 3));
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    public void jsonShouldQuoteSpecialChars() {
        var quoted = EurekaJson.quote("a\"b\\c\n");
        assertTrue(quoted.contains("\\\""));
        assertTrue(quoted.contains("\\\\"));
        assertTrue(quoted.contains("\\n"));
    }

    // ---- InstanceInfo tests ----

    @Test
    public void instanceInfoShouldDefaultToUp() {
        var info = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        assertTrue(info.isUp());
        assertEquals(InstanceInfo.UP, info.getStatus());
    }

    @Test
    public void instanceInfoShouldRenew() throws InterruptedException {
        var info = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        var original = info.getLastHeartbeatTime();
        Thread.sleep(10);
        info.renew();
        assertTrue(info.getLastHeartbeatTime() > original);
    }

    @Test
    public void instanceInfoToMapShouldContainAllFields() {
        var info = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        info.setIpAddr("127.0.0.1");
        info.setSecurePort(8443);
        var map = info.toMap();
        assertEquals("ORDER", map.get("appName"));
        assertEquals("order-1", map.get("instanceId"));
        assertEquals("localhost", map.get("hostName"));
        assertEquals("127.0.0.1", map.get("ipAddr"));
        assertEquals(8080, map.get("port"));
        assertEquals(8443, map.get("securePort"));
        assertEquals("UP", map.get("status"));
    }

    @Test
    public void instanceInfoFromMapShouldRestoreFields() {
        var info = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        info.setIpAddr("127.0.0.1");
        var restored = InstanceInfo.fromMap(info.toMap());
        assertEquals("ORDER", restored.getAppName());
        assertEquals("order-1", restored.getInstanceId());
        assertEquals("localhost", restored.getHostName());
        assertEquals("127.0.0.1", restored.getIpAddr());
        assertEquals(8080, restored.getPort());
    }

    @Test
    public void instanceInfoToStringShouldContainInfo() {
        var info = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        var str = info.toString();
        assertTrue(str.contains("ORDER"));
        assertTrue(str.contains("order-1"));
        assertTrue(str.contains("8080"));
    }

    // ---- ApplicationInfo tests ----

    @Test
    public void applicationInfoShouldManageInstances() {
        var app = new ApplicationInfo("ORDER");
        app.addInstance(new InstanceInfo("ORDER", "order-1", "host1", 8080));
        app.addInstance(new InstanceInfo("ORDER", "order-2", "host2", 8081));
        assertEquals(2, app.getInstances().size());
        assertNotNull(app.getInstance("order-1"));
        assertNotNull(app.getInstance("order-2"));
    }

    @Test
    public void applicationInfoShouldRemoveInstance() {
        var app = new ApplicationInfo("ORDER");
        app.addInstance(new InstanceInfo("ORDER", "order-1", "host1", 8080));
        assertTrue(app.removeInstance("order-1"));
        assertEquals(0, app.getInstances().size());
        assertFalse(app.removeInstance("order-1"));
    }

    @Test
    public void applicationInfoShouldGetUpInstances() {
        var app = new ApplicationInfo("ORDER");
        var up = new InstanceInfo("ORDER", "order-1", "host1", 8080);
        var down = new InstanceInfo("ORDER", "order-2", "host2", 8081);
        down.setStatus(InstanceInfo.DOWN);
        app.addInstance(up);
        app.addInstance(down);
        var upInstances = app.getUpInstances();
        assertEquals(1, upInstances.size());
        assertEquals("order-1", upInstances.get(0).getInstanceId());
    }

    @Test
    public void applicationInfoToMapShouldContainNameAndInstances() {
        var app = new ApplicationInfo("ORDER");
        app.addInstance(new InstanceInfo("ORDER", "order-1", "host1", 8080));
        var map = app.toMap();
        assertEquals("ORDER", map.get("name"));
        var instances = (List<?>) map.get("instances");
        assertEquals(1, instances.size());
    }

    // ---- EurekaServerRegistry tests ----

    @Test
    public void registryShouldRegisterInstance() {
        var registry = new EurekaServerRegistry(90000, 60000);
        var instance = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        registry.register(instance);
        assertEquals(1, registry.getApplicationCount());
        assertEquals(1, registry.getInstanceCount());
        assertNotNull(registry.getApplication("ORDER"));
        registry.shutdown();
    }

    @Test
    public void registryShouldRenewInstance() {
        var registry = new EurekaServerRegistry(90000, 60000);
        registry.register(new InstanceInfo("ORDER", "order-1", "localhost", 8080));
        assertTrue(registry.renew("ORDER", "order-1"));
        assertFalse(registry.renew("ORDER", "nonexistent"));
        assertFalse(registry.renew("NONEXISTENT", "order-1"));
        registry.shutdown();
    }

    @Test
    public void registryShouldCancelInstance() {
        var registry = new EurekaServerRegistry(90000, 60000);
        registry.register(new InstanceInfo("ORDER", "order-1", "localhost", 8080));
        assertTrue(registry.cancel("ORDER", "order-1"));
        assertEquals(0, registry.getInstanceCount());
        registry.shutdown();
    }

    @Test
    public void registryShouldUpdateStatus() {
        var registry = new EurekaServerRegistry(90000, 60000);
        registry.register(new InstanceInfo("ORDER", "order-1", "localhost", 8080));
        assertTrue(registry.updateStatus("ORDER", "order-1", InstanceInfo.DOWN));
        var app = registry.getApplication("ORDER");
        assertEquals(InstanceInfo.DOWN, app.getInstance("order-1").getStatus());
        registry.shutdown();
    }

    @Test
    public void registryShouldGetUpInstances() {
        var registry = new EurekaServerRegistry(90000, 60000);
        var up = new InstanceInfo("ORDER", "order-1", "host1", 8080);
        var down = new InstanceInfo("ORDER", "order-2", "host2", 8081);
        down.setStatus(InstanceInfo.DOWN);
        registry.register(up);
        registry.register(down);
        var upInstances = registry.getUpInstances("ORDER");
        assertEquals(1, upInstances.size());
        registry.shutdown();
    }

    @Test
    public void registryShouldEvictExpiredInstances() {
        var registry = new EurekaServerRegistry(1, 100);
        registry.register(new InstanceInfo("ORDER", "order-1", "localhost", 8080));
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        registry.evict();
        assertEquals(0, registry.getInstanceCount());
        registry.shutdown();
    }

    @Test
    public void registryShouldHandleMultipleApps() {
        var registry = new EurekaServerRegistry(90000, 60000);
        registry.register(new InstanceInfo("ORDER", "order-1", "localhost", 8080));
        registry.register(new InstanceInfo("PAYMENT", "payment-1", "localhost", 8081));
        assertEquals(2, registry.getApplicationCount());
        assertEquals(2, registry.getInstanceCount());
        var allApps = registry.getAllApplications();
        assertEquals(2, allApps.size());
        registry.shutdown();
    }

    @Test
    public void registryShouldReplaceExistingInstance() {
        var registry = new EurekaServerRegistry(90000, 60000);
        registry.register(new InstanceInfo("ORDER", "order-1", "host1", 8080));
        registry.register(new InstanceInfo("ORDER", "order-1", "host2", 8081));
        var app = registry.getApplication("ORDER");
        assertEquals(1, app.getInstances().size());
        assertEquals("host2", app.getInstance("order-1").getHostName());
        registry.shutdown();
    }

    // ---- EurekaConfiguration tests ----

    @Test
    public void configurationDefaults() {
        var config = new EurekaConfiguration();
        assertTrue(config.isEnable());
        assertFalse(config.isServerEnable());
        assertTrue(config.isClientEnable());
        assertEquals("/eureka", config.getServerPrefix());
        assertEquals("http://localhost:8761", config.getClientServerUrl());
        assertEquals(8080, config.getClientPort());
        assertEquals(30000L, config.getClientHeartbeatInterval());
        assertTrue(config.isClientAutoRegister());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new EurekaConfiguration();
        config.setServerEnable(true);
        config.setClientServiceName("my-service");
        config.setClientPort(9090);

        var copy = config.<EurekaConfiguration>copy();
        assertTrue(copy.isServerEnable());
        assertEquals("my-service", copy.getClientServiceName());
        assertEquals(9090, copy.getClientPort());
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new EurekaConfiguration();
        config.setServerEnable(true);
        config.setServerPrefix("/custom");
        config.setServerEvictionTimeout(120000);
        config.setServerEvictionInterval(30000);
        config.setClientEnable(false);
        config.setClientServerUrl("http://eureka:8761");
        config.setClientPrefix("/custom");
        config.setClientServiceName("my-app");
        config.setClientInstanceId("my-app-1");
        config.setClientHost("my-host");
        config.setClientPort(9090);
        config.setClientSecurePort(9443);
        config.setClientHeartbeatInterval(15000);
        config.setClientConnectTimeout(3000);
        config.setClientReadTimeout(5000);
        config.setClientAutoRegister(false);

        assertTrue(config.isServerEnable());
        assertEquals("/custom", config.getServerPrefix());
        assertEquals(120000, config.getServerEvictionTimeout());
        assertEquals(30000, config.getServerEvictionInterval());
        assertFalse(config.isClientEnable());
        assertEquals("http://eureka:8761", config.getClientServerUrl());
        assertEquals("my-app", config.getClientServiceName());
        assertEquals("my-app-1", config.getClientInstanceId());
        assertEquals("my-host", config.getClientHost());
        assertEquals(9090, config.getClientPort());
        assertEquals(9443, config.getClientSecurePort());
        assertEquals(15000, config.getClientHeartbeatInterval());
        assertEquals(3000, config.getClientConnectTimeout());
        assertEquals(5000, config.getClientReadTimeout());
        assertFalse(config.isClientAutoRegister());
    }

    // ---- EurekaException tests ----

    @Test
    public void exceptionShouldStoreStatusCode() {
        var ex = new EurekaException(404, "not found");
        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("404"));
    }

    @Test
    public void exceptionShouldHandleCause() {
        var cause = new RuntimeException("connection refused");
        var ex = new EurekaException("failed", cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    public void exceptionShouldHandleSimpleMessage() {
        var ex = new EurekaException("simple error");
        assertTrue(ex.getMessage().contains("simple error"));
        assertEquals(-1, ex.getStatusCode());
    }

    // ---- EurekaClient tests (no server needed) ----

    @Test
    public void clientShouldNotBeAvailableWhenUnreachable() {
        var client = new EurekaClient("http://localhost:19999", "/eureka");
        assertFalse(client.isServerAvailable());
    }

    @Test
    public void clientShouldThrowWhenRegisteringToUnreachable() {
        var client = new EurekaClient("http://localhost:19999", "/eureka");
        var instance = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        assertThrows(EurekaException.class, () -> client.register(instance));
    }

    @Test
    public void clientShouldThrowWhenRenewingToUnreachable() {
        var client = new EurekaClient("http://localhost:19999", "/eureka");
        assertThrows(EurekaException.class, () -> client.renew("ORDER", "order-1"));
    }

    @Test
    public void clientShouldNormalizeUrl() {
        var client = new EurekaClient("http://localhost:8761/", "/eureka");
        assertFalse(client.getServerUrl().endsWith("/"));
    }

    @Test
    public void clientShouldDefaultUrlWhenNull() {
        var client = new EurekaClient(null, null);
        assertEquals("http://localhost:8761", client.getServerUrl());
    }

    @Test
    public void clientToStringShouldContainUrl() {
        var client = new EurekaClient("http://eureka:8761", "/eureka");
        var str = client.toString();
        assertTrue(str.contains("eureka:8761"));
    }

    // ---- EurekaHeartbeatScheduler tests ----

    @Test
    public void heartbeatSchedulerShouldStartAndStop() {
        var client = new EurekaClient("http://localhost:19999", "/eureka");
        var instance = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        var scheduler = new EurekaHeartbeatScheduler(client, instance, 60000);
        assertFalse(scheduler.isRunning());
        scheduler.start();
        assertTrue(scheduler.isRunning());
        scheduler.stop();
        assertFalse(scheduler.isRunning());
    }

    @Test
    public void heartbeatSchedulerShouldReturnInterval() {
        var client = new EurekaClient("http://localhost:19999", "/eureka");
        var instance = new InstanceInfo("ORDER", "order-1", "localhost", 8080);
        var scheduler = new EurekaHeartbeatScheduler(client, instance, 45000);
        assertEquals(45000, scheduler.getIntervalMillis());
        scheduler.stop();
    }
}
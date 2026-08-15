/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.etcd.test;

import com.truthbean.debbie.etcd.EtcdClient;
import com.truthbean.debbie.etcd.EtcdConfiguration;
import com.truthbean.debbie.etcd.EtcdException;
import com.truthbean.debbie.etcd.EtcdHealthIndicator;
import com.truthbean.debbie.etcd.EtcdJson;
import com.truthbean.debbie.etcd.EtcdProperties;
import com.truthbean.debbie.etcd.config.EtcdConfigClient;
import com.truthbean.debbie.etcd.discovery.EtcdServiceDiscovery;
import com.truthbean.debbie.etcd.discovery.EtcdServiceInstance;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class EtcdTest {

    // ---- EtcdJson tests ----

    @Test
    public void jsonShouldParseObject() {
        var result = EtcdJson.parseObject("{\"key\":\"value\",\"num\":123}");
        assertEquals("value", result.get("key"));
        assertEquals(123, ((Number) result.get("num")).intValue());
    }

    @Test
    public void jsonShouldParseNested() {
        var json = "{\"kvs\":[{\"key\":\"YQ==\",\"value\":\"Yg==\"}]}";
        var result = EtcdJson.parseObject(json);
        var kvs = (List<?>) result.get("kvs");
        assertEquals(1, kvs.size());
        var kv = (Map<?, ?>) kvs.get(0);
        assertEquals("YQ==", kv.get("key"));
        assertEquals("Yg==", kv.get("value"));
    }

    @Test
    public void jsonShouldParseEmpty() {
        assertTrue(EtcdJson.parseObject("{}").isEmpty());
    }

    @Test
    public void jsonShouldHandleNull() {
        assertEquals("null", EtcdJson.toJson(null));
    }

    @Test
    public void jsonShouldSerializeString() {
        assertEquals("\"hello\"", EtcdJson.toJson("hello"));
    }

    @Test
    public void jsonShouldSerializeNumber() {
        assertEquals("42", EtcdJson.toJson(42));
    }

    @Test
    public void jsonShouldSerializeBoolean() {
        assertEquals("true", EtcdJson.toJson(true));
    }

    @Test
    public void jsonShouldSerializeMap() {
        var json = EtcdJson.toJson(Map.of("a", "1", "b", "2"));
        var parsed = EtcdJson.parseObject(json);
        assertEquals("1", parsed.get("a"));
        assertEquals("2", parsed.get("b"));
    }

    @Test
    public void jsonShouldSerializeList() {
        var json = EtcdJson.toJson(List.of(1, 2, 3));
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    public void jsonShouldQuoteSpecialChars() {
        var quoted = EtcdJson.quote("a\"b\\c\n");
        assertTrue(quoted.contains("\\\""));
        assertTrue(quoted.contains("\\\\"));
        assertTrue(quoted.contains("\\n"));
    }

    // ---- EtcdProperties tests ----

    @Test
    public void propertiesShouldBuildBaseUrl() {
        var props = new EtcdProperties("localhost", 2379);
        assertEquals("http://localhost:2379", props.getBaseUrl());
    }

    @Test
    public void propertiesShouldBuildHttpsUrl() {
        var props = new EtcdProperties("etcd", 2379, "https");
        assertEquals("https://etcd:2379", props.getBaseUrl());
    }

    @Test
    public void propertiesShouldDetectCredentials() {
        var props = new EtcdProperties("localhost", 2379);
        assertFalse(props.hasCredentials());
        props.setUsername("root");
        props.setPassword("secret");
        assertTrue(props.hasCredentials());
    }

    @Test
    public void propertiesOfShouldCreateInstance() {
        var props = EtcdProperties.of("10.0.0.1", 2379);
        assertEquals("10.0.0.1", props.getHost());
        assertEquals(2379, props.getPort());
    }

    @Test
    public void propertiesToStringShouldContainUrl() {
        var props = new EtcdProperties("etcd-host", 2379);
        assertTrue(props.toString().contains("etcd-host:2379"));
    }

    // ---- EtcdConfiguration tests ----

    @Test
    public void configurationDefaults() {
        var config = new EtcdConfiguration();
        assertTrue(config.isEnable());
        assertEquals("localhost", config.getHost());
        assertEquals(2379, config.getPort());
        assertEquals("http", config.getScheme());
        assertTrue(config.isDiscoveryEnable());
        assertFalse(config.isConfigEnable());
        assertTrue(config.isHealthEnable());
        assertEquals("application", config.getConfigAppName());
        assertEquals("default", config.getConfigProfile());
        assertFalse(config.isConfigFailFast());
        assertEquals(5000, config.getConnectTimeout());
        assertEquals(10000, config.getReadTimeout());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new EtcdConfiguration();
        config.setHost("etcd.example.com");
        config.setPort(12379);
        config.setConfigEnable(true);
        config.setConfigProfile("production");

        var copy = config.<EtcdConfiguration>copy();
        assertEquals("etcd.example.com", copy.getHost());
        assertEquals(12379, copy.getPort());
        assertTrue(copy.isConfigEnable());
        assertEquals("production", copy.getConfigProfile());
    }

    @Test
    public void configurationToPropertiesShouldUseConfigValues() {
        var config = new EtcdConfiguration();
        config.setHost("etcd.example.com");
        config.setPort(12379);
        config.setScheme("https");
        config.setUsername("root");
        config.setPassword("secret");

        var props = config.toProperties();
        assertEquals("etcd.example.com", props.getHost());
        assertEquals(12379, props.getPort());
        assertEquals("https", props.getScheme());
        assertEquals("root", props.getUsername());
        assertEquals("secret", props.getPassword());
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new EtcdConfiguration();
        config.setEnable(false);
        config.setHost("etcd");
        config.setPort(12379);
        config.setScheme("https");
        config.setUsername("user");
        config.setPassword("pass");
        config.setPrefix("/my");
        config.setDiscoveryEnable(false);
        config.setConfigEnable(true);
        config.setConfigAppName("my-app");
        config.setConfigProfile("prod");
        config.setConfigFailFast(true);
        config.setHealthEnable(false);
        config.setConnectTimeout(3000);
        config.setReadTimeout(5000);

        assertFalse(config.isEnable());
        assertEquals("etcd", config.getHost());
        assertEquals(12379, config.getPort());
        assertEquals("https", config.getScheme());
        assertEquals("user", config.getUsername());
        assertEquals("pass", config.getPassword());
        assertEquals("/my", config.getPrefix());
        assertFalse(config.isDiscoveryEnable());
        assertTrue(config.isConfigEnable());
        assertEquals("my-app", config.getConfigAppName());
        assertEquals("prod", config.getConfigProfile());
        assertTrue(config.isConfigFailFast());
        assertFalse(config.isHealthEnable());
        assertEquals(3000, config.getConnectTimeout());
        assertEquals(5000, config.getReadTimeout());
    }

    // ---- EtcdException tests ----

    @Test
    public void exceptionShouldStoreStatusCode() {
        var ex = new EtcdException(404, "not found");
        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("404"));
    }

    @Test
    public void exceptionShouldHandleCause() {
        var cause = new RuntimeException("connection refused");
        var ex = new EtcdException("failed", cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    public void exceptionShouldHandleSimpleMessage() {
        var ex = new EtcdException("simple error");
        assertTrue(ex.getMessage().contains("simple error"));
        assertEquals(-1, ex.getStatusCode());
    }

    // ---- EtcdServiceInstance tests ----

    @Test
    public void instanceShouldBuildUrl() {
        var instance = new EtcdServiceInstance("order", "10.0.0.1", 8080);
        assertEquals("http://10.0.0.1:8080", instance.getUrl());
    }

    @Test
    public void instanceShouldBuildHttpsUrl() {
        var instance = new EtcdServiceInstance("order", "10.0.0.1", 8443);
        instance.setScheme("https");
        assertEquals("https://10.0.0.1:8443", instance.getUrl());
    }

    @Test
    public void instanceShouldDefaultToHealthy() {
        var instance = new EtcdServiceInstance("order", "10.0.0.1", 8080);
        assertTrue(instance.isHealthy());
    }

    @Test
    public void instanceToValueShouldEncodeHostPort() {
        var instance = new EtcdServiceInstance("order", "order-1", "10.0.0.1", 8080);
        var value = instance.toValue();
        assertTrue(value.contains("10.0.0.1:8080"));
    }

    @Test
    public void instanceToValueShouldEncodeScheme() {
        var instance = new EtcdServiceInstance("order", "order-1", "10.0.0.1", 8443);
        instance.setScheme("https");
        var value = instance.toValue();
        assertTrue(value.contains("scheme=https"));
    }

    @Test
    public void instanceFromValueShouldRestore() {
        var original = new EtcdServiceInstance("order", "order-1", "10.0.0.1", 8080);
        original.setScheme("https");
        original.getMetadata().put("zone", "us-east-1");
        var value = original.toValue();
        var restored = EtcdServiceInstance.fromValue("order", "order-1", value);
        assertEquals("order", restored.getServiceName());
        assertEquals("order-1", restored.getInstanceId());
        assertEquals("10.0.0.1", restored.getHost());
        assertEquals(8080, restored.getPort());
        assertEquals("https", restored.getScheme());
        assertEquals("us-east-1", restored.getMetadata().get("zone"));
    }

    @Test
    public void instanceFromValueShouldHandleNull() {
        assertNull(EtcdServiceInstance.fromValue("svc", "id", null));
        assertNull(EtcdServiceInstance.fromValue("svc", "id", ""));
    }

    @Test
    public void instanceToStringShouldContainUrl() {
        var instance = new EtcdServiceInstance("order", "10.0.0.1", 8080);
        assertTrue(instance.toString().contains("10.0.0.1:8080"));
    }

    // ---- EtcdClient tests (no etcd server needed) ----

    @Test
    public void clientShouldNotBeAvailableWhenUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        assertFalse(client.isAvailable());
    }

    @Test
    public void clientShouldThrowWhenPuttingToUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        assertThrows(EtcdException.class, () -> client.put("key", "value"));
    }

    @Test
    public void clientShouldThrowWhenGettingFromUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        assertThrows(EtcdException.class, () -> client.get("key"));
    }

    @Test
    public void clientShouldReturnProperties() {
        var props = EtcdProperties.of("etcd", 2379);
        var client = new EtcdClient(props);
        assertSame(props, client.getProperties());
    }

    // ---- EtcdServiceDiscovery tests (no etcd server needed) ----

    @Test
    public void discoveryShouldReturnEmptyWhenUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        var discovery = new EtcdServiceDiscovery(client);
        var instances = discovery.getInstances("test-service");
        assertTrue(instances.isEmpty());
    }

    @Test
    public void discoveryShouldReturnEmptyServiceNamesWhenUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        var discovery = new EtcdServiceDiscovery(client);
        var names = discovery.getAllServiceNames();
        assertTrue(names.isEmpty());
    }

    @Test
    public void discoveryShouldReturnEmptyHealthyWhenUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        var discovery = new EtcdServiceDiscovery(client);
        var instances = discovery.getHealthyInstances("test-service");
        assertTrue(instances.isEmpty());
    }

    // ---- EtcdConfigClient tests (no etcd server needed) ----

    @Test
    public void configClientShouldReturnNullWhenUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        var configClient = new EtcdConfigClient(client);
        var value = configClient.get("app", "default", "key");
        assertNull(value);
    }

    @Test
    public void configClientShouldReturnEmptyWhenUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        var configClient = new EtcdConfigClient(client);
        var config = configClient.getConfig("app", "default");
        assertTrue(config.isEmpty());
    }

    // ---- EtcdHealthIndicator tests ----

    @Test
    public void healthShouldBeUnhealthyWhenUnreachable() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        var health = new EtcdHealthIndicator(client);
        var status = health.check();
        assertFalse(status.isHealthy());
    }

    @Test
    public void healthShouldContainEndpointInDetails() {
        var props = EtcdProperties.of("localhost", 19999);
        var client = new EtcdClient(props);
        var health = new EtcdHealthIndicator(client);
        var status = health.check();
        assertNotNull(status.getDetails().get("endpoint"));
    }
}

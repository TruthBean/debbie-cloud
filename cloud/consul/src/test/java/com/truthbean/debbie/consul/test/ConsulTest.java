/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.consul.test;

import com.truthbean.debbie.consul.ConsulClient;
import com.truthbean.debbie.consul.ConsulConfiguration;
import com.truthbean.debbie.consul.ConsulException;
import com.truthbean.debbie.consul.config.ConsulConfigClient;
import com.truthbean.debbie.consul.discovery.ConsulServiceInstance;
import com.truthbean.debbie.consul.discovery.ConsulServiceRegistration;
import com.truthbean.debbie.consul.health.ConsulHealthIndicator;
import com.truthbean.debbie.consul.json.SimpleJson;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ConsulTest {

    // ---- SimpleJson tests ----

    @Test
    public void jsonShouldParseSimpleObject() {
        var json = "{\"name\":\"order-service\",\"port\":8080,\"enabled\":true}";
        var result = SimpleJson.parseObject(json);
        assertEquals("order-service", result.get("name"));
        assertEquals(8080L, result.get("port"));
        assertEquals(true, result.get("enabled"));
    }

    @Test
    public void jsonShouldParseArray() {
        var json = "[{\"key\":\"a\",\"Value\":\"YQ==\"},{\"key\":\"b\",\"Value\":\"Yg==\"}]";
        var result = SimpleJson.parseArray(json);
        assertEquals(2, result.size());
        var first = (Map<String, Object>) result.get(0);
        assertEquals("a", first.get("key"));
    }

    @Test
    public void jsonShouldParseNestedObject() {
        var json = "{\"Service\":{\"ID\":\"svc-1\",\"Service\":\"order\",\"Address\":\"10.0.0.1\",\"Port\":8080},\"Checks\":[{\"Status\":\"passing\"}]}";
        var result = SimpleJson.parseObject(json);
        var service = (Map<String, Object>) result.get("Service");
        assertEquals("svc-1", service.get("ID"));
        assertEquals("order", service.get("Service"));
        assertEquals(8080L, service.get("Port"));

        var checks = (List<Object>) result.get("Checks");
        var firstCheck = (Map<String, Object>) checks.get(0);
        assertEquals("passing", firstCheck.get("Status"));
    }

    @Test
    public void jsonShouldParseEmptyArray() {
        var result = SimpleJson.parseArray("[]");
        assertTrue(result.isEmpty());
    }

    @Test
    public void jsonShouldParseEmptyObject() {
        var result = SimpleJson.parseObject("{}");
        assertTrue(result.isEmpty());
    }

    @Test
    public void jsonShouldParseNull() {
        assertNull(SimpleJson.parse("null"));
        assertNull(SimpleJson.parse(null));
        assertNull(SimpleJson.parse(""));
    }

    @Test
    public void jsonShouldParseString() {
        var result = SimpleJson.parse("\"hello world\"");
        assertEquals("hello world", result);
    }

    @Test
    public void jsonShouldParseNumber() {
        assertEquals(42L, SimpleJson.parse("42"));
        assertEquals(3.14, SimpleJson.parse("3.14"));
    }

    @Test
    public void jsonShouldHandleEscapes() {
        var result = SimpleJson.parse("\"hello\\nworld\"");
        assertEquals("hello\nworld", result);
    }

    @Test
    public void jsonShouldSerializeString() {
        assertEquals("\"hello\"", SimpleJson.toJsonString("hello"));
        assertEquals("\"a\\\"b\"", SimpleJson.toJsonString("a\"b"));
        assertEquals("null", SimpleJson.toJsonString(null));
        assertEquals("42", SimpleJson.toJsonString(42));
        assertEquals("true", SimpleJson.toJsonString(true));
    }

    @Test
    public void jsonShouldSerializeMap() {
        var json = SimpleJson.toJsonString(Map.of("name", "test", "port", 8080));
        var parsed = SimpleJson.parseObject(json);
        assertEquals("test", parsed.get("name"));
        assertEquals(8080L, parsed.get("port"));
    }

    @Test
    public void jsonShouldSerializeList() {
        var json = SimpleJson.toJsonString(List.of("a", "b", "c"));
        var parsed = SimpleJson.parseArray(json);
        assertEquals(3, parsed.size());
        assertEquals("a", parsed.get(0));
        assertEquals("c", parsed.get(2));
    }

    // ---- Configuration tests ----

    @Test
    public void consulConfigurationDefaults() {
        var config = new ConsulConfiguration();
        assertTrue(config.isEnable());
        assertEquals("localhost", config.getHost());
        assertEquals(8500, config.getPort());
        assertEquals("http", config.getScheme());
        assertNull(config.getToken());
        assertTrue(config.isDiscoveryEnable());
        assertTrue(config.isDiscoveryRegister());
        assertFalse(config.isDiscoveryPreferIpAddress());
        assertFalse(config.isConfigEnable());
        assertEquals("config", config.getConfigPrefix());
        assertEquals("/", config.getConfigProfileSeparator());
        assertFalse(config.isConfigWatch());
        assertTrue(config.isHealthEnable());
        assertEquals(8080, config.getServicePort());
    }

    @Test
    public void consulConfigurationCopyShouldBeEqual() {
        var config = new ConsulConfiguration();
        config.setHost("consul.example.com");
        config.setPort(8501);
        config.setServiceName("my-app");

        var copy = config.<ConsulConfiguration>copy();
        assertEquals("consul.example.com", copy.getHost());
        assertEquals(8501, copy.getPort());
        assertEquals("my-app", copy.getServiceName());
    }

    // ---- Model tests ----

    @Test
    public void serviceRegistrationShouldBuildCorrectly() {
        var reg = new ConsulServiceRegistration();
        reg.setId("order-8080");
        reg.setName("order-service");
        reg.setAddress("10.0.0.1");
        reg.setPort(8080);
        reg.setTags(List.of("v1", "primary"));
        reg.setCheckHttp("http://10.0.0.1:8080/actuator/health");
        reg.setCheckInterval("15s");

        assertEquals("order-8080", reg.getId());
        assertEquals("order-service", reg.getName());
        assertEquals("10.0.0.1", reg.getAddress());
        assertEquals(8080, reg.getPort());
        assertEquals(2, reg.getTags().size());
        assertTrue(reg.hasHealthCheck());
    }

    @Test
    public void serviceInstanceShouldCheckPassing() {
        var instance = new ConsulServiceInstance("order", "10.0.0.1", 8080);
        instance.setStatus("passing");
        assertTrue(instance.isPassing());

        instance.setStatus("critical");
        assertFalse(instance.isPassing());
    }

    @Test
    public void serviceInstanceToStringShouldContainName() {
        var instance = new ConsulServiceInstance("order", "10.0.0.1", 8080);
        instance.setId("order-1");
        var str = instance.toString();
        assertTrue(str.contains("order"));
        assertTrue(str.contains("10.0.0.1"));
        assertTrue(str.contains("8080"));
    }

    // ---- Exception tests ----

    @Test
    public void consulExceptionShouldStoreStatusCode() {
        var ex = new ConsulException(404, "not found");
        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("404"));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    public void consulExceptionShouldHandleCause() {
        var cause = new RuntimeException("connection refused");
        var ex = new ConsulException("failed", cause);
        assertEquals(-1, ex.getStatusCode());
        assertSame(cause, ex.getCause());
    }

    // ---- Health indicator tests ----

    @Test
    public void healthStatusShouldBeUnhealthyWhenAgentUnreachable() {
        var client = new ConsulClient("localhost", 19999, "http");
        var indicator = new ConsulHealthIndicator(client);
        var status = indicator.check();
        assertFalse(status.isHealthy());
    }

    @Test
    public void healthStatusToStringShouldContainMessage() {
        var client = new ConsulClient("localhost", 19999, "http");
        var indicator = new ConsulHealthIndicator(client);
        var status = indicator.check();
        var str = status.toString();
        assertTrue(str.contains("healthy="));
    }

    // ---- ConsulClient tests (no agent needed) ----

    @Test
    public void consulClientShouldNotBeAvailableWhenUnreachable() {
        var client = new ConsulClient("localhost", 19999, "http");
        assertFalse(client.isAgentAvailable());
    }

    @Test
    public void consulClientShouldThrowWhenGettingKeyValueFromUnreachable() {
        var client = new ConsulClient("localhost", 19999, "http");
        assertThrows(ConsulException.class, () -> client.getKeyValue("test-key"));
    }

    @Test
    public void consulClientShouldThrowWhenRegisteringToUnreachable() {
        var client = new ConsulClient("localhost", 19999, "http");
        var reg = new ConsulServiceRegistration();
        reg.setName("test");
        reg.setId("test-1");
        assertThrows(ConsulException.class, () -> client.registerService(reg));
    }
}
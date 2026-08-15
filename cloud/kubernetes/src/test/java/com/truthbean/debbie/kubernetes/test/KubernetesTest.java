/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.kubernetes.test;

import com.truthbean.debbie.kubernetes.KubernetesClient;
import com.truthbean.debbie.kubernetes.KubernetesConfiguration;
import com.truthbean.debbie.kubernetes.KubernetesException;
import com.truthbean.debbie.kubernetes.KubernetesProperties;
import com.truthbean.debbie.kubernetes.config.KubernetesConfigClient;
import com.truthbean.debbie.kubernetes.discovery.KubernetesServiceDiscovery;
import com.truthbean.debbie.kubernetes.discovery.KubernetesServiceInstance;
import com.truthbean.debbie.kubernetes.health.KubernetesHealthIndicator;
import com.truthbean.debbie.kubernetes.json.SimpleJson;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class KubernetesTest {

    // ---- SimpleJson tests ----

    @Test
    public void jsonShouldParseObject() {
        var result = SimpleJson.parseObject("{\"name\":\"test\",\"port\":8080}");
        assertEquals("test", result.get("name"));
        assertEquals(8080L, result.get("port"));
    }

    @Test
    public void jsonShouldParseArray() {
        var result = SimpleJson.parseArray("[1, 2, 3]");
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0));
    }

    @Test
    public void jsonShouldParseNested() {
        var json = "{\"metadata\":{\"name\":\"my-service\",\"namespace\":\"default\"},\"data\":{\"key\":\"value\"}}";
        var result = SimpleJson.parseObject(json);
        var metadata = (Map<String, Object>) result.get("metadata");
        assertEquals("my-service", metadata.get("name"));
        var data = (Map<String, Object>) result.get("data");
        assertEquals("value", data.get("key"));
    }

    @Test
    public void jsonShouldParseEmpty() {
        assertTrue(SimpleJson.parseObject("{}").isEmpty());
        assertTrue(SimpleJson.parseArray("[]").isEmpty());
    }

    @Test
    public void jsonShouldHandleNull() {
        assertNull(SimpleJson.parse(null));
        assertNull(SimpleJson.parse(""));
        assertNull(SimpleJson.parse("null"));
    }

    @Test
    public void jsonShouldSerialize() {
        assertEquals("\"hello\"", SimpleJson.toJsonString("hello"));
        assertEquals("42", SimpleJson.toJsonString(42));
        assertEquals("true", SimpleJson.toJsonString(true));
        assertEquals("null", SimpleJson.toJsonString(null));
    }

    @Test
    public void jsonShouldSerializeMap() {
        var json = SimpleJson.toJsonString(Map.of("a", "1", "b", "2"));
        var parsed = SimpleJson.parseObject(json);
        assertEquals("1", parsed.get("a"));
        assertEquals("2", parsed.get("b"));
    }

    // ---- KubernetesProperties tests ----

    @Test
    public void propertiesShouldCreateWithDefaults() {
        var props = KubernetesProperties.of("localhost", 8443, "default", null);
        assertEquals("localhost", props.getApiServerHost());
        assertEquals(8443, props.getApiServerPort());
        assertEquals("default", props.getNamespace());
        assertFalse(props.hasToken());
        assertFalse(props.isInsidePod());
    }

    @Test
    public void propertiesShouldBuildBaseUrl() {
        var props = KubernetesProperties.of("10.0.0.1", 443, "my-ns", "token123");
        assertEquals("https://10.0.0.1:443", props.getBaseUrl());
        assertTrue(props.hasToken());
    }

    @Test
    public void propertiesAutoDetectShouldNotFailOutsidePod() {
        var props = KubernetesProperties.autoDetect();
        assertNotNull(props.getApiServerHost());
        assertTrue(props.getApiServerPort() > 0);
        assertNotNull(props.getNamespace());
    }

    // ---- KubernetesConfiguration tests ----

    @Test
    public void configurationDefaults() {
        var config = new KubernetesConfiguration();
        assertTrue(config.isEnable());
        assertEquals("default", config.getNamespace());
        assertEquals(443, config.getPort());
        assertTrue(config.isAutoDetect());
        assertTrue(config.isDiscoveryEnable());
        assertFalse(config.isConfigEnable());
        assertTrue(config.isHealthEnable());
    }

    @Test
    public void configurationShouldParseConfigMapNames() {
        var config = new KubernetesConfiguration();
        config.setConfigMaps("app-config, db-config, extra-config");
        var names = config.getConfigMapNames();
        assertEquals(3, names.length);
        assertEquals("app-config", names[0]);
        assertEquals("db-config", names[1]);
        assertEquals("extra-config", names[2]);
    }

    @Test
    public void configurationShouldParseSecretNames() {
        var config = new KubernetesConfiguration();
        config.setSecrets("db-credentials, api-keys");
        var names = config.getSecretNames();
        assertEquals(2, names.length);
        assertEquals("db-credentials", names[0]);
    }

    @Test
    public void configurationShouldReturnEmptyArrayWhenNoConfigMaps() {
        var config = new KubernetesConfiguration();
        assertEquals(0, config.getConfigMapNames().length);
        assertEquals(0, config.getSecretNames().length);
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new KubernetesConfiguration();
        config.setNamespace("production");
        config.setConfigEnable(true);
        config.setConfigMaps("my-config");

        var copy = config.<KubernetesConfiguration>copy();
        assertEquals("production", copy.getNamespace());
        assertTrue(copy.isConfigEnable());
        assertEquals("my-config", copy.getConfigMaps());
    }

    @Test
    public void configurationToPropertiesShouldUseConfigValues() {
        var config = new KubernetesConfiguration();
        config.setAutoDetect(false);
        config.setHost("k8s.example.com");
        config.setPort(6443);
        config.setNamespace("production");
        config.setToken("my-token");

        var props = config.toProperties();
        assertEquals("k8s.example.com", props.getApiServerHost());
        assertEquals(6443, props.getApiServerPort());
        assertEquals("production", props.getNamespace());
        assertEquals("my-token", props.getToken());
    }

    // ---- Model tests ----

    @Test
    public void serviceInstanceShouldBuildUrl() {
        var instance = new KubernetesServiceInstance("order", "10.0.0.1", 8080);
        instance.setProtocol("http");
        assertEquals("http://10.0.0.1:8080", instance.getUrl());
    }

    @Test
    public void serviceInstanceShouldDefaultToHttp() {
        var instance = new KubernetesServiceInstance("order", "10.0.0.1", 8080);
        assertEquals("http://10.0.0.1:8080", instance.getUrl());
    }

    @Test
    public void serviceInstanceToStringShouldContainInfo() {
        var instance = new KubernetesServiceInstance("order", "10.0.0.1", 8080);
        instance.setNamespace("default");
        var str = instance.toString();
        assertTrue(str.contains("order"));
        assertTrue(str.contains("10.0.0.1"));
        assertTrue(str.contains("8080"));
    }

    // ---- Exception tests ----

    @Test
    public void exceptionShouldStoreStatusCode() {
        var ex = new KubernetesException(404, "not found");
        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("404"));
    }

    @Test
    public void exceptionShouldHandleCause() {
        var cause = new RuntimeException("connection refused");
        var ex = new KubernetesException("failed", cause);
        assertSame(cause, ex.getCause());
    }

    // ---- Client tests (no K8s cluster needed) ----

    @Test
    public void clientShouldNotBeAvailableWhenUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        assertFalse(client.isApiServerAvailable());
    }

    @Test
    public void clientShouldThrowWhenGettingConfigMapFromUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        assertThrows(KubernetesException.class, () -> client.getConfigMap("test"));
    }

    @Test
    public void clientShouldThrowWhenGettingServiceFromUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        assertThrows(KubernetesException.class, () -> client.getService("test"));
    }

    // ---- Health indicator tests ----

    @Test
    public void healthShouldBeUnhealthyWhenApiUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        var health = new KubernetesHealthIndicator(client);
        var status = health.check();
        assertFalse(status.isHealthy());
    }

    // ---- Discovery tests ----

    @Test
    public void discoveryShouldReturnEmptyWhenApiUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        var discovery = new KubernetesServiceDiscovery(client);
        var instances = discovery.getInstances("test-service");
        assertTrue(instances.isEmpty());
    }

    @Test
    public void discoveryShouldReturnEmptyServiceNamesWhenApiUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        var discovery = new KubernetesServiceDiscovery(client);
        var names = discovery.getAllServiceNames();
        assertTrue(names.isEmpty());
    }

    // ---- Config client tests ----

    @Test
    public void configClientShouldReturnEmptyWhenApiUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        var configClient = new KubernetesConfigClient(client);
        var config = configClient.getConfigMap("test");
        assertTrue(config.isEmpty());
    }

    @Test
    public void configClientShouldReturnEmptySecretWhenApiUnreachable() {
        var props = KubernetesProperties.of("localhost", 19999, "default", null);
        var client = new KubernetesClient(props);
        var configClient = new KubernetesConfigClient(client);
        var secret = configClient.getSecret("test");
        assertTrue(secret.isEmpty());
    }
}
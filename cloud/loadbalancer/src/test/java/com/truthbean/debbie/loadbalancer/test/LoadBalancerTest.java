/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.loadbalancer.test;

import com.truthbean.debbie.loadbalancer.LoadBalancer;
import com.truthbean.debbie.loadbalancer.LoadBalancerConfiguration;
import com.truthbean.debbie.loadbalancer.LoadBalancerException;
import com.truthbean.debbie.loadbalancer.LoadBalancerFactory;
import com.truthbean.debbie.loadbalancer.LoadBalancerRegistry;
import com.truthbean.debbie.loadbalancer.ServiceInstance;
import com.truthbean.debbie.loadbalancer.strategy.LeastConnectionsLoadBalancer;
import com.truthbean.debbie.loadbalancer.strategy.RandomLoadBalancer;
import com.truthbean.debbie.loadbalancer.strategy.RoundRobinLoadBalancer;
import com.truthbean.debbie.loadbalancer.strategy.WeightedRoundRobinLoadBalancer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class LoadBalancerTest {

    // ---- ServiceInstance tests ----

    @Test
    public void instanceShouldBuildUrl() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8080);
        assertEquals("http://10.0.0.1:8080", instance.getUrl());
    }

    @Test
    public void instanceShouldBuildHttpsUrl() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8443);
        instance.setScheme("https");
        assertEquals("https://10.0.0.1:8443", instance.getUrl());
    }

    @Test
    public void instanceShouldDefaultToHealthy() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8080);
        assertTrue(instance.isHealthy());
    }

    @Test
    public void instanceShouldDefaultToWeightOne() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8080);
        assertEquals(1, instance.getWeight());
    }

    @Test
    public void instanceShouldManageConnections() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8080);
        assertEquals(0, instance.getActiveConnections());
        instance.incrementConnections();
        instance.incrementConnections();
        assertEquals(2, instance.getActiveConnections());
        instance.decrementConnections();
        assertEquals(1, instance.getActiveConnections());
    }

    @Test
    public void instanceShouldNotGoNegativeConnections() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8080);
        instance.decrementConnections();
        assertEquals(0, instance.getActiveConnections());
    }

    @Test
    public void instanceEqualsShouldWork() {
        var a = new ServiceInstance("order", "host", 8080);
        var b = new ServiceInstance("order", "host", 8080);
        var c = new ServiceInstance("order", "host", 8081);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    public void instanceToStringShouldContainUrl() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8080);
        var str = instance.toString();
        assertTrue(str.contains("10.0.0.1:8080"));
    }

    @Test
    public void instanceShouldSetMetadata() {
        var instance = new ServiceInstance("order", "10.0.0.1", 8080);
        instance.setMetadata(Map.of("zone", "us-east-1"));
        assertEquals("us-east-1", instance.getMetadata().get("zone"));
    }

    // ---- RoundRobinLoadBalancer tests ----

    @Test
    public void roundRobinShouldCycleInOrder() {
        var lb = new RoundRobinLoadBalancer();
        var instances = List.of(
                new ServiceInstance("svc", "h1", 8080),
                new ServiceInstance("svc", "h2", 8080),
                new ServiceInstance("svc", "h3", 8080));
        assertEquals("h1", lb.choose(instances).getHost());
        assertEquals("h2", lb.choose(instances).getHost());
        assertEquals("h3", lb.choose(instances).getHost());
        assertEquals("h1", lb.choose(instances).getHost());
    }

    @Test
    public void roundRobinShouldReturnNullForEmpty() {
        var lb = new RoundRobinLoadBalancer();
        assertNull(lb.choose(List.of()));
        assertNull(lb.choose(null));
    }

    @Test
    public void roundRobinShouldSkipUnhealthy() {
        var lb = new RoundRobinLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        var i2 = new ServiceInstance("svc", "h2", 8080);
        i2.setHealthy(false);
        var instances = List.of(i1, i2);
        assertEquals("h1", lb.choose(instances).getHost());
        assertEquals("h1", lb.choose(instances).getHost());
    }

    @Test
    public void roundRobinShouldReturnNullIfAllUnhealthy() {
        var lb = new RoundRobinLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        i1.setHealthy(false);
        assertNull(lb.choose(List.of(i1)));
    }

    @Test
    public void roundRobinShouldReturnCorrectName() {
        assertEquals("round-robin", new RoundRobinLoadBalancer().name());
    }

    @Test
    public void roundRobinShouldReset() {
        var lb = new RoundRobinLoadBalancer();
        var instances = List.of(
                new ServiceInstance("svc", "h1", 8080),
                new ServiceInstance("svc", "h2", 8080));
        lb.choose(instances);
        lb.choose(instances);
        lb.reset();
        assertEquals("h1", lb.choose(instances).getHost());
    }

    // ---- RandomLoadBalancer tests ----

    @Test
    public void randomShouldReturnAnInstance() {
        var lb = new RandomLoadBalancer();
        var instances = List.of(
                new ServiceInstance("svc", "h1", 8080),
                new ServiceInstance("svc", "h2", 8080),
                new ServiceInstance("svc", "h3", 8080));
        var chosen = lb.choose(instances);
        assertNotNull(chosen);
        assertTrue(instances.contains(chosen));
    }

    @Test
    public void randomShouldReturnNullForEmpty() {
        var lb = new RandomLoadBalancer();
        assertNull(lb.choose(List.of()));
        assertNull(lb.choose(null));
    }

    @Test
    public void randomShouldSkipUnhealthy() {
        var lb = new RandomLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        var i2 = new ServiceInstance("svc", "h2", 8080);
        i2.setHealthy(false);
        var chosen = lb.choose(List.of(i1, i2));
        assertEquals("h1", chosen.getHost());
    }

    @Test
    public void randomShouldReturnCorrectName() {
        assertEquals("random", new RandomLoadBalancer().name());
    }

    @Test
    public void randomShouldEventuallySelectAllInstances() {
        var lb = new RandomLoadBalancer();
        var instances = List.of(
                new ServiceInstance("svc", "h1", 8080),
                new ServiceInstance("svc", "h2", 8080),
                new ServiceInstance("svc", "h3", 8080));
        var selected = new HashMap<String, Boolean>();
        for (int i = 0; i < 1000; i++) {
            selected.put(lb.choose(instances).getHost(), true);
        }
        assertTrue(selected.containsKey("h1"));
        assertTrue(selected.containsKey("h2"));
        assertTrue(selected.containsKey("h3"));
    }

    // ---- WeightedRoundRobinLoadBalancer tests ----

    @Test
    public void weightedRoundRobinShouldRespectWeights() {
        var lb = new WeightedRoundRobinLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        i1.setWeight(3);
        var i2 = new ServiceInstance("svc", "h2", 8080);
        i2.setWeight(1);
        var instances = List.of(i1, i2);

        var counts = new HashMap<String, Integer>();
        for (int i = 0; i < 4; i++) {
            var chosen = lb.choose(instances);
            counts.merge(chosen.getHost(), 1, Integer::sum);
        }
        assertEquals(3, counts.get("h1"));
        assertEquals(1, counts.get("h2"));
    }

    @Test
    public void weightedRoundRobinShouldReturnNullForEmpty() {
        var lb = new WeightedRoundRobinLoadBalancer();
        assertNull(lb.choose(List.of()));
        assertNull(lb.choose(null));
    }

    @Test
    public void weightedRoundRobinShouldSkipUnhealthy() {
        var lb = new WeightedRoundRobinLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        var i2 = new ServiceInstance("svc", "h2", 8080);
        i2.setHealthy(false);
        assertEquals("h1", lb.choose(List.of(i1, i2)).getHost());
    }

    @Test
    public void weightedRoundRobinShouldHandleZeroWeight() {
        var lb = new WeightedRoundRobinLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        i1.setWeight(0);
        assertNotNull(lb.choose(List.of(i1)));
    }

    @Test
    public void weightedRoundRobinShouldReturnCorrectName() {
        assertEquals("weighted-round-robin", new WeightedRoundRobinLoadBalancer().name());
    }

    // ---- LeastConnectionsLoadBalancer tests ----

    @Test
    public void leastConnectionsShouldSelectFewestConnections() {
        var lb = new LeastConnectionsLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        i1.setActiveConnections(5);
        var i2 = new ServiceInstance("svc", "h2", 8080);
        i2.setActiveConnections(2);
        var i3 = new ServiceInstance("svc", "h3", 8080);
        i3.setActiveConnections(8);
        var chosen = lb.choose(List.of(i1, i2, i3));
        assertEquals("h2", chosen.getHost());
    }

    @Test
    public void leastConnectionsShouldReturnNullForEmpty() {
        var lb = new LeastConnectionsLoadBalancer();
        assertNull(lb.choose(List.of()));
        assertNull(lb.choose(null));
    }

    @Test
    public void leastConnectionsShouldSkipUnhealthy() {
        var lb = new LeastConnectionsLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        i1.setActiveConnections(5);
        i1.setHealthy(false);
        var i2 = new ServiceInstance("svc", "h2", 8080);
        i2.setActiveConnections(2);
        var chosen = lb.choose(List.of(i1, i2));
        assertEquals("h2", chosen.getHost());
    }

    @Test
    public void leastConnectionsShouldHandleTie() {
        var lb = new LeastConnectionsLoadBalancer();
        var i1 = new ServiceInstance("svc", "h1", 8080);
        var i2 = new ServiceInstance("svc", "h2", 8080);
        var chosen = lb.choose(List.of(i1, i2));
        assertNotNull(chosen);
        assertEquals(0, chosen.getActiveConnections());
    }

    @Test
    public void leastConnectionsShouldReturnCorrectName() {
        assertEquals("least-connections", new LeastConnectionsLoadBalancer().name());
    }

    // ---- LoadBalancerFactory tests ----

    @Test
    public void factoryShouldCreateRoundRobin() {
        var lb = LoadBalancerFactory.create("round-robin");
        assertNotNull(lb);
        assertEquals("round-robin", lb.name());
    }

    @Test
    public void factoryShouldCreateRandom() {
        var lb = LoadBalancerFactory.create("random");
        assertEquals("random", lb.name());
    }

    @Test
    public void factoryShouldCreateWeightedRoundRobin() {
        var lb = LoadBalancerFactory.create("weighted-round-robin");
        assertEquals("weighted-round-robin", lb.name());
    }

    @Test
    public void factoryShouldCreateLeastConnections() {
        var lb = LoadBalancerFactory.create("least-connections");
        assertEquals("least-connections", lb.name());
    }

    @Test
    public void factoryShouldDefaultToRoundRobinForUnknown() {
        var lb = LoadBalancerFactory.create("unknown-strategy");
        assertEquals("round-robin", lb.name());
    }

    @Test
    public void factoryShouldDefaultToRoundRobinForNull() {
        var lb = LoadBalancerFactory.create(null);
        assertEquals("round-robin", lb.name());
    }

    @Test
    public void factoryShouldHandleCaseInsensitive() {
        var lb = LoadBalancerFactory.create("RANDOM");
        assertEquals("random", lb.name());
    }

    @Test
    public void factoryShouldCreateDefault() {
        var lb = LoadBalancerFactory.createDefault();
        assertEquals("round-robin", lb.name());
    }

    // ---- LoadBalancerRegistry tests ----

    @Test
    public void registryShouldCreateBalancerForNewService() {
        var registry = new LoadBalancerRegistry();
        var lb = registry.getOrCreate("order-service");
        assertNotNull(lb);
        assertEquals("round-robin", lb.name());
    }

    @Test
    public void registryShouldReturnSameBalancerForSameService() {
        var registry = new LoadBalancerRegistry();
        var lb1 = registry.getOrCreate("order-service");
        var lb2 = registry.getOrCreate("order-service");
        assertSame(lb1, lb2);
    }

    @Test
    public void registryShouldCreateDifferentBalancersForDifferentServices() {
        var registry = new LoadBalancerRegistry();
        var lb1 = registry.getOrCreate("order-service");
        var lb2 = registry.getOrCreate("payment-service");
        assertNotSame(lb1, lb2);
    }

    @Test
    public void registryShouldCreateWithCustomStrategy() {
        var registry = new LoadBalancerRegistry();
        var lb = registry.getOrCreate("order-service", "random");
        assertEquals("random", lb.name());
    }

    @Test
    public void registryShouldRemoveBalancer() {
        var registry = new LoadBalancerRegistry();
        registry.getOrCreate("order-service");
        assertEquals(1, registry.size());
        registry.remove("order-service");
        assertEquals(0, registry.size());
    }

    @Test
    public void registryShouldClearAll() {
        var registry = new LoadBalancerRegistry();
        registry.getOrCreate("svc1");
        registry.getOrCreate("svc2");
        registry.clear();
        assertEquals(0, registry.size());
    }

    @Test
    public void registryShouldReturnServiceNames() {
        var registry = new LoadBalancerRegistry();
        registry.getOrCreate("svc1");
        registry.getOrCreate("svc2");
        var names = registry.getServiceNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("svc1"));
        assertTrue(names.contains("svc2"));
    }

    @Test
    public void registryShouldReturnDefaultStrategy() {
        var registry = new LoadBalancerRegistry("least-connections");
        assertEquals("least-connections", registry.getDefaultStrategy());
    }

    @Test
    public void registryShouldPutCustomBalancer() {
        var registry = new LoadBalancerRegistry();
        var custom = new RandomLoadBalancer();
        registry.put("custom-svc", custom);
        assertSame(custom, registry.get("custom-svc"));
    }

    // ---- LoadBalancerConfiguration tests ----

    @Test
    public void configurationDefaults() {
        var config = new LoadBalancerConfiguration();
        assertTrue(config.isEnable());
        assertEquals("round-robin", config.getDefaultStrategy());
        assertFalse(config.isHealthCheckEnable());
        assertEquals(10000, config.getHealthCheckInterval());
        assertTrue(config.isRetryEnable());
        assertEquals(3, config.getRetryMaxAttempts());
        assertFalse(config.isStickyEnable());
        assertTrue(config.isCacheEnable());
        assertEquals(30000, config.getCacheTtl());
    }

    @Test
    public void configurationCopyShouldBeEqual() {
        var config = new LoadBalancerConfiguration();
        config.setDefaultStrategy("random");
        config.setRetryMaxAttempts(5);
        config.setCacheEnable(false);

        var copy = config.<LoadBalancerConfiguration>copy();
        assertEquals("random", copy.getDefaultStrategy());
        assertEquals(5, copy.getRetryMaxAttempts());
        assertFalse(copy.isCacheEnable());
    }

    @Test
    public void configurationShouldGetAndSetAllFields() {
        var config = new LoadBalancerConfiguration();
        config.setEnable(false);
        config.setDefaultStrategy("least-connections");
        config.setHealthCheckEnable(true);
        config.setHealthCheckInterval(5000);
        config.setRetryEnable(false);
        config.setRetryMaxAttempts(10);
        config.setStickyEnable(true);
        config.setCacheEnable(false);
        config.setCacheTtl(60000);

        assertFalse(config.isEnable());
        assertEquals("least-connections", config.getDefaultStrategy());
        assertTrue(config.isHealthCheckEnable());
        assertEquals(5000, config.getHealthCheckInterval());
        assertFalse(config.isRetryEnable());
        assertEquals(10, config.getRetryMaxAttempts());
        assertTrue(config.isStickyEnable());
        assertFalse(config.isCacheEnable());
        assertEquals(60000, config.getCacheTtl());
    }

    // ---- LoadBalancerException tests ----

    @Test
    public void exceptionShouldStoreMessage() {
        var ex = new LoadBalancerException("no instances available");
        assertTrue(ex.getMessage().contains("no instances available"));
    }

    @Test
    public void exceptionShouldHandleCause() {
        var cause = new RuntimeException("connection refused");
        var ex = new LoadBalancerException("failed", cause);
        assertSame(cause, ex.getCause());
    }

    // ---- Integration: registry + balancer ----

    @Test
    public void integrationShouldSelectInstanceViaRegistry() {
        var registry = new LoadBalancerRegistry("round-robin");
        var instances = List.of(
                new ServiceInstance("order", "h1", 8080),
                new ServiceInstance("order", "h2", 8080),
                new ServiceInstance("order", "h3", 8080));

        var lb = registry.getOrCreate("order");
        var selected = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            selected.add(lb.choose(instances).getHost());
        }
        assertEquals("h1", selected.get(0));
        assertEquals("h2", selected.get(1));
        assertEquals("h3", selected.get(2));
    }
}
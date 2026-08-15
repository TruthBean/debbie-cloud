# debbie-loadbalancer

> Spring Cloud LoadBalancer like client-side load balancing module — **without Spring**.

## 概述

`debbie-loadbalancer` 是基于 debbie 框架的客户端负载均衡模块，提供类似 Spring Cloud LoadBalancer 的功能，但完全不依赖任何 Spring 相关框架。它仅使用 `debbie-core`、`debbie-mvc` 和 JDK 内置功能。

### 核心特性

- **多种负载均衡策略**：轮询（Round Robin）、随机（Random）、加权轮询（Weighted Round Robin）、最少连接（Least Connections）
- **健康检查过滤**：自动跳过不健康的实例
- **按服务独立管理**：每个服务拥有独立的负载均衡器实例，状态互不影响
- **SPI 自动装配**：通过 `DebbieModuleStarter` SPI 机制自动启动
- **可扩展**：实现 `LoadBalancer` 接口即可自定义策略

## 架构

```
com.truthbean.debbie.loadbalancer
├── LoadBalancer               — 负载均衡器接口
├── LoadBalancerFactory        — 策略工厂
├── LoadBalancerRegistry       — 按服务名管理负载均衡器
├── LoadBalancerConfiguration  — 配置类（prefix: debbie.loadbalancer）
├── LoadBalancerModuleStarter  — SPI 模块启动器
├── LoadBalancerException      — 异常类
├── ServiceInstance            — 服务实例模型
└── strategy
    ├── RoundRobinLoadBalancer          — 轮询策略
    ├── RandomLoadBalancer              — 随机策略
    ├── WeightedRoundRobinLoadBalancer  — 加权轮询策略
    └── LeastConnectionsLoadBalancer    — 最少连接策略
```

## 负载均衡策略

| 策略 | 名称 | 说明 |
|------|------|------|
| 轮询 | `round-robin` | 按顺序依次选择实例，默认策略 |
| 随机 | `random` | 随机选择一个健康实例 |
| 加权轮询 | `weighted-round-robin` | 按实例权重比例分配请求 |
| 最少连接 | `least-connections` | 选择当前活跃连接数最少的实例 |

## 配置

所有配置项前缀为 `debbie.loadbalancer`。

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.loadbalancer.enable` | `true` | 是否启用模块 |
| `debbie.loadbalancer.default-strategy` | `round-robin` | 默认负载均衡策略 |
| `debbie.loadbalancer.health-check.enable` | `false` | 是否启用健康检查 |
| `debbie.loadbalancer.health-check.interval` | `10000` | 健康检查间隔（毫秒） |
| `debbie.loadbalancer.retry.enable` | `true` | 是否启用重试 |
| `debbie.loadbalancer.retry.max-attempts` | `3` | 最大重试次数 |
| `debbie.loadbalancer.sticky.enable` | `false` | 是否启用粘性会话 |
| `debbie.loadbalancer.cache.enable` | `true` | 是否缓存实例列表 |
| `debbie.loadbalancer.cache.ttl` | `30000` | 缓存过期时间（毫秒） |

## 使用示例

### 编程式使用

```java
// 创建负载均衡器
var lb = LoadBalancerFactory.create("round-robin");

// 准备实例列表
var instances = List.of(
    new ServiceInstance("order", "10.0.0.1", 8080),
    new ServiceInstance("order", "10.0.0.2", 8080),
    new ServiceInstance("order", "10.0.0.3", 8080)
);

// 选择实例
var selected = lb.choose(instances);
System.out.println(selected.getUrl()); // http://10.0.0.1:8080
```

### 加权轮询

```java
var lb = LoadBalancerFactory.create("weighted-round-robin");

var i1 = new ServiceInstance("order", "10.0.0.1", 8080);
i1.setWeight(5);  // 5/8 的请求
var i2 = new ServiceInstance("order", "10.0.0.2", 8080);
i2.setWeight(3);  // 3/8 的请求

var selected = lb.choose(List.of(i1, i2));
```

### 最少连接

```java
var lb = LoadBalancerFactory.create("least-connections");

var i1 = new ServiceInstance("order", "10.0.0.1", 8080);
i1.setActiveConnections(10);
var i2 = new ServiceInstance("order", "10.0.0.2", 8080);
i2.setActiveConnections(3);

// 将选择 10.0.0.2（连接数更少）
var selected = lb.choose(List.of(i1, i2));
```

### 通过 Registry 管理

```java
var registry = new LoadBalancerRegistry("round-robin");

// 每个服务拥有独立的负载均衡器
var orderLb = registry.getOrCreate("order-service");
var paymentLb = registry.getOrCreate("payment-service");

// 选择实例
var selected = orderLb.choose(orderInstances);
```

### 自定义策略

```java
public class MyLoadBalancer implements LoadBalancer {
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        // 自定义选择逻辑
        return instances.get(0);
    }

    @Override
    public String name() {
        return "my-strategy";
    }
}
```

## ServiceInstance 属性

| 属性 | 说明 |
|------|------|
| `serviceName` | 服务名称 |
| `instanceId` | 实例 ID |
| `host` | 主机地址 |
| `port` | 端口 |
| `scheme` | 协议（http/https） |
| `weight` | 权重（用于加权轮询） |
| `activeConnections` | 当前活跃连接数（用于最少连接） |
| `healthy` | 健康状态 |
| `metadata` | 元数据（zone、region 等） |

## 依赖

- `debbie-core` — 核心框架
- `debbie-mvc` — MVC 框架

**不依赖任何 Spring 相关框架。**

## 测试

```bash
mvn -f cloud/loadbalancer/pom.xml test
```

53 个单元测试覆盖：ServiceInstance 模型、四种负载均衡策略、工厂、注册表、配置、异常、集成测试。

## 许可证

Mulan PSL v2
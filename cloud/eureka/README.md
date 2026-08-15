# debbie-eureka

> Spring Cloud Netflix Eureka like service registry and discovery module — **without Spring**.

## 概述

`debbie-eureka` 是基于 debbie 框架的服务注册与发现模块，提供类似 Spring Cloud Netflix Eureka 的功能，但完全不依赖任何 Spring 相关框架。它仅使用 `debbie-core`、`debbie-mvc` 和 JDK 内置功能（`java.net.http.HttpClient`）。

### 核心特性

- **Eureka Server**：内存服务注册表，支持实例注册、心跳续约、注销、状态更新
- **自动驱逐**：定时清理超时未续约的实例
- **Eureka Client**：HTTP 客户端，支持服务注册、心跳、发现
- **自动注册**：启动时自动将当前实例注册到 Eureka Server
- **心跳调度**：定时发送心跳，续约失败时自动重新注册
- **SPI 自动装配**：通过 `DebbieModuleStarter` SPI 机制自动启动

## 架构

```
com.truthbean.debbie.eureka
├── EurekaException          — 异常类
├── EurekaJson               — 内置 JSON 工具（不依赖外部 JSON 库）
├── EurekaConfiguration      — 配置类（prefix: debbie.eureka）
├── EurekaModuleStarter      — SPI 模块启动器
├── model
│   ├── InstanceInfo         — 服务实例模型
│   └── ApplicationInfo      — 应用模型（含多实例）
├── server
│   ├── EurekaServerRegistry — 内存注册表（含驱逐定时器）
│   └── EurekaServerEndpoint — HTTP REST 端点
└── client
    ├── EurekaClient              — Eureka HTTP 客户端
    └── EurekaHeartbeatScheduler  — 心跳定时调度器
```

## 配置

所有配置项前缀为 `debbie.eureka`。

### 通用配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.eureka.enable` | `true` | 是否启用 eureka 模块 |

### Server 配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.eureka.server.enable` | `false` | 是否启用 Eureka Server |
| `debbie.eureka.server.prefix` | `/eureka` | REST API 路径前缀 |
| `debbie.eureka.server.eviction-timeout` | `90000` | 实例超时驱逐时间（毫秒） |
| `debbie.eureka.server.eviction-interval` | `60000` | 驱逐检查间隔（毫秒） |

### Client 配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.eureka.client.enable` | `true` | 是否启用 Eureka Client |
| `debbie.eureka.client.server-url` | `http://localhost:8761` | Eureka Server 地址 |
| `debbie.eureka.client.prefix` | `/eureka` | Server REST API 路径前缀 |
| `debbie.eureka.client.service-name` | — | 服务名称（用于自动注册） |
| `debbie.eureka.client.instance-id` | — | 实例 ID（不设则自动生成） |
| `debbie.eureka.client.host` | — | 实例主机名（不设则自动检测） |
| `debbie.eureka.client.port` | `8080` | 实例端口 |
| `debbie.eureka.client.secure-port` | `0` | HTTPS 端口 |
| `debbie.eureka.client.heartbeat-interval` | `30000` | 心跳间隔（毫秒） |
| `debbie.eureka.client.connect-timeout` | `5000` | 连接超时（毫秒） |
| `debbie.eureka.client.read-timeout` | `10000` | 读取超时（毫秒） |
| `debbie.eureka.client.auto-register` | `true` | 是否自动注册到 Server |

## REST API

Eureka Server 暴露以下 REST 端点（前缀可配置，默认 `/eureka`）：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/{prefix}/apps` | 列出所有应用 |
| GET | `/{prefix}/apps/{appName}` | 获取指定应用的实例列表 |
| POST | `/{prefix}/apps/{appName}` | 注册实例（body 为 JSON） |
| PUT | `/{prefix}/apps/{appName}/{instanceId}` | 心跳续约 |
| DELETE | `/{prefix}/apps/{appName}/{instanceId}` | 注销实例 |
| PUT | `/{prefix}/apps/{appName}/{instanceId}/status?value=UP` | 更新实例状态 |

### 注册请求示例

```json
{
  "appName": "ORDER-SERVICE",
  "instanceId": "order-1",
  "hostName": "192.168.1.10",
  "ipAddr": "192.168.1.10",
  "port": 8080,
  "status": "UP"
}
```

## 使用示例

### 作为 Eureka Server

在 `application.properties` 中配置：

```properties
debbie.eureka.enable=true
debbie.eureka.server.enable=true
debbie.eureka.server.prefix=/eureka
```

### 作为 Eureka Client

```properties
debbie.eureka.enable=true
debbie.eureka.client.enable=true
debbie.eureka.client.server-url=http://eureka-server:8761
debbie.eureka.client.service-name=order-service
debbie.eureka.client.port=8080
debbie.eureka.client.auto-register=true
```

### 编程式使用

```java
// 创建 Client
var client = new EurekaClient("http://localhost:8761", "/eureka");

// 注册实例
var instance = new InstanceInfo("ORDER-SERVICE", "order-1", "192.168.1.10", 8080);
client.register(instance);

// 发送心跳
client.renew("ORDER-SERVICE", "order-1");

// 发现服务
var instances = client.getUpInstances("ORDER-SERVICE");

// 注销
client.cancel("ORDER-SERVICE", "order-1");
```

## 实例状态

| 状态 | 说明 |
|------|------|
| `UP` | 实例正常运行，可接收流量 |
| `DOWN` | 实例不可用 |
| `STARTING` | 实例正在启动 |
| `OUT_OF_SERVICE` | 实例已下线，不接收流量 |

## 依赖

- `debbie-core` — 核心框架
- `debbie-mvc` — MVC 框架（用于 Server HTTP 端点）
- `java.net.http` — JDK 内置 HTTP 客户端

**不依赖任何 Spring 相关框架。**

## 测试

```bash
mvn -f cloud/eureka/pom.xml test
```

41 个单元测试覆盖：JSON 解析、模型类、注册表操作、驱逐机制、配置、客户端、心跳调度器。

## 许可证

Mulan PSL v2
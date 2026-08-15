# debbie-etcd

> Spring Cloud Etcd like service discovery and configuration module — **without Spring**.

## 概述

`debbie-etcd` 是基于 debbie 框架的 etcd 集成模块，提供类似 Spring Cloud Etcd 的功能，但完全不依赖任何 Spring 相关框架。它仅使用 `debbie-core`、`debbie-mvc` 和 JDK 内置功能（`java.net.http.HttpClient`）。

### 核心特性

- **etcd v3 API**：通过 HTTP/gRPC 网关与 etcd 交互，支持 base64 编解码
- **键值操作**：put、get、getRange、getByPrefix、delete、deleteByPrefix
- **租约管理**：grantLease、keepAlive、revokeLease，支持带租约的键值
- **服务发现**：服务注册、注销、实例查询、健康实例过滤
- **配置管理**：从 etcd 读取应用配置并注入到环境变量
- **健康检查**：检测 etcd 可用性和集群状态
- **SPI 自动装配**：通过 `DebbieModuleStarter` SPI 机制自动启动

## 架构

```
com.truthbean.debbie.etcd
├── EtcdClient              — etcd v3 HTTP 客户端
├── EtcdProperties          — 连接属性
├── EtcdConfiguration       — 配置类（prefix: debbie.etcd）
├── EtcdModuleStarter       — SPI 模块启动器
├── EtcdHealthIndicator     — 健康检查
├── EtcdException           — 异常类
├── EtcdJson                — 内置 JSON 工具
├── config
│   └── EtcdConfigClient    — 配置管理客户端
└── discovery
    ├── EtcdServiceDiscovery  — 服务发现
    └── EtcdServiceInstance   — 服务实例模型
```

## 配置

所有配置项前缀为 `debbie.etcd`。

### 连接配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.etcd.enable` | `true` | 是否启用模块 |
| `debbie.etcd.host` | `localhost` | etcd 主机地址 |
| `debbie.etcd.port` | `2379` | etcd 端口 |
| `debbie.etcd.scheme` | `http` | 协议（http/https） |
| `debbie.etcd.username` | — | 用户名（可选） |
| `debbie.etcd.password` | — | 密码（可选） |
| `debbie.etcd.prefix` | — | 键前缀 |
| `debbie.etcd.connect-timeout` | `5000` | 连接超时（毫秒） |
| `debbie.etcd.read-timeout` | `10000` | 读取超时（毫秒） |

### 服务发现配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.etcd.discovery.enable` | `true` | 是否启用服务发现 |

### 配置管理配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.etcd.config.enable` | `false` | 是否启用配置管理 |
| `debbie.etcd.config.app-name` | `application` | 应用名称 |
| `debbie.etcd.config.profile` | `default` | 配置 profile |
| `debbie.etcd.config.fail-fast` | `false` | 配置加载失败是否中断启动 |

### 健康检查配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.etcd.health.enable` | `true` | 是否启用健康检查 |

## etcd v3 API 映射

| 操作 | etcd API 端点 | 说明 |
|------|---------------|------|
| put | `PUT /v3/kv/put` | 设置键值 |
| get | `POST /v3/kv/range` | 获取键值 |
| getRange | `POST /v3/kv/range` | 获取键值范围 |
| delete | `POST /v3/kv/deleterange` | 删除键值 |
| grantLease | `POST /v3/lease/grant` | 创建租约 |
| keepAlive | `POST /v3/lease/keepalive` | 续约 |
| revokeLease | `POST /v3/lease/revoke` | 撤销租约 |
| getClusterStatus | `POST /v3/cluster/status` | 集群状态 |

## 使用示例

### 键值操作

```java
var client = new EtcdClient(EtcdProperties.of("localhost", 2379));

// 设置键值
client.put("my-key", "my-value");

// 获取键值
var value = client.get("my-key");

// 按前缀获取
var entries = client.getByPrefix("my-");

// 删除
client.delete("my-key");
```

### 租约管理

```java
var client = new EtcdClient(EtcdProperties.of("localhost", 2379));

// 创建 60 秒 TTL 的租约
long leaseId = client.grantLease(60);

// 带租约写入（键会在租约过期后自动删除）
client.putWithLease("ephemeral-key", "value", leaseId);

// 续约
client.keepAlive(leaseId);

// 撤销租约
client.revokeLease(leaseId);
```

### 服务发现

```java
var client = new EtcdClient(EtcdProperties.of("localhost", 2379));
var discovery = new EtcdServiceDiscovery(client);

// 注册服务实例
var instance = new EtcdServiceInstance("order-service", "order-1", "10.0.0.1", 8080);
discovery.register(instance);

// 带租约注册（实例自动注销）
long leaseId = client.grantLease(30);
discovery.registerWithLease(instance, leaseId);

// 查询实例
var instances = discovery.getInstances("order-service");
var healthy = discovery.getHealthyInstances("order-service");

// 注销
discovery.deregister("order-service", "order-1");
```

### 配置管理

```java
var client = new EtcdClient(EtcdProperties.of("localhost", 2379));
var configClient = new EtcdConfigClient(client);

// 写入配置
configClient.put("my-app", "production", "db.url", "jdbc:postgresql://db:5432/myapp");

// 读取配置
var dbUrl = configClient.get("my-app", "production", "db.url");

// 读取所有配置
var allConfig = configClient.getConfig("my-app", "production");
```

### 健康检查

```java
var client = new EtcdClient(EtcdProperties.of("localhost", 2379));
var health = new EtcdHealthIndicator(client);
var status = health.check();
if (status.isHealthy()) {
    // etcd 可用
}
```

## 服务注册键结构

服务实例存储在 etcd 中的键结构：

```
/services/{serviceName}/{instanceId} = host:port?scheme=https&zone=us-east-1
```

## 配置存储键结构

应用配置存储在 etcd 中的键结构：

```
/config/{appName}/{profile}/{key} = value
```

## 依赖

- `debbie-core` — 核心框架
- `debbie-mvc` — MVC 框架
- `java.net.http` — JDK 内置 HTTP 客户端

**不依赖任何 Spring 相关框架。**

## 测试

```bash
mvn -f cloud/etcd/pom.xml test
```

41 个单元测试覆盖：JSON 解析、Properties、Configuration、Exception、ServiceInstance 序列化/反序列化、Client（不可达场景）、ServiceDiscovery、ConfigClient、HealthIndicator。

## 许可证

Mulan PSL v2
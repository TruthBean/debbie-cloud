# debbie-consul

> truthbean debbie consul framework — a [Spring Cloud Consul](https://spring.io/projects/spring-cloud-consul) like module for service discovery, distributed configuration and health checking **without any Spring dependency**.

## 概述

`debbie-consul` 提供与 [HashiCorp Consul](https://www.consul.io/) 集成的三大能力：

| 能力 | 对应 Spring Cloud Consul | 说明 |
|------|--------------------------|------|
| **服务注册发现** | spring-cloud-consul-discovery | 服务注册、注销、健康实例查询 |
| **分布式配置** | spring-cloud-consul-config | 从 Consul KV Store 读取配置并注入环境 |
| **健康检查** | spring-cloud-consul-discovery (health) | Consul Agent 可达性检查 |

仅依赖 `debbie-core` 和 `debbie-mvc`，使用 JDK 内置 `java.net.http.HttpClient` 与 Consul Agent HTTP API 通信，**不引入任何 Spring 框架**。

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-consul</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置

```properties
# Consul Agent 连接
debbie.consul.enable=true
debbie.consul.host=localhost
debbie.consul.port=8500
debbie.consul.scheme=http
debbie.consul.token=                    # ACL token（可选）

# 服务注册发现
debbie.consul.discovery.enable=true
debbie.consul.discovery.register=true
debbie.consul.discovery.health-check-path=/actuator/health
debbie.consul.discovery.heartbeat-interval=10s
debbie.consul.discovery.deregister-critical-after=30s

# 分布式配置
debbie.consul.config.enable=true
debbie.consul.config.prefix=config
debbie.consul.config.profile-separator=/
debbie.consul.config.watch=false
debbie.consul.config.fail-fast=false

# 当前服务信息
debbie.consul.service.name=my-application
debbie.consul.service.address=10.0.0.1
debbie.consul.service.port=8080
```

### 3. 服务注册

模块启动时自动将当前服务注册到 Consul：

```
PUT /v1/agent/service/register
{
  "ID": "my-application-8080",
  "Name": "my-application",
  "Address": "10.0.0.1",
  "Port": 8080,
  "Check": {
    "HTTP": "http://10.0.0.1:8080/actuator/health",
    "Interval": "10s",
    "Timeout": "5s",
    "DeregisterCriticalServiceAfter": "30s"
  }
}
```

应用关闭时自动注销。

### 4. 服务发现

```java
// 从 debbie 容器获取 DiscoveryClient
ConsulDiscoveryClient discoveryClient = applicationContext
    .getGlobalBeanFactory()
    .factory(ConsulDiscoveryClient.class);

// 查询健康的服务实例
List<ConsulServiceInstance> instances = discoveryClient.getInstances("order-service");

// 随机选择一个实例（简单负载均衡）
ConsulServiceInstance instance = discoveryClient.getOneInstanceRandom("order-service");

// 列出所有服务
Map<String, List<ConsulServiceInstance>> all = discoveryClient.getAllServices();
```

### 5. 分布式配置

启用 `debbie.consul.config.enable=true` 后，模块自动从 Consul KV Store 拉取配置并注入到 `EnvironmentDepositoryHolder`。

KV 路径约定：
```
config/application/          # 所有应用共享配置
config/application/dev/      # dev profile 共享配置
config/my-application/       # my-application 专属配置
config/my-application/dev/   # my-application dev profile 专属配置
```

也可手动操作 KV：

```java
ConsulConfigClient configClient = applicationContext
    .getGlobalBeanFactory()
    .factory(ConsulConfigClient.class);

// 读取配置
Map<String, String> config = configClient.getConfig("my-application", "dev");

// 设置 KV
configClient.setValue("config/my-application/server.port", "9090");

// 读取单个值
String value = configClient.getValue("config/my-application/server.port");
```

### 6. 健康检查

```java
ConsulHealthIndicator health = applicationContext
    .getGlobalBeanFactory()
    .factory(ConsulHealthIndicator.class);

ConsulHealthIndicator.HealthStatus status = health.check();
// status.isHealthy() → true/false
// status.getMessage() → "consul agent reachable, leader=10.0.0.1:8300"
```

## 架构

```
                    ┌──────────────────┐
                    │  Consul Agent    │
                    │  (HTTP API :8500)│
                    └──────┬───────────┘
                           │
                    ┌──────▼───────────┐
                    │   ConsulClient    │  (JDK HttpClient)
                    │  register/dereg   │
                    │  health/service   │
                    │  KV get/set/del   │
                    └──────┬───────────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
  ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐
  │ ServiceReg  │  │ ConfigClient│  │ HealthInd   │
  │ Discovery   │  │ ConfigSource│  │             │
  └─────────────┘  └─────────────┘  └─────────────┘
         │                 │                 │
         └─────────────────┼─────────────────┘
                           │
                    ┌──────▼───────────┐
                    │ ConsulModule     │
                    │ Starter (SPI)    │
                    └──────────────────┘
```

## Consul HTTP API 覆盖

| API | 方法 | 对应方法 |
|-----|------|---------|
| `/v1/agent/service/register` | PUT | `ConsulClient.registerService()` |
| `/v1/agent/service/deregister/:id` | PUT | `ConsulClient.deregisterService()` |
| `/v1/health/service/:name?passing=true` | GET | `ConsulClient.getHealthyInstances()` |
| `/v1/health/service/:name` | GET | `ConsulClient.getAllInstances()` |
| `/v1/catalog/services` | GET | `ConsulClient.getAllServices()` |
| `/v1/kv/:key?raw=true` | GET | `ConsulClient.getKeyValue()` |
| `/v1/kv/:prefix?recurse=true` | GET | `ConsulClient.getKeyValuesRecursive()` |
| `/v1/kv/:key` | PUT | `ConsulClient.setKeyValue()` |
| `/v1/kv/:key` | DELETE | `ConsulClient.deleteKeyValue()` |
| `/v1/kv/:prefix?recurse=true` | DELETE | `ConsulClient.deleteKeyValuesRecursive()` |
| `/v1/status/leader` | GET | `ConsulClient.getLeader()` |
| `/v1/agent/self` | GET | `ConsulClient.getAgentInfo()` |

## API 详解

### ConsulClient

低层 HTTP 客户端，直接映射 Consul API：

```java
ConsulClient client = new ConsulClient("localhost", 8500, "http");

// 服务注册
var reg = new ConsulServiceRegistration();
reg.setId("order-8080");
reg.setName("order-service");
reg.setAddress("10.0.0.1");
reg.setPort(8080);
reg.setTags(List.of("v1", "primary"));
reg.setCheckHttp("http://10.0.0.1:8080/actuator/health");
reg.setCheckInterval("10s");
client.registerService(reg);

// 查询健康实例
List<ConsulServiceInstance> instances = client.getHealthyInstances("order-service");

// KV 操作
client.setKeyValue("config/order/db.url", "jdbc:postgresql://db:5432/order");
String value = client.getKeyValue("config/order/db.url");
Map<String, String> all = client.getKeyValuesRecursive("config/order/");

// Agent 状态
String leader = client.getLeader();
boolean available = client.isAgentAvailable();
```

### ConsulServiceRegistry

高层服务注册管理：

```java
var registry = new ConsulServiceRegistry(client);
registry.register(reg);     // 注册
registry.deregister();      // 注销当前服务
registry.isRegistered();    // 是否已注册
```

### ConsulDiscoveryClient

服务发现客户端，支持简单负载均衡：

```java
var discovery = new ConsulDiscoveryClient(client);
var instances = discovery.getInstances("order-service");  // 仅健康实例
var one = discovery.getOneInstance("order-service");       // 第一个
var random = discovery.getOneInstanceRandom("order-service"); // 随机
```

### ConsulConfigClient

从 Consul KV 读取分层配置：

```java
var configClient = new ConsulConfigClient(client, "config", "/");
// 读取 config/my-app/ 和 config/my-app/dev/ 下的配置
Map<String, String> config = configClient.getConfig("my-app", "dev");
```

### SimpleJson

内置轻量 JSON 解析器（非通用 JSON 库，仅满足 Consul API 解析需求）：

```java
Map<String, Object> obj = SimpleJson.parseObject("{\"name\":\"test\"}");
List<Object> arr = SimpleJson.parseArray("[1, 2, 3]");
String json = SimpleJson.toJsonString(Map.of("key", "value"));
```

## 配置项

### Consul Agent 连接

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.consul.enable` | true | 是否启用 consul 模块 |
| `debbie.consul.host` | localhost | Consul Agent 地址 |
| `debbie.consul.port` | 8500 | Consul Agent 端口 |
| `debbie.consul.scheme` | http | http 或 https |
| `debbie.consul.token` | (空) | ACL Token |
| `debbie.consul.connect-timeout` | 5000 | 连接超时（毫秒） |
| `debbie.consul.read-timeout` | 10000 | 读取超时（毫秒） |

### 服务发现

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.consul.discovery.enable` | true | 是否启用服务发现 |
| `debbie.consul.discovery.register` | true | 是否注册当前服务 |
| `debbie.consul.discovery.prefer-ip-address` | false | 注册 IP 而非主机名 |
| `debbie.consul.discovery.heartbeat-interval` | 10s | 健康检查间隔 |
| `debbie.consul.discovery.deregister-critical-after` | 30s | 临界状态后注销时间 |
| `debbie.consul.discovery.health-check-path` | /actuator/health | 健康检查路径 |

### 分布式配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.consul.config.enable` | false | 是否启用配置中心 |
| `debbie.consul.config.prefix` | config | KV 前缀 |
| `debbie.consul.config.profile-separator` | / | Profile 分隔符 |
| `debbie.consul.config.watch` | false | 是否监听配置变更 |
| `debbie.consul.config.watch-interval` | 5000 | 监听间隔（毫秒） |
| `debbie.consul.config.fail-fast` | false | Agent 不可达时是否快速失败 |

### 当前服务

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.consul.service.name` | (空) | 服务名称 |
| `debbie.consul.service.address` | (空) | 服务地址 |
| `debbie.consul.service.port` | 8080 | 服务端口 |

## 自动装配

模块通过 `META-INF/services/com.truthbean.debbie.boot.DebbieModuleStarter` SPI 自动注册：

```
com.truthbean.debbie.consul.ConsulModuleStarter
```

启动流程：
1. 读取 `ConsulConfiguration`（前缀 `debbie.consul`）
2. 创建 `ConsulClient` 并检测 Agent 可达性
3. 若 `health.enable=true`：创建 `ConsulHealthIndicator`
4. 若 `discovery.enable=true`：创建 `ConsulServiceRegistry` + `ConsulDiscoveryClient`
5. 若 `discovery.register=true`：注册当前服务到 Consul
6. 若 `config.enable=true`：创建 `ConsulConfigClient`，从 KV 拉取配置注入 `EnvironmentDepositoryHolder`
7. 关闭时自动注销服务

## 模块结构

```
cloud/consul/
├── pom.xml
├── src/main/java/
│   ├── module-info.java
│   └── com/truthbean/debbie/consul/
│       ├── ConsulClient.java                    # Consul HTTP API 客户端
│       ├── ConsulConfiguration.java             # debbie 配置类
│       ├── ConsulModuleStarter.java             # 模块启动器
│       ├── ConsulException.java                 # 异常
│       ├── discovery/
│       │   ├── ConsulServiceRegistration.java   # 服务注册模型
│       │   ├── ConsulServiceInstance.java       # 服务实例模型
│       │   ├── ConsulServiceRegistry.java       # 服务注册管理
│       │   └── ConsulDiscoveryClient.java       # 服务发现客户端
│       ├── config/
│       │   ├── ConsulConfigClient.java          # KV 配置客户端
│       │   └── ConsulConfigSource.java          # 配置源
│       ├── health/
│       │   └── ConsulHealthIndicator.java       # 健康检查
│       └── json/
│           └── SimpleJson.java                  # 轻量 JSON 解析器
└── src/test/java/
    ├── module-info.java
    └── com/truthbean/debbie/consul/test/
        └── ConsulTest.java                      # 24 个单元测试
```

## 与 Spring Cloud Consul 对比

| 特性 | Spring Cloud Consul | debbie-consul |
|------|---------------------|---------------|
| 服务注册 | ✅ | ✅ |
| 服务发现 | ✅ | ✅ |
| 健康检查 | ✅ | ✅ |
| KV 配置 | ✅ | ✅ |
| 配置监听 (watch) | ✅ | ✅ (可配置间隔轮询) |
| ACL Token | ✅ | ✅ |
| HTTPS/TLS | ✅ | ✅ (scheme=https) |
| Ribbon 负载均衡 | ✅ | ❌ (仅随机选择) |
| @ConsulDiscovery 注解 | ✅ | ❌ |
| Spring 依赖 | ✅ | ❌ |
| JPMS 模块化 | ❌ | ✅ |

## 测试

```bash
mvn -f cloud/consul/pom.xml test
```

24 个测试覆盖：
- JSON 解析（对象、数组、嵌套、转义、空值、数字）
- JSON 序列化（字符串、Map、List、转义）
- 配置默认值和 copy()
- 服务注册模型构建
- 服务实例状态判断
- 异常状态码和原因链
- 健康检查（不可达 Agent 场景）
- ConsulClient 不可达场景异常抛出

> 注：完整集成测试需要运行中的 Consul Agent（`consul agent -dev`）。

## 依赖

| 依赖 | 版本 | scope | 说明 |
|------|------|-------|------|
| `debbie-core` | `${truthbean.version}` | compile | 核心框架（IOC、配置、事件） |
| `debbie-mvc` | `${truthbean.version}` | compile | MVC 框架（HTTP 路由） |
| `debbie-test` | `${truthbean.version}` | test | 测试支持 |
| `truthbean-stdout-boot` | `${truthbean.version}` | test | 测试启动器 |

**无任何 Spring 依赖。** HTTP 通信使用 JDK 内置 `java.net.http.HttpClient`，JSON 解析使用内置 `SimpleJson`。

## License

Mulan PSL v2

## 作者

TruthBean / Rogar·Q (truthbean@outlook.com)
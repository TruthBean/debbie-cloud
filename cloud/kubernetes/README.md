# debbie-kubernetes

> truthbean debbie kubernetes framework — a module for service discovery, ConfigMap/Secret configuration and health checking.

## 概述

`debbie-kubernetes` 提供与 [Kubernetes](https://kubernetes.io/) 集成的三大能力：

| 能力 | 对应 Spring Cloud Kubernetes | 说明 |
|------|------------------------------|------|
| **服务发现** | spring-cloud-kubernetes-discovery | 通过 K8s API 查询 Service Endpoints |
| **分布式配置** | spring-cloud-kubernetes-config | 从 ConfigMap 和 Secret 读取配置注入环境 |
| **健康检查** | spring-cloud-kubernetes-discovery (health) | K8s API Server 可达性检查 |

仅依赖 `debbie-core` 和 `debbie-mvc`，使用 JDK 内置 `java.net.http.HttpClient` 与 Kubernetes API Server 通信，**不引入任何 Spring 框架**。

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-kubernetes</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 在 Kubernetes Pod 中运行

当应用部署在 Kubernetes Pod 中时，模块自动检测连接信息：

| 来源 | 环境变量/文件 | 说明 |
|------|-------------|------|
| API Server 地址 | `KUBERNETES_SERVICE_HOST` / `KUBERNETES_SERVICE_PORT` | 自动读取 |
| Service Account Token | `/var/run/secrets/kubernetes.io/serviceaccount/token` | 自动读取 |
| 命名空间 | `/var/run/secrets/kubernetes.io/serviceaccount/namespace` | 自动读取 |
| CA 证书 | `/var/run/secrets/kubernetes.io/serviceaccount/ca.crt` | 自动读取 |

### 3. 配置

```properties
# Kubernetes 连接（在 Pod 中可自动检测）
debbie.kubernetes.enable=true
debbie.kubernetes.auto-detect=true
debbie.kubernetes.host=                     # 覆盖自动检测的 API Server 地址
debbie.kubernetes.port=443
debbie.kubernetes.namespace=default
debbie.kubernetes.token=                    # Service Account Bearer Token

# 服务发现
debbie.kubernetes.discovery.enable=true

# 配置中心（ConfigMap + Secret）
debbie.kubernetes.config.enable=true
debbie.kubernetes.config.configmaps=app-config,db-config
debbie.kubernetes.config.secrets=db-credentials
debbie.kubernetes.config.fail-fast=false

# 健康检查
debbie.kubernetes.health.enable=true

# 当前服务名称（用于自动发现 ConfigMap/Secret）
debbie.kubernetes.service.name=my-application
```

### 4. 服务发现

```java
// 从容器获取 DiscoveryClient
KubernetesServiceDiscovery discovery = applicationContext
    .getGlobalBeanFactory()
    .factory(KubernetesServiceDiscovery.class);

// 查询服务的所有端点实例
List<KubernetesServiceInstance> instances = discovery.getInstances("order-service");

// 随机选择一个实例（简单负载均衡）
KubernetesServiceInstance instance = discovery.getOneInstanceRandom("order-service");

// 列出命名空间中的所有服务名称
List<String> serviceNames = discovery.getAllServiceNames();
```

### 5. ConfigMap 和 Secret 配置

启用 `debbie.kubernetes.config.enable=true` 后，模块自动从 ConfigMap 和 Secret 拉取配置并注入到 `EnvironmentDepositoryHolder`。

ConfigMap 示例：
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: default
data:
  server.port: "8080"
  db.url: "jdbc:postgresql://db:5432/myapp"
  log.level: "INFO"
```

Secret 示例：
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
  namespace: default
data:
  db.username: cG9zdGdyZXM=      # base64("postgres")
  db.password: c2VjcmV0          # base64("secret")
```

也可手动操作：
```java
KubernetesConfigClient configClient = applicationContext
    .getGlobalBeanFactory()
    .factory(KubernetesConfigClient.class);

// 读取 ConfigMap
Map<String, String> config = configClient.getConfigMap("app-config");

// 读取 Secret（自动 base64 解码）
Map<String, String> secret = configClient.getSecret("db-credentials");

// 按应用名和 profile 读取（app-config, app-config-dev, app, app-dev）
Map<String, String> all = configClient.getConfig("my-app", "dev");
```

### 6. 健康检查

```java
KubernetesHealthIndicator health = applicationContext
    .getGlobalBeanFactory()
    .factory(KubernetesHealthIndicator.class);

KubernetesHealthIndicator.HealthStatus status = health.check();
// status.isHealthy() → true/false
// status.getMessage() → "kubernetes api server reachable, version=v1.28.0"
```

## Kubernetes API 覆盖

| API | 方法 | 对应方法 |
|-----|------|---------|
| `/api/v1/namespaces/{ns}/services` | GET | `KubernetesClient.listServices()` |
| `/api/v1/namespaces/{ns}/services/{name}` | GET | `KubernetesClient.getService()` |
| `/api/v1/namespaces/{ns}/endpoints/{name}` | GET | `KubernetesClient.getEndpoints()` |
| `/api/v1/namespaces/{ns}/configmaps/{name}` | GET | `KubernetesClient.getConfigMap()` |
| `/api/v1/namespaces/{ns}/secrets/{name}` | GET | `KubernetesClient.getSecret()` |
| `/api/v1/namespaces/{ns}/pods/{name}` | GET | `KubernetesClient.getPod()` |
| `/version` | GET | `KubernetesClient.getVersion()` |

## API 详解

### KubernetesClient

低层 HTTP 客户端，直接映射 K8s API：

```java
var props = KubernetesProperties.of("k8s.example.com", 6443, "default", "bearer-token");
KubernetesClient client = new KubernetesClient(props);

// 服务
var service = client.getService("order-service");
var'var services = client.listServices();

// 端点
var endpoints = client.getEndpoints("order-service");

// ConfigMap
var config = client.getConfigMap("app-config");

// Secret（自动 base64 解码）
var secret = client.getSecret("db-credentials");

// 版本
var version = client.getVersion();
boolean available = client.isApiServerAvailable();
```

### KubernetesProperties

连接属性，支持自动检测和手动配置：

```java
// 自动检测（在 Pod 中）
var props = KubernetesProperties.autoDetect();

// 手动配置
var props = KubernetesProperties.of("host", 443, "namespace", "token");

// 检查
props.getBaseUrl();      // "https://host:443"
props.isInsidePod();     // 是否在 Pod 中运行
props.hasToken();        // 是否有认证 token
```

### KubernetesServiceDiscovery

服务发现客户端：

```java
var discovery = new KubernetesServiceDiscovery(client);
var instances = discovery.getInstances("order-service");
var one = discovery.getOneInstance("order-service");
var random = discovery.getOneInstanceRandom("order-service");
var names = discovery.getAllServiceNames();
```

### KubernetesConfigClient

配置客户端，支持 ConfigMap 和 Secret：

```java
var configClient = new KubernetesConfigClient(client);

// 单个 ConfigMap/Secret
var config = configClient.getConfigMap("app-config");
var secret = configClient.getSecret("db-credentials");

// 多个 ConfigMap/Secret
var all = configClient.getConfigFromConfigMaps("cm1", "cm2");
var allSecrets = configClient.getConfigFromSecrets("s1", "s2");

// 按应用名和 profile（自动查找 app, app-profile 的 ConfigMap 和 Secret）
var full = configClient.getConfig("my-app", "dev");
```

## 配置项

### 连接

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.kubernetes.enable` | true | 是否启用模块 |
| `debbie.kubernetes.auto-detect` | true | 是否自动检测 Pod 环境 |
| `debbie.kubernetes.host` | (自动) | API Server 地址 |
| `debbie.kubernetes.port` | 443 | API Server 端口 |
| `debbie.kubernetes.namespace` | default | 命名空间 |
| `debbie.kubernetes.token` | (自动) | Bearer Token |

### 服务发现

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.kubernetes.discovery.enable` | true | 是否启用服务发现 |

### 配置中心

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.kubernetes.config.enable` | false | 是否启用配置中心 |
| `debbie.kubernetes.config.configmaps` | (空) | ConfigMap 名称列表（逗号分隔） |
| `debbie.kubernetes.config.secrets` | (空) | Secret 名称列表（逗号分隔） |
| `debbie.kubernetes.config.fail-fast` | false | API 不可达时是否快速失败 |

### 健康检查

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.kubernetes.health.enable` | true | 是否启用健康检查 |

## 自动装配

模块通过 `META-INF/services/com.truthbean.debbie.boot.DebbieModuleStarter` SPI 自动注册：

```
com.truthbean.debbie.kubernetes.KubernetesModuleStarter
```

启动流程：
1. 读取 `KubernetesConfiguration`（前缀 `debbie.kubernetes`）
2. 自动检测或手动配置 `KubernetesProperties`
3. 创建 `KubernetesClient` 并检测 API Server 可达性
4. 若 `health.enable=true`：创建 `KubernetesHealthIndicator`
5. 若 `discovery.enable=true`：创建 `KubernetesServiceDiscovery`
6. 若 `config.enable=true`：创建 `KubernetesConfigClient`，从 ConfigMap/Secret 拉取配置注入 `EnvironmentDepositoryHolder`

## 模块结构

```
cloud/kubernetes/
├── pom.xml
├── src/main/java/
│   ├── module-info.java
│   └── com/truthbean/debbie/kubernetes/
│       ├── KubernetesClient.java                  # K8s API HTTP 客户端
│       ├── KubernetesProperties.java              # 连接属性（自动检测）
│       ├── KubernetesConfiguration.java           # debbie 配置类
│       ├── KubernetesModuleStarter.java           # 模块启动器
│       ├── KubernetesException.java               # 异常
│       ├── discovery/
│       │   ├── KubernetesServiceInstance.java     # 服务实例模型
│       │   └── KubernetesServiceDiscovery.java    # 服务发现客户端
│       ├── config/
│       │   ├── KubernetesConfigClient.java        # ConfigMap/Secret 配置客户端
│       │   ├── KubernetesConfigMapSource.java     # ConfigMap 配置源
│       │   └── KubernetesSecretSource.java        # Secret 配置源
│       ├── health/
│       │   └── KubernetesHealthIndicator.java     # 健康检查
│       └── json/
│           └── SimpleJson.java                    # 轻量 JSON 解析器
└── src/test/java/
    ├── module-info.java
    └── com/truthbean/debbie/kubernetes/test/
        └── KubernetesTest.java                    # 29 个单元测试
```

## 与 Spring Cloud Kubernetes 对比

| 特性 | Spring Cloud Kubernetes | debbie-kubernetes |
|------|------------------------|-------------------|
| 服务发现 (Endpoints) | ✅ | ✅ |
| ConfigMap 配置 | ✅ | ✅ |
| Secret 配置 | ✅ | ✅ |
| 健康检查 | ✅ | ✅ |
| Pod 自动检测 | ✅ | ✅ |
| Service Account Token | ✅ | ✅ |
| Ribbon 负载均衡 | ✅ | ❌ (仅随机选择) |
| ConfigMap Watch | ✅ | ❌ (可后续添加) |
| @KubernetesService 注解 | ✅ | ❌ |
| Spring 依赖 | ✅ | ❌ |
| JPMS 模块化 | ❌ | ✅ |

## 测试

```bash
mvn -f cloud/kubernetes/pom.xml test
```

29 个测试覆盖：
- JSON 解析（对象、数组、嵌套、空值、序列化）
- KubernetesProperties（手动配置、自动检测、URL 构建）
- KubernetesConfiguration（默认值、ConfigMap/Secret 名称解析、copy、toProperties）
- 服务实例模型（URL 构建、toString）
- 异常（状态码、原因链）
- 不可达场景（API Server、ConfigMap、Secret、Service、Discovery、Health）

> 注：完整集成测试需要运行中的 Kubernetes 集群（如 `minikube` 或 `kind`）。

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
# debbie-config

一个不依赖 Spring 框架的轻量级集中配置管理模块，灵感来自 [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/)，
基于 [debbie](https://github.com/TruthBean/debbie-cloud) 框架自身的 MVC、环境与模块体系实现。

提供 **Config Server**（配置服务器）和 **Config Client**（配置客户端）两部分：
Server 从后端仓库（默认文件系统，可扩展 Git / JDBC / Vault）读取配置并通过 HTTP 提供；
Client 在启动时从 Server 拉取配置并注入到本地 `EnvironmentDepositoryHolder`，支持运行时刷新。

---

## 目录

- [特性](#特性)
- [依赖关系](#依赖关系)
- [架构概览](#架构概览)
- [快速开始：Config Server](#快速开始config-server)
- [快速开始：Config Client](#快速开始config-client)
- [配置项](#配置项)
- [核心概念](#核心概念)
- [ConfigRepository 仓库抽象](#configrepository-仓库抽象)
- [自定义 ConfigRepository 实现](#自定义-configrepository-实现)
- [ConfigEncryptor 加密解密](#configencryptor-加密解密)
- [自定义 ConfigEncryptor 实现](#自定义-configencryptor-实现)
- [Config Server HTTP 端点](#config-server-http-端点)
- [Config Client 拉取与注入](#config-client-拉取与注入)
- [运行时刷新](#运行时刷新)
- [与 debbie-bus 联动](#与-debbie-bus-联动)
- [API 速查](#api-速查)
- [设计说明](#设计说明)
- [示例](#示例)

---

## 特性

- **零 Spring 依赖**：仅依赖 `debbie-core` 与 `debbie-mvc`，使用 debbie 自有的 `@Router`、`@GetRouter`、`@PropertiesConfiguration`、`DebbieModuleStarter` 等机制。
- **Server / Client 双角色**：同一模块即可作为配置服务器提供配置，也可作为客户端拉取配置，可独立开关、可同时运行。
- **JDK 原生 HTTP 客户端**：Client 使用 `java.net.http.HttpClient`（JDK 11+ 内置）拉取配置，无需额外依赖。
- **SPI 可插拔仓库**：`ConfigRepositoryFactory` 通过 `java.util.ServiceLoader` 发现，默认提供 `FileConfigRepository`（文件系统），可扩展 Git / JDBC / Vault 等。
- **SPI 可插拔加密**：`ConfigEncryptorFactory` 发现加密器，默认 `NoopConfigEncryptor`（不加密），可扩展 AES / RSA 等。支持 `{cipher}` 前缀自动解密。
- **多 Profile 支持**：按 `application → application-{profile} → {app} → {app}-{profile}` 顺序加载，后者覆盖前者。
- **JPMS 模块化**：自带 `module-info.java`，模块名 `com.truthbean.debbie.config`。
- **自动装配**：通过 `DebbieModuleStarter` SPI 自动注册，引入依赖 + 配置即生效。
- **Fail-Fast**：Client 可配置启动时拉取失败是否直接抛异常终止应用。

---

## 依赖关系

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-config</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

模块依赖图：

```
debbie-config
    ├── debbie-core  (transitive)
    └── debbie-mvc   (transitive)
```

不引入任何 `spring-*`、`spring-boot-*` 或 `spring-cloud-*` 依赖。HTTP 客户端使用 JDK 内置的 `java.net.http.HttpClient`。

---

## 架构概览

```
                        ┌─────────────────────────────────────────┐
                        │           debbie-config                 │
                        │                                         │
  配置文件仓库          │  ┌─────────────┐    SPI    ┌──────────┐  │
  (file/git/jdbc) ─────►│  │ Repository  │◄─────────│ Factory  │  │
                        │  └──────┬──────┘          └──────────┘  │
                        │         │                               │
                        │  ┌──────▼──────┐    SPI    ┌──────────┐  │
                        │  │  Encryptor  │◄─────────│ Factory  │  │
                        │  └──────┬──────┘          └──────────┘  │
                        │         │                               │
                        │  ┌──────▼──────┐                       │
                        │  │   Server    │── HTTP ──►  Client    │
                        │  │  (Endpoint) │           (HttpClient) │
                        │  └─────────────┘           └─────┬─────┘  │
                        └──────────────────────────────────┼───────┘
                                                           │
                                                           ▼
                                                EnvironmentDepositoryHolder
                                                   (本地环境注入)
```

---

## 快速开始：Config Server

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-config</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置

```properties
# 启用 config 模块
debbie.config.enable=true
# 启用配置服务器角色
debbie.config.server.enable=true
# 仓库类型（默认 file）
debbie.config.server.repository=file
# 配置文件根目录（相对于工作目录）
debbie.config.server.base-dir=config
# 默认标签（类似 git 分支）
debbie.config.server.default-label=master
# HTTP 端点前缀
debbie.config.server.prefix=/config
```

### 3. 准备配置文件

在 `config/master/` 目录下创建配置文件：

```
config/
└── master/
    ├── application.properties          # 全局默认配置
    ├── application-dev.properties      # dev 环境配置
    ├── order.properties                # order 应用配置
    └── order-dev.properties            # order 应用 dev 环境配置
```

示例 `order-dev.properties`：

```properties
order.max-amount=5000
order.feature.enabled=true
db.url=jdbc:mysql://dev-db:3306/order
```

### 4. 启动

```java
@DebbieBootApplication
public class ConfigServerApp {
    public static void main(String[] args) {
        DebbieApplication.run(ConfigServerApp.class, args);
    }
}
```

启动后可通过 HTTP 访问配置：

```bash
# 获取 order 应用 dev 环境的完整配置环境（JSON）
curl http://localhost:8888/config/order/dev

# 指定 label
curl http://localhost:8888/config/order/dev/master

# 获取扁平化 properties 格式
curl http://localhost:8888/config/order-dev.properties
```

---

## 快速开始：Config Client

### 1. 引入依赖（同上）

### 2. 配置

```properties
# 启用 config 模块
debbie.config.enable=true
# 启用配置客户端角色
debbie.config.client.enable=true
# 配置服务器地址
debbie.config.client.uri=http://localhost:8888/config
# 应用名称
debbie.config.client.name=order
# 环境 profile
debbie.config.client.profile=dev
# 标签（可选）
debbie.config.client.label=master
# 拉取失败是否终止启动
debbie.config.client.fail-fast=true
```

### 3. 启动

```java
@DebbieBootApplication
public class OrderServiceApp {
    public static void main(String[] args) {
        DebbieApplication.run(OrderServiceApp.class, args);
    }
}
```

应用启动时，`ConfigClient` 会自动从 `http://localhost:8888/config/order/dev/master` 拉取配置，
并将所有属性注入到本地 `EnvironmentDepositoryHolder`，后续可通过 `@PropertyInject` 或 `Environment` 获取。

---

## 配置项

所有配置项前缀为 `debbie.config`，通过 `ConfigConfiguration` 类承载。

### 通用

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.config.enable` | boolean | `true` | 是否启用 config 模块 |

### Server 端

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.config.server.enable` | boolean | `false` | 是否启用配置服务器 |
| `debbie.config.server.repository` | String | `file` | 仓库类型，对应 `ConfigRepositoryFactory.name()` |
| `debbie.config.server.base-dir` | String | `config` | 配置文件根目录 |
| `debbie.config.server.default-label` | String | `master` | 默认标签（如 git 分支） |
| `debbie.config.server.encrypt` | boolean | `false` | 是否启用加密解密 |
| `debbie.config.server.prefix` | String | `/config` | HTTP 端点前缀 |

### Client 端

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.config.client.enable` | boolean | `false` | 是否启用配置客户端 |
| `debbie.config.client.uri` | String | `http://localhost:8888/config` | 配置服务器地址 |
| `debbie.config.client.name` | String | `application` | 应用名称 |
| `debbie.config.client.profile` | String | `default` | 环境 profile |
| `debbie.config.client.label` | String | `null` | 标签（如 git 分支） |
| `debbie.config.client.connect-timeout` | int | `5000` | 连接超时（毫秒） |
| `debbie.config.client.read-timeout` | int | `10000` | 读取超时（毫秒） |
| `debbie.config.client.fail-fast` | boolean | `false` | 拉取失败是否终止启动 |

---

## 核心概念

### ConfigEnvironment（配置环境）

类似 Spring Cloud Config 的 `Environment`，包含：

| 字段 | 类型 | 说明 |
|------|------|------|
| `name` | String | 应用名称 |
| `profiles` | `List<String>` | 激活的 profile 列表 |
| `label` | String | 标签（如 git 分支） |
| `propertySources` | `List<ConfigPropertySource>` | 有序属性源列表 |

```java
var env = new ConfigEnvironment("order", List.of("dev"), "master");
env.addPropertySource(new ConfigPropertySource("order-dev.properties", Map.of(
    "order.max-amount", "5000",
    "db.url", "jdbc:mysql://dev-db:3306/order"
)));
```

**扁平化方法**：

- `asFlattenedMap()`：后者覆盖前者（Spring Cloud Config 语义）
- `asFlattenedMapFirstWins()`：前者优先

### ConfigPropertySource（属性源）

一个命名的 key-value 集合：

```java
var source = new ConfigPropertySource("order-dev.properties");
source.put("order.max-amount", "5000");
source.put("db.url", "jdbc:mysql://dev-db:3306/order");
```

---

## ConfigRepository 仓库抽象

`ConfigRepository` 是配置后端的抽象，`ConfigRepositoryFactory` 是其 SPI 工厂。

```
ConfigRepositoryFactory (SPI)
    │  name()        返回仓库名称（匹配 debbie.config.server.repository）
    │  support()     判断是否支持当前配置
    │  create()      创建 Repository 实例
    ▼
ConfigRepository
    │  name()        仓库名称
    │  findOne()     按 application/profile/label 查找配置环境
    ▼
FileConfigRepository (默认文件系统实现)
```

### 内置实现

| 名称 | 类 | 说明 |
|------|----|------|
| `file` | `FileConfigRepository` | 从文件系统读取，目录结构 `{base-dir}/{label}/` |

### 文件加载策略

对于应用 `order`、profile `dev`，`FileConfigRepository` 按以下顺序加载（后者覆盖前者）：

| 顺序 | 文件名 | 说明 |
|------|--------|------|
| 1 | `application.properties` | 全局默认 |
| 2 | `application-dev.properties` | 全局 dev 环境 |
| 3 | `order.properties` | 应用专属 |
| 4 | `order-dev.properties` | 应用专属 dev 环境 |

同时支持 `.yml` / `.yaml` 扩展名。

### SPI 发现机制

`ConfigRepositoryFactory` 通过 `java.util.ServiceLoader` 发现：

```
META-INF/services/com.truthbean.debbie.config.repository.ConfigRepositoryFactory
```

---

## 自定义 ConfigRepository 实现

以 JDBC 后端为例：

### 1. 实现 ConfigRepository

```java
package com.truthbean.debbie.config.repository.jdbc;

public class JdbcConfigRepository implements ConfigRepository {

    private final DataSource dataSource;

    public JdbcConfigRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String name() { return "jdbc"; }

    @Override
    public ConfigEnvironment findOne(String application, String profile, String label) {
        var env = new ConfigEnvironment(application, List.of(profile), label);
        try (var conn = dataSource.getConnection()) {
            var sql = "SELECT prop_key, prop_value FROM config_properties "
                    + "WHERE application = ? AND profile = ? AND label = ?";
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, application);
                ps.setString(2, profile);
                ps.setString(3, label);
                var props = new LinkedHashMap<String, String>();
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        props.put(rs.getString("prop_key"), rs.getString("prop_value"));
                    }
                }
                if (!props.isEmpty()) {
                    env.addPropertySource(new ConfigPropertySource("jdbc-" + application, props));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return env;
    }
}
```

### 2. 实现 ConfigRepositoryFactory

```java
package com.truthbean.debbie.config.repository.jdbc;

public class JdbcConfigRepositoryFactory implements ConfigRepositoryFactory {
    @Override
    public String name() { return "jdbc"; }

    @Override
    public boolean support(ConfigConfiguration config) {
        return config != null && "jdbc".equalsIgnoreCase(config.getServerRepository());
    }

    @Override
    public ConfigRepository create(ConfigConfiguration config) {
        // 从配置创建 DataSource
        var dataSource = createDataSource(config);
        return new JdbcConfigRepository(dataSource);
    }
}
```

### 3. 注册 SPI

在 `META-INF/services/com.truthbean.debbie.config.repository.ConfigRepositoryFactory` 中：

```
com.truthbean.debbie.config.repository.jdbc.JdbcConfigRepositoryFactory
```

### 4. 配置启用

```properties
debbie.config.server.repository=jdbc
```

---

## ConfigEncryptor 加密解密

`ConfigEncryptor` 接口提供配置值的加密与解密能力。

```
ConfigEncryptorFactory (SPI)
    │
    ▼
ConfigEncryptor
    │  encrypt()     加密
    │  decrypt()     解密
    │  isEncrypted() 判断是否 {cipher} 前缀
    │  decryptIfEncrypted()  自动解密
    ▼
NoopConfigEncryptor (默认不加密)
```

### 加密值格式

以 `{cipher}` 前缀标识加密值，Config Server 在返回配置前自动解密：

```properties
# 加密的数据库密码
db.password={cipher}AQB1j8k7...
```

### 内置实现

| 名称 | 类 | 说明 |
|------|----|------|
| `noop` | `NoopConfigEncryptor` | 不加密，原样返回 |

---

## 自定义 ConfigEncryptor 实现

以 AES 对称加密为例：

```java
package com.truthbean.debbie.config.encrypt.aes;

public class AesConfigEncryptor implements ConfigEncryptor {

    private final SecretKey key;
    private final Cipher cipher;

    public AesConfigEncryptor(String keyStr) throws Exception {
        this.key = new SecretKeySpec(keyStr.getBytes(StandardCharsets.UTF_8), "AES");
        this.cipher = Cipher.getInstance("AES");
    }

    @Override
    public String name() { return "aes"; }

    @Override
    public String encrypt(String plain) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            var encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            var decoded = Base64.getDecoder().decode(cipherText);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

注册 SPI 并配置：

```properties
debbie.config.server.encrypt=true
```

---

## Config Server HTTP 端点

`ConfigServerEndpoint` 通过 debbie MVC 的 `@Router` + `@GetRouter` 提供 HTTP 接口。

端点前缀由 `debbie.config.server.prefix` 配置（默认 `/config`）。

| 路径 | 方法 | 返回 | 说明 |
|------|------|------|------|
| `/{application}/{profile}` | GET | JSON `ConfigEnvironment` | 获取配置环境 |
| `/{application}/{profile}/{label}` | GET | JSON `ConfigEnvironment` | 指定 label 获取 |
| `/{application}-{profile}.properties` | GET | text/plain | 扁平化 properties 格式 |

### 响应示例

`GET /config/order/dev` 返回：

```json
{
  "name": "order",
  "profiles": ["dev"],
  "label": "master",
  "propertySources": [
    {
      "name": "application.properties",
      "source": {
        "logging.level.root": "info"
      }
    },
    {
      "name": "order-dev.properties",
      "source": {
        "order.max-amount": "5000",
        "db.url": "jdbc:mysql://dev-db:3306/order"
      }
    }
  ]
}
```

---

## Config Client 拉取与注入

`ConfigClient` 在应用启动的 `postStarter` 阶段自动执行：

1. 构建 URL：`{uri}/{name}/{profile}[/{label}]`
2. 通过 `java.net.http.HttpClient` 发送 GET 请求
3. 解析响应为 `ConfigEnvironment`
4. 将所有 `ConfigPropertySource` 的属性注入到 `EnvironmentDepositoryHolder`

### 手动拉取

```java
@BeanInject
private ConfigClient configClient;

// 手动拉取并注入
ConfigEnvironment env = configClient.fetchAndInject();

// 仅拉取不注入
ConfigEnvironment env2 = configClient.fetch();

// 手动注入已有的环境
configClient.inject(env);

// 获取缓存的配置环境
ConfigEnvironment cached = configClient.getCachedEnvironment();
```

### Fail-Fast

当 `debbie.config.client.fail-fast=true` 时，拉取失败会抛出 `ConfigClientException` 终止应用启动；
为 `false` 时仅打印错误日志，应用继续启动（使用本地已有配置）。

---

## 运行时刷新

`ConfigClient.refresh()` 重新从 Server 拉取并注入，可用于运行时配置热更新：

```java
@Router
public class RefreshController {

    @BeanInject
    private ConfigClient configClient;

    @PostRouter(value = "/refresh")
    public String refresh() {
        var env = configClient.refresh();
        return "refreshed " + env.getPropertySources().size() + " sources";
    }
}
```

---

## 与 debbie-bus 联动

debbie-config 可与 [debbie-bus](../bus/README.md) 联动实现**集群配置广播刷新**：

1. Config Server 修改配置文件
2. Server 调用 `BusEventPublisher.refresh()` 广播 `RefreshBusEvent`
3. 所有 Config Client 收到事件后调用 `ConfigClient.refresh()` 重新拉取

```java
@Router
public class ConfigBusController {

    @BeanInject
    private BusEventPublisher busEventPublisher;

    @PostRouter(value = "/config-bus-refresh")
    public String busRefresh() {
        // 广播刷新事件，所有客户端自动重新拉取配置
        busEventPublisher.refresh(BusDestination.all(),
            Map.of("source", "config-server"));
        return "config refresh broadcasted";
    }
}
```

Client 侧注册 `RefreshHandler`：

```java
public class ConfigClientRefreshHandler implements RefreshHandler {
    private final ConfigClient configClient;

    public ConfigClientRefreshHandler(ConfigClient configClient) {
        this.configClient = configClient;
    }

    @Override
    public void onRefresh(RefreshBusEvent event) {
        configClient.refresh();
    }
}
```

---

## API 速查

### ConfigEnvironment

| 方法 | 说明 |
|------|------|
| `getName()` / `setName()` | 应用名称 |
| `getProfiles()` / `setProfiles()` | profile 列表 |
| `getLabel()` / `setLabel()` | 标签 |
| `addPropertySource()` | 添加属性源 |
| `getPropertySources()` | 获取所有属性源 |
| `asFlattenedMap()` | 扁平化（后者覆盖前者） |
| `asFlattenedMapFirstWins()` | 扁平化（前者优先） |

### ConfigPropertySource

| 方法 | 说明 |
|------|------|
| `getName()` | 属性源名称 |
| `getSource()` | key-value 映射 |
| `put()` / `putAll()` | 添加属性 |
| `get()` / `contains()` | 查询属性 |

### ConfigRepository

| 方法 | 说明 |
|------|------|
| `name()` | 仓库名称 |
| `findOne(application, profile, label)` | 查找配置环境 |

### ConfigEncryptor

| 方法 | 说明 |
|------|------|
| `encrypt(plain)` | 加密 |
| `decrypt(cipher)` | 解密 |
| `isEncrypted(value)` | 是否 `{cipher}` 前缀 |
| `decryptIfEncrypted(value)` | 自动解密 |

### ConfigClient

| 方法 | 说明 |
|------|------|
| `fetchAndInject()` | 拉取并注入本地环境 |
| `fetch()` | 仅拉取 |
| `inject(env)` | 注入已有环境 |
| `refresh()` | 重新拉取并注入 |
| `getCachedEnvironment()` | 获取缓存环境 |

---

## 设计说明

### 与 Spring Cloud Config 的对比

| 特性 | Spring Cloud Config | debbie-config |
|------|---------------------|---------------|
| 依赖框架 | Spring Boot / Spring Cloud | debbie-core + debbie-mvc |
| 环境抽象 | `Environment` / `PropertySource` | `ConfigEnvironment` / `ConfigPropertySource` |
| 仓库抽象 | `EnvironmentRepository` | `ConfigRepository` (SPI) |
| 内置后端 | Git / File / Vault / JDBC | File (可扩展) |
| 加密 | `TextEncryptor` | `ConfigEncryptor` (SPI) |
| HTTP 客户端 | RestTemplate / WebClient | `java.net.http.HttpClient` (JDK 内置) |
| 自动装配 | Spring Boot AutoConfiguration | `DebbieModuleStarter` (SPI) |
| 端点 | `@RestController` | `@Router` + `@GetRouter` |
| 模块化 | 无 JPMS | 有 `module-info.java` |
| Server/Client 共存 | 需要独立部署 | 可在同一应用共存 |

### 模块信息

- **JPMS 模块名**：`com.truthbean.debbie.config`
- **导出包**：
  - `com.truthbean.debbie.config`
  - `com.truthbean.debbie.config.env`
  - `com.truthbean.debbie.config.repository`
  - `com.truthbean.debbie.config.encrypt`
  - `com.truthbean.debbie.config.server`
  - `com.truthbean.debbie.config.client`
- **uses**：
  - `com.truthbean.debbie.config.repository.ConfigRepositoryFactory`
  - `com.truthbean.debbie.config.encrypt.ConfigEncryptorFactory`
- **provides**：`com.truthbean.debbie.boot.DebbieModuleStarter` with `com.truthbean.debbie.config.ConfigModuleStarter`

### SPI 注册

debbie-config 注册了三类 SPI：

1. **`DebbieModuleStarter`**：`ConfigModuleStarter`，debbie 启动时自动加载。
2. **`ConfigRepositoryFactory`**：`FileConfigRepositoryFactory`，文件系统仓库。
3. **`ConfigEncryptorFactory`**：`NoopConfigEncryptorFactory`，默认不加密。

### 类一览

| 类 | 角色 |
|----|------|
| `ConfigConfiguration` | 配置类（`debbie.config.*`） |
| `ConfigModuleStarter` | 模块启动器（SPI 自动注册） |
| `ConfigEnvironment` | 环境抽象 |
| `ConfigPropertySource` | 命名属性源 |
| `ConfigRepository` | 仓库抽象接口 |
| `ConfigRepositoryFactory` | 仓库 SPI 工厂 |
| `FileConfigRepository` | 文件系统仓库实现 |
| `FileConfigRepositoryFactory` | 文件系统仓库工厂 |
| `ConfigEncryptor` | 加密解密接口 |
| `ConfigEncryptorFactory` | 加密解密 SPI 工厂 |
| `NoopConfigEncryptor` | 默认不加密实现 |
| `NoopConfigEncryptorFactory` | 默认不加密工厂 |
| `ConfigServer` | 服务器组件容器 |
| `ConfigServerEndpoint` | HTTP 端点（`@Router`） |
| `ConfigClient` | 配置客户端（拉取 + 注入） |
| `ConfigClientException` | 客户端异常 |

---

## 示例

### 完整 Server + Client 示例

**Config Server**（端口 8888）：

```properties
debbie.config.enable=true
debbie.config.server.enable=true
debbie.config.server.repository=file
debbie.config.server.base-dir=config
debbie.config.server.default-label=master
```

目录结构：

```
config/master/
├── application.properties
├── application-dev.properties
├── order.properties
└── order-dev.properties
```

**Order Service**（Config Client）：

```properties
debbie.config.enable=true
debbie.config.client.enable=true
debbie.config.client.uri=http://localhost:8888/config
debbie.config.client.name=order
debbie.config.client.profile=dev
debbie.config.client.fail-fast=true
```

```java
@DebbieBootApplication
public class OrderServiceApp {
    public static void main(String[] args) {
        DebbieApplication.run(OrderServiceApp.class, args);
    }
}

@Router
public class OrderController {

    @BeanInject
    private ConfigClient configClient;

    @GetRouter(value = "/max-amount")
    public String maxAmount(Environment env) {
        // 从注入的远程配置读取
        return env.getValue("order.max-amount");
    }

    @PostRouter(value = "/refresh")
    public String refresh() {
        var env = configClient.refresh();
        return "refreshed: " + env.asFlattenedMap().size() + " properties";
    }
}
```

### Server + Client 共存

一个应用同时作为 Server（为下游服务提供配置）和 Client（从上游拉取配置）：

```properties
debbie.config.enable=true
debbie.config.server.enable=true
debbie.config.server.base-dir=config
debbie.config.client.enable=true
debbie.config.client.uri=http://upstream-config:8888/config
debbie.config.client.name=gateway
debbie.config.client.profile=prod
```

### 加密配置

```properties
# Server 端启用加密
debbie.config.server.encrypt=true
```

配置文件中使用加密值：

```properties
# order-dev.properties
db.password={cipher}AQB1j8k7sZ9...
```

Server 返回给 Client 时自动解密为明文。

---

## License

[穆兰 PSL v2](../../LICENSE)
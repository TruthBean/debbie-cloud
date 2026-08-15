# debbie-mongo

> MongoDB integration module for the debbie framework.

## 概述

`debbie-mongo` 是基于 debbie 框架的 MongoDB 集成模块，封装了 MongoDB Java Sync Driver（`mongodb-driver-sync`），通过 debbie 的 SPI 机制自动创建和管理 `MongoClient` 实例。

### 核心特性

- **自动装配**：通过 `DebbieModuleStarter` SPI 机制自动创建 `MongoClientFactory`
- **连接字符串构建**：支持 URI 直连和分参数配置两种方式
- **连接池配置**：最大/最小连接数、空闲时间、等待时间等
- **SSL/TLS**：支持 TLS 连接和无效主机名容忍
- **认证**：支持用户名/密码认证和自定义 authSource
- **重试策略**：可配置 retryWrites 和 retryReads

## 架构

```
com.truthbean.debbie.mongo
├── MongoConfiguration    — 配置类（prefix: debbie.mongo）
├── MongoClientFactory    — MongoDB 客户端工厂
└── MongoModuleStarter    — SPI 模块启动器
```

## 配置

所有配置项前缀为 `debbie.mongo`。

### 连接配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.mongo.enable` | `true` | 是否启用模块 |
| `debbie.mongo.uri` | — | MongoDB 连接 URI（设置后忽略 host/port 等配置） |
| `debbie.mongo.host` | `localhost` | MongoDB 主机地址 |
| `debbie.mongo.port` | `27017` | MongoDB 端口 |
| `debbie.mongo.database` | — | 默认数据库 |
| `debbie.mongo.username` | — | 用户名 |
| `debbie.mongo.password` | — | 密码 |
| `debbie.mongo.auth-source` | — | 认证源 |

### 超时配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.mongo.connect-timeout` | `10000` | 连接超时（毫秒） |
| `debbie.mongo.socket-timeout` | `0` | Socket 超时（毫秒，0=无限） |
| `debbie.mongo.server-selection-timeout` | `30000` | 服务器选择超时（毫秒） |

### 连接池配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.mongo.max-pool-size` | `100` | 最大连接数 |
| `debbie.mongo.min-pool-size` | `0` | 最小连接数 |
| `debbie.mongo.max-idle-time` | `60000` | 最大空闲时间（毫秒） |
| `debbie.mongo.max-wait-time` | `120000` | 最大等待时间（毫秒） |

### SSL/TLS 配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.mongo.ssl` | `false` | 是否启用 TLS |
| `debbie.mongo.ssl-invalid-host-allowed` | `false` | 是否允许无效主机名 |

### 重试配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.mongo.retry-writes` | `true` | 是否重试写入 |
| `debbie.mongo.retry-reads` | `true` | 是否重试读取 |

## 使用示例

### 配置

```properties
debbie.mongo.host=mongo
debbie.mongo.port=27017
debbie.mongo.database=mydb
debbie.mongo.username=root
debbie.mongo.password=secret
debbie.mongo.auth-source=admin
debbie.mongo.max-pool-size=50
```

### 使用 URI 直连

```properties
debbie.mongo.uri=mongodb+srv://cluster.mongodb.net/mydb?retryWrites=true
```

### 编程式使用

```java
// 通过依赖注入获取
@BeanInject MongoClientFactory factory;

// 获取 MongoClient
MongoClient client = factory.getClient();

// 获取默认数据库
MongoDatabase db = factory.getDatabase();

// 获取指定数据库
MongoDatabase otherDb = factory.getDatabase("otherdb");

// 获取集合
MongoCollection<Document> collection = db.getCollection("users");
```

## 依赖

- `debbie-core` — 核心框架
- `mongodb-driver-sync` 4.7.2 — MongoDB Java Sync Driver

## 测试

```bash
mvn -f boot/mongo/pom.xml test
```

13 个单元测试覆盖：配置默认值、连接字符串构建、URI 模式、认证参数、SSL 参数、连接池参数、超时参数、配置拷贝。

## 许可证

Mulan PSL v2
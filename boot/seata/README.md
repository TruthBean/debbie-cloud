# debbie-seata

Alibaba Seata 分布式事务框架集成模块，基于 [Apache Seata](https://seata.apache.org/) 2.x，为 debbie 框架提供分布式事务能力（AT、TCC、SAGA、XA 模式）。

## 特性

- 自动装配 Seata 配置和事务管理器，通过 SPI 注册 `DebbieModuleStarter` 实现
- 支持完整的 Seata 客户端配置（应用 ID、事务服务组、服务端地址等）
- 支持 AT 和 XA 两种数据源代理模式
- 支持 Transport 配置（类型、序列化、压缩、心跳）
- 支持 Client RM 配置（异步提交缓冲、重试、锁策略、SQL 解析器）
- 支持 Client TM 配置（提交/回滚重试、全局事务超时）
- 支持 Undo 日志配置（数据校验、序列化、仅关注更新列）
- 支持多种注册中心（file、nacos、eureka、redis、zk、consul、etcd3）
- 支持多种配置中心（file、nacos、apollo、zk、consul、etcd3）
- 支持负载均衡配置（Random、RoundRobin、ConsistentHash）
- 支持 TCC Fence 配置（日志表名、清理周期）
- 提供全局事务模板方法 `executeInGlobalTransaction()`
- 提供 XID 管理和分支类型管理
- 自动初始化 TMClient 和 RMClient
- 无 Spring 依赖，纯 debbie 框架实现

## 依赖

- `debbie-core` — debbie 框架核心
- `seata-all` 2.x — Apache Seata 全量客户端

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-seata</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置

在 `debbie.properties` 或环境变量中配置：

```properties
# 基础配置
debbie.seata.application-id=order-service
debbie.seata.tx-service-group=order-tx-group
debbie.seata.server-addr=localhost:8091

# 数据源代理
debbie.seata.enable-auto-data-source-proxy=true
debbie.seata.data-source-proxy-mode=AT

# Transport
debbie.seata.transport.type=TCP
debbie.seata.transport.serialization=seata
debbie.seata.transport.compressor=none

# Client RM
debbie.seata.client.rm.async-commit-buffer-limit=10000
debbie.seata.client.rm.report-retry-count=5
debbie.seata.client.rm.sql-parser-type=druid
debbie.seata.client.rm.lock.retry-interval=10
debbie.seata.client.rm.lock.retry-times=30

# Client TM
debbie.seata.client.tm.commit-retry-count=5
debbie.seata.client.tm.rollback-retry-count=5
debbie.seata.client.tm.default-global-transaction-timeout=60000

# Undo
debbie.seata.client.undo.data-validation=true
debbie.seata.client.undo.log-serialization=jackson

# 注册中心 (file / nacos / eureka / redis / zk / consul / etcd3)
debbie.seata.registry.type=file
# Nacos 注册中心
# debbie.seata.registry.type=nacos
# debbie.seata.registry.nacos.server-addr=nacos:8848
# debbie.seata.registry.nacos.namespace=seata-ns
# debbie.seata.registry.nacos.group=SEATA_GROUP
# debbie.seata.registry.nacos.cluster=default

# 配置中心 (file / nacos / apollo / zk / consul / etcd3)
debbie.seata.config.type=file

# 负载均衡
debbie.seata.load-balance.type=RandomLoadBalance
debbie.seata.load-balance.virtual-nodes=10

# 日志
debbie.seata.log.exception-rate=100

# TCC Fence
debbie.seata.tcc.fence.log-table-name=tcc_fence_log
debbie.seata.tcc.fence.clean-period=3600000

# 启用/禁用模块
debbie.seata.enable=true
```

### 3. 使用

通过 `@BeanInject` 注入 `SeataTransactionFactory` 管理全局事务：

```java
@Router
public class OrderRouter {

    @BeanInject
    private SeataTransactionFactory seata;

    @PostRouter
    public void createOrder(OrderRequest request) throws TransactionException {
        // 方式1: 使用模板方法
        seata.executeInGlobalTransaction(() -> {
            orderService.createOrder(request);
            inventoryService.deduct(request.getProductId(), request.getQuantity());
            accountService.debit(request.getUserId(), request.getAmount());
        });

        // 方式2: 手动管理
        String xid = seata.beginGlobalTransaction(60000, "createOrder");
        try {
            orderService.createOrder(request);
            inventoryService.deduct(request.getProductId(), request.getQuantity());
            accountService.debit(request.getUserId(), request.getAmount());
            seata.commitGlobalTransaction();
        } catch (Exception e) {
            seata.rollbackGlobalTransaction();
            throw e;
        }
    }
}
```

XID 传播（用于跨服务分布式事务）：

```java
// 服务A: 获取 XID 并传递给服务B
String xid = seata.getXid();

// 服务B: 绑定 XID 后执行分支事务
seata.bind(xid);
try {
    branchService.execute();
} finally {
    seata.unbind();
}
```

检查全局事务状态：

```java
if (seata.inGlobalTransaction()) {
    // 当前线程在全局事务中
    String xid = seata.getXid();
    GlobalStatus status = seata.getGlobalTransactionStatus();
}
```

## 配置项

### 基础配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.enable` | boolean | `true` | 启用/禁用模块 |
| `debbie.seata.application-id` | String | `debbie-app` | 应用 ID |
| `debbie.seata.tx-service-group` | String | `default` | 事务服务组 |
| `debbie.seata.server-addr` | String | `localhost:8091` | Seata Server 地址 |
| `debbie.seata.enable-auto-data-source-proxy` | boolean | `true` | 自动数据源代理 |
| `debbie.seata.data-source-proxy-mode` | String | `AT` | 代理模式（AT/XA） |
| `debbie.seata.use-jdk-proxy` | boolean | `false` | 使用 JDK 代理 |
| `debbie.seata.disable-global-transaction` | boolean | `false` | 禁用全局事务 |

### Transport

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.transport.type` | String | `TCP` | 传输类型 |
| `debbie.seata.transport.server` | String | `NIO` | 服务器类型 |
| `debbie.seata.transport.serialization` | String | `seata` | 序列化方式 |
| `debbie.seata.transport.compressor` | String | `none` | 压缩方式 |
| `debbie.seata.transport.heartbeat` | boolean | `true` | 心跳 |
| `debbie.seata.transport.shutdown.wait` | int | `3` | 关闭等待 |
| `debbie.seata.transport.shutdown.wait-period` | long | `1000` | 关闭等待周期（ms） |

### Client RM

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.client.rm.async-commit-buffer-limit` | int | `10000` | 异步提交缓冲限制 |
| `debbie.seata.client.rm.report-retry-count` | int | `5` | 上报重试次数 |
| `debbie.seata.client.rm.table-meta-check-enable` | boolean | `false` | 表元数据检查 |
| `debbie.seata.client.rm.table-meta-checker-interval` | long | `60000` | 检查间隔（ms） |
| `debbie.seata.client.rm.sql-parser-type` | String | `druid` | SQL 解析器 |
| `debbie.seata.client.rm.lock.retry-interval` | int | `10` | 锁重试间隔（ms） |
| `debbie.seata.client.rm.lock.retry-times` | int | `30` | 锁重试次数 |
| `debbie.seata.client.rm.lock.retry-policy-branch-rollback-on-conflict` | boolean | `true` | 分支回滚冲突策略 |

### Client TM

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.client.tm.commit-retry-count` | int | `5` | 提交重试次数 |
| `debbie.seata.client.tm.rollback-retry-count` | int | `5` | 回滚重试次数 |
| `debbie.seata.client.tm.default-global-transaction-timeout` | int | `60000` | 默认全局事务超时（ms） |

### Client Undo

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.client.undo.data-validation` | boolean | `true` | 数据校验 |
| `debbie.seata.client.undo.log-serialization` | String | `jackson` | Undo 日志序列化 |
| `debbie.seata.client.undo.only-care-update-columns` | boolean | `false` | 仅关注更新列 |

### Registry

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.registry.type` | String | `file` | 注册中心类型 |
| `debbie.seata.registry.nacos.server-addr` | String | - | Nacos 地址 |
| `debbie.seata.registry.nacos.namespace` | String | - | Nacos 命名空间 |
| `debbie.seata.registry.nacos.group` | String | `SEATA_GROUP` | Nacos 分组 |
| `debbie.seata.registry.nacos.cluster` | String | `default` | Nacos 集群 |

### Config

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.config.type` | String | `file` | 配置中心类型 |
| `debbie.seata.config.nacos.server-addr` | String | - | Nacos 地址 |
| `debbie.seata.config.nacos.namespace` | String | - | Nacos 命名空间 |
| `debbie.seata.config.nacos.group` | String | `SEATA_GROUP` | Nacos 分组 |

### 其他

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.seata.load-balance.type` | String | `RandomLoadBalance` | 负载均衡类型 |
| `debbie.seata.load-balance.virtual-nodes` | int | `10` | 虚拟节点数 |
| `debbie.seata.log.exception-rate` | int | `100` | 异常日志率 |
| `debbie.seata.tcc.fence.log-table-name` | String | `tcc_fence_log` | TCC Fence 表名 |
| `debbie.seata.tcc.fence.clean-period` | long | `3600000` | TCC Fence 清理周期（ms） |

## 架构

```
SeataConfiguration         — 配置类，绑定 debbie.seata.* 属性
SeataTransactionFactory    — 事务管理器，负责初始化 TM/RM 客户端和全局事务管理
SeataModuleStarter         — SPI 模块启动器，自动装配 Bean
```

### SPI 自动装配

模块通过 `module-info.java` 中的 `provides DebbieModuleStarter with SeataModuleStarter` 注册，
debbie 框架启动时自动发现并执行：

1. `registerBean()` — 注册 `SeataConfiguration` Bean
2. `starter()` — 创建 `SeataTransactionFactory`，调用 `init()` 初始化 Seata 客户端，注册为 Bean
3. `release()` — 关闭事务工厂，释放资源

### 初始化流程

`SeataTransactionFactory.init()` 执行以下步骤：

1. `initSystemProperties()` — 设置 Seata 系统属性（服务映射、传输、客户端 RM/TM、Undo、注册中心、配置中心等）
2. `TMClient.init()` — 初始化事务管理器客户端
3. `RMClient.init()` — 初始化资源管理器客户端

### 全局事务管理

`SeataTransactionFactory` 提供以下全局事务操作：

| 方法 | 说明 |
|------|------|
| `beginGlobalTransaction()` | 开始全局事务 |
| `beginGlobalTransaction(timeout)` | 开始全局事务（指定超时） |
| `beginGlobalTransaction(timeout, name)` | 开始全局事务（指定超时和名称） |
| `commitGlobalTransaction()` | 提交全局事务 |
| `rollbackGlobalTransaction()` | 回滚全局事务 |
| `getGlobalTransactionStatus()` | 获取全局事务状态 |
| `executeInGlobalTransaction(Runnable)` | 在全局事务中执行（模板方法） |
| `executeInGlobalTransaction(Callable)` | 在全局事务中执行并返回结果 |
| `executeInGlobalTransaction(Runnable, timeout)` | 在全局事务中执行（指定超时） |

### XID 管理

| 方法 | 说明 |
|------|------|
| `getXid()` | 获取当前 XID |
| `bind(xid)` | 绑定 XID 到当前线程 |
| `unbind()` | 解绑 XID |
| `inGlobalTransaction()` | 检查是否在全局事务中 |
| `getBranchType()` | 获取分支类型 |
| `setDefaultBranchType(type)` | 设置默认分支类型 |

## 模块信息

- **模块名**: `com.truthbean.debbie.seata`
- **启动顺序**: `280`
- **配置前缀**: `debbie.seata`
- **Java 版本**: 17+
- **Seata 版本**: 2.6.0

## 许可证

Mulan PSL v2
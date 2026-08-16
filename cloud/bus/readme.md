# debbie-bus

一个轻量级事件总线模块，基于 [debbie](https://github.com/TruthBean/debbie-cloud) 框架自身的事件机制与模块体系实现。

通过消息中间件（默认内存实现，可扩展 Kafka / RabbitMQ / Redis Pub-Sub 等）在微服务节点之间广播事件，
最典型的用途是**广播配置变更**，让所有目标节点刷新本地配置。

---

## 目录

- [特性](#特性)
- [依赖关系](#依赖关系)
- [快速开始](#快速开始)
- [配置项](#配置项)
- [核心概念](#核心概念)
- [事件体系](#事件体系)
- [消息中间件抽象](#消息中间件抽象)
- [自定义 BusMessageBroker 实现](#自定义-busmessagebroker-实现)
- [自定义 RefreshHandler](#自定义-refreshhandler)
- [监听总线事件](#监听总线事件)
- [API 速查](#api-速查)
- [设计说明](#设计说明)
- [示例](#示例)

---

## 特性

- **零 Spring 依赖**：仅依赖 `debbie-core`，使用 debbie 自有的 `@PropertiesConfiguration`、`@PropertyInject`、`DebbieModuleStarter`、`DebbieEventPublisher` 等机制。
- **SPI 可插拔消息中间件**：`BusMessageBrokerFactory` 通过 `java.util.ServiceLoader` 发现，默认提供 `SimpleBusMessageBroker`（内存），可按需扩展。
- **JPMS 模块化**：自带 `module-info.java`，模块名 `com.truthbean.debbie.bus`。
- **自动装配**：通过 `DebbieModuleStarter` SPI 自动注册到 debbie 应用上下文，引入依赖即生效。
- **配置刷新**：内置 `EnvironmentRefreshHandler`，收到 `RefreshBusEvent` 后自动将变更 key 写回 `EnvironmentDepositoryHolder`。
- **定向广播**：`BusDestination` 支持服务 ID + profile 匹配语法（如 `order-service:dev,**`）。
- **ACK 机制**：可选开启确认回执，接收方处理完后向总线发送 `AckBusEvent`。

---

## 依赖关系

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-bus</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

模块依赖图：

```
debbie-bus
    └── debbie-core (transitive)
```

不引入任何 `spring-*`、`spring-boot-*` 依赖。消息中间件的具体实现（Kafka、RabbitMQ 等）通过 SPI 在运行时发现，编译期不耦合。

---

## 快速开始

### 1. 引入依赖

在应用的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-bus</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置（可选）

在 `application.properties` 中：

```properties
# 启用 bus（默认 true）
debbie.bus.enable=true
# 当前服务在总线上的唯一标识
debbie.bus.id=order-service:8080
# 默认广播目标，** 表示所有服务
debbie.bus.destination=**
# 消息中间件类型，默认 simple（内存），可填 kafka / rabbitmq / redis 等（需有对应 SPI 实现）
debbie.bus.broker=simple
# 是否开启 ACK 确认
debbie.bus.ack=false
# 是否打印事件追踪日志
debbie.bus.trace=false
```

### 3. 启动应用

debbie 应用启动时会自动通过 SPI 加载 `BusModuleStarter`，完成 `BusMessageBroker` 解析、`BusEventPublisher` / `BusEventListener` 装配。

```java
@DebbieBootApplication
public class MyApplication {
    public static void main(String[] args) {
        DebbieApplication.run(MyApplication.class, args);
    }
}
```

### 4. 发布刷新事件

注入 `BusEventPublisher` 并调用 `refresh()`：

```java
@Router
public class ConfigRouter {

    @BeanInject
    private BusEventPublisher busEventPublisher;

    @PostRouter(value = "/bus-refresh")
    public String refresh() {
        // 广播配置刷新到所有服务
        busEventPublisher.refresh();
        return "refresh broadcasted";
    }

    @PostRouter(value = "/bus-refresh-targeted")
    public String refreshTargeted() {
        // 只广播到 order-service
        busEventPublisher.refresh(BusDestination.of("order-service"));
        return "refresh sent to order-service";
    }

    @PostRouter(value = "/bus-refresh-keys")
    public String refreshKeys() {
        // 广播带具体变更 key 的刷新
        busEventPublisher.refresh(Map.of(
            "order.feature.enabled", "true",
            "order.max-amount", "5000"
        ));
        return "refresh with keys broadcasted";
    }
}
```

---

## 配置项

所有配置项前缀为 `debbie.bus`，通过 `BusConfiguration` 类承载。

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.bus.enable` | boolean | `true` | 是否启用 bus 模块 |
| `debbie.bus.id` | String | `default` | 当前服务在总线上的唯一标识，用于区分来源与匹配目标 |
| `debbie.bus.destination` | String | `**` | 默认广播目标，`**` 表示所有服务 |
| `debbie.bus.topic` | String | `debbie-bus` | 事件主题（供 broker 实现使用，如 Kafka topic） |
| `debbie.bus.broker` | String | `simple` | 消息中间件类型，对应 `BusMessageBrokerFactory.name()` |
| `debbie.bus.ack` | boolean | `false` | 是否开启 ACK 确认回执 |
| `debbie.bus.trace` | boolean | `false` | 是否打印事件追踪日志 |
| `debbie.bus.publish-timeout` | int | `5000` | 发布超时时间（毫秒，供 broker 实现使用） |

---

## 核心概念

### BusIdentity（服务标识）

标识总线上的一个服务实例，类似 Spring Cloud Bus 的 `ContextId`。

- 发布事件时作为 `originService`，接收方据此判断是否是自己发出的（避免回环）。
- 匹配 `BusDestination` 时作为被匹配的目标。

```java
BusIdentity self = new BusIdentity("order-service:8080");
```

### BusDestination（广播目标）

描述一个事件要广播给哪些服务，类似 Spring Cloud Bus 的 `Destination`。

语法为逗号分隔的服务 ID 列表，可选附带 profile 后缀：

| 表达式 | 含义 |
|--------|------|
| `**` 或空 | 所有服务 |
| `order-service` | 名为 `order-service` 的服务 |
| `order-service:dev` | `order-service` 且 profile 为 `dev` |
| `order-service:**` | 所有以 `order-service` 开头的服务 |
| `order-service,product-service` | `order-service` 或 `product-service` |

```java
BusDestination all = BusDestination.all();
BusDestination targeted = BusDestination.of("order-service,product-service:dev");

// 匹配判断
boolean match = targeted.matches("order-service"); // true
```

---

## 事件体系

所有总线事件继承 `BusEvent`（继承 debbie 的 `AbstractDebbieEvent`），携带 `eventId`、`originService`、`destination`。

```
AbstractDebbieEvent (debbie-core)
    └── BusEvent
            ├── RefreshBusEvent    配置刷新事件
            ├── AckBusEvent        确认回执事件
            └── GenericBusEvent    通用事件（携带任意 payload）
```

### BusEvent

基类，核心方法：

| 方法 | 说明 |
|------|------|
| `getEventId()` | 事件唯一 ID（UUID） |
| `getOriginService()` | 发布方服务标识 |
| `getDestination()` | 广播目标 |
| `isForSelf(BusIdentity)` | 是否应当被当前服务处理（排除自己发出的 + 匹配目标） |

### RefreshBusEvent

配置刷新事件，携带变更的 key-value 映射：

```java
var event = new RefreshBusEvent(
    this,
    new BusIdentity("config-server"),
    BusDestination.all(),
    Map.of("order.feature.enabled", "true")
);
busEventPublisher.publish(event);
```

### AckBusEvent

确认回执事件，引用被确认事件的 ID 与类型。开启 `debbie.bus.ack=true` 后，接收方处理完事件会自动发送 ACK。

### GenericBusEvent

通用事件，携带 `type` 字符串和 `payload` 映射，用于广播自定义数据：

```java
var event = new GenericBusEvent(
    this,
    self,
    BusDestination.of("user-service"),
    "cache-evict",
    Map.of("key", "user:123")
);
busEventPublisher.publish(event);
```

---

## 消息中间件抽象

`BusMessageBroker` 是消息中间件的抽象接口，`BusMessageBrokerFactory` 是其 SPI 工厂。

```
BusMessageBrokerFactory (SPI)
    │  name()        返回中间件名称（匹配 debbie.bus.broker）
    │  support()     判断是否支持当前配置
    │  create()      创建 Broker 实例
    ▼
BusMessageBroker
    │  start()       启动
    │  publish()     发布事件
    │  subscribe()   订阅事件
    │  close()       关闭
    ▼
SimpleBusMessageBroker (默认内存实现)
```

### 内置实现

| 名称 | 类 | 说明 |
|------|----|------|
| `simple` | `SimpleBusMessageBroker` | 内存实现，单进程内异步分发，用于测试和单机场景 |

### SPI 发现机制

`BusMessageBrokerFactory` 通过 `java.util.ServiceLoader` 发现：

```
META-INF/services/com.truthbean.debbie.bus.broker.BusMessageBrokerFactory
```

`BusModuleStarter` 启动时：
1. 加载所有 `BusMessageBrokerFactory`；
2. 找到 `support(configuration)` 为 `true` 的工厂；
3. 若无匹配，回退到 `name()` 为 `simple` 的工厂；
4. 创建 `BusMessageBroker`，调用 `start()`，订阅 `BusEventListener`。

---

## 自定义 BusMessageBroker 实现

以接入 Kafka 为例（需自行依赖 `kafka-clients`，debbie-bus 本身不引入）：

### 1. 实现 BusMessageBroker

```java
package com.truthbean.debbie.bus.broker.kafka;

public class KafkaBusMessageBroker implements BusMessageBroker {
    private final KafkaProducer<String, byte[]> producer;
    private final KafkaConsumer<String, byte[]> consumer;
    private final List<BusMessageListener> listeners = new CopyOnWriteArrayList<>();
    private final String topic;
    // ... 初始化 producer / consumer

    @Override
    public String name() { return "kafka"; }

    @Override
    public void start() {
        consumer.subscribe(List.of(topic));
        // 启动消费线程，反序列化后调用 listeners.forEach(l -> l.onMessage(event))
    }

    @Override
    public void publish(BusEvent event) {
        // 序列化 event 为 byte[]，发送到 Kafka topic
        producer.send(new ProducerRecord<>(topic, serialize(event)));
    }

    @Override
    public void subscribe(BusMessageListener listener) { listeners.add(listener); }

    @Override
    public void unsubscribe(BusMessageListener listener) { listeners.remove(listener); }

    @Override
    public void close() { producer.close(); consumer.close(); }
}
```

### 2. 实现 BusMessageBrokerFactory

```java
package com.truthbean.debbie.bus.broker.kafka;

public class KafkaBusMessageBrokerFactory implements BusMessageBrokerFactory {
    @Override
    public String name() { return "kafka"; }

    @Override
    public boolean support(BusConfiguration config) {
        return config != null && "kafka".equalsIgnoreCase(config.getBroker());
    }

    @Override
    public BusMessageBroker create(BusConfiguration config) {
        return new KafkaBusMessageBroker(config);
    }
}
```

### 3. 注册 SPI

在 `META-INF/services/com.truthbean.debbie.bus.broker.BusMessageBrokerFactory` 中：

```
com.truthbean.debbie.bus.broker.kafka.KafkaBusMessageBrokerFactory
```

### 4. 配置启用

```properties
debbie.bus.broker=kafka
```

---

## 自定义 RefreshHandler

除了内置的 `EnvironmentRefreshHandler`（刷新 `EnvironmentDepositoryHolder`），可以注册自定义处理器处理 `RefreshBusEvent`：

```java
@BeanComponent
public class CacheRefreshHandler implements RefreshHandler {

    @Override
    public void onRefresh(RefreshBusEvent event) {
        Map<String, String> keys = event.getKeys();
        // 清理本地缓存
        for (var key : keys.keySet()) {
            CacheManager.evict(key);
        }
    }
}
```

在 `BusModuleStarter.postStarter` 阶段或应用启动后，将自定义 handler 注册到 `BusEventListener`：

```java
@EventMethodListener
public void onReady(DebbieReadyEvent event) {
    var ctx = event.getApplicationContext();
    var busListener = ctx.getGlobalBeanFactory().factory(BusEventListener.class);
    busListener.addRefreshHandler(new CacheRefreshHandler());
}
```

---

## 监听总线事件

### 方式一：Debbie 事件监听注解

`BusEvent` 继承 `AbstractDebbieEvent`，可以用 debbie 标准的 `@EventMethodListener` 监听：

```java
@EventMethodListener
public void onRefreshEvent(RefreshBusEvent event) {
    // 仅当事件 isForSelf(self) 时 BusEventListener 才会转发到 DebbieEventPublisher
    System.out.println("received refresh: " + event.getKeys());
}

@EventMethodListener
public void onGenericEvent(GenericBusEvent event) {
    System.out.println("received: type=" + event.getEventType() + ", payload=" + event.getPayload());
}
```

### 方式二：实现 DebbieEventListener

```java
public class MyBusEventListener implements DebbieEventListener<RefreshBusEvent> {
    @Override
    public void onEvent(RefreshBusEvent event) {
        // 处理刷新
    }

    @Override
    public Class<RefreshBusEvent> getEventType() {
        return RefreshBusEvent.class;
    }
}
```

---

## API 速查

### BusEventPublisher

| 方法 | 说明 |
|------|------|
| `publish(BusEvent)` | 发布任意总线事件 |
| `refresh()` | 广播配置刷新到所有服务 |
| `refresh(BusDestination)` | 广播刷新到指定目标 |
| `refresh(Map<String,String> keys)` | 广播带变更 key 的刷新到所有服务 |
| `refresh(BusDestination, Map<String,String> keys)` | 广播带变更 key 的刷新到指定目标 |
| `ack(BusEvent)` | 发送 ACK 确认（需开启 `debbie.bus.ack`） |
| `getSelf()` | 当前服务标识 |

### BusEventListener

| 方法 | 说明 |
|------|------|
| `addRefreshHandler(RefreshHandler)` | 注册刷新处理器 |
| `removeRefreshHandler(RefreshHandler)` | 移除刷新处理器 |
| `asBrokerListener()` | 转为 `BusMessageListener` 供 broker 订阅 |
| `asDebbieEventListener()` | 转为 `DebbieEventListener` 供 debbie 事件机制监听 |

### BusDestination

| 方法 | 说明 |
|------|------|
| `all()` | 创建匹配所有服务的目标 |
| `of(String)` | 按表达式创建目标 |
| `matches(String)` | 判断服务 ID 是否匹配 |
| `matches(BusIdentity)` | 判断服务标识是否匹配 |
| `isAll()` | 是否匹配所有服务 |

---

## 设计说明

### 与 Spring Cloud Bus 的对比

| 特性 | Spring Cloud Bus | debbie-bus |
|------|-----------------|------------|
| 依赖框架 | Spring Boot / Spring Cloud | debbie-core |
| 事件基类 | `RemoteApplicationEvent` | `BusEvent` |
| 刷新事件 | `RefreshRemoteApplicationEvent` | `RefreshBusEvent` |
| ACK 事件 | `AckRemoteApplicationEvent` | `AckBusEvent` |
| 服务匹配 | `ServiceMatcher` / `ContextId` | `BusIdentity` / `BusDestination` |
| 消息中间件 | spring-cloud-stream (Binder) | `BusMessageBroker` (SPI) |
| 配置刷新 | `ContextRefresher` | `EnvironmentRefreshHandler` |
| 自动装配 | Spring Boot AutoConfiguration | `DebbieModuleStarter` (SPI) |
| 模块化 | 无 JPMS | 有 `module-info.java` |

### 事件流转

```
发布方                          消息中间件                        接收方
  │                                │                               │
  │ BusEventPublisher.publish()    │                               │
  ├───────────────────────────────►│  broker.publish(event)        │
  │                                │  (广播到所有订阅者)            │
  │                                ├──────────────────────────────►│
  │                                │                               │ BusEventListener.onMessage()
  │                                │                               │   ├─ isForSelf(self) 过滤
  │                                │                               │   ├─ RefreshHandler.onRefresh()
  │                                │                               │   ├─ DebbieEventPublisher.publishEvent()
  │                                │                               │   └─ (可选) ack() 回执
  │                                │◄──────────────────────────────┤
  │                                │  broker.publish(ackEvent)     │
```

### SPI 注册

debbie-bus 注册了两类 SPI：

1. **`DebbieModuleStarter`**：`BusModuleStarter`，在 `META-INF/services/com.truthbean.debbie.boot.DebbieModuleStarter` 注册，debbie 启动时自动加载。
2. **`BusMessageBrokerFactory`**：`SimpleBusMessageBrokerFactory`，在 `META-INF/services/com.truthbean.debbie.bus.broker.BusMessageBrokerFactory` 注册，用于发现消息中间件实现。

### 模块信息

- **JPMS 模块名**：`com.truthbean.debbie.bus`
- **导出包**：
  - `com.truthbean.debbie.bus`
  - `com.truthbean.debbie.bus.event`
  - `com.truthbean.debbie.bus.broker`
  - `com.truthbean.debbie.bus.identity`
  - `com.truthbean.debbie.bus.refresh`
- **uses**：`com.truthbean.debbie.bus.broker.BusMessageBrokerFactory`
- **provides**：`com.truthbean.debbie.boot.DebbieModuleStarter` with `com.truthbean.debbie.bus.BusModuleStarter`

---

## 示例

### 单进程刷新

```java
@DebbieBootApplication
public class SingleProcessApp {
    public static void main(String[] args) {
        DebbieApplication.run(SingleProcessApp.class, args);
    }
}

@Router
public class ConfigRouter {
    @BeanInject
    private BusEventPublisher busEventPublisher;

    @PostRouter(value = "/update-config")
    public String update(@Param("key") String key, @Param("value") String value) {
        busEventPublisher.refresh(Map.of(key, value));
        return "ok";
    }
}
```

### 多进程广播（需实现 KafkaBusMessageBroker）

节点 A（config-server）：

```properties
debbie.bus.id=config-server
debbie.bus.broker=kafka
debbie.bus.destination=**
```

```java
busEventPublisher.refresh(BusDestination.of("order-service:dev"),
    Map.of("order.feature.enabled", "true"));
```

节点 B（order-service，profile=dev）：

```properties
debbie.bus.id=order-service:dev
debbie.bus.broker=kafka
```

节点 B 的 `EnvironmentRefreshHandler` 会自动将 `order.feature.enabled=true` 写入本地 `EnvironmentDepositoryHolder`，
同时 `@EventMethodListener` 标注的方法也会收到 `RefreshBusEvent`。

### 自定义事件广播

```java
// 发布
var event = new GenericBusEvent(
    this, self,
    BusDestination.of("user-service"),
    "cache-evict",
    Map.of("userId", "123")
);
busEventPublisher.publish(event);

// 接收（在 user-service 节点）
@EventMethodListener
public void onCacheEvict(GenericBusEvent event) {
    if ("cache-evict".equals(event.getEventType())) {
        var userId = event.getPayload().get("userId");
        cache.evict("user:" + userId);
    }
}
```

---

## License

[穆兰 PSL v2](../../LICENSE)
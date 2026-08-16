# debbie-integration

> enterprise integration patterns (EIP) module.

## 概述

`debbie-integration` 是基于 debbie 框架的企业集成模式（Enterprise Integration Patterns, EIP）模块。它仅使用 `debbie-core`、`debbie-mvc` 和 JDK 内置功能。

### 核心特性

- **消息模型**：泛型 `Message<T>`（payload + headers），自动生成消息 ID 和时间戳
- **消息通道**：直接通道（DirectChannel）、队列通道（QueueChannel）、发布订阅通道（PublishSubscribeChannel）
- **端点**：服务激活器（ServiceActivator）、转换器（Transformer）、过滤器（Filter）、路由器（Router）、拆分器（Splitter）、聚合器（Aggregator）
- **集成流**：流畅 API（fluent API）定义消息处理流水线，支持链式 transform → filter → handle
- **流上下文**：`IntegrationFlowContext` 管理流和通道的注册、查找、生命周期
- **SPI 自动装配**：通过 `DebbieModuleStarter` SPI 机制自动启动

## 架构

```
com.truthbean.debbie.integration
├── Message                       — 泛型消息（payload + headers）
├── MessageChannel                — 消息通道接口
├── MessageHandler                — 消息处理器接口（@FunctionalInterface）
├── Subscriber                    — 订阅者接口
├── IntegrationFlow               — 集成流定义（fluent API + builder）
├── IntegrationFlowContext        — 流上下文（注册、查找、生命周期管理）
├── IntegrationConfiguration      — 配置类（prefix: debbie.integration）
├── IntegrationModuleStarter      — SPI 模块启动器
├── channel
│   ├── DirectChannel             — 同步直接通道
│   ├── QueueChannel              — 队列缓冲通道（LinkedBlockingQueue）
│   └── PublishSubscribeChannel   — 发布订阅通道
└── endpoint
    ├── ServiceActivator          — 服务激活器
    ├── Transformer               — 消息转换器
    ├── Filter                    — 消息过滤器
    ├── Router                    — 消息路由器
    ├── Splitter                  — 消息拆分器
    └── Aggregator                — 消息聚合器
```

## 配置

所有配置项前缀为 `debbie.integration`。

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.integration.enable` | `true` | 是否启用模块 |
| `debbie.integration.default-channel-type` | `direct` | 默认通道类型（`direct`/`queue`/`pubsub`） |
| `debbie.integration.queue-capacity` | `1024` | 队列通道默认容量 |
| `debbie.integration.auto-start` | `true` | 是否自动启动流上下文 |
| `debbie.integration.global-timeout` | `30000` | 全局超时（毫秒） |
| `debbie.integration.error-channel` | — | 错误通道名称 |

## 使用示例

### 基本消息发送

```java
var channel = new DirectChannel("greeting");
channel.subscribe(msg -> System.out.println("Received: " + msg.getPayload()));
channel.send(Message.of("Hello, Debbie!"));
```

### 集成流定义

```java
var flow = new IntegrationFlow("order-processing")
    .transform(obj -> ((Order) obj).getItems())
    .filter(items -> !items.isEmpty())
    .split(items -> items)
    .handle(msg -> processItem(msg.getPayload()));

flow.send(Message.of(order));
```

### 使用 Builder

```java
var flow = IntegrationFlow.builder()
    .name("pipeline")
    .fromDirect("input")
    .transform(obj -> String.valueOf(obj).toUpperCase())
    .filter(obj -> obj instanceof String s && !s.isEmpty())
    .handle(msg -> saveToDb(msg.getPayload()))
    .toDirect("output")
    .build();
```

### 流上下文管理

```java
var context = new IntegrationFlowContext();
context.start();

var flow = new IntegrationFlow("my-flow")
    .handle(msg -> System.out.println(msg.getPayload()));
context.register(flow);

context.send("my-flow", Message.of("data"));
context.stop();
```

### 队列通道

```java
var queue = new QueueChannel("task-queue", 100);
queue.send(Message.of(task1));
queue.send(Message.of(task2));

var task = queue.receive(5, TimeUnit.SECONDS);
```

### 发布订阅

```java
var pubsub = new PublishSubscribeChannel("events");
pubsub.subscribe(msg -> logEvent(msg));
pubsub.subscribe(msg -> auditEvent(msg));
pubsub.subscribe(msg -> metricsEvent(msg));

pubsub.send(Message.of(event));
```

### 路由

```java
var highPriority = new DirectChannel("high");
var lowPriority = new DirectChannel("low");

var router = new Router(
    obj -> ((Task) obj).getPriority() > 5 ? "high" : "low",
    Map.of("high", highPriority, "low", lowPriority)
);

router.route(Message.of(task));
```

## 模块信息

- **模块名**：`com.truthbean.debbie.integration`
- **SPI 服务**：`DebbieModuleStarter` → `IntegrationModuleStarter`
- **启动顺序**：59（在 tracing 之后）
- **依赖**：`debbie-core`、`debbie-mvc`（transitive）

## 不依赖 Spring

本模块完全不依赖任何 Spring 相关框架，仅使用 debbie 框架自身能力：
- `DebbieModuleStarter`（SPI 自动装配）
- `@PropertiesConfiguration` / `@PropertyInject`（配置注入）
- `SimpleBeanFactory` / `DebbieReflectionBeanFactory`（Bean 注册）
- `DebbieConfiguration`（配置接口）

## 许可证

Mulan PSL v2
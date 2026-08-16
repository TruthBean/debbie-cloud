# debbie-tracing

> Spring Cloud Sleuth / Micrometer Tracing like distributed tracing module — **without Spring**.

## 概述

`debbie-tracing` 是基于 debbie 框架的分布式链路追踪模块。它仅使用 `debbie-core`、`debbie-mvc` 和 JDK 内置功能（`java.net.http.HttpClient`）。

### 核心特性

- **Span/Trace 模型**：完整的 Span 生命周期管理（开始、结束、标签、事件、状态）
- **TraceContext 传播**：跨服务传递 traceId、spanId、parentSpanId
- **ID 生成**：128 位 traceId + 64 位 spanId（hex 编码）
- **采样策略**：始终采样（Always）、永不采样（Never）、概率采样（Probability）
- **多种上报器**：日志上报（Logging）、内存收集（InMemory）、HTTP 上报（Http）、组合上报（Composite）
- **线程局部 Span 栈**：自动管理当前线程的 Span 层级关系
- **SPI 自动装配**：通过 `DebbieModuleStarter` SPI 机制自动启动

## 架构

```
com.truthbean.debbie.tracing
├── Tracer                     — 主入口，管理 Span 生命周期
├── Span                       — Span 模型（标签、事件、状态、时长）
├── TraceContext               — 追踪上下文（traceId、spanId、parentSpanId）
├── TraceIdGenerator           — ID 生成器
├── TracingConfiguration       — 配置类（prefix: debbie.tracing）
├── TracingModuleStarter       — SPI 模块启动器
├── TracingException           — 异常类
├── sampler
│   ├── Sampler                — 采样器接口
│   ├── AlwaysSampler          — 始终采样
│   ├── NeverSampler           — 永不采样
│   ├── ProbabilitySampler     — 概率采样
│   └── SamplerFactory         — 采样器工厂
└── reporter
    ├── TraceReporter          — 上报器接口
    ├── LoggingReporter        — 日志上报
    ├── InMemoryReporter       — 内存收集（测试用）
    ├── HttpReporter           — HTTP 上报（Zipkin/Jaeger 兼容）
    └── CompositeReporter      — 组合上报
```

## 配置

所有配置项前缀为 `debbie.tracing`。

### 通用配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.tracing.enable` | `true` | 是否启用模块 |
| `debbie.tracing.service-name` | `application` | 服务名称（标记在 Span 上） |

### 采样器配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.tracing.sampler.strategy` | `always` | 采样策略：`always`/`never`/`probability` |
| `debbie.tracing.sampler.rate` | `0.1` | 概率采样率（0.0~1.0） |

### 上报器配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.tracing.reporter.type` | `logging` | 上报类型：`logging`/`http` |
| `debbie.tracing.reporter.endpoint` | — | HTTP 上报地址（如 Zipkin API） |
| `debbie.tracing.reporter.batch-size` | `100` | HTTP 批量上报大小 |
| `debbie.tracing.reporter.connect-timeout` | `5000` | HTTP 连接超时（毫秒） |

### 传播配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.tracing.propagation.type` | `w3c` | 传播格式：`w3c`/`b3` |
| `debbie.tracing.propagation.include-tags` | `false` | 是否在传播头中包含标签 |

## 使用示例

### 基本用法

```java
var reporter = new InMemoryReporter();
var tracer = new Tracer("order-service", new AlwaysSampler(), reporter);

// 开始一个 Span
var span = tracer.startSpan("place-order");
try {
    // 业务逻辑...
    span.tag("order.id", "12345");
} finally {
    tracer.endSpan(span);
}
```

### 嵌套 Span

```java
var tracer = new Tracer("order-service", new AlwaysSampler(), reporter);

var root = tracer.startSpan("place-order");
var child = tracer.startChildSpan("validate-order"); // 自动关联父 Span
// ...
tracer.endSpan(child);
tracer.endSpan(root);
```

### 使用 withSpan（自动管理生命周期）

```java
tracer.withSpan("place-order", () -> {
    tracer.withSpan("validate-order", () -> {
        // 验证逻辑
    });
    tracer.withSpan("save-order", () -> {
        // 保存逻辑
    });
});
// 所有 Span 自动结束并上报
```

### 错误记录

```java
tracer.withSpan("risky-operation", () -> {
    // 如果抛出异常，Span 自动标记为 ERROR
    // 并记录 exception.class 和 exception.message 标签
    throw new RuntimeException("connection refused");
});
```

### 概率采样

```java
// 只采样 10% 的请求
var sampler = new ProbabilitySampler(0.1);
var tracer = new Tracer("my-service", sampler, reporter);
```

### HTTP 上报到 Zipkin

```java
var reporter = new HttpReporter("http://zipkin:9411/api/v2/spans");
var tracer = new Tracer("my-service", new AlwaysSampler(), reporter);
```

### 组合上报

```java
var reporter = new CompositeReporter(
    new LoggingReporter(),
    new HttpReporter("http://zipkin:9411/api/v2/spans")
);
var tracer = new Tracer("my-service", new AlwaysSampler(), reporter);
```

## Span 属性

| 属性 | 说明 |
|------|------|
| `name` | Span 名称（操作名） |
| `traceId` | 追踪链路 ID（32 hex chars） |
| `spanId` | 当前 Span ID（16 hex chars） |
| `parentSpanId` | 父 Span ID（根 Span 为 null） |
| `startMicros` / `endMicros` | 开始/结束时间（微秒） |
| `status` | 状态：`OK` / `ERROR` |
| `tags` | 键值对标签 |
| `events` | 时间戳事件 |
| `sampled` | 是否被采样 |

## 依赖

- `debbie-core` — 核心框架
- `debbie-mvc` — MVC 框架
- `java.net.http` — JDK 内置 HTTP 客户端（用于 HttpReporter）

**不依赖任何 Spring 相关框架。**

## 测试

```bash
mvn -f cloud/tracing/pom.xml test
```

55 个单元测试覆盖：ID 生成、TraceContext、Span 生命周期、三种采样器、四种上报器、Tracer 嵌套追踪、配置、异常、集成测试。

## 许可证

Mulan PSL v2
# debbie-sentinel

Alibaba Sentinel 流量控制框架集成模块，基于 [Sentinel](https://sentinelguard.io/) 1.8.x，为 debbie 框架提供熔断降级、流量控制、系统自适应限流等能力。

## 特性

- 自动装配 Sentinel 配置和规则管理器，通过 SPI 注册 `DebbieModuleStarter` 实现
- 支持完整的 Sentinel 核心配置（应用名、字符集、metric 文件、统计参数等）
- 支持 Sentinel Dashboard 通信配置（dashboard 地址、心跳间隔、客户端端口）
- 支持四种规则管理：FlowRule、DegradeRule、SystemRule、AuthorityRule
- 支持系统自适应限流默认配置（系统负载、CPU 使用率、平均 RT、线程数、QPS）
- 支持 Web Filter 配置（block page、URL patterns、filter order）
- 通过系统属性配置 Sentinel transport，兼容 sentinel-transport-simple-http
- 无 Spring 依赖，纯 debbie 框架实现

## 依赖

- `debbie-core` — debbie 框架核心
- `sentinel-core` 1.8.x — Sentinel 核心库

可选依赖（用于 Dashboard 通信）：

- `sentinel-transport-simple-http` — Sentinel HTTP transport，实现与 Dashboard 的心跳和命令通信

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-sentinel</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置

在 `debbie.properties` 或环境变量中配置：

```properties
# 应用名（注册到 Sentinel Dashboard）
debbie.sentinel.app-name=my-application

# Sentinel Dashboard 地址
debbie.sentinel.transport.dashboard=localhost:8080

# 客户端监听端口
debbie.sentinel.transport.port=8719

# 心跳间隔（毫秒）
debbie.sentinel.transport.heartbeat-interval-ms=10000

# 日志目录
debbie.sentinel.log-dir=/var/log/sentinel

# 字符集
debbie.sentinel.charset=UTF-8

# Metric 文件配置
debbie.sentinel.metric.file-size=52428800
debbie.sentinel.metric.file-count=6
debbie.sentinel.metric.flush-interval=1

# 统计参数
debbie.sentinel.statistic.max-rt=4900
debbie.sentinel.cold-factor=3

# 系统自适应限流默认值
debbie.sentinel.system.load=2.0
debbie.sentinel.system.cpu-usage=0.8
debbie.sentinel.system.avg-rt=100
debbie.sentinel.system.max-thread=500
debbie.sentinel.system.qps=1000

# Web Filter
debbie.sentinel.block-page=/sentinel-block
debbie.sentinel.filter.enabled=true
debbie.sentinel.filter.url-patterns=/*
debbie.sentinel.filter.order=-2147483648

# 启用/禁用模块
debbie.sentinel.enable=true
```

### 3. 使用

通过 `@BeanInject` 注入 `SentinelManagerFactory` 加载规则：

```java
@Router
public class FlowControlRouter {

    @BeanInject
    private SentinelManagerFactory sentinelManager;

    @PostRouter
    public void initRules() {
        var flowRules = new ArrayList<FlowRule>();
        var rule = new FlowRule("orderService");
        rule.setCount(100);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRules.add(rule);
        sentinelManager.loadFlowRules(flowRules);

        var degradeRules = new ArrayList<DegradeRule>();
        var degradeRule = new DegradeRule("orderService");
        degradeRule.setGrade(DegradeRule.DEGRADE_GRADE_EXCEPTION_RATIO);
        degradeRule.setCount(0.5);
        degradeRule.setTimeWindow(10);
        degradeRules.add(degradeRule);
        sentinelManager.loadDegradeRules(degradeRules);
    }
}
```

直接使用 Sentinel API：

```java
try (var entry = SphU.entry("resourceName")) {
    // 业务逻辑
} catch (BlockException e) {
    // 被限流/降级的处理
}
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.sentinel.enable` | boolean | `true` | 启用/禁用模块 |
| `debbie.sentinel.app-name` | String | - | 应用名（注册到 Dashboard） |
| `debbie.sentinel.eager` | boolean | `false` | 是否立即初始化 transport |
| **日志** ||||
| `debbie.sentinel.log-dir` | String | - | 日志目录 |
| `debbie.sentinel.log-name-prefix` | String | `sentinel-record` | 日志文件名前缀 |
| `debbie.sentinel.log-use-pid` | boolean | `false` | 日志文件名是否使用 PID |
| `debbie.sentinel.charset` | String | `UTF-8` | 字符集 |
| **Transport / Dashboard** ||||
| `debbie.sentinel.transport.dashboard` | String | - | Dashboard 地址（如 `localhost:8080`） |
| `debbie.sentinel.transport.port` | int | `8719` | 客户端监听端口 |
| `debbie.sentinel.transport.heartbeat-interval-ms` | long | `10000` | 心跳间隔（毫秒） |
| `debbie.sentinel.transport.client-ip` | String | - | 客户端 IP |
| **Metric** ||||
| `debbie.sentinel.metric.file-size` | long | `52428800` | 单个 metric 文件大小（字节） |
| `debbie.sentinel.metric.file-count` | int | `6` | metric 文件总数 |
| `debbie.sentinel.metric.flush-interval` | long | `1` | metric 刷新间隔（秒） |
| **统计** ||||
| `debbie.sentinel.statistic.max-rt` | int | `4900` | 最大 RT（毫秒） |
| `debbie.sentinel.cold-factor` | int | `3` | 冷启动因子 |
| **系统自适应限流** ||||
| `debbie.sentinel.system.load` | double | `-1` | 系统负载阈值（-1 表示不设置） |
| `debbie.sentinel.system.cpu-usage` | double | `-1` | CPU 使用率阈值（0.0~1.0） |
| `debbie.sentinel.system.avg-rt` | long | `-1` | 平均 RT 阈值（毫秒） |
| `debbie.sentinel.system.max-thread` | long | `-1` | 最大线程数阈值 |
| `debbie.sentinel.system.qps` | double | `-1` | 入口 QPS 阈值 |
| **Web Filter** ||||
| `debbie.sentinel.block-page` | String | - | 被限流后的跳转页面 |
| `debbie.sentinel.filter.enabled` | boolean | `true` | 是否启用 Web Filter |
| `debbie.sentinel.filter.url-patterns` | String | `/*` | URL 匹配模式（逗号分隔） |
| `debbie.sentinel.filter.order` | int | `-2147483648` | Filter 顺序 |

## 架构

```
SentinelConfiguration    — 配置类，绑定 debbie.sentinel.* 属性
SentinelManagerFactory   — Sentinel 管理器，负责初始化配置和规则管理
SentinelModuleStarter    — SPI 模块启动器，自动装配 Bean
```

### SPI 自动装配

模块通过 `module-info.java` 中的 `provides DebbieModuleStarter with SentinelModuleStarter` 注册，
debbie 框架启动时自动发现并执行：

1. `registerBean()` — 注册 `SentinelConfiguration` Bean
2. `starter()` — 创建 `SentinelManagerFactory`，调用 `init()` 初始化 Sentinel 配置，注册为 Bean
3. `release()` — 关闭管理器，释放资源

### 初始化流程

`SentinelManagerFactory.init()` 执行以下步骤：

1. `initSentinelConfig()` — 配置应用名、字符集、metric 文件大小/数量、统计参数、冷启动因子
2. `initLogConfig()` — 配置日志目录、PID 使用、字符集（通过系统属性）
3. `initTransportConfig()` — 配置 Dashboard 地址、心跳间隔、客户端端口（通过系统属性）
4. `initSystemRules()` — 加载系统自适应限流默认规则

### 规则管理

`SentinelManagerFactory` 提供四种规则的加载和查询：

| 方法 | 说明 |
|------|------|
| `loadFlowRules(List<FlowRule>)` | 加载流控规则 |
| `getFlowRules()` | 获取当前流控规则 |
| `loadDegradeRules(List<DegradeRule>)` | 加载降级规则 |
| `getDegradeRules()` | 获取当前降级规则 |
| `loadSystemRules(List<SystemRule>)` | 加载系统规则 |
| `getSystemRules()` | 获取当前系统规则 |
| `loadAuthorityRules(List<AuthorityRule>)` | 加载授权规则 |
| `getAuthorityRules()` | 获取当前授权规则 |

## 模块信息

- **模块名**: `com.truthbean.debbie.sentinel`
- **启动顺序**: `270`
- **配置前缀**: `debbie.sentinel`
- **Java 版本**: 17+
- **Sentinel 版本**: 1.8.10

## 许可证

Mulan PSL v2
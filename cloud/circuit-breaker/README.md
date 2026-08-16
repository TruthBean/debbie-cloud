# debbie-circuit-breaker

> truthbean debbie circuit breaker framework.

## 概述

`debbie-circuit-breaker` 提供熔断（Circuit Breaking）、降级（Fallback）和限流（Rate Limiting）三大弹性能力，仅依赖 `debbie-core`，不引入任何 Spring 框架。

### 核心特性

| 特性 | 说明 |
|------|------|
| **熔断器状态机** | CLOSED → OPEN → HALF_OPEN → CLOSED/OPEN 三态切换 |
| **滑动窗口计数** | 基于环形缓冲区的滑动窗口统计失败率/慢调用率 |
| **可配置阈值** | 失败率阈值、慢调用率阈值、最小调用次数、等待时长等 |
| **降级处理** | `FallbackHandler<T>` 接口，调用失败或熔断时返回降级值 |
| **限流器** | 令牌桶算法（Token Bucket），支持阻塞式和非阻塞式获取 |
| **事件监听** | 状态转换、成功、失败、拒绝事件均可监听 |
| **注册表** | `CircuitBreakerRegistry` 统一管理命名的熔断器和限流器实例 |
| **自动装配** | 通过 `DebbieModuleStarter` SPI 自动注册到 debbie 容器 |

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-circuit-breaker</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 基本用法

```java
// 创建熔断器
CircuitBreakerConfig config = CircuitBreakerConfig.builder()
    .failureRateThreshold(50f)          // 失败率阈值 50%
    .minimumNumberOfCalls(10)           // 至少 10 次调用后才计算
    .slidingWindowSize(100)             // 滑动窗口大小
    .waitDurationInOpenStateMillis(60_000) // OPEN 状态等待 60s
    .permittedNumberOfCallsInHalfOpenState(10) // HALF_OPEN 允许 10 次试探
    .build();

CircuitBreaker cb = new CircuitBreakerImpl("order-service", config);

// 执行受保护的调用
try {
    String result = cb.execute(() -> callRemoteService());
} catch (CircuitBreakerOpenException e) {
    // 熔断器开启，调用被拒绝
} catch (Exception e) {
    // 业务异常
}

// 带降级的调用
String result = cb.execute(
    () -> callRemoteService(),
    throwable -> "fallback-value"  // 降级处理
);
```

### 3. 通过注册表使用

```java
CircuitBreakerRegistry registry = new CircuitBreakerRegistry();

// 获取或创建命名的熔断器
CircuitBreaker cb = registry.getOrCreate("order-service");

// 获取或创建命名的限流器
RateLimiter rl = registry.getOrCreateRateLimiter("order-api");
if (rl.tryAcquire()) {
    // 执行调用
}
```

### 4. 在 debbie 应用中使用

模块通过 SPI 自动装配，注入 `CircuitBreakerRegistry` 到容器中：

```java
// 在其他模块中获取注册表
CircuitBreakerRegistry registry = applicationContext
    .getGlobalBeanFactory()
    .factory(CircuitBreakerRegistry.class);

CircuitBreaker cb = registry.getOrCreate("my-service");
```

## 状态机

```
         失败率 >= 阈值
    CLOSED ──────────────► OPEN
      ▲                     │
      │                     │ 等待时长结束
      │ 成功试探足够         │
      │                     ▼
    CLOSED ◄──────────── HALF_OPEN
                              │
                              │ 失败试探过多
                              ▼
                            OPEN
```

| 状态 | 行为 |
|------|------|
| **CLOSED** | 正常放行调用，滑动窗口内统计失败率/慢调用率，超过阈值则切换到 OPEN |
| **OPEN** | 拒绝所有调用（抛出 `CircuitBreakerOpenException`），等待 `waitDurationInOpenState` 后切换到 HALF_OPEN |
| **HALF_OPEN** | 允许有限次试探调用，根据成功率决定切换到 CLOSED 或回退到 OPEN |

## 配置

### 熔断器配置 (`CircuitBreakerConfig`)

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `failureRateThreshold` | 50.0 | 失败率阈值（百分比），超过则开启熔断 |
| `slowCallRateThreshold` | 100.0 | 慢调用率阈值（百分比） |
| `waitDurationInOpenStateMillis` | 60000 | OPEN 状态等待时长（毫秒） |
| `permittedNumberOfCallsInHalfOpenState` | 10 | HALF_OPEN 允许的试探调用数 |
| `slidingWindowSize` | 100 | 滑动窗口大小 |
| `minimumNumberOfCalls` | 10 | 计算失败率前的最小调用次数 |
| `slowCallDurationThresholdMillis` | 60000 | 慢调用时长阈值（毫秒） |
| `automaticTransitionFromOpenToHalfOpen` | false | 是否自动从 OPEN 转换到 HALF_OPEN |

### 限流器配置 (`RateLimiterConfig`)

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `limitForPeriod` | 50 | 每个刷新周期允许的调用数 |
| `limitRefreshPeriodMillis` | 1000 | 刷新周期（毫秒） |

### Properties 配置

通过 `debbie.circuit-breaker` 前缀配置：

```properties
# 熔断器全局默认
debbie.circuit-breaker.enable=true
debbie.circuit-breaker.failure-rate-threshold=50
debbie.circuit-breaker.slow-call-rate-threshold=100
debbie.circuit-breaker.wait-duration-in-open-state=60000
debbie.circuit-breaker.permitted-number-of-calls-in-half-open=10
debbie.circuit-breaker.sliding-window-size=100
debbie.circuit-breaker.minimum-number-of-calls=10
debbie.circuit-breaker.slow-call-duration-threshold=60000
debbie.circuit-breaker.automatic-transition-from-open-to-half-open=false

# 限流器全局默认
debbie.circuit-breaker.rate-limiter.enable=true
debbie.circuit-breaker.rate-limiter.limit-for-period=50
debbie.circuit-breaker.rate-limiter.limit-refresh-period=1000
```

## API 详解

### CircuitBreaker

```java
public interface CircuitBreaker {
    // 执行 Callable，受熔断器保护
    <T> T execute(Callable<T> task) throws Exception;

    // 执行 Callable，带降级处理
    <T> T execute(Callable<T> task, FallbackHandler<T> fallback);

    // 执行 Runnable，受熔断器保护
    void execute(Runnable task) throws Exception;

    // 执行 Runnable，带降级
    void execute(Runnable task, Runnable fallback);

    // 状态查询
    CircuitBreakerState getState();
    float getFailureRate();
    int getNumberOfBufferedCalls();
    int getNumberOfFailedCalls();
    int getNumberOfSuccessfulCalls();

    // 重置
    void reset();

    // 监听器
    void addListener(CircuitBreakerListener listener);
    void removeListener(CircuitBreakerListener listener);
}
```

### FallbackHandler

```java
@FunctionalInterface
public interface FallbackHandler<T> {
    T handle(Throwable throwable);
}

// 使用示例
FallbackHandler<String> fallback = t -> {
    if (t instanceof CircuitBreakerOpenException) {
        return "service-unavailable";
    }
    return "error: " + t.getMessage();
};

String result = cb.execute(() -> callRemote(), fallback);
```

### RateLimiter

```java
RateLimiter limiter = new SimpleRateLimiter("api",
    RateLimiterConfig.builder()
        .limitForPeriod(100)
        .limitRefreshPeriodMillis(1000)
        .build());

// 非阻塞
if (limiter.tryAcquire()) {
    // 执行调用
}

// 阻塞
limiter.acquire(); // 等待直到获取许可
```

### CircuitBreakerListener

```java
cb.addListener(new CircuitBreakerListener() {
    @Override
    public void onStateTransition(CircuitBreakerStateTransitionEvent event) {
        log.info("circuit {} transitioned: {} -> {}",
            event.getCircuitBreakerName(),
            event.getFromState(),
            event.getState());
    }

    @Override
    public void onSuccess(long durationMillis) {
        log.debug("call succeeded in {}ms", durationMillis);
    }

    @Override
    public void onError(Throwable t, long durationMillis) {
        log.warn("call failed in {}ms", durationMillis, t);
    }

    @Override
    public void onCallRejected() {
        log.warn("call rejected by open circuit");
    }
});
```

## 模块结构

```
cloud/circuit-breaker/
├── pom.xml
├── src/main/java/
│   ├── module-info.java
│   └── com/truthbean/debbie/circuitbreaker/
│       ├── CircuitBreaker.java                 # 核心接口
│       ├── CircuitBreakerImpl.java             # 默认实现（状态机 + 滑动窗口）
│       ├── CircuitBreakerConfig.java           # 不可变配置（Builder 模式）
│       ├── CircuitBreakerState.java            # 状态枚举
│       ├── CircuitBreakerRegistry.java         # 注册表
│       ├── CircuitBreakerListener.java         # 事件监听器接口
│       ├── CircuitBreakerOpenException.java    # 熔断开启异常
│       ├── CircuitBreakerConfiguration.java    # debbie 配置类
│       ├── CircuitBreakerModuleStarter.java    # 模块启动器
│       ├── fallback/
│       │   ├── FallbackHandler.java            # 降级接口
│       │   └── DefaultFallbackHandler.java     # 默认降级实现
│       ├── ratelimit/
│       │   ├── RateLimiter.java                # 限流器接口
│       │   ├── RateLimiterConfig.java          # 限流配置
│       │   └── SimpleRateLimiter.java          # 令牌桶实现
│       └── event/
│           ├── CircuitBreakerEvent.java        # 事件基类
│           └── CircuitBreakerStateTransitionEvent.java  # 状态转换事件
└── src/test/java/
    ├── module-info.java
    └── com/truthbean/debbie/circuitbreaker/test/
        └── CircuitBreakerTest.java             # 21 个单元测试
```

## 与 Spring Cloud Circuit Breaker / Resilience4j 对比

| 特性 | Spring Cloud Circuit Breaker | Resilience4j | debbie-circuit-breaker |
|------|-----|-------------|------------------------|
| 熔断器 | ✅（封装 Resilience4j 等） | ✅ | ✅ |
| 降级 | ✅ | ✅ | ✅ |
| 限流 | ✅ | ✅ | ✅ |
| 重试 | ✅ | ✅ | ❌（可后续添加） |
| 缓存 | ❌ | ✅ | ❌ |
| 舱壁隔离 | ❌ | ✅ | ❌（可后续添加） |
| 时间限制器 | ❌ | ✅ | ❌ |
| 注解驱动 | ✅（`@CircuitBreaker`） | ✅（`@CircuitBreaker`） | ❌（可后续添加） |
| Spring 依赖 | ✅ | ❌ | ❌ |
| JPMS 模块化 | ❌ | ❌ | ✅ |

## 自动装配

模块通过 `META-INF/services/com.truthbean.debbie.boot.DebbieModuleStarter` SPI 文件自动注册：

```
com.truthbean.debbie.circuitbreaker.CircuitBreakerModuleStarter
```

启动时 `CircuitBreakerModuleStarter` 会：
1. 读取 `CircuitBreakerConfiguration`（前缀 `debbie.circuit-breaker`）
2. 创建 `CircuitBreakerRegistry` 并注册为 bean
3. 其他模块通过 `CircuitBreakerRegistry` 获取命名的熔断器和限流器

## 测试

```bash
mvn -f cloud/circuit-breaker/pom.xml test
```

21 个测试覆盖：
- 状态机转换（CLOSED → OPEN → HALF_OPEN → CLOSED/OPEN）
- 失败率计算和阈值触发
- 调用拒绝（`CircuitBreakerOpenException`）
- 降级处理（Callable 和 Runnable）
- 限流器令牌桶（获取、拒绝、刷新）
- 注册表（创建、缓存、移除）
- 事件监听器（状态转换通知）
- 配置校验（Builder 参数验证）
- 重置

## 依赖

| 依赖 | 版本 | scope | 说明 |
|------|------|-------|------|
| `debbie-core` | `${truthbean.version}` | compile | 核心框架（IOC、配置、事件） |
| `debbie-test` | `${truthbean.version}` | test | 测试支持 |
| `truthbean-stdout-boot` | `${truthbean.version}` | test | 测试启动器 |

**无任何 Spring 依赖。**

## License

Mulan PSL v2

## 作者

TruthBean / Rogar·Q (truthbean@outlook.com)
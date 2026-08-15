# debbie-function

> truthbean debbie function framework — a [Spring Cloud Function](https://spring.io/projects/spring-cloud-function) like module for functional programming, function registry, composition, routing and HTTP binding **without any Spring dependency**.

## 概述

`debbie-function` 将 `Function<T, R>`、`Consumer<T>`、`Supplier<T>` 提升为一等公民，提供：

| 能力 | 说明 |
|------|------|
| **函数注册表** | 按名称注册和查找 Function / Consumer / Supplier |
| **函数组合** | `fn1|fn2|fn3` 管道式组合，前一个函数的输出作为后一个的输入 |
| **函数路由** | 根据 Message Header 或自定义路由函数将消息分发到目标函数 |
| **HTTP 绑定** | 将注册的函数暴露为 HTTP 端点（GET 调用 Supplier，POST 调用 Function） |
| **消息信封** | `FunctionMessage<T>` 携带 payload 和 headers |

仅依赖 `debbie-core` 和 `debbie-mvc`，**不引入任何 Spring 框架**。

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-function</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 注册和使用函数

```java
// 从容器获取注册表
FunctionRegistry registry = applicationContext
    .getGlobalBeanFactory()
    .factory(FunctionRegistry.class);

// 注册 Function
registry.register("uppercase", (String s) -> s.toUpperCase());
registry.register("reverse", (String s) -> new StringBuilder(s).reverse().toString());

// 注册 Supplier
registry.registerSupplier("timestamp", () -> System.currentTimeMillis());

// 注册 Consumer
registry.registerConsumer("log", (String s) -> System.out.println("LOG: " + s));

// 调用
String result = registry.invoke("uppercase", "hello");  // "HELLO"
long time = registry.invokeSupplier("timestamp");
registry.invoke("log", "something");
```

### 3. 函数组合

```java
FunctionComposition composition = applicationContext
    .getGlobalBeanFactory()
    .factory(FunctionComposition.class);

// 注册函数
registry.register("uppercase", (String s) -> s.toUpperCase());
registry.register("reverse", (String s) -> new StringBuilder(s).reverse().toString());
registry.register("addPrefix", (String s) -> ">> " + s);

// 组合调用: uppercase -> reverse -> addPrefix
String result = composition.compose("uppercase|reverse|addPrefix", "hello");
// 结果: ">> OLLEH"

// 组合为可复用的 Function 对象
Function<String, String> pipeline = composition.composeAsFunction("uppercase|reverse");
String applied = pipeline.apply("hello");  // "OLLEH"
```

### 4. 函数路由

```java
FunctionRouter router = applicationContext
    .getGlobalBeanFactory()
    .factory(FunctionRouter.class);

// 注册函数
registry.register("processA", (String s) -> "A:" + s);
registry.register("processB", (String s) -> "B:" + s);

// 按消息头路由
var message = FunctionMessage.of("data", "processA");
var result = router.route(message);
// result.getPayload() = "A:data"

// 自定义路由函数
router.withRoutingFunction(msg -> msg.getPayload().length() > 10 ? "processA" : "processB");

// 带默认函数的路由
var result2 = router.route(FunctionMessage.of("data"), "processB");
```

### 5. HTTP 端点

启用 `debbie.function.http.enable=true` 后，函数自动暴露为 HTTP 端点：

```
GET  /function                — 列出所有已注册函数名
GET  /function/{name}         — 调用 Supplier
POST /function/{name}         — 调用 Function/Consumer，请求体作为输入
```

示例：
```bash
# 列出所有函数
curl http://localhost:8080/function

# 调用 Supplier
curl http://localhost:8080/function/timestamp

# 调用 Function
curl -X POST http://localhost:8080/function/uppercase \
     -H "Content-Type: application/json" \
     -d '"hello"'
# 返回: "HELLO"

# 调用组合函数
curl -X POST http://localhost:8080/function/uppercase|reverse \
     -H "Content-Type: application/json" \
     -d '"hello"'
# 返回: "OLLEH"
```

## API 详解

### FunctionWrapper

统一封装 `Function`、`Consumer`、`Supplier`：

```java
// 创建
var fn = FunctionWrapper.ofFunction("double", (Integer x) -> x * 2);
var cn = FunctionWrapper.ofConsumer("print", (String s) -> System.out.println(s));
var sp = FunctionWrapper.ofSupplier("answer", () -> 42);

// 调用
fn.invoke(5);     // 10
cn.invoke("hi"); // void
sp.invoke();     // 42

// 组合
var composed = fn.andThen((Integer x) -> x + 1);  // double then addOne
composed.invoke(5);  // 11

// 转为 Function
Function<Integer, Integer> asFn = fn.asFunction();
```

### FunctionRegistry

线程安全的函数注册表：

```java
// 注册
registry.register("name", function);
registry.registerConsumer("name", consumer);
registry.registerSupplier("name", supplier);

// 查找
var wrapper = registry.lookup("name");
boolean exists = registry.contains("name");
var names = registry.getFunctionNames();

// 调用
var result = registry.invoke("name", input);
var result = registry.invokeSupplier("name");

// 管理
registry.remove("name");
registry.clear();
int count = registry.size();
```

### FunctionMessage

消息信封，携带 payload 和 headers：

```java
var msg = FunctionMessage.of("hello")
    .setFunctionName("uppercase")
    .addHeader("source", "test");

msg.getPayload();       // "hello"
msg.getFunctionName();  // "uppercase"
msg.getHeader("source"); // "test"

// 转换 payload，保留 headers
var newMsg = msg.withPayload(42);
```

### FunctionComposition

管道式函数组合：

```java
// 解析组合表达式
List<String> names = composition.parse("fn1|fn2|fn3");

// 组合调用
var result = composition.compose("fn1|fn2|fn3", input);

// 组合为 Function 对象
Function<I, O> fn = composition.composeAsFunction("fn1|fn2|fn3");

// 组合为 FunctionWrapper
var wrapper = composition.composeAsWrapper("pipeline", "fn1|fn2|fn3");
```

### FunctionRouter

消息路由：

```java
// 按消息头路由
router.route(FunctionMessage.of("data", "functionName"));

// 自定义路由函数
router.withRoutingFunction(msg -> decideFunction(msg));

// 路由规则
router.addRoutingRule("type", "processA");

// 带默认函数
router.route(message, "defaultFunction");
```

### FunctionBinding / HttpFunctionBinding

绑定接口，将函数暴露为可调用端点：

```java
var binding = new HttpFunctionBinding();
binding.bind(registry);

// 调用
var result = binding.invoke("functionName", input);
var result = binding.invokeSupplier("supplierName");

// 处理消息
var outputMsg = binding.process(inputMessage);

// 解绑
binding.unbind();
```

## 配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `debbie.function.enable` | true | 是否启用 function 模块 |
| `debbie.function.http.enable` | true | 是否暴露 HTTP 端点 |
| `debbie.function.http.prefix` | /function | HTTP 端点前缀 |
| `debbie.function.routing.enable` | true | 是否启用函数路由 |
| `debbie.function.composition.enable` | true | 是否启用函数组合 |
| `debbie.function.auto-scan` | false | 是否自动扫描注册函数（待实现） |

## 架构

```
                    ┌──────────────────┐
                    │ FunctionRegistry │
                    │  (name → wrapper)│
                    └────┬─────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
  ┌─────▼─────┐  ┌──────▼──────┐  ┌─────▼─────┐
  │Composition│  │   Router    │  │  Binding  │
  │ fn1|fn2   │  │ header→fn   │  │  (HTTP)   │
  └───────────┘  └─────────────┘  └─────┬─────┘
                                        │
                                 ┌──────▼──────┐
                                 │  Endpoint   │
                                 │ GET  /fn    │
                                 │ POST /fn    │
                                 └─────────────┘
```

## 自动装配

模块通过 `META-INF/services/com.truthbean.debbie.boot.DebbieModuleStarter` SPI 自动注册：

```
com.truthbean.debbie.function.FunctionModuleStarter
```

启动时创建并注册以下 bean：
- `FunctionRegistry` — 函数注册表
- `FunctionComposition` — 函数组合器
- `FunctionRouter` — 函数路由器
- `HttpFunctionBinding` — HTTP 绑定
- `FunctionEndpoint` — HTTP 端点（如果 `http.enable=true`）

## 模块结构

```
cloud/function/
├── pom.xml
├── src/main/java/
│   ├── module-info.java
│   └── com/truthbean/debbie/function/
│       ├── FunctionWrapper.java              # 统一函数封装
│       ├── FunctionRegistry.java             # 函数注册表
│       ├── FunctionComposition.java          # 函数组合
│       ├── FunctionMessage.java              # 消息信封
│       ├── FunctionEndpoint.java             # HTTP 端点
│       ├── FunctionConfiguration.java        # debbie 配置
│       ├── FunctionModuleStarter.java        # 模块启动器
│       ├── FunctionException.java            # 异常
│       ├── binding/
│       │   ├── FunctionBinding.java          # 绑定接口
│       │   └── HttpFunctionBinding.java      # HTTP 绑定实现
│       └── routing/
│           └── FunctionRouter.java           # 函数路由
└── src/test/java/
    ├── module-info.java
    └── com/truthbean/debbie/function/test/
        └── FunctionTest.java                 # 39 个单元测试
```

## 与 Spring Cloud Function 对比

| 特性 | Spring Cloud Function | debbie-function |
|------|----------------------|-----------------|
| Function/Consumer/Supplier | ✅ | ✅ |
| 函数注册表 | ✅ | ✅ |
| 函数组合 | ✅ | ✅ |
| 函数路由 | ✅ | ✅ |
| HTTP 绑定 | ✅ | ✅ |
| 消息信封 (Message) | ✅ | ✅ |
| Stream 集成 (Kafka/Rabbit) | ✅ | ❌ (可后续添加) |
| @Bean 函数自动扫描 | ✅ | ❌ (auto-scan 待实现) |
| AWS Lambda 集成 | ✅ | ❌ |
| Spring 依赖 | ✅ | ❌ |
| JPMS 模块化 | ❌ | ✅ |

## 测试

```bash
mvn -f cloud/function/pom.xml test
```

39 个测试覆盖：
- FunctionWrapper：Function/Consumer/Supplier 调用、andThen/compose 组合、asFunction 转换
- FunctionRegistry：注册、查找、调用、列举、删除、异常
- FunctionMessage：payload/headers、函数名、withPayload
- FunctionComposition：表达式解析、链式调用、组合为 Function/Wrapper、异常
- FunctionRouter：按 header 路由、自定义路由函数、路由规则、默认函数
- HttpFunctionBinding：invoke、invokeSupplier、process、未绑定异常、unbind
- FunctionConfiguration：默认值、copy

## 依赖

| 依赖 | 版本 | scope | 说明 |
|------|------|-------|------|
| `debbie-core` | `${truthbean.version}` | compile | 核心框架（IOC、配置、事件） |
| `debbie-mvc` | `${truthbean.version}` | compile | MVC 框架（HTTP 路由） |
| `debbie-test` | `${truthbean.version}` | test | 测试支持 |
| `truthbean-stdout-boot` | `${truthbean.version}` | test | 测试启动器 |

**无任何 Spring 依赖。**

## License

Mulan PSL v2

## 作者

TruthBean / Rogar·Q (truthbean@outlook.com)
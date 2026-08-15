# debbie-gateway

一个不依赖 Spring 框架的轻量级 API 网关模块，灵感来自 [Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/)，
基于 [debbie](https://github.com/TruthBean/debbie-cloud) 框架自身的 MVC 过滤器机制与 `java.net.http.HttpClient` 实现。

通过路由谓词（Predicate）匹配 incoming 请求，经过过滤器链（Filter Chain）预处理后，
将请求代理转发到后端服务，再将响应经过过滤器链后处理返回给客户端。

---

## 目录

- [特性](#特性)
- [依赖关系](#依赖关系)
- [快速开始](#快速开始)
- [配置项](#配置项)
- [核心概念](#核心概念)
- [路由定义](#路由定义)
- [内置谓词](#内置谓词)
- [内置过滤器](#内置过滤器)
- [自定义谓词](#自定义谓词)
- [自定义过滤器](#自定义过滤器)
- [请求流转](#请求流转)
- [API 速查](#api-速查)
- [设计说明](#设计说明)
- [示例](#示例)

---

## 特性

- **零 Spring 依赖**：仅依赖 `debbie-core` 与 `debbie-mvc`，使用 debbie 自有的 `RouterFilter`、`@PropertiesConfiguration`、`DebbieModuleStarter` 等机制。
- **JDK 原生 HTTP 客户端**：代理转发使用 `java.net.http.HttpClient`（JDK 11+ 内置），无需额外依赖。
- **路由谓词匹配**：内置 `PathPredicate`（Ant 风格路径）、`MethodPredicate`（HTTP 方法）、`HeaderPredicate`（请求头正则），支持自定义扩展。
- **过滤器链**：`GatewayFilter` 支持 `preFilter`（转发前）与 `postFilter`（转发后），按 `order` 排序，pre 正序、post 逆序执行。
- **内置过滤器**：`StripPrefixFilter`（去除路径前缀）、`AddPrefixPathFilter`（添加路径前缀）。
- **自动重试**：代理失败时按 `max-retries` 自动重试，超出后返回 `502 Bad Gateway`。
- **JPMS 模块化**：自带 `module-info.java`，模块名 `com.truthbean.cloud.gateway`。
- **自动装配**：通过 `DebbieModuleStarter` SPI 自动注册到 debbie 应用上下文，引入依赖即生效。
- **与 MVC 共存**：未匹配到网关路由的请求会继续走 debbie MVC 正常路由，可在同一应用中同时提供网关与本地接口。

---

## 依赖关系

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-gateway</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

模块依赖图：

```
debbie-gateway
    ├── debbie-core  (transitive)
    └── debbie-mvc   (transitive)
```

不引入任何 `spring-*`、`spring-boot-*` 或 `spring-cloud-*` 依赖。代理转发使用 JDK 内置的 `java.net.http.HttpClient`。

---

## 快速开始

### 1. 引入依赖

在应用的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-gateway</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置（可选）

在 `application.properties` 中：

```properties
# 启用网关（默认 true）
debbie.gateway.enable=true
# 连接后端服务的超时时间（毫秒）
debbie.gateway.connect-timeout=5000
# 读取后端响应的超时时间（毫秒）
debbie.gateway.read-timeout=30000
# 代理失败时的最大重试次数
debbie.gateway.max-retries=3
```

### 3. 定义路由

在应用启动后，通过 `GatewayRouteLocator` 注册路由：

```java
@DebbieBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        DebbieApplication.run(GatewayApplication.class, args);
    }
}

@BeanComponent
public class GatewayRouteRegistrar {

    @EventMethodListener
    public void onReady(DebbieReadyEvent event) {
        var ctx = event.getApplicationContext();
        var routeLocator = ctx.getGlobalBeanFactory().factory(GatewayRouteLocator.class);

        // 路由1: /api/order/** -> http://localhost:8081
        routeLocator.addRoute(new GatewayRoute("order-service", "http://localhost:8081")
                .addPredicate(new PathPredicate("/api/order/**"))
                .addFilter(new StripPrefixFilter(2))  // 去除 /api/order 前缀
                .setOrder(10));

        // 路由2: /api/user/** GET -> http://localhost:8082
        routeLocator.addRoute(new GatewayRoute("user-service", "http://localhost:8082")
                .addPredicate(new PathPredicate("/api/user/**"))
                .addPredicate(new MethodPredicate("GET"))
                .setOrder(20));
    }
}
```

### 4. 启动应用

debbie 应用启动时会自动通过 SPI 加载 `GatewayModuleStarter`，完成 `GatewayRouteLocator`、`GatewayProxyHandler`、`GatewayRouterFilter` 的装配，并注册到 MVC 过滤器链（order=-10，最高优先级）。

```
请求 /api/order/123  -->  debbie-gateway  -->  http://localhost:8081/123
请求 /api/user/list  -->  debbie-gateway  -->  http://localhost:8082/api/user/list
请求 /local/hello    -->  未匹配网关路由   -->  debbie MVC 正常处理
```

---

## 配置项

所有配置项前缀为 `debbie.gateway`，通过 `GatewayConfiguration` 类承载。

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.gateway.enable` | boolean | `true` | 是否启用网关模块 |
| `debbie.gateway.connect-timeout` | int | `5000` | 连接后端服务的超时时间（毫秒） |
| `debbie.gateway.read-timeout` | int | `30000` | 读取后端响应的超时时间（毫秒） |
| `debbie.gateway.max-retries` | int | `3` | 代理失败时的最大重试次数 |

---

## 核心概念

### GatewayRoute（路由）

一条路由定义了"匹配什么请求"和"转发到哪里"：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | String | 路由唯一标识 |
| `uri` | String | 后端服务地址（如 `http://localhost:8081`） |
| `predicates` | `List<GatewayPredicate>` | 匹配谓词列表，全部满足才命中 |
| `filters` | `List<GatewayFilter>` | 过滤器列表，按 order 排序执行 |
| `order` | int | 路由优先级，值越小越优先匹配 |

```java
var route = new GatewayRoute("my-route", "http://backend:8080")
        .addPredicate(new PathPredicate("/api/**"))
        .addFilter(new StripPrefixFilter(1))
        .setOrder(10);
```

### GatewayRouteLocator（路由定位器）

管理所有路由，并根据请求找到第一个匹配的路由：

- `addRoute(GatewayRoute)`：添加路由，自动按 `order` 排序。
- `locate(RouterRequest)`：遍历路由，返回第一个所有谓词都匹配的路由；无匹配返回 `null`。
- `getRoutes()`：获取所有已注册路由。

### GatewayProxyHandler（代理处理器）

使用 `java.net.http.HttpClient` 将请求转发到后端：

- 自动复制请求头（排除 `Host`、`Connection`）。
- 支持 GET / HEAD / DELETE / POST / PUT / PATCH 等所有 HTTP 方法。
- 请求体以 `byte[]` 透传。
- 响应头、状态码、响应体原样回写。
- 失败时按 `max-retries` 重试，超出返回 `502 Bad Gateway`。

### GatewayRouterFilter（MVC 过滤器）

实现 debbie MVC 的 `RouterFilter` 接口，在 `preRouter` 阶段拦截请求：

1. 通过 `GatewayRouteLocator.locate()` 查找匹配路由。
2. 无匹配 → 返回 `true`，继续走 MVC 正常路由。
3. 有匹配 → 执行 pre-filters → 代理转发 → 执行 post-filters → 返回 `false`，停止 MVC 路由。

---

## 路由定义

### 编程式定义

```java
var route = new GatewayRoute("order", "http://localhost:8081")
        .addPredicate(new PathPredicate("/api/order/**"))
        .addPredicate(new MethodPredicate("GET"))
        .addFilter(new StripPrefixFilter(2))
        .setOrder(10);
routeLocator.addRoute(route);
```

### 链式 API

`GatewayRoute` 的 setter 返回 `this`，支持链式调用：

```java
routeLocator.addRoute(
    new GatewayRoute("product", "http://product-service:8080")
        .addPredicate(new PathPredicate("/api/product/**"))
        .addFilter(new AddPrefixPathFilter("/internal"))
        .setOrder(20)
);
```

### 路由匹配规则

- `GatewayRouteLocator` 按路由的 `order` 升序排列，**第一个所有谓词都匹配的路由**被选中。
- 一条路由的所有谓词是 **AND** 关系，必须全部满足。
- 无谓词的路由不会被匹配（`matches` 返回 `false`）。

---

## 内置谓词

### PathPredicate（路径匹配）

支持 Ant 风格路径模式，内部转换为正则表达式：

| 模式 | 说明 | 示例匹配 |
|------|------|----------|
| `/api/order/**` | 匹配 `/api/order/` 下所有路径 | `/api/order/123` ✓ |
| `/api/user/*` | 匹配单层路径 | `/api/user/list` ✓，`/api/user/a/b` ✗ |
| `/api/v1.0/data` | 精确匹配（`.` 转义） | `/api/v1.0/data` ✓ |

```java
new PathPredicate("/api/order/**")
```

### MethodPredicate（HTTP 方法匹配）

匹配指定的 HTTP 方法（大小写不敏感）：

```java
new MethodPredicate("GET")
new MethodPredicate("POST")
```

### HeaderPredicate（请求头匹配）

匹配请求头的值是否符合正则表达式：

```java
// 匹配 X-Request-Source 头为 mobile 的请求
new HeaderPredicate("X-Request-Source", "mobile")

// 匹配 Authorization 头以 Bearer 开头
new HeaderPredicate("Authorization", "Bearer .*")
```

---

## 内置过滤器

### StripPrefixFilter（去除路径前缀）

在转发前去除请求路径的前 N 段：

```java
// 请求 /api/order/123 -> 后端 /123
new StripPrefixFilter(2)

// 请求 /api/order/123 -> 后端 /order/123
new StripPrefixFilter(1)
```

| 参数 | 说明 |
|------|------|
| `parts` | 要去除的路径段数 |
| `order` | 过滤器执行顺序（可选，默认 0） |

### AddPrefixPathFilter（添加路径前缀）

在转发前给请求路径添加前缀：

```java
// 请求 /api/product/list -> 后端 /internal/api/product/list
new AddPrefixPathFilter("/internal")
```

| 参数 | 说明 |
|------|------|
| `prefix` | 要添加的路径前缀 |
| `order` | 过滤器执行顺序（可选，默认 0） |

---

## 自定义谓词

实现 `GatewayPredicate` 接口（函数式接口）：

```java
/**
 * 匹配请求参数中包含指定 key 的请求
 */
public class QueryParamPredicate implements GatewayPredicate {

    private final String paramName;

    public QueryParamPredicate(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public boolean test(RouterRequest request) {
        var queries = request.getQueries();
        return queries != null && queries.containsKey(paramName);
    }
}
```

使用：

```java
routeLocator.addRoute(new GatewayRoute("with-token", "http://backend:8080")
        .addPredicate(new PathPredicate("/api/**"))
        .addPredicate(new QueryParamPredicate("token"))
        .setOrder(30));
```

---

## 自定义过滤器

实现 `GatewayFilter` 接口，按需覆盖 `preFilter`、`postFilter`、`getOrder`：

### Pre 过滤器（转发前）

```java
/**
 * 添加鉴权头
 */
public class AuthHeaderFilter implements GatewayFilter {

    private final String token;

    public AuthHeaderFilter(String token) {
        this.token = token;
    }

    @Override
    public boolean preFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
        // 添加内部鉴权头
        request.getHeader().addHeader("X-Internal-Token", token);
        return true; // 继续过滤器链
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级，先执行
    }
}
```

### Post 过滤器（转发后）

```java
/**
 * 记录响应耗时
 */
public class TimingFilter implements GatewayFilter {

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
        startTime.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
        long elapsed = System.currentTimeMillis() - startTime.get();
        response.addHeader("X-Response-Time", elapsed + "ms");
        startTime.remove();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
```

### 中断过滤器

`preFilter` 返回 `false` 会中断过滤器链和代理转发：

```java
public class RateLimitFilter implements GatewayFilter {

    private final RateLimiter limiter = new RateLimiter(100); // 100 QPS

    @Override
    public boolean preFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
        if (!limiter.tryAcquire()) {
            response.setStatus(429);
            response.setResponseType(MediaType.TEXT_PLAIN_UTF8);
            response.setContent("Too Many Requests");
            return false; // 中断，不转发
        }
        return true;
    }
}
```

---

## 请求流转

```
客户端请求
    │
    ▼
debbie MVC RouterFilter 链
    │
    ├─ GatewayRouterFilter.preRouter()        (order=-10, 最高优先级)
    │       │
    │       ├─ GatewayRouteLocator.locate()   查找匹配路由
    │       │       │
    │       │       ├─ 无匹配 → return true   继续走 MVC 正常路由
    │       │       │
    │       │       └─ 有匹配 → route
    │       │
    │       ├─ GatewayFilterChain.applyPreFilters()   pre 正序执行
    │       │       │
    │       │       ├─ filter1.preFilter()
    │       │       ├─ filter2.preFilter()
    │       │       │       └─ 返回 false → 中断，return false
    │       │       └─ filterN.preFilter()
    │       │
    │       ├─ GatewayProxyHandler.proxy()    转发到后端
    │       │       │
    │       │       ├─ buildTargetUri()       构建目标 URI
    │       │       ├─ buildProxyRequest()   构建代理请求（复制头、体）
    │       │       ├─ HttpClient.send()     发送（失败重试 max-retries 次）
    │       │       └─ mapResponse()         回写响应
    │       │
    │       ├─ GatewayFilterChain.applyPostFilters()  post 逆序执行
    │       │       │
    │       │       ├─ filterN.postFilter()
    │       │       ├─ filter2.postFilter()
    │       │       └─ filter1.postFilter()
    │       │
    │       └─ return false                   停止 MVC 路由
    │
    └─ 后续 MVC 路由（仅当网关未匹配时）
```

---

## API 速查

### GatewayRoute

| 方法 | 说明 |
|------|------|
| `setId(String)` / `getId()` | 路由 ID |
| `setUri(String)` / `getUri()` | 后端服务地址 |
| `addPredicate(GatewayPredicate)` | 添加匹配谓词 |
| `addFilter(GatewayFilter)` | 添加过滤器 |
| `setOrder(int)` / `getOrder()` | 路由优先级（值小优先） |

### GatewayRouteLocator

| 方法 | 说明 |
|------|------|
| `addRoute(GatewayRoute)` | 添加路由（自动排序） |
| `locate(RouterRequest)` | 查找匹配路由 |
| `getRoutes()` | 获取所有路由 |

### GatewayFilter

| 方法 | 说明 |
|------|------|
| `preFilter(request, response, route)` | 转发前处理，返回 false 中断 |
| `postFilter(request, response, route)` | 转发后处理 |
| `getOrder()` | 过滤器顺序（值小优先） |

### GatewayFilterChain

| 方法 | 说明 |
|------|------|
| `addFilter(GatewayFilter)` | 添加过滤器（自动排序） |
| `applyPreFilters(request, response, route)` | 正序执行 pre，返回 false 表示中断 |
| `applyPostFilters(request, response, route)` | 逆序执行 post |

### GatewayPredicate

| 方法 | 说明 |
|------|------|
| `test(RouterRequest)` | 判断请求是否匹配 |

---

## 设计说明

### 与 Spring Cloud Gateway 的对比

| 特性 | Spring Cloud Gateway | debbie-gateway |
|------|---------------------|----------------|
| 依赖框架 | Spring WebFlux / Spring Cloud | debbie-core + debbie-mvc |
| 运行模型 | Reactor 响应式 | 同步阻塞 |
| HTTP 客户端 | Netty | `java.net.http.HttpClient` (JDK 内置) |
| 路由谓词 | `PredicateSpec` (DSL) | `GatewayPredicate` 接口 |
| 过滤器 | `GatewayFilter` (Mono/Flux) | `GatewayFilter` (同步 pre/post) |
| 路由配置 | `RouteLocator` Bean DSL | `GatewayRouteLocator` 编程式 |
| 自动装配 | Spring Boot AutoConfiguration | `DebbieModuleStarter` (SPI) |
| 模块化 | 无 JPMS | 有 `module-info.java` |
| 与 MVC 共存 | 需要独立部署 | 可在同一应用共存 |

### 模块信息

- **JPMS 模块名**：`com.truthbean.cloud.gateway`
- **导出包**：`com.truthbean.cloud.gateway`
- **requires**：
  - `transitive com.truthbean.debbie.core`
  - `transitive com.truthbean.debbie.mvc`
  - `java.net.http`
- **provides**：`com.truthbean.debbie.boot.DebbieModuleStarter` with `com.truthbean.cloud.gateway.GatewayModuleStarter`

### 类一览

| 类 | 角色 |
|----|------|
| `GatewayConfiguration` | 配置类（`debbie.gateway.*`） |
| `GatewayModuleStarter` | 模块启动器（SPI 自动注册） |
| `GatewayRoute` | 路由定义 |
| `GatewayRouteLocator` | 路由定位器（匹配请求） |
| `GatewayPredicate` | 谓词接口 |
| `PathPredicate` | 路径匹配谓词（Ant 风格） |
| `MethodPredicate` | HTTP 方法匹配谓词 |
| `HeaderPredicate` | 请求头匹配谓词（正则） |
| `GatewayFilter` | 过滤器接口（pre + post） |
| `GatewayFilterChain` | 过滤器链（pre 正序、post 逆序） |
| `StripPrefixFilter` | 去除路径前缀过滤器 |
| `AddPrefixPathFilter` | 添加路径前缀过滤器 |
| `GatewayProxyHandler` | 代理处理器（`HttpClient` 转发） |
| `GatewayRouterFilter` | MVC 过滤器（接入 debbie MVC 过滤器链） |

---

## 示例

### 基础网关

```properties
debbie.gateway.enable=true
debbie.gateway.connect-timeout=5000
debbie.gateway.read-timeout=30000
debbie.gateway.max-retries=3
```

```java
@EventMethodListener
public void onReady(DebbieReadyEvent event) {
    var routeLocator = event.getApplicationContext()
            .getGlobalBeanFactory().factory(GatewayRouteLocator.class);

    // 订单服务
    routeLocator.addRoute(new GatewayRoute("order", "http://localhost:8081")
            .addPredicate(new PathPredicate("/api/order/**"))
            .addFilter(new StripPrefixFilter(2))
            .setOrder(10));

    // 用户服务
    routeLocator.addRoute(new GatewayRoute("user", "http://localhost:8082")
            .addPredicate(new PathPredicate("/api/user/**"))
            .addFilter(new StripPrefixFilter(2))
            .setOrder(20));
}
```

效果：

```
GET /api/order/123  ->  http://localhost:8081/123
GET /api/user/list  ->  http://localhost:8082/list
```

### 带鉴权的网关

```java
public class AuthFilter implements GatewayFilter {
    private final String internalToken;

    public AuthFilter(String internalToken) {
        this.internalToken = internalToken;
    }

    @Override
    public boolean preFilter(RouterRequest request, RouterResponse response, GatewayRoute route) {
        var auth = request.getHeader().getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setResponseType(MediaType.TEXT_PLAIN_UTF8);
            response.setContent("Unauthorized");
            return false;
        }
        // 注入内部 token 供后端校验
        request.getHeader().addHeader("X-Gateway-Token", internalToken);
        return true;
    }

    @Override
    public int getOrder() { return -100; }
}

// 注册
routeLocator.addRoute(new GatewayRoute("secure-api", "http://backend:8080")
        .addPredicate(new PathPredicate("/api/**"))
        .addFilter(new AuthFilter("gateway-secret-token"))
        .addFilter(new StripPrefixFilter(1))
        .setOrder(10));
```

### 按方法路由

```java
// GET 请求走只读副本
routeLocator.addRoute(new GatewayRoute("read-replica", "http://read-only:8080")
        .addPredicate(new PathPredicate("/api/data/**"))
        .addPredicate(new MethodPredicate("GET"))
        .setOrder(10));

// POST/PUT/DELETE 走主库
routeLocator.addRoute(new GatewayRoute("write-primary", "http://primary:8080")
        .addPredicate(new PathPredicate("/api/data/**"))
        .addPredicate(new MethodPredicate("POST"))
        .setOrder(20));

routeLocator.addRoute(new GatewayRoute("update-primary", "http://primary:8080")
        .addPredicate(new PathPredicate("/api/data/**"))
        .addPredicate(new MethodPredicate("PUT"))
        .setOrder(21));
```

### 按请求头路由（金丝雀发布）

```java
// 带 X-Canary: true 头的请求路由到灰度版本
routeLocator.addRoute(new GatewayRoute("canary", "http://canary:8080")
        .addPredicate(new PathPredicate("/api/**"))
        .addPredicate(new HeaderPredicate("X-Canary", "true"))
        .setOrder(5));  // 更高优先级

// 其余走稳定版本
routeLocator.addRoute(new GatewayRoute("stable", "http://stable:8080")
        .addPredicate(new PathPredicate("/api/**"))
        .setOrder(10));
```

### 网关与本地 MVC 共存

```java
@DebbieBootApplication
public class GatewayApp {
    public static void main(String[] args) {
        DebbieApplication.run(GatewayApp.class, args);
    }
}

// 本地接口，不会被网关拦截（路径不匹配 /api/**）
@Router
public class LocalController {

    @GetRouter(value = "/health", responseType = MediaType.TEXT_PLAIN_UTF8)
    public String health() {
        return "OK";
    }

    @GetRouter(value = "/gateway/routes")
    public List<GatewayRoute> routes(@BeanInject GatewayRouteLocator locator) {
        return locator.getRoutes();
    }
}
```

---

## License

[穆兰 PSL v2](../../LICENSE)
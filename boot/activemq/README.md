# debbie-activemq

ActiveMQ JMS 集成模块，基于 [Apache ActiveMQ](https://activemq.apache.org/) 6.x 客户端，为 debbie 框架提供消息队列能力。

## 特性

- 自动装配 ActiveMQ `ConnectionFactory`，通过 SPI 注册 `DebbieModuleStarter` 实现
- 支持完整的 ActiveMQ 连接配置（broker URL、认证、连接池、异步发送等）
- 内置重投递策略（Redelivery Policy）配置
- 提供创建 Connection / Session / Queue / Topic 的便捷方法
- 无 Spring 依赖，纯 debbie 框架实现

## 依赖

- `debbie-core` — debbie 框架核心
- `activemq-client` 6.x — ActiveMQ JMS 客户端
- `jakarta.messaging` — JMS 3.x API

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-activemq</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置

在 `debbie.properties` 或环境变量中配置：

```properties
# ActiveMQ broker URL
debbie.activemq.broker-url=tcp://localhost:61616

# 认证（可选）
debbie.activemq.username=admin
debbie.activemq.password=secret

# 连接池
debbie.activemq.max-connections=10

# 发送模式
debbie.activemq.use-async-send=true
debbie.activemq.always-sync-send=false

# 超时与窗口
debbie.activemq.close-timeout=15000
debbie.activemq.producer-window-size=0
debbie.activemq.dispatch-async=true

# 重投递策略
debbie.activemq.redelivery.max-redeliveries=6
debbie.activemq.redelivery.initial-redelivery-delay=1000
debbie.activemq.redelivery.back-off-multiplier=2.0
debbie.activemq.redelivery.use-exponential-back-off=true

# 启用/禁用模块
debbie.activemq.enable=true
```

### 3. 使用

通过 `@BeanInject` 注入 `ActiveMqConnectionFactory`：

```java
@Router
public class MessageRouter {

    @BeanInject
    private ActiveMqConnectionFactory factory;

    @PostRouter
    public void sendMessage(String text) throws JMSException {
        try (var connection = factory.createConnection()) {
            connection.start();
            var session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            var queue = session.createQueue("test-queue");
            var producer = session.createProducer(queue);
            producer.send(session.createTextMessage(text));
        }
    }
}
```

或直接使用 `jakarta.jms.ConnectionFactory`：

```java
@BeanInject
private ConnectionFactory connectionFactory;
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.activemq.enable` | boolean | `true` | 启用/禁用模块 |
| `debbie.activemq.broker-url` | String | `tcp://localhost:61616` | Broker 地址 |
| `debbie.activemq.username` | String | - | 用户名 |
| `debbie.activemq.password` | String | - | 密码 |
| `debbie.activemq.max-connections` | int | `1` | 最大连接数 |
| `debbie.activemq.use-async-send` | boolean | `true` | 异步发送 |
| `debbie.activemq.always-sync-send` | boolean | `false` | 始终同步发送 |
| `debbie.activemq.close-timeout` | int | `15000` | 关闭超时（毫秒） |
| `debbie.activemq.producer-window-size` | int | `0` | 生产者窗口大小（字节） |
| `debbie.activemq.dispatch-async` | boolean | `true` | 异步分发 |
| `debbie.activemq.redelivery.max-redeliveries` | int | `6` | 最大重投递次数 |
| `debbie.activemq.redelivery.initial-redelivery-delay` | int | `1000` | 初始重投递延迟（毫秒） |
| `debbie.activemq.redelivery.back-off-multiplier` | double | `2.0` | 退避乘数 |
| `debbie.activemq.redelivery.use-exponential-back-off` | boolean | `true` | 指数退避 |

## 架构

```
ActiveMqConfiguration        — 配置类，绑定 debbie.activemq.* 属性
ActiveMqConnectionFactory    — 连接工厂，创建/管理 ActiveMQ ConnectionFactory
ActiveMqModuleStarter        — SPI 模块启动器，自动装配 Bean
```

### SPI 自动装配

模块通过 `module-info.java` 中的 `provides DebbieModuleStarter with ActiveMqModuleStarter` 注册，
debbie 框架启动时自动发现并执行：

1. `registerBean()` — 注册 `ActiveMqConfiguration` Bean
2. `starter()` — 创建 `ActiveMqConnectionFactory` 并注册为 Bean
3. `release()` — 关闭连接工厂，释放资源

## 模块信息

- **模块名**: `com.truthbean.debbie.activemq`
- **启动顺序**: `200`
- **配置前缀**: `debbie.activemq`
- **Java 版本**: 17+

## 许可证

Mulan PSL v2
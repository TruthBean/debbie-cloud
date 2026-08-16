# debbie-mail

邮件发送模块，基于 [Jakarta Mail](https://eclipse-ee4j.github.io/angus-mail/) (原 JavaMail) API，为 debbie 框架提供邮件发送能力。

## 特性

- 自动装配邮件配置和发送器，通过 SPI 注册 `DebbieModuleStarter` 实现
- 支持完整的 SMTP 配置（主机、端口、认证、SSL、STARTTLS）
- 支持简单文本邮件（`SimpleMailMessage`）
- 支持 MIME 邮件（`MimeMessage`，可添加附件、HTML 内容）
- 支持连接超时、读写超时配置
- 支持默认发件人配置
- 无 Spring 依赖，纯 debbie 框架 + Jakarta Mail 实现

## 依赖

- `debbie-core` — debbie 框架核心
- `jakarta.mail-api` 2.1.x — Jakarta Mail API
- `angus-mail` 2.0.x — Jakarta Mail 实现

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.truthbean</groupId>
    <artifactId>debbie-mail</artifactId>
    <version>0.6.3-RELEASE</version>
</dependency>
```

### 2. 配置

在 `debbie.properties` 或环境变量中配置：

```properties
# SMTP 服务器
debbie.mail.host=smtp.example.com
debbie.mail.port=587

# 认证
debbie.mail.username=user@example.com
debbie.mail.password=secret
debbie.mail.auth=true

# 协议与编码
debbie.mail.protocol=smtp
debbie.mail.default-encoding=UTF-8

# SSL / STARTTLS
debbie.mail.ssl=false
debbie.mail.starttls=true

# 超时（毫秒）
debbie.mail.connection-timeout=5000
debbie.mail.timeout=5000
debbie.mail.write-timeout=5000

# 默认发件人
debbie.mail.default-from=noreply@example.com
debbie.mail.default-from-personal=No Reply

# 启用/禁用模块
debbie.mail.enable=true
```

### 3. 使用

通过 `@BeanInject` 注入 `MailSender` 发送邮件：

```java
@Router
public class NotificationRouter {

    @BeanInject
    private MailSender mailSender;

    @PostRouter
    public void sendWelcome(String email) throws MessagingException {
        var msg = new SimpleMailMessage();
        msg.setFrom("noreply@example.com");
        msg.setTo(email);
        msg.setSubject("Welcome");
        msg.setText("Welcome to debbie!");
        mailSender.send(msg);
    }
}
```

发送 MIME 邮件（支持附件、HTML）：

```java
@PostRouter
public void sendHtmlWithAttachment() throws MessagingException, IOException {
    var mime = mailSender.createMimeMessage();
    mime.setFrom(new InternetAddress("noreply@example.com"));
    mime.setRecipients(Message.RecipientType.TO, "user@example.com");
    mime.setSubject("HTML Email with Attachment");

    var multipart = new MimeMultipart();

    var htmlPart = new MimeBodyPart();
    htmlPart.setContent("<h1>Hello</h1><p>HTML content</p>", "text/html; charset=UTF-8");
    multipart.addBodyPart(htmlPart);

    var attachPart = new MimeBodyPart();
    attachPart.attachFile("/path/to/file.pdf");
    multipart.addBodyPart(attachPart);

    mime.setContent(multipart);
    mailSender.send(mime);
}
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `debbie.mail.enable` | boolean | `true` | 启用/禁用模块 |
| `debbie.mail.host` | String | `localhost` | SMTP 主机 |
| `debbie.mail.port` | int | `25` | SMTP 端口 |
| `debbie.mail.username` | String | - | 用户名 |
| `debbie.mail.password` | String | - | 密码 |
| `debbie.mail.protocol` | String | `smtp` | 协议（smtp/smtps） |
| `debbie.mail.default-encoding` | String | `UTF-8` | 默认编码 |
| `debbie.mail.ssl` | boolean | `false` | 使用 SSL |
| `debbie.mail.starttls` | boolean | `false` | 使用 STARTTLS |
| `debbie.mail.auth` | boolean | `true` | SMTP 认证 |
| `debbie.mail.connection-timeout` | int | `5000` | 连接超时（ms） |
| `debbie.mail.timeout` | int | `5000` | 读取超时（ms） |
| `debbie.mail.write-timeout` | int | `5000` | 写入超时（ms） |
| `debbie.mail.default-from` | String | - | 默认发件人 |
| `debbie.mail.default-from-personal` | String | - | 默认发件人名称 |

## 架构

```
MailConfiguration    — 配置类，绑定 debbie.mail.* 属性
SimpleMailMessage    — 简单文本邮件消息
MailSender           — 邮件发送器，封装 Jakarta Mail Session 和 Transport
MailModuleStarter    — SPI 模块启动器，自动装配 Bean
```

### SPI 自动装配

模块通过 `module-info.java` 中的 `provides DebbieModuleStarter with MailModuleStarter` 注册，
debbie 框架启动时自动发现并执行：

1. `registerBean()` — 注册 `MailConfiguration` Bean
2. `starter()` — 创建 `MailSender`，调用 `init()` 初始化 Jakarta Mail Session，注册为 Bean
3. `release()` — 关闭发送器，释放资源

## 模块信息

- **模块名**: `com.truthbean.debbie.mail`
- **启动顺序**: `210`
- **配置前缀**: `debbie.mail`
- **Java 版本**: 17+

## 许可证

Mulan PSL v2
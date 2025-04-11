# SmartClass智慧教学平台后端

## 项目介绍

SmartClass是一个基于AI技术的智慧教学平台，旨在为教师和学生提供智能化、个性化的教学服务。本项目是SmartClass的后端部分，采用Spring Boot框架开发，集成了Dify AI接口，提供丰富的教学管理与AI互动功能。

## 技术栈

- JDK 1.8
- Spring Boot 2.7.2
- MyBatis Plus 3.5.2
- Redis
- MySQL
- Elasticsearch
- OkHttp
- Knife4j (API文档)

## 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+

## 安装指南

### 1. 克隆代码

```bash
git clone https://github.com/yourusername/smartclass-backend.git
cd smartclass-backend
```

### 2. 配置数据库

创建MySQL数据库`smart_class`，然后导入`sql`目录下的SQL文件：

```bash
mysql -u username -p smart_class < sql/create_table.sql
```

### 3. 修改配置文件

根据环境配置`src/main/resources/application.yml`文件：

```yaml
# 数据库配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/smart_class
    username: root
    password: your_password

# Redis配置
  redis:
    database: 0
    host: localhost
    port: 6379
    timeout: 5000
    password: your_redis_password
```

### 4. 编译项目

```bash
mvn clean package -DskipTests
```

## 运行方法

### 开发环境运行

```bash
mvn spring-boot:run
```

### 生产环境运行

```bash
java -jar target/smartclass-ubanillx-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

也可以使用Docker运行：

```bash
docker build -t smartclass-backend .
docker run -p 12345:12345 smartclass-backend
```

## 主要功能

### 1. AI头像聊天

- 基于Dify API的智能聊天功能
- 支持流式(流畅)和阻塞式响应模式
- 文字转语音功能
- 聊天历史记录管理
- 会话管理与总结

### 2. 课程管理

- 课程分类管理
- 课程章节、小节管理
- 课程收藏与评价
- 课程资料管理

### 3. 学习管理

- 用户学习记录
- 学习统计数据
- 每日学习文章
- 每日单词学习

### 4. 用户系统

- 用户注册、登录
- 用户等级管理
- 个人信息管理
- 微信公众平台集成

### 5. 内容管理

- 公告管理
- 文章管理
- 贴子管理
- 文件管理

## API文档

启动项目后，访问Knife4j接口文档：

```
http://localhost:12345/api/doc.html
```

## 开发指南

项目使用Maven进行依赖管理，使用MyBatis Plus进行ORM映射。

Dify API集成参考`DifyAPI.md`文件。

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 许可证

[MIT License](LICENSE)
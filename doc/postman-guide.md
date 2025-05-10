# 智慧课堂 Postman 接口调试指南

本文档提供智慧课堂后端API的Postman调试指南，包括REST API和WebSocket接口测试方法。

## 1. 准备工作

### 1.1 安装Postman

首先，请确保您已安装Postman。如果尚未安装，请访问 [Postman官网](https://www.postman.com/downloads/) 下载并安装。

### 1.2 导入Collection

智慧课堂提供了一个完整的Postman Collection，包含所有API和WebSocket接口。导入步骤如下：

1. 启动项目后，访问 `http://<your-server-ip>:12345/api/doc/postman-collection`
2. 下载生成的JSON文件
3. 打开Postman，点击左上角的"Import"按钮
4. 选择下载的JSON文件
5. 导入完成后，您会在左侧边栏看到"智慧课堂API"的Collection

### 1.3 配置环境变量

导入Collection后，您需要设置一些环境变量：

1. 点击Postman右上角的"Environments"按钮
2. 创建一个新环境，例如命名为"智慧课堂环境"
3. 在环境变量中添加以下变量：
   - `apiBase`: API基础URL，例如 `http://localhost:12345/api`
   - `wsUrl`: WebSocket URL，例如 `ws://localhost:12346/ws`
   - `sessionId`: 用于认证的会话ID，登录后从Cookie中获取
   - `token`: 用于WebSocket认证的令牌

您也可以直接通过调用 `GET /api/doc/server-info` 接口获取正确的服务器URL配置。

## 2. REST API测试

### 2.1 用户认证

大多数API需要用户认证才能访问。认证步骤如下：

1. 找到并发送 `POST /api/user/login` 请求
2. 登录成功后，Postman会自动保存Cookie
3. 检查Cookie中的JSESSIONID，并将其复制到环境变量中的 `sessionId` 变量

### 2.2 使用Collection测试API

导入Collection后，所有接口都按控制器分组整理好了。您可以直接点击相应的接口进行测试。

请求发送前，请确保：
1. 选择了正确的环境
2. 填写了必要的参数
3. 对于POST请求，设置了正确的请求体

## 3. WebSocket接口测试

Postman支持WebSocket测试，可以用来调试智慧课堂的实时通信功能。

### 3.1 连接WebSocket

1. 在Collection中找到"WebSocket接口/建立WebSocket连接"
2. 确保URL中包含token参数，格式为：`{{wsUrl}}?token={{token}}`
3. 点击"Connect"按钮建立连接

> 注意：token可以在用户登录后通过API获取，或直接使用JSESSIONID作为token

### 3.2 发送消息

连接建立后，您可以发送各种类型的消息：

#### 心跳消息

```json
{
  "type": "heartbeat",
  "timestamp": 1621234567890
}
```

#### 聊天消息

```json
{
  "type": "chat",
  "content": "你好，这是一条测试消息",
  "sessionId": "abc123",
  "timestamp": 1621234567890
}
```

#### 命令消息

```json
{
  "type": "command",
  "content": "online_users",
  "timestamp": 1621234567890
}
```

### 3.3 接收消息

连接WebSocket后，Postman会自动显示从服务器接收的所有消息。您可以在消息列表中查看每条消息的详细内容。

### 3.4 关闭连接

测试完成后，点击"Disconnect"按钮关闭WebSocket连接。

## 4. 高级用例

### 4.1 流式响应测试

对于streaming API（如AI聊天），Postman可以通过两种方式测试：

1. 使用SSE（Server-Sent Events）接口：
   - 找到 `POST /api/chat/message/stream` 接口
   - 发送请求后，Postman会显示实时更新的响应

2. 使用WebSocket：
   - 通过WebSocket连接发送相应的消息
   - 实时接收服务器的流式响应

### 4.2 文件上传测试

对于文件上传API：
1. 选择相应的接口，如 `POST /api/chat/upload`
2. 在Body选项卡中选择"form-data"
3. 添加文件字段，并选择要上传的文件
4. 发送请求测试上传功能

## 5. 排查常见问题

### 5.1 认证失败

如果遇到认证问题（401错误），请检查：
1. JSESSIONID是否正确设置
2. 会话是否已过期（需重新登录）
3. 用户权限是否足够

### 5.2 WebSocket连接问题

如果WebSocket连接失败，请检查：
1. wsUrl是否正确
2. token是否有效
3. 防火墙是否阻止了WebSocket连接
4. 服务器WebSocket服务是否正常运行

### 5.3 调试技巧

1. 使用Postman的Console查看详细的请求和响应日志
2. 对于WebSocket，查看服务器日志以获取更多信息
3. 使用环境变量简化测试流程

## 6. 更多资源

- [Postman官方文档](https://learning.postman.com/docs/getting-started/introduction/)
- [WebSocket测试指南](https://blog.postman.com/postman-supports-websocket-apis/)
- [服务器API文档](/api/swagger-ui.html)

如有任何问题，请联系项目维护人员获取支持。 
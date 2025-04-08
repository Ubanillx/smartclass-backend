package com.ubanillx.smartclass.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ubanillx.smartclass.config.DifyConfig;
import com.ubanillx.smartclass.model.dto.dify.DifyChatRequest;
import com.ubanillx.smartclass.model.dto.dify.DifyChatResponse;
import com.ubanillx.smartclass.model.dto.dify.DifyStreamChunk;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.service.AiAvatarChatHistoryService;
import com.ubanillx.smartclass.service.DifyService;
import com.ubanillx.smartclass.util.ChatMessageHelper;
import com.ubanillx.smartclass.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DifyAPI服务实现
 */
@Service
@Slf4j
public class DifyServiceImpl implements DifyService {

    @Resource
    private DifyConfig difyConfig;

    @Resource
    private AiAvatarChatHistoryService aiAvatarChatHistoryService;

    @Resource
    private ChatMessageHelper chatMessageHelper;

    @Resource
    private OkHttpUtils okHttpUtils;

    @Override
    @Transactional
    public AiAvatarChatHistory sendChatMessage(Long userId, Long aiAvatarId, String sessionId, String content,
                                               String baseUrl, String avatarAuth) {
        // 创建用户消息对象
        AiAvatarChatHistory userMessage = chatMessageHelper.createUserMessage(userId, aiAvatarId, sessionId, content);

        // 保存用户消息
        boolean saved = aiAvatarChatHistoryService.save(userMessage);
        if (!saved) {
            log.error("Failed to save user message");
            throw new RuntimeException("保存用户消息失败");
        }

        try {
            return sendChatMessageWithRetry(userId, aiAvatarId, sessionId, content, baseUrl, avatarAuth, userMessage, false);
        } catch (Exception e) {
            log.error("Error sending chat message to Dify", e);
            throw new RuntimeException("发送聊天消息失败: " + e.getMessage());
        }
    }

    /**
     * 发送聊天消息并处理会话不存在的情况，使用 OkHttp 实现
     */
    private AiAvatarChatHistory sendChatMessageWithRetry(Long userId, Long aiAvatarId, String sessionId,
                                                         String content, String baseUrl, String avatarAuth,
                                                         AiAvatarChatHistory userMessage, boolean retried) {
        try {
            // 构建请求对象
            DifyChatRequest chatRequest = buildChatRequest(userId, sessionId, content);

            // 如果是重试并且会话不存在，则不传会话ID
            if (retried) {
                chatRequest.setConversation_id(null);
                log.info("重试请求，不传会话ID");
            }

            // 发送请求
            String chatMessagesPath = "/chat-messages"; // API路径
            String url = baseUrl + chatMessagesPath;
            String requestJson = JSONUtil.toJsonStr(chatRequest);
            log.info("Sending request to Dify: {}", requestJson);

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送请求
            Response response = okHttpUtils.postJson(url, requestJson, headers);

            try {
                if (!response.isSuccessful()) {
                    String responseBody = "";
                    try (ResponseBody body = response.body()) {
                        if (body != null) {
                            responseBody = body.string();
                        }
                    } catch (IOException e) {
                        log.error("读取错误响应体异常", e);
                        throw new RuntimeException("读取响应失败: " + e.getMessage());
                    }

                    log.error("Dify API error: {}, Headers: {}, Body: {}",
                            response.code(), response.headers(), responseBody);

                    // 检查是否为会话不存在的错误
                    if (response.code() == 404 && !retried) {
                        if (responseBody.contains("Conversation Not Exists")) {
                            log.info("会话ID不存在，将创建新会话: {}", sessionId);
                            // 递归调用，但设置retried标志，不传会话ID
                            return sendChatMessageWithRetry(userId, aiAvatarId, null, content, baseUrl, avatarAuth, userMessage, true);
                        }
                    }

                    throw new RuntimeException("调用Dify API失败: " + response.code() + ", " + responseBody);
                }

                // 解析响应
                String responseBody = "";
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        responseBody = body.string();
                    }
                } catch (IOException e) {
                    log.error("读取响应体异常", e);
                    throw new RuntimeException("读取响应失败: " + e.getMessage());
                }

                DifyChatResponse chatResponse = JSONUtil.toBean(responseBody, DifyChatResponse.class);

                // 检查是否返回了新的会话ID，如果有且与原会话ID不同，则更新会话记录
                if (chatResponse.getConversation_id() != null && !chatResponse.getConversation_id().equals(sessionId)) {
                    log.info("Dify创建了新会话ID: {}, 原会话ID: {}", chatResponse.getConversation_id(), sessionId);

                    // 更新用户消息的会话ID
                    AiAvatarChatHistory updatedUserMessage = new AiAvatarChatHistory();
                    updatedUserMessage.setId(userMessage.getId());
                    updatedUserMessage.setSessionId(chatResponse.getConversation_id());
                    aiAvatarChatHistoryService.updateById(updatedUserMessage);

                    // 更新会话ID
                    sessionId = chatResponse.getConversation_id();
                }

                // 创建并保存AI响应
                AiAvatarChatHistory aiResponse = chatMessageHelper.createAiResponse(userId, aiAvatarId, sessionId, chatResponse);
                boolean saved = aiAvatarChatHistoryService.save(aiResponse);
                if (!saved) {
                    log.error("Failed to save AI response");
                    throw new RuntimeException("保存AI响应失败");
                }

                return aiResponse;
            } finally {
                response.close();
            }

        } catch (Exception e) {
            if (!retried && (e.getMessage().contains("Conversation Not Exists") || e.getMessage().contains("404"))) {
                log.info("会话ID不存在异常，将创建新会话: {}", sessionId);
                // 递归调用，但设置retried标志
                return sendChatMessageWithRetry(userId, aiAvatarId, null, content, baseUrl, avatarAuth, userMessage, true);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public AiAvatarChatHistory sendChatMessageStreaming(Long userId, Long aiAvatarId, String sessionId,
                                                        String content, String baseUrl, String avatarAuth,
                                                        DifyStreamCallback callback) {
        // 创建用户消息对象
        AiAvatarChatHistory userMessage = chatMessageHelper.createUserMessage(userId, aiAvatarId, sessionId, content);

        // 保存用户消息
        boolean saved = aiAvatarChatHistoryService.save(userMessage);
        if (!saved) {
            log.error("Failed to save user message");
            throw new RuntimeException("保存用户消息失败");
        }

        // 用于存储完整响应
        final AtomicReference<String> fullResponseRef = new AtomicReference<>("");
        final AtomicReference<String> messageIdRef = new AtomicReference<>("");
        final AtomicReference<String> conversationIdRef = new AtomicReference<>(sessionId);

        try {
            return sendStreamingWithRetry(userId, aiAvatarId, sessionId, content, baseUrl, avatarAuth,
                    callback, userMessage, fullResponseRef, messageIdRef, conversationIdRef, false);
        } catch (Exception e) {
            log.error("Error sending streaming chat message to Dify", e);
            callback.onError(e);
            throw new RuntimeException("发送流式聊天消息失败: " + e.getMessage());
        }
    }

    /**
     * 发送流式消息并处理会话不存在的情况，使用 OkHttp 实现
     */
    private AiAvatarChatHistory sendStreamingWithRetry(Long userId, Long aiAvatarId, String sessionId,
                                                       String content, String baseUrl, String avatarAuth,
                                                       DifyStreamCallback callback,
                                                       AiAvatarChatHistory userMessage,
                                                       AtomicReference<String> fullResponseRef,
                                                       AtomicReference<String> messageIdRef,
                                                       AtomicReference<String> conversationIdRef,
                                                       boolean retried) throws IOException {
        try {
            // 构建请求对象
            DifyChatRequest chatRequest = buildChatRequest(userId, sessionId, content);
            chatRequest.setResponse_mode("streaming"); // 强制使用流式模式

            // 如果是重试并且会话不存在，则不传会话ID
            if (retried) {
                chatRequest.setConversation_id(null);
                log.info("重试流式请求，不传会话ID");
            }

            // 发送请求
            String chatMessagesPath = "/chat-messages"; // API路径
            String url = baseUrl + chatMessagesPath;
            String requestJson = JSONUtil.toJsonStr(chatRequest);
            log.info("Sending streaming request to Dify: {}", requestJson);

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + avatarAuth);
            headers.put("Accept", "text/event-stream"); // 明确指定接受SSE格式

            // 使用 OkHttp 发送流式请求
            Response response = okHttpUtils.postJsonStream(url, requestJson, headers);

            try {
                if (!response.isSuccessful()) {
                    String responseBody = "";
                    try (ResponseBody body = response.body()) {
                        if (body != null) {
                            responseBody = body.string();
                        }
                    }

                    log.error("Dify API error: {}, Headers: {}, Body: {}",
                            response.code(), response.headers(), responseBody);

                    // 检查是否为会话不存在的错误
                    if (response.code() == 404 && !retried) {
                        if (responseBody.contains("Conversation Not Exists")) {
                            log.info("流式请求会话ID不存在，将创建新会话: {}", sessionId);
                            // 关闭响应
                            response.close();
                            // 递归调用，但设置retried标志，不传会话ID
                            return sendStreamingWithRetry(userId, aiAvatarId, null, content, baseUrl, avatarAuth,
                                    callback, userMessage, fullResponseRef, messageIdRef, conversationIdRef, true);
                        }
                    }

                    callback.onError(new RuntimeException("调用Dify API失败: " + response.code() + ", " + responseBody));
                    throw new RuntimeException("调用Dify API失败: " + response.code() + ", " + responseBody);
                }

                // 获取响应体
                final ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    callback.onError(new RuntimeException("Dify API返回空响应"));
                    throw new RuntimeException("Dify API返回空响应");
                }

                // 使用CompletableFuture异步处理流式响应
                CompletableFuture.runAsync(() -> {
                    // 使用BufferedReader逐行读取SSE流
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.byteStream()))) {
                        String line;
                        log.info("开始接收流式数据...");
                        int lineCount = 0;

                        try {
                            while ((line = reader.readLine()) != null) {
                                lineCount++;
                                if (lineCount % 10 == 0) {
                                    log.info("已接收 {} 行SSE数据", lineCount);
                                }

                                // 处理SSE数据行
                                if (line.startsWith("data: ")) {
                                    // 提取JSON数据内容
                                    String jsonData = line.substring(6); // 移除 "data: " 前缀

                                    if (difyConfig.isEnableStreamingVerboseLog()) {
                                        log.info("接收到SSE数据行: {}", line.substring(0, Math.min(50, line.length())) + (line.length() > 50 ? "..." : ""));
                                    } else {
                                        log.debug("接收到SSE数据行: {}", line.substring(0, Math.min(50, line.length())) + (line.length() > 50 ? "..." : ""));
                                    }

                                    // 立即将数据传递给回调，不等待整个事件结束
                                    if (!jsonData.isEmpty()) {
                                        try {
                                            // 先发送原始数据给回调
                                            if (difyConfig.isEnableStreamingVerboseLog()) {
                                                log.info("立即转发数据行给回调函数");
                                            } else {
                                                log.debug("立即转发数据行给回调函数");
                                            }
                                            callback.onMessage(jsonData);

                                            // 解析数据进行额外处理
                                            DifyStreamChunk chunk = JSONUtil.toBean(jsonData, DifyStreamChunk.class);

                                            // 处理不同类型的事件
                                            if ("message".equals(chunk.getEvent()) && chunk.getAnswer() != null) {
                                                // 累积完整响应
                                                fullResponseRef.updateAndGet(prev -> prev + chunk.getAnswer());
                                                log.debug("累积内容: {}", chunk.getAnswer());

                                                // 保存消息ID
                                                if (messageIdRef.get().isEmpty() && chunk.getId() != null) {
                                                    messageIdRef.set(chunk.getId());
                                                    log.info("获取到消息ID: {}", chunk.getId());
                                                }

                                                // 保存会话ID
                                                if (chunk.getConversation_id() != null) {
                                                    conversationIdRef.set(chunk.getConversation_id());
                                                    log.debug("获取到会话ID: {}", chunk.getConversation_id());
                                                }
                                            } else if ("message_end".equals(chunk.getEvent())) {
                                                // 消息结束事件，记录但不做特殊处理
                                                log.info("收到消息结束事件: {}", jsonData);
                                            } else if ("error".equals(chunk.getEvent())) {
                                                // 错误事件
                                                log.error("收到错误事件: {}", jsonData);
                                            } else if ("ping".equals(chunk.getEvent())) {
                                                // ping事件，用于保持连接活跃
                                                log.debug("收到ping事件");
                                            }
                                        } catch (Exception e) {
                                            log.error("处理数据行异常: {}", jsonData, e);
                                        }
                                    }
                                } else if (line.trim().isEmpty()) {
                                    // 空行，忽略
                                    log.debug("接收到空行");
                                    continue;
                                } else {
                                    // 其他类型的SSE行，例如event:、id:等
                                    log.debug("其他SSE行: {}", line);
                                }
                            }
                        } catch (IOException e) {
                            log.error("读取流式数据行时发生IO异常", e);
                            callback.onError(e);
                        }

                        log.info("流式数据接收完成，共接收 {} 行数据", lineCount);

                        // 流结束，处理最终工作
                        String fullResponse = fullResponseRef.get();
                        String finalConversationId = conversationIdRef.get();

                        // 检查会话ID是否有变化
                        if (!finalConversationId.equals(sessionId)) {
                            log.info("Dify创建了新会话ID: {}, 原会话ID: {}", finalConversationId, sessionId);

                            // 更新用户消息的会话ID
                            AiAvatarChatHistory updatedUserMessage = new AiAvatarChatHistory();
                            updatedUserMessage.setId(userMessage.getId());
                            updatedUserMessage.setSessionId(finalConversationId);
                            aiAvatarChatHistoryService.updateById(updatedUserMessage);
                        }

                        // 保存到数据库
                        if (!fullResponse.isEmpty()) {
                            boolean saved = aiAvatarChatHistoryService.saveMessage(
                                    userId, aiAvatarId, finalConversationId, "ai", fullResponse);

                            if (!saved) {
                                log.error("Failed to save AI response to database");
                            }
                        }

                        // 处理完成通知回调
                        callback.onComplete(fullResponse);

                    } catch (IOException e) {
                        log.error("Error reading streaming response", e);
                        callback.onError(e);
                    } finally {
                        try {
                            responseBody.close();
                        } catch (Exception e) {
                            log.error("Error closing response body", e);
                        }
                    }
                });

                // 创建一个消息对象返回，实际内容会被异步更新
                AiAvatarChatHistory aiResponse = chatMessageHelper.createEmptyAiResponse(userId, aiAvatarId, sessionId);
                return aiResponse;

            } catch (Exception e) {
                if (response != null) {
                    response.close();
                }
                throw e;
            }

        } catch (Exception e) {
            if (!retried && (e.getMessage().contains("Conversation Not Exists") || e.getMessage().contains("404"))) {
                log.info("流式请求会话ID不存在异常，将创建新会话: {}", sessionId);
                // 递归调用，但设置retried标志
                return sendStreamingWithRetry(userId, aiAvatarId, null, content, baseUrl, avatarAuth,
                        callback, userMessage, fullResponseRef, messageIdRef, conversationIdRef, true);
            }
            throw e;
        }
    }

    /**
     * 构建聊天请求对象
     */
    private DifyChatRequest buildChatRequest(Long userId, String sessionId, String content) {
        DifyChatRequest chatRequest = new DifyChatRequest();
        chatRequest.setQuery(content);
        chatRequest.setInputs(new HashMap<>());
        chatRequest.setResponse_mode("blocking");
        chatRequest.setUser(difyConfig.getUserPrefix() + userId);

        // 验证会话ID格式，如果不是UUID则设为null让Dify自动创建
        if (sessionId != null && isValidUUID(sessionId)) {
            chatRequest.setConversation_id(sessionId);
        } else {
            // 不传conversation_id或传null，Dify会自动创建新会话
            chatRequest.setConversation_id(null);
            log.info("会话ID不是有效UUID，将让Dify自动创建新会话。原会话ID: {}", sessionId);
        }

        chatRequest.setAuto_generate_name(true);
        return chatRequest;
    }

    /**
     * 验证字符串是否为有效的UUID
     */
    private boolean isValidUUID(String uuidStr) {
        try {
            UUID.fromString(uuidStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getSessionSummary(String sessionId, String baseUrl, String avatarAuth) {
        if (!StringUtils.hasLength(sessionId) || !StringUtils.hasLength(baseUrl) || !StringUtils.hasLength(avatarAuth)) {
            throw new RuntimeException("参数错误");
        }

        try {
            // 构建请求URL
            String apiUrl = baseUrl + "/chat-messages/summarize";

            // 创建请求JSON对象
            JSONObject requestBody = new JSONObject();
            requestBody.set("conversation_id", sessionId);
            String jsonBody = requestBody.toString();

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送请求
            Response response = okHttpUtils.postJson(apiUrl, jsonBody, headers);

            // 检查响应状态
            if (!response.isSuccessful()) {
                String responseBody = "";
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        responseBody = body.string();
                    }
                } catch (IOException e) {
                    log.error("读取错误响应体异常", e);
                    throw new RuntimeException("读取响应失败: " + e.getMessage());
                }
                response.close();
                throw new RuntimeException("获取会话总结失败: " + response.code() + " " + responseBody);
            }

            // 解析响应
            String responseBody = "";
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    responseBody = body.string();
                }
            } catch (IOException e) {
                log.error("读取响应体异常", e);
                throw new RuntimeException("读取响应失败: " + e.getMessage());
            }

            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);

            // 提取总结内容
            String summary = jsonResponse.getStr("summary");
            if (!StringUtils.hasLength(summary)) {
                return "聊天记录总结";
            }

            return summary;
        } catch (Exception e) {
            log.error("获取会话总结失败", e);
            throw new RuntimeException("获取会话总结失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteConversation(Long userId, String sessionId, String baseUrl, String avatarAuth) {
        if (!StringUtils.hasLength(sessionId) || !StringUtils.hasLength(baseUrl) || !StringUtils.hasLength(avatarAuth)) {
            throw new RuntimeException("参数错误");
        }

        try {
            // 构建请求URL
            String apiUrl = baseUrl + "/conversations/" + sessionId;

            // 创建请求JSON对象
            JSONObject requestBody = new JSONObject();
            requestBody.set("user", difyConfig.getUserPrefix() + userId);
            String jsonBody = requestBody.toString();

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送DELETE请求
            Response response = okHttpUtils.delete(apiUrl, jsonBody, headers);

            // 检查响应状态
            if (!response.isSuccessful()) {
                // 如果是404错误，则表示会话不存在，也算成功
                if (response.code() == 404) {
                    log.warn("Dify会话不存在，视为删除成功: {}", sessionId);
                    response.close();
                    return true;
                }
                String responseBody = "";
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        responseBody = body.string();
                    }
                } catch (IOException e) {
                    log.error("读取错误响应体异常", e);
                    return false;
                }
                log.error("删除Dify会话失败: {}, {}", response.code(), responseBody);
                response.close();
                return false;
            }

            // 解析响应
            String responseBody = "";
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    responseBody = body.string();
                }
            } catch (IOException e) {
                log.error("读取响应体异常", e);
                return false;
            }

            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);

            // 检查结果
            String result = jsonResponse.getStr("result");
            return "success".equals(result);

        } catch (Exception e) {
            log.error("删除Dify会话异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean textToAudio(Long userId, String text, String messageId,
                               String baseUrl, String avatarAuth, HttpServletResponse response) {
        if ((!StringUtils.hasLength(text) && !StringUtils.hasLength(messageId))
                || !StringUtils.hasLength(baseUrl)
                || !StringUtils.hasLength(avatarAuth)) {
            throw new RuntimeException("参数错误：必须提供文本内容或消息ID");
        }

        try {
            // 构建请求URL
            String apiUrl = baseUrl + "/text-to-audio";

            // 构建表单参数
            Map<String, String> formData = new HashMap<>();
            formData.put("user", difyConfig.getUserPrefix() + userId);

            // 优先使用messageId
            if (StringUtils.hasLength(messageId)) {
                formData.put("message_id", messageId);
            } else {
                formData.put("text", text);
            }

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送请求
            Response okResponse = okHttpUtils.postForm(apiUrl, formData, headers);

            // 检查响应状态
            if (!okResponse.isSuccessful()) {
                String responseBody = "";
                try (ResponseBody body = okResponse.body()) {
                    if (body != null) {
                        responseBody = body.string();
                    }
                }
                log.error("文字转语音失败: {}, {}", okResponse.code(), responseBody);
                okResponse.close();
                return false;
            }

            // 设置响应头
            response.setContentType("audio/wav");
            response.setHeader("Content-Disposition", "attachment; filename=audio.wav");

            // 将音频内容写入响应
            try (ResponseBody responseBody = okResponse.body()) {
                if (responseBody != null) {
                    byte[] audioBytes = responseBody.bytes();
                    try {
                        response.getOutputStream().write(audioBytes);
                        response.getOutputStream().flush();
                        return true;
                    } catch (IOException e) {
                        log.error("写入音频数据到HTTP响应流失败", e);
                        return false;
                    }
                } else {
                    log.error("文字转语音响应体为空");
                    return false;
                }
            } catch (IOException e) {
                log.error("读取音频流失败", e);
                return false;
            } finally {
                okResponse.close();
            }

        } catch (Exception e) {
            log.error("文字转语音异常", e);
            return false;
        }
    }

    @Override
    public boolean stopStreamingResponse(Long userId, String taskId, String baseUrl, String avatarAuth) {
        if (!StringUtils.hasLength(taskId) || !StringUtils.hasLength(baseUrl) || !StringUtils.hasLength(avatarAuth)) {
            log.error("停止流式响应参数错误: taskId={}, baseUrl={}", taskId, baseUrl);
            return false;
        }

        try {
            // 构建请求URL
            String apiUrl = baseUrl + "/chat-messages/" + taskId + "/stop";

            // 创建请求JSON对象
            JSONObject requestBody = new JSONObject();
            requestBody.set("user", difyConfig.getUserPrefix() + userId);
            String jsonBody = requestBody.toString();

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送POST请求
            Response response = okHttpUtils.postJson(apiUrl, jsonBody, headers);

            // 检查响应状态
            if (!response.isSuccessful()) {
                String responseBody = "";
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        responseBody = body.string();
                    }
                } catch (IOException e) {
                    log.error("读取错误响应体异常", e);
                    return false;
                }
                log.error("停止流式响应失败: {}, {}", response.code(), responseBody);
                response.close();
                return false;
            }

            // 解析响应
            String responseBody = "";
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    responseBody = body.string();
                }
            } catch (IOException e) {
                log.error("读取响应体异常", e);
                return false;
            }

            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);

            // 检查结果
            String result = jsonResponse.getStr("result");
            return "success".equals(result);

        } catch (Exception e) {
            log.error("停止流式响应异常", e);
            return false;
        }
    }

    @Override
    public String uploadFile(Long userId, String fileName, InputStream fileInputStream,
                             String mimeType, String baseUrl, String avatarAuth) {
        if (userId == null || !StringUtils.hasLength(fileName) || fileInputStream == null ||
                !StringUtils.hasLength(mimeType) || !StringUtils.hasLength(baseUrl) ||
                !StringUtils.hasLength(avatarAuth)) {
            log.error("上传文件参数错误: userId={}, fileName={}, mimeType={}, baseUrl={}",
                    userId, fileName, mimeType, baseUrl);
            return null;
        }

        // 验证是否为支持的图片类型
        if (!mimeType.startsWith("image/")) {
            log.error("不支持的文件类型: {}, 目前仅支持图片格式", mimeType);
            return null;
        }

        try {
            // 构建请求URL
            String apiUrl = baseUrl + "/files/upload";

            // 构建表单数据
            Map<String, String> formData = new HashMap<>();
            formData.put("user", difyConfig.getUserPrefix() + userId);

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送 multipart/form-data 请求
            Response response = okHttpUtils.postMultipart(
                    apiUrl, formData, "file", fileName, fileInputStream, mimeType, headers);

            // 检查响应状态
            if (!response.isSuccessful()) {
                String responseBody = "";
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        responseBody = body.string();
                    }
                }
                log.error("上传文件失败: {}, {}", response.code(), responseBody);
                return null;
            }

            // 解析响应
            String responseBody = "";
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    responseBody = body.string();
                }
            }

            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);

            // 获取文件ID
            String fileId = jsonResponse.getStr("id");
            if (!StringUtils.hasLength(fileId)) {
                log.error("上传文件成功但未返回文件ID: {}", responseBody);
                return null;
            }

            log.info("文件上传成功: fileId={}, name={}, size={}, type={}",
                    fileId, jsonResponse.getStr("name"),
                    jsonResponse.getInt("size"), jsonResponse.getStr("mime_type"));

            return fileId;

        } catch (Exception e) {
            log.error("上传文件异常", e);
            return null;
        }
    }

    @Override
    @Transactional
    public AiAvatarChatHistory sendChatMessageWithFiles(Long userId, Long aiAvatarId, String sessionId,
                                                        String content, List<String> fileIds,
                                                        String baseUrl, String avatarAuth) {
        // 创建用户消息对象
        AiAvatarChatHistory userMessage = chatMessageHelper.createUserMessage(userId, aiAvatarId, sessionId, content);

        // 保存用户消息
        boolean saved = aiAvatarChatHistoryService.save(userMessage);
        if (!saved) {
            log.error("Failed to save user message");
            throw new RuntimeException("保存用户消息失败");
        }

        try {
            return sendChatMessageWithFilesRetry(userId, aiAvatarId, sessionId, content, fileIds, baseUrl, avatarAuth, userMessage, false);
        } catch (Exception e) {
            log.error("Error sending chat message with files to Dify", e);
            throw new RuntimeException("发送带文件的聊天消息失败: " + e.getMessage());
        }
    }

    /**
     * 发送带文件的聊天消息并处理会话不存在的情况，使用 OkHttp 实现
     */
    private AiAvatarChatHistory sendChatMessageWithFilesRetry(Long userId, Long aiAvatarId, String sessionId,
                                                              String content, List<String> fileIds,
                                                              String baseUrl, String avatarAuth,
                                                              AiAvatarChatHistory userMessage, boolean retried) {
        try {
            // 构建请求对象
            DifyChatRequest chatRequest = buildChatRequestWithFiles(userId, sessionId, content, fileIds);

            // 如果是重试并且会话不存在，则不传会话ID
            if (retried) {
                chatRequest.setConversation_id(null);
                log.info("重试带文件请求，不传会话ID");
            }

            // 发送请求
            String chatMessagesPath = "/chat-messages"; // API路径
            String url = baseUrl + chatMessagesPath;
            String requestJson = JSONUtil.toJsonStr(chatRequest);
            log.info("Sending request with files to Dify: {}", requestJson);

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送请求
            Response response = okHttpUtils.postJson(url, requestJson, headers);

            try {
                if (!response.isSuccessful()) {
                    String responseBody = "";
                    try (ResponseBody body = response.body()) {
                        if (body != null) {
                            responseBody = body.string();
                        }
                    } catch (IOException e) {
                        log.error("读取错误响应体异常", e);
                        throw new RuntimeException("读取响应失败: " + e.getMessage());
                    }

                    log.error("Dify API error: {}, Headers: {}, Body: {}",
                            response.code(), response.headers(), responseBody);

                    // 检查是否为会话不存在的错误
                    if (response.code() == 404 && !retried) {
                        if (responseBody.contains("Conversation Not Exists")) {
                            log.info("会话ID不存在，将创建新会话: {}", sessionId);
                            // 递归调用，但设置retried标志，不传会话ID
                            return sendChatMessageWithFilesRetry(userId, aiAvatarId, null, content, fileIds,
                                    baseUrl, avatarAuth, userMessage, true);
                        }
                    }

                    throw new RuntimeException("调用Dify API失败: " + response.code() + ", " + responseBody);
                }

                // 解析响应
                String responseBody = "";
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        responseBody = body.string();
                    }
                } catch (IOException e) {
                    log.error("读取响应体异常", e);
                    throw new RuntimeException("读取响应失败: " + e.getMessage());
                }

                DifyChatResponse chatResponse = JSONUtil.toBean(responseBody, DifyChatResponse.class);

                // 检查是否返回了新的会话ID，如果有且与原会话ID不同，则更新会话记录
                if (chatResponse.getConversation_id() != null && !chatResponse.getConversation_id().equals(sessionId)) {
                    log.info("Dify创建了新会话ID: {}, 原会话ID: {}", chatResponse.getConversation_id(), sessionId);

                    // 更新用户消息的会话ID
                    AiAvatarChatHistory updatedUserMessage = new AiAvatarChatHistory();
                    updatedUserMessage.setId(userMessage.getId());
                    updatedUserMessage.setSessionId(chatResponse.getConversation_id());
                    aiAvatarChatHistoryService.updateById(updatedUserMessage);

                    // 更新会话ID
                    sessionId = chatResponse.getConversation_id();
                }

                // 创建并保存AI响应
                AiAvatarChatHistory aiResponse = chatMessageHelper.createAiResponse(userId, aiAvatarId, sessionId, chatResponse);
                boolean saved = aiAvatarChatHistoryService.save(aiResponse);
                if (!saved) {
                    log.error("Failed to save AI response");
                    throw new RuntimeException("保存AI响应失败");
                }

                return aiResponse;
            } finally {
                response.close();
            }

        } catch (Exception e) {
            if (!retried && (e.getMessage().contains("Conversation Not Exists") || e.getMessage().contains("404"))) {
                log.info("会话ID不存在异常，将创建新会话: {}", sessionId);
                // 递归调用，但设置retried标志
                return sendChatMessageWithFilesRetry(userId, aiAvatarId, null, content, fileIds, baseUrl, avatarAuth, userMessage, true);
            }
            throw e;
        }
    }

    /**
     * 构建带文件的聊天请求对象
     */
    private DifyChatRequest buildChatRequestWithFiles(Long userId, String sessionId, String content, List<String> fileIds) {
        DifyChatRequest chatRequest = buildChatRequest(userId, sessionId, content);

        // 添加文件信息
        if (fileIds != null && !fileIds.isEmpty()) {
            List<Map<String, Object>> files = new ArrayList<>();
            for (String fileId : fileIds) {
                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("type", "image");
                fileMap.put("transfer_method", "local_file");
                fileMap.put("upload_file_id", fileId);
                files.add(fileMap);
            }
            chatRequest.setFiles(files);
        }

        return chatRequest;
    }

    @Override
    @Transactional
    public AiAvatarChatHistory sendChatMessageStreamingWithFiles(Long userId, Long aiAvatarId, String sessionId,
                                                                 String content, List<String> fileIds,
                                                                 String baseUrl, String avatarAuth,
                                                                 DifyStreamCallback callback) {
        // 创建用户消息对象
        AiAvatarChatHistory userMessage = chatMessageHelper.createUserMessage(userId, aiAvatarId, sessionId, content);

        // 保存用户消息
        boolean saved = aiAvatarChatHistoryService.save(userMessage);
        if (!saved) {
            log.error("Failed to save user message");
            throw new RuntimeException("保存用户消息失败");
        }

        // 用于存储完整响应
        final AtomicReference<String> fullResponseRef = new AtomicReference<>("");
        final AtomicReference<String> messageIdRef = new AtomicReference<>("");
        final AtomicReference<String> conversationIdRef = new AtomicReference<>(sessionId);

        try {
            return sendStreamingWithFilesRetry(userId, aiAvatarId, sessionId, content, fileIds,
                    baseUrl, avatarAuth, callback, userMessage,
                    fullResponseRef, messageIdRef, conversationIdRef, false);
        } catch (Exception e) {
            log.error("Error sending streaming chat message with files to Dify", e);
            callback.onError(e);
            throw new RuntimeException("发送带文件的流式聊天消息失败: " + e.getMessage());
        }
    }

    /**
     * 发送带文件的流式消息并处理会话不存在的情况，使用 OkHttp 实现
     */
    private AiAvatarChatHistory sendStreamingWithFilesRetry(Long userId, Long aiAvatarId, String sessionId,
                                                            String content, List<String> fileIds,
                                                            String baseUrl, String avatarAuth,
                                                            DifyStreamCallback callback,
                                                            AiAvatarChatHistory userMessage,
                                                            AtomicReference<String> fullResponseRef,
                                                            AtomicReference<String> messageIdRef,
                                                            AtomicReference<String> conversationIdRef,
                                                            boolean retried) throws IOException {
        try {
            // 构建带文件的表单请求
            String chatMessagesPath = "/files-chat-messages"; // 文件聊天API路径
            String url = baseUrl + chatMessagesPath;

            // 构建请求表单
            Map<String, String> formData = new HashMap<>();
            // 添加用户文本
            formData.put("query", content);
            formData.put("user", difyConfig.getUserPrefix() + userId);
            formData.put("response_mode", "streaming"); // 强制使用流式模式

            // 如果会话ID存在且不是重试，添加会话ID
            if (sessionId != null && !retried && isValidUUID(sessionId)) {
                formData.put("conversation_id", sessionId);
            }

            // 添加文件
            if (fileIds != null && !fileIds.isEmpty()) {
                // 对于表单格式，要把每个文件ID单独添加为一个表单项
                for (int i = 0; i < fileIds.size(); i++) {
                    formData.put("files[" + i + "]", fileIds.get(i));
                }
            }

            log.info("Sending streaming files request to Dify: {}", JSONUtil.toJsonStr(formData));

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + avatarAuth);
            headers.put("Accept", "text/event-stream"); // 明确指定接受SSE格式

            // 使用 OkHttp 发送流式表单请求
            Response response = okHttpUtils.postFormStream(url, formData, headers);

            try {
                if (!response.isSuccessful()) {
                    String responseBody = "";
                    try (ResponseBody body = response.body()) {
                        if (body != null) {
                            responseBody = body.string();
                        }
                    }

                    log.error("Dify API error: {}, Headers: {}, Body: {}",
                            response.code(), response.headers(), responseBody);

                    // 检查是否为会话不存在的错误
                    if (response.code() == 404 && !retried) {
                        if (responseBody.contains("Conversation Not Exists")) {
                            log.info("带文件的流式请求会话ID不存在，将创建新会话: {}", sessionId);
                            // 关闭响应
                            response.close();
                            // 递归调用，但设置retried标志，不传会话ID
                            return sendStreamingWithFilesRetry(userId, aiAvatarId, null, content, fileIds, baseUrl, avatarAuth,
                                    callback, userMessage, fullResponseRef, messageIdRef, conversationIdRef, true);
                        }
                    }

                    callback.onError(new RuntimeException("调用Dify带文件API失败: " + response.code() + ", " + responseBody));
                    throw new RuntimeException("调用Dify带文件API失败: " + response.code() + ", " + responseBody);
                }

                // 获取响应体
                final ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    callback.onError(new RuntimeException("Dify API返回空响应"));
                    throw new RuntimeException("Dify API返回空响应");
                }

                // 使用CompletableFuture异步处理流式响应
                CompletableFuture.runAsync(() -> {
                    // 使用BufferedReader逐行读取SSE流
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.byteStream()))) {
                        String line;
                        log.info("开始接收带文件的流式数据...");
                        int lineCount = 0;

                        try {
                            while ((line = reader.readLine()) != null) {
                                lineCount++;
                                if (lineCount % 10 == 0) {
                                    log.info("已接收 {} 行SSE数据", lineCount);
                                }

                                // 处理SSE数据行
                                if (line.startsWith("data: ")) {
                                    // 提取JSON数据内容
                                    String jsonData = line.substring(6); // 移除 "data: " 前缀

                                    if (difyConfig.isEnableStreamingVerboseLog()) {
                                        log.info("接收到SSE数据行: {}", line.substring(0, Math.min(50, line.length())) + (line.length() > 50 ? "..." : ""));
                                    } else {
                                        log.debug("接收到SSE数据行: {}", line.substring(0, Math.min(50, line.length())) + (line.length() > 50 ? "..." : ""));
                                    }

                                    // 立即将数据传递给回调，不等待整个事件结束
                                    if (!jsonData.isEmpty()) {
                                        try {
                                            // 先发送原始数据给回调
                                            if (difyConfig.isEnableStreamingVerboseLog()) {
                                                log.info("立即转发数据行给回调函数");
                                            } else {
                                                log.debug("立即转发数据行给回调函数");
                                            }
                                            callback.onMessage(jsonData);

                                            // 解析数据进行额外处理
                                            DifyStreamChunk chunk = JSONUtil.toBean(jsonData, DifyStreamChunk.class);

                                            // 处理不同类型的事件
                                            if ("message".equals(chunk.getEvent()) && chunk.getAnswer() != null) {
                                                // 累积完整响应
                                                fullResponseRef.updateAndGet(prev -> prev + chunk.getAnswer());
                                                log.debug("累积内容: {}", chunk.getAnswer());

                                                // 保存消息ID
                                                if (messageIdRef.get().isEmpty() && chunk.getId() != null) {
                                                    messageIdRef.set(chunk.getId());
                                                    log.info("获取到消息ID: {}", chunk.getId());
                                                }

                                                // 保存会话ID
                                                if (chunk.getConversation_id() != null) {
                                                    conversationIdRef.set(chunk.getConversation_id());
                                                    log.debug("获取到会话ID: {}", chunk.getConversation_id());
                                                }
                                            } else if ("message_file".equals(chunk.getEvent())) {
                                                // 文件消息事件，记录但由回调处理具体逻辑
                                                log.debug("收到文件消息事件: {}", jsonData);
                                            } else if ("message_end".equals(chunk.getEvent())) {
                                                // 消息结束事件，记录但不做特殊处理
                                                log.info("收到消息结束事件: {}", jsonData);
                                            } else if ("error".equals(chunk.getEvent())) {
                                                // 错误事件
                                                log.error("收到错误事件: {}", jsonData);
                                            } else if ("ping".equals(chunk.getEvent())) {
                                                // ping事件，用于保持连接活跃
                                                log.debug("收到ping事件");
                                            }
                                        } catch (Exception e) {
                                            log.error("处理数据行异常: {}", jsonData, e);
                                        }
                                    }
                                } else if (line.trim().isEmpty()) {
                                    // 空行，忽略
                                    log.debug("接收到空行");
                                    continue;
                                } else {
                                    // 其他类型的SSE行，例如event:、id:等
                                    log.debug("其他SSE行: {}", line);
                                }
                            }
                        } catch (IOException e) {
                            log.error("读取带文件的流式数据行时发生IO异常", e);
                            callback.onError(e);
                        }

                        log.info("流式数据接收完成，共接收 {} 行数据", lineCount);

                        // 流结束，处理最终工作
                        String fullResponse = fullResponseRef.get();
                        String finalConversationId = conversationIdRef.get();

                        // 检查会话ID是否有变化
                        if (finalConversationId != null && !finalConversationId.equals(sessionId)) {
                            log.info("Dify创建了新会话ID: {}, 原会话ID: {}", finalConversationId, sessionId);

                            // 更新用户消息的会话ID
                            AiAvatarChatHistory updatedUserMessage = new AiAvatarChatHistory();
                            updatedUserMessage.setId(userMessage.getId());
                            updatedUserMessage.setSessionId(finalConversationId);
                            aiAvatarChatHistoryService.updateById(updatedUserMessage);
                        }

                        // 保存到数据库
                        if (!fullResponse.isEmpty()) {
                            boolean saved = aiAvatarChatHistoryService.saveMessage(
                                    userId, aiAvatarId, finalConversationId, "ai", fullResponse);

                            if (!saved) {
                                log.error("Failed to save AI response to database");
                            }
                        }

                        // 处理完成通知回调
                        callback.onComplete(fullResponse);

                    } catch (IOException e) {
                        log.error("Error reading streaming response", e);
                        callback.onError(e);
                    } finally {
                        try {
                            responseBody.close();
                        } catch (Exception e) {
                            log.error("Error closing response body", e);
                        }
                    }
                });

                // 创建一个消息对象返回，实际内容会被异步更新
                AiAvatarChatHistory aiResponse = chatMessageHelper.createEmptyAiResponse(userId, aiAvatarId, sessionId);
                return aiResponse;

            } catch (Exception e) {
                if (response != null) {
                    response.close();
                }
                throw e;
            }

        } catch (Exception e) {
            if (!retried && (e.getMessage().contains("Conversation Not Exists") || e.getMessage().contains("404"))) {
                log.info("带文件的流式请求会话ID不存在异常，将创建新会话: {}", sessionId);
                // 递归调用，但设置retried标志
                return sendStreamingWithFilesRetry(userId, aiAvatarId, null, content, fileIds, baseUrl, avatarAuth,
                        callback, userMessage, fullResponseRef, messageIdRef, conversationIdRef, true);
            }
            throw e;
        }
    }
} 
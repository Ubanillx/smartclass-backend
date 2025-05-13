package com.ubanillx.smartclass.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ubanillx.smartclass.config.DifyConfig;
import com.ubanillx.smartclass.model.dto.dify.DifyChatRequest;
import com.ubanillx.smartclass.model.dto.dify.DifyChatResponse;
import com.ubanillx.smartclass.model.dto.dify.DifyStreamChunk;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.ChatMessageVO;
import com.ubanillx.smartclass.service.AiAvatarChatHistoryService;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.DifyService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.ChatMessageHelper;
import com.ubanillx.smartclass.utils.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;
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

    @Resource
    private AiAvatarService aiAvatarService;

    @Resource
    private UserService userService;

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
            }

            // 发送请求
            String chatMessagesPath = "/chat-messages"; // API路径
            String url = baseUrl + chatMessagesPath;
            String requestJson = JSONUtil.toJsonStr(chatRequest);

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
            }

            // 发送请求
            String chatMessagesPath = "/chat-messages"; // API路径
            String url = baseUrl + chatMessagesPath;
            String requestJson = JSONUtil.toJsonStr(chatRequest);

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
                        int lineCount = 0;

                        try {
                            while ((line = reader.readLine()) != null) {
                                lineCount++;
                                // 移除日志计数相关日志打印

                                // 处理SSE数据行
                                if (line.startsWith("data: ")) {
                                    // 提取JSON数据内容
                                    String jsonData = line.substring(6); // 移除 "data: " 前缀

                                    // 立即将数据传递给回调，不等待整个事件结束
                                    if (!jsonData.isEmpty()) {
                                        try {
                                            // 首先发送给回调，确保前端立即收到
                                            callback.onMessage(jsonData);

                                            // 解析数据进行额外处理
                                            try {
                                                DifyStreamChunk chunk = JSONUtil.toBean(jsonData, DifyStreamChunk.class);
    
                                                // 处理不同类型的事件
                                                if ("message".equals(chunk.getEvent()) && chunk.getAnswer() != null) {
                                                    // 累积完整响应
                                                    fullResponseRef.updateAndGet(prev -> prev + chunk.getAnswer());
    
                                                    // 保存消息ID
                                                    if (messageIdRef.get().isEmpty() && chunk.getId() != null) {
                                                        messageIdRef.set(chunk.getId());
                                                    }
    
                                                    // 保存会话ID
                                                    if (chunk.getConversation_id() != null) {
                                                        conversationIdRef.set(chunk.getConversation_id());
                                                    }
                                                } else if ("message_end".equals(chunk.getEvent())) {
                                                    // 消息结束事件，记录但不做特殊处理
                                                } else if ("error".equals(chunk.getEvent())) {
                                                    // 错误事件，保留内部处理逻辑但移除日志
                                                } else if ("ping".equals(chunk.getEvent())) {
                                                    // ping事件，用于保持连接活跃
                                                }
                                            } catch (Exception e) {
                                                // 移除日志，但保留异常处理逻辑
                                            }
                                        } catch (Exception e) {
                                            // 移除日志，但保留异常处理逻辑
                                        }
                                    }
                                } else if (line.trim().isEmpty()) {
                                    // 空行，忽略
                                    continue;
                                }
                            }
                        } catch (IOException e) {
                            // 简化日志但保留错误处理
                            log.error("读取流式数据IO异常");
                            callback.onError(e);
                        }

                        // 流结束，处理最终工作
                        String fullResponse = fullResponseRef.get();
                        String finalConversationId = conversationIdRef.get();

                        // 检查会话ID是否有变化
                        if (!finalConversationId.equals(sessionId)) {
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
                        // 简化日志但保留错误处理
                        log.error("处理流式响应异常");
                        callback.onError(e);
                    } finally {
                        try {
                            responseBody.close();
                        } catch (Exception e) {
                            // 简化关闭响应体的日志
                            log.error("关闭响应体异常");
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
    public boolean stopStreamingResponse(Long userId, String taskId, String baseUrl, String avatarAuth) {
        if (userId == null || !StringUtils.hasLength(taskId) ||
                !StringUtils.hasLength(baseUrl) || !StringUtils.hasLength(avatarAuth)) {
            log.error("停止流式响应参数错误: userId={}, taskId={}, baseUrl={}",
                    userId, taskId, baseUrl);
            return false;
        }

        try {
            // 构建请求URL
            String apiUrl = baseUrl + "/chat-messages/" + taskId + "/stop";

            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("user", difyConfig.getUserPrefix() + userId);

            // 转换为JSON
            String requestJson = JSONUtil.toJsonStr(params);

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + avatarAuth);

            // 使用 OkHttp 发送请求
            Response response = okHttpUtils.postJson(apiUrl, requestJson, headers);

            try {
                if (!response.isSuccessful()) {
                    log.error("停止流式响应失败: {}", response.code());
                    return false;
                }

                // 解析响应体
                String responseBody = response.body().string();
                JSONObject jsonResponse = JSONUtil.parseObj(responseBody);

                // 检查结果
                String result = jsonResponse.getStr("result");
                return "success".equals(result);

            } catch (Exception e) {
                log.error("停止流式响应异常", e);
                return false;
            } finally {
                response.close();
            }

        } catch (Exception e) {
            log.error("停止流式响应异常", e);
            return false;
        }
    }

    /**
     * 处理常规消息发送请求的完整业务逻辑
     */
    @Override
    public ChatMessageVO handleSendMessageRequest(Long userId, Long aiAvatarId, String sessionId, String content, 
                                               boolean endChat, AiAvatarChatHistoryService chatHistoryService, 
                                               AiAvatarService aiAvatarService, UserService userService) {
        // 获取或创建会话ID
        if (org.apache.commons.lang3.StringUtils.isBlank(sessionId)) {
            sessionId = chatHistoryService.createNewSession(userId, aiAvatarId);
        }
        
        // 获取AI分身信息
        AiAvatar aiAvatar = aiAvatarService.getById(aiAvatarId);
        if (aiAvatar == null) {
            throw new RuntimeException("AI分身不存在");
        }
        
        // 验证API信息
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(aiAvatar.getBaseUrl(), aiAvatar.getAvatarAuth())) {
            throw new RuntimeException("AI分身配置不完整");
        }
        
        // 发送消息
        AiAvatarChatHistory result = sendChatMessage(
                userId, aiAvatarId, sessionId, 
                content, aiAvatar.getBaseUrl(), aiAvatar.getAvatarAuth()
        );
        
        // 构建响应
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        BeanUtils.copyProperties(result, chatMessageVO);
        chatMessageVO.setAiAvatarName(aiAvatar.getName());
        chatMessageVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
        
        // 填充用户信息
        User user = userService.getById(userId);
        if (user != null) {
            chatMessageVO.setUserName(user.getUserName());
            chatMessageVO.setUserAvatar(user.getUserAvatar());
        }
        
        // 异步获取会话总结
        if (endChat) {
            processSessionSummary(sessionId, aiAvatar.getBaseUrl(), aiAvatar.getAvatarAuth());
        }
        
        return chatMessageVO;
    }
    
    /**
     * 处理流式消息发送请求的完整业务逻辑
     */
    @Override
    public SseEmitter handleStreamMessageRequest(Long userId, Long aiAvatarId, String sessionId, String content,
                                              AiAvatarChatHistoryService chatHistoryService, AiAvatarService aiAvatarService) {
        // 获取或创建会话ID
        if (org.apache.commons.lang3.StringUtils.isBlank(sessionId)) {
            sessionId = chatHistoryService.createNewSession(userId, aiAvatarId);
        }
        
        // 创建SseEmitter，超时设置为5分钟
        final SseEmitter emitter = new SseEmitter(300000L);
        
        try {
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarService.getById(aiAvatarId);
            if (aiAvatar == null || org.apache.commons.lang3.StringUtils.isAnyBlank(aiAvatar.getBaseUrl(), aiAvatar.getAvatarAuth())) {
                throw new RuntimeException("AI分身不存在或配置不完整");
            }
            
            // 发送初始连接事件
            sendInitialConnectEvent(emitter);
            
            // 处理流式消息发送
            handleStreamingMessage(emitter, userId, aiAvatarId, sessionId, 
                    content, aiAvatar.getBaseUrl(), aiAvatar.getAvatarAuth());
            
            // 设置事件监听器
            setupEmitterListeners(emitter);
            
        } catch (Exception e) {
            log.error("设置流式聊天时出错: {}", e.getMessage());
            safeCompleteEmitter(emitter, e);
        }
        
        return emitter;
    }
    
    /**
     * 异步处理会话总结
     */
    private void processSessionSummary(String sessionId, String baseUrl, String avatarAuth) {
        CompletableFuture.runAsync(() -> {
            try {
                String summary = getSessionSummary(sessionId, baseUrl, avatarAuth);
                aiAvatarChatHistoryService.updateSessionSummary(sessionId, summary);
            } catch (Exception e) {
                log.error("获取会话总结失败", e);
            }
        });
    }
    
    /**
     * 发送初始连接事件
     */
    private void sendInitialConnectEvent(SseEmitter emitter) {
        try {
            SseEmitter.SseEventBuilder initialEvent = SseEmitter.event()
                .data("{\"event\":\"connected\",\"message\":\"SSE连接已建立\"}")
                .id("connect-" + System.currentTimeMillis())
                .name("connect");
            emitter.send(initialEvent);
        } catch (Exception e) {
            log.warn("发送初始连接事件失败，客户端可能已断开", e);
            safeCompleteEmitter(emitter, null);
            throw new RuntimeException("连接建立失败");
        }
    }
    
    /**
     * 处理流式消息发送
     */
    private void handleStreamingMessage(SseEmitter emitter, Long userId, Long aiAvatarId, 
                                       String sessionId, String content, String baseUrl, String avatarAuth) {
        CompletableFuture.runAsync(() -> {
            try {
                sendChatMessageStreaming(
                        userId, aiAvatarId, sessionId, content, baseUrl, avatarAuth,
                        new DifyStreamCallback() {
                            @Override
                            public void onMessage(String chunk) {
                                try {
                                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                                        .data(chunk)
                                        .id(String.valueOf(System.currentTimeMillis()))
                                        .name("message");
                                    emitter.send(event);
                                } catch (Exception e) {
                                    safeCompleteEmitter(emitter, e);
                                }
                            }
                            
                            @Override
                            public void onComplete(String fullResponse) {
                                try {
                                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                                        .data("{\"event\":\"complete\",\"message\":\"流式响应已完成\"}")
                                        .id("complete-" + System.currentTimeMillis())
                                        .name("complete");
                                    emitter.send(event);
                                    safeCompleteEmitter(emitter, null);
                                } catch (Exception e) {
                                    safeCompleteEmitter(emitter, e);
                                }
                            }
                            
                            @Override
                            public void onError(Throwable error) {
                                try {
                                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                                        .data("{\"event\":\"error\",\"message\":\"" + error.getMessage() + "\"}")
                                        .id("error-" + System.currentTimeMillis())
                                        .name("error");
                                    emitter.send(event);
                                    safeCompleteEmitter(emitter, error);
                                } catch (Exception e) {
                                    safeCompleteEmitter(emitter, error);
                                }
                            }
                        }
                );
            } catch (Exception e) {
                log.error("流式聊天过程中出错: {}", e.getMessage());
                try {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data("{\"event\":\"error\",\"message\":\"" + e.getMessage() + "\"}")
                        .id("error-" + System.currentTimeMillis())
                        .name("error");
                    emitter.send(event);
                } catch (Exception ignored) { }
                safeCompleteEmitter(emitter, e);
            }
        });
    }
    
    /**
     * 设置SseEmitter的监听器
     */
    private void setupEmitterListeners(SseEmitter emitter) {
        emitter.onTimeout(() -> {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data("{\"event\":\"timeout\",\"message\":\"连接超时\"}")
                    .id("timeout-" + System.currentTimeMillis())
                    .name("timeout");
                emitter.send(event);
            } catch (Exception ignored) { }
        });
        
        // 空实现保持完整性
        emitter.onCompletion(() -> {});
        emitter.onError(error -> {});
    }
    
    /**
     * 安全地完成SseEmitter，避免重复完成导致的异常
     */
    private void safeCompleteEmitter(SseEmitter emitter, Throwable error) {
        try {
            if (error != null) {
                emitter.completeWithError(error);
            } else {
                emitter.complete();
            }
        } catch (Exception e) {
            // 通常这意味着emitter已经被完成或关闭了
            // 忽略异常
        }
    }
} 
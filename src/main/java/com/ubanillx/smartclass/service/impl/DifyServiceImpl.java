package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ubanillx.smartclass.config.DifyConfig;
import com.ubanillx.smartclass.model.dto.dify.DifyChatRequest;
import com.ubanillx.smartclass.model.dto.dify.DifyChatResponse;
import com.ubanillx.smartclass.model.dto.dify.DifyStreamChunk;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.service.AiAvatarChatHistoryService;
import com.ubanillx.smartclass.service.DifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
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

    @Override
    @Transactional
    public AiAvatarChatHistory sendChatMessage(Long userId, Long aiAvatarId, String sessionId, String content, 
                                             String baseUrl, String avatarAuth) {
        // 先记录用户消息
        AiAvatarChatHistory userMessage = saveUserMessage(userId, aiAvatarId, sessionId, content);
        
        try {
            return sendChatMessageWithRetry(userId, aiAvatarId, sessionId, content, baseUrl, avatarAuth, userMessage, false);
        } catch (Exception e) {
            log.error("Error sending chat message to Dify", e);
            throw new RuntimeException("发送聊天消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送聊天消息并处理会话不存在的情况
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
            
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + avatarAuth)
                    .body(requestJson)
                    .execute();
            
            if (!response.isOk()) {
                log.error("Dify API error: {}, Headers: {}, Body: {}", response.getStatus(), response.headers(), response.body());
                
                // 检查是否为会话不存在的错误
                if (response.getStatus() == 404 && !retried) {
                    String responseBody = response.body();
                    if (responseBody.contains("Conversation Not Exists")) {
                        log.info("会话ID不存在，将创建新会话: {}", sessionId);
                        // 递归调用，但设置retried标志，不传会话ID
                        return sendChatMessageWithRetry(userId, aiAvatarId, sessionId, content, baseUrl, avatarAuth, userMessage, true);
                    }
                }
                
                throw new RuntimeException("调用Dify API失败: " + response.getStatus() + ", " + response.body());
            }
            
            // 解析响应
            DifyChatResponse chatResponse = JSONUtil.toBean(response.body(), DifyChatResponse.class);
            
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
            
            // 记录AI响应
            return saveAiResponse(userId, aiAvatarId, sessionId, chatResponse);
            
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
        // 先记录用户消息
        AiAvatarChatHistory userMessage = saveUserMessage(userId, aiAvatarId, sessionId, content);
        
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
     * 发送流式消息并处理会话不存在的情况
     */
    private AiAvatarChatHistory sendStreamingWithRetry(Long userId, Long aiAvatarId, String sessionId, 
                                                    String content, String baseUrl, String avatarAuth, 
                                                    DifyStreamCallback callback, 
                                                    AiAvatarChatHistory userMessage,
                                                    AtomicReference<String> fullResponseRef,
                                                    AtomicReference<String> messageIdRef,
                                                    AtomicReference<String> conversationIdRef,
                                                    boolean retried) {
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
            
            // 同步发送请求获取响应
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + avatarAuth)
                    .header("Accept", "text/event-stream")
                    .body(requestJson)
                    .execute();
            
            if (!response.isOk()) {
                log.error("Dify API error: {}, Headers: {}, Body: {}", response.getStatus(), response.headers(), response.body());
                
                // 检查是否为会话不存在的错误
                if (response.getStatus() == 404 && !retried) {
                    String responseBody = response.body();
                    if (responseBody.contains("Conversation Not Exists")) {
                        log.info("流式请求会话ID不存在，将创建新会话: {}", sessionId);
                        // 递归调用，但设置retried标志，不传会话ID
                        return sendStreamingWithRetry(userId, aiAvatarId, null, content, baseUrl, avatarAuth, 
                                                   callback, userMessage, fullResponseRef, messageIdRef, conversationIdRef, true);
                    }
                }
                
                callback.onError(new RuntimeException("调用Dify API失败: " + response.getStatus() + ", " + response.body()));
                throw new RuntimeException("调用Dify API失败: " + response.getStatus() + ", " + response.body());
            }
            
            // 使用CompletableFuture异步处理流式响应
            CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.bodyStream()))) {
                    String line;
                    StringBuilder eventData = new StringBuilder();
                    
                    while ((line = reader.readLine()) != null) {
                        if (line.isEmpty()) {
                            // 空行表示事件结束，处理累积的数据
                            if (eventData.length() > 0) {
                                String data = eventData.toString();
                                if (data.startsWith("data: ")) {
                                    data = data.substring(6); // 移除 "data: " 前缀
                                }
                                
                                try {
                                    DifyStreamChunk chunk = JSONUtil.toBean(data, DifyStreamChunk.class);
                                    
                                    // 处理流式消息
                                    if ("message".equals(chunk.getEvent())) {
                                        // 累积完整响应
                                        fullResponseRef.set(fullResponseRef.get() + chunk.getAnswer());
                                        
                                        // 保存消息ID
                                        if (messageIdRef.get().isEmpty() && chunk.getId() != null) {
                                            messageIdRef.set(chunk.getId());
                                        }
                                        
                                        // 保存会话ID
                                        if (chunk.getConversation_id() != null) {
                                            conversationIdRef.set(chunk.getConversation_id());
                                        }
                                        
                                        // 回调处理
                                        callback.onMessage(data);
                                    }
                                } catch (Exception e) {
                                    log.error("Error parsing stream chunk: {}", data, e);
                                    callback.onError(e);
                                }
                                
                                // 重置事件数据缓冲区
                                eventData.setLength(0);
                            }
                        } else if (line.startsWith("data: ")) {
                            // 累积事件数据
                            eventData.append(line);
                        }
                    }
                    
                    // 流结束，记录AI响应
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
                    boolean saved = aiAvatarChatHistoryService.saveMessage(
                            userId, aiAvatarId, finalConversationId, "ai", fullResponse);
                    
                    if (!saved) {
                        log.error("Failed to save AI response to database");
                    }
                    
                    // 处理完成
                    callback.onComplete(fullResponse);
                    
                } catch (IOException e) {
                    log.error("Error reading streaming response", e);
                    callback.onError(e);
                }
            });
            
            // 创建一个消息对象返回，实际内容会被异步更新
            AiAvatarChatHistory aiResponse = new AiAvatarChatHistory();
            aiResponse.setUserId(userId);
            aiResponse.setAiAvatarId(aiAvatarId);
            aiResponse.setSessionId(sessionId);
            aiResponse.setMessageType("ai");
            aiResponse.setContent(""); // 初始为空，内容会被异步填充
            aiResponse.setCreateTime(new Date());
            
            return aiResponse;
            
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
    
    /**
     * 保存用户消息
     */
    private AiAvatarChatHistory saveUserMessage(Long userId, Long aiAvatarId, String sessionId, String content) {
        boolean saved = aiAvatarChatHistoryService.saveMessage(userId, aiAvatarId, sessionId, "user", content);
        if (!saved) {
            log.error("Failed to save user message");
            throw new RuntimeException("保存用户消息失败");
        }
        
        // 查询刚保存的消息
        AiAvatarChatHistory userMessage = new AiAvatarChatHistory();
        userMessage.setUserId(userId);
        userMessage.setAiAvatarId(aiAvatarId);
        userMessage.setSessionId(sessionId);
        userMessage.setMessageType("user");
        userMessage.setContent(content);
        return userMessage;
    }
    
    /**
     * 保存AI响应
     */
    private AiAvatarChatHistory saveAiResponse(Long userId, Long aiAvatarId, String sessionId, DifyChatResponse chatResponse) {
        int tokens = 0;
        if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
            tokens = chatResponse.getMetadata().getUsage().getCompletion_tokens();
        }
        
        boolean saved = aiAvatarChatHistoryService.saveMessage(userId, aiAvatarId, sessionId, "ai", chatResponse.getAnswer());
        if (!saved) {
            log.error("Failed to save AI response");
            throw new RuntimeException("保存AI响应失败");
        }
        
        // 查询刚保存的消息
        AiAvatarChatHistory aiMessage = new AiAvatarChatHistory();
        aiMessage.setUserId(userId);
        aiMessage.setAiAvatarId(aiAvatarId);
        aiMessage.setSessionId(sessionId);
        aiMessage.setMessageType("ai");
        aiMessage.setContent(chatResponse.getAnswer());
        aiMessage.setTokens(tokens);
        return aiMessage;
    }
} 
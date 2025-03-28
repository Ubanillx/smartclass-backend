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
import com.ubanillx.smartclass.util.ChatMessageHelper;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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
            
            // 创建并保存AI响应
            AiAvatarChatHistory aiResponse = chatMessageHelper.createAiResponse(userId, aiAvatarId, sessionId, chatResponse);
            boolean saved = aiAvatarChatHistoryService.save(aiResponse);
            if (!saved) {
                log.error("Failed to save AI response");
                throw new RuntimeException("保存AI响应失败");
            }
            
            return aiResponse;
            
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
            
            // 使用HuTool发送请求
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + avatarAuth)
                .body(requestBody.toString())
                .timeout(60000) // 设置60秒超时
                .execute();
            
            if (!response.isOk()) {
                throw new RuntimeException("获取会话总结失败: " + response.getStatus() + " " + response.body());
            }
            
            // 解析响应
            String responseBody = response.body();
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
            
            // 使用HuTool发送DELETE请求
            HttpResponse response = HttpRequest.delete(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + avatarAuth)
                .body(requestBody.toString())
                .timeout(30000) // 设置30秒超时
                .execute();
            
            // 检查响应状态
            if (!response.isOk()) {
                // 如果是404错误，则表示会话不存在，也算成功
                if (response.getStatus() == 404) {
                    log.warn("Dify会话不存在，视为删除成功: {}", sessionId);
                    return true;
                }
                log.error("删除Dify会话失败: {}, {}", response.getStatus(), response.body());
                return false;
            }
            
            // 解析响应
            String responseBody = response.body();
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
            Map<String, Object> formMap = new HashMap<>();
            formMap.put("user", difyConfig.getUserPrefix() + userId);
            
            // 优先使用messageId
            if (StringUtils.hasLength(messageId)) {
                formMap.put("message_id", messageId);
            } else {
                formMap.put("text", text);
            }
            
            // 使用HuTool发送POST请求，直接将响应输出到HttpServletResponse
            HttpResponse httpResponse = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + avatarAuth)
                .form(formMap)
                .timeout(60000) // 设置60秒超时
                .execute();
            
            // 检查响应状态
            if (!httpResponse.isOk()) {
                log.error("文字转语音失败: {}, {}", httpResponse.getStatus(), httpResponse.body());
                return false;
            }
            
            // 设置响应头
            response.setContentType("audio/wav");
            response.setHeader("Content-Disposition", "attachment; filename=audio.wav");
            
            // 将音频内容写入响应
            try {
                byte[] audioBytes = httpResponse.bodyBytes();
                response.getOutputStream().write(audioBytes);
                response.getOutputStream().flush();
                return true;
            } catch (IOException e) {
                log.error("写入音频流失败", e);
                return false;
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
            
            // 使用HuTool发送POST请求
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + avatarAuth)
                .body(requestBody.toString())
                .timeout(30000) // 设置30秒超时
                .execute();
            
            // 检查响应状态
            if (!response.isOk()) {
                log.error("停止流式响应失败: {}, {}", response.getStatus(), response.body());
                return false;
            }
            
            // 解析响应
            String responseBody = response.body();
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
            
            // 使用HuTool发送multipart/form-data请求
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + avatarAuth)
                .form("file", fileInputStream, fileName)
                .form("user", difyConfig.getUserPrefix() + userId)
                .timeout(60000) // 设置60秒超时
                .execute();
            
            // 检查响应状态
            if (!response.isOk()) {
                log.error("上传文件失败: {}, {}", response.getStatus(), response.body());
                return null;
            }
            
            // 解析响应
            String responseBody = response.body();
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
     * 发送带文件的聊天消息并处理会话不存在的情况
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
                        return sendChatMessageWithFilesRetry(userId, aiAvatarId, sessionId, content, fileIds, baseUrl, avatarAuth, userMessage, true);
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
            
            // 创建并保存AI响应
            AiAvatarChatHistory aiResponse = chatMessageHelper.createAiResponse(userId, aiAvatarId, sessionId, chatResponse);
            boolean saved = aiAvatarChatHistoryService.save(aiResponse);
            if (!saved) {
                log.error("Failed to save AI response");
                throw new RuntimeException("保存AI响应失败");
            }
            
            return aiResponse;
            
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
     * 发送带文件的流式消息并处理会话不存在的情况
     */
    private AiAvatarChatHistory sendStreamingWithFilesRetry(Long userId, Long aiAvatarId, String sessionId, 
                                                         String content, List<String> fileIds,
                                                         String baseUrl, String avatarAuth, 
                                                         DifyStreamCallback callback, 
                                                         AiAvatarChatHistory userMessage,
                                                         AtomicReference<String> fullResponseRef,
                                                         AtomicReference<String> messageIdRef,
                                                         AtomicReference<String> conversationIdRef,
                                                         boolean retried) {
        try {
            // 构建请求对象
            DifyChatRequest chatRequest = buildChatRequestWithFiles(userId, sessionId, content, fileIds);
            chatRequest.setResponse_mode("streaming"); // 强制使用流式模式
            
            // 如果是重试并且会话不存在，则不传会话ID
            if (retried) {
                chatRequest.setConversation_id(null);
                log.info("重试带文件流式请求，不传会话ID");
            }
            
            // 发送请求
            String chatMessagesPath = "/chat-messages"; // API路径
            String url = baseUrl + chatMessagesPath;
            String requestJson = JSONUtil.toJsonStr(chatRequest);
            log.info("Sending streaming request with files to Dify: {}", requestJson);
            
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
                        return sendStreamingWithFilesRetry(userId, aiAvatarId, null, content, fileIds, 
                                                        baseUrl, avatarAuth, callback, userMessage, 
                                                        fullResponseRef, messageIdRef, conversationIdRef, true);
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
                                    } else if ("message_file".equals(chunk.getEvent())) {
                                        // 处理文件消息事件
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
                return sendStreamingWithFilesRetry(userId, aiAvatarId, null, content, fileIds, 
                                                baseUrl, avatarAuth, callback, userMessage, 
                                                fullResponseRef, messageIdRef, conversationIdRef, true);
            }
            throw e;
        }
    }
} 
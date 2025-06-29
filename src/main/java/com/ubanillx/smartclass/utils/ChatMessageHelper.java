package com.ubanillx.smartclass.utils;

import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.model.dto.dify.DifyChatResponse;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 聊天消息帮助类，用于处理消息相关逻辑
 * 这个类主要目的是打破DifyServiceImpl和AiAvatarChatHistoryServiceImpl之间的循环依赖
 */
@Component
public class ChatMessageHelper {

    /**
     * 创建用户消息对象（不保存到数据库）
     *
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param sessionId 会话ID
     * @param content 消息内容
     * @return 用户消息对象
     */
    public AiAvatarChatHistory createUserMessage(Long userId, Long aiAvatarId, String sessionId, String content) {
        AiAvatarChatHistory userMessage = new AiAvatarChatHistory();
        userMessage.setUserId(userId);
        userMessage.setAiAvatarId(aiAvatarId);
        userMessage.setSessionId(sessionId);
        userMessage.setMessageType("user");
        userMessage.setContent(content);
        userMessage.setCreateTime(new Date());
        
        // 计算tokens (简单实现)
        int tokens = content.length() / 4; // 简单估算，4个字符约等于1个token
        userMessage.setTokens(tokens);
        
        return userMessage;
    }
    
    /**
     * 创建AI响应消息对象（不保存到数据库）
     *
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param sessionId 会话ID
     * @param chatResponse Dify聊天响应
     * @return AI响应消息对象
     */
    public AiAvatarChatHistory createAiResponse(Long userId, Long aiAvatarId, String sessionId, DifyChatResponse chatResponse) {
        int tokens = 0;
        if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
            tokens = chatResponse.getMetadata().getUsage().getCompletion_tokens();
        }
        
        AiAvatarChatHistory aiMessage = new AiAvatarChatHistory();
        aiMessage.setUserId(userId);
        aiMessage.setAiAvatarId(aiAvatarId);
        aiMessage.setSessionId(sessionId);
        aiMessage.setMessageType("ai");
        aiMessage.setContent(chatResponse.getAnswer());
        aiMessage.setTokens(tokens);
        aiMessage.setCreateTime(new Date());
        
        return aiMessage;
    }
    
    /**
     * 创建空的AI响应消息对象（用于流式响应）
     *
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param sessionId 会话ID
     * @return 空的AI响应消息对象
     */
    public AiAvatarChatHistory createEmptyAiResponse(Long userId, Long aiAvatarId, String sessionId) {
        AiAvatarChatHistory aiResponse = new AiAvatarChatHistory();
        aiResponse.setUserId(userId);
        aiResponse.setAiAvatarId(aiAvatarId);
        aiResponse.setSessionId(sessionId);
        aiResponse.setMessageType("ai");
        aiResponse.setContent(""); // 初始为空，内容会被异步填充
        aiResponse.setCreateTime(new Date());
        return aiResponse;
    }
} 
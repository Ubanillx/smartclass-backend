package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.PrivateChatSession;

/**
 * 聊天会话工具服务接口
 * 提取ChatMessageService和PrivateChatSessionService的共享功能
 * 用于解决循环依赖问题
 */
public interface ChatSessionUtils {
    
    /**
     * 获取或创建聊天会话
     *
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 聊天会话
     */
    PrivateChatSession getOrCreateChatSession(Long userId1, Long userId2);
    
    /**
     * 更新会话最后消息时间
     *
     * @param chatSession 聊天会话
     */
    void updateSessionLastMessageTime(PrivateChatSession chatSession);
    
    /**
     * 获取未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Long getUnreadMessageCount(Long userId);
    
    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param userId 用户ID
     */
    void markMessageAsRead(Long messageId, Long userId);
} 
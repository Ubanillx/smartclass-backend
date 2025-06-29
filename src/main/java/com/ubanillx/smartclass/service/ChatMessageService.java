package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.dto.websocket.WebSocketMessage;
import com.ubanillx.smartclass.model.entity.PrivateMessage;

import java.util.List;

/**
 * 聊天消息服务接口
 */
public interface ChatMessageService {
    
    /**
     * 处理聊天消息
     * 
     * @param message WebSocket消息
     */
    void handleChatMessage(WebSocketMessage message);
    
    /**
     * 处理私聊消息（旧接口，已废弃）
     * 
     * @deprecated 使用 {@link #handleChatMessage(WebSocketMessage)} 代替
     */
    @Deprecated
    void handlePrivateMessage(com.ubanillx.smartclass.model.websocket.WebSocketMessage message);
    
    /**
     * 处理好友请求（旧接口，已废弃）
     * 
     * @deprecated 使用 {@link #handleChatMessage(WebSocketMessage)} 代替
     */
    @Deprecated
    void handleFriendRequest(com.ubanillx.smartclass.model.websocket.WebSocketMessage message);
    
    /**
     * 处理系统通知（旧接口，已废弃）
     * 
     * @deprecated 使用 {@link #handleChatMessage(WebSocketMessage)} 代替
     */
    @Deprecated
    void handleSystemNotification(com.ubanillx.smartclass.model.websocket.WebSocketMessage message);
    
    /**
     * 发送私聊消息
     */
    void sendPrivateMessage(Long senderId, Long receiverId, String content);
    
    /**
     * 发送WebSocket消息
     * 
     * @param message WebSocket消息
     */
    void sendWebSocketMessage(WebSocketMessage message);
    
    /**
     * 从缓存获取未读私聊消息
     */
    List<PrivateMessage> getUnreadMessages(Long userId);
    
    /**
     * 标记消息为已读
     */
    void markMessageAsRead(Long messageId, Long userId);
    
    /**
     * 标记所有消息为已读
     */
    void markAllMessagesAsRead(Long userId);
    
    /**
     * 获取用户未读消息数量
     */
    int getUnreadMessageCount(Long userId);
} 
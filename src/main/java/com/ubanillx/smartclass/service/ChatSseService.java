package com.ubanillx.smartclass.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 聊天消息SSE服务接口
 * 用于实时发送用户对话消息给前端
 */
public interface ChatSseService {
    
    /**
     * 创建新的SSE连接
     *
     * @param userId 用户ID
     * @return SSE发射器
     */
    SseEmitter createConnection(Long userId);
    
    /**
     * 关闭指定用户的SSE连接
     *
     * @param userId 用户ID
     */
    void closeConnection(Long userId);
    
    /**
     * 发送聊天消息到指定用户
     *
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @param content 消息内容
     * @param sessionId 会话ID
     * @return 是否发送成功
     */
    boolean sendChatMessage(Long receiverId, Long senderId, String content, String sessionId);
    
    /**
     * 发送系统通知到指定用户
     *
     * @param userId 用户ID
     * @param content 消息内容
     * @param data 附加数据
     * @return 是否发送成功
     */
    boolean sendSystemNotification(Long userId, String content, Object data);
    
    /**
     * 广播系统通知给所有在线用户
     *
     * @param content 消息内容
     * @param data 附加数据
     * @return 是否发送成功
     */
    boolean broadcastSystemNotification(String content, Object data);
    
    /**
     * 发送用户状态更新通知
     *
     * @param userId 用户ID
     * @param online 是否在线
     * @return 是否发送成功
     */
    boolean sendUserStatusUpdate(Long userId, boolean online);
    
    /**
     * 发送消息已读状态更新
     *
     * @param receiverId 接收者ID
     * @param messageId 消息ID
     * @param sessionId 会话ID
     * @param isRead 是否已读
     * @return 是否发送成功
     */
    boolean sendReadStatusUpdate(Long receiverId, Long messageId, String sessionId, boolean isRead);
    
    /**
     * 发送消息已读状态批量更新
     *
     * @param receiverId 接收者ID
     * @param messageIds 消息ID列表
     * @param sessionId 会话ID
     * @param isRead 是否已读
     * @return 是否发送成功
     */
    boolean sendBatchReadStatusUpdate(Long receiverId, List<Long> messageIds, String sessionId, boolean isRead);
    
    /**
     * 发送会话所有消息已读状态更新
     *
     * @param receiverId 接收者ID
     * @param sessionId 会话ID
     * @return 是否发送成功
     */
    boolean sendSessionReadStatusUpdate(Long receiverId, String sessionId);
    
    /**
     * 发送未读消息数量更新
     *
     * @param userId 用户ID
     * @param unreadCount 未读消息数量
     * @return 是否发送成功
     */
    boolean sendUnreadCountUpdate(Long userId, int unreadCount);
} 
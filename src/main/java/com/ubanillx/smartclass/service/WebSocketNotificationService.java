package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.dto.websocket.WebSocketMessage;

/**
 * WebSocket通知服务接口
 * 提供实时推送系统通知和管理员监控功能
 */
public interface WebSocketNotificationService {

    /**
     * 发送系统通知给指定用户
     * 
     * @param userId 用户ID
     * @param content 通知内容
     * @param data 附加数据
     */
    void sendSystemNotification(Long userId, String content, Object data);
    
    /**
     * 广播系统通知给所有用户
     * 
     * @param content 通知内容
     * @param data 附加数据
     */
    void broadcastSystemNotification(String content, Object data);
    
    /**
     * 发送系统通知给所有管理员
     * 
     * @param content 通知内容
     * @param data 附加数据
     */
    void notifyAdmins(String content, Object data);
    
    /**
     * 向管理员推送聊天监控数据
     * 
     * @param sessionId 会话ID
     * @param chatMessage 聊天消息
     */
    void pushChatMonitorData(String sessionId, WebSocketMessage chatMessage);
    
    /**
     * 当用户上线时通知管理员
     * 
     * @param userId 上线用户ID
     */
    void notifyUserOnline(Long userId);
    
    /**
     * 当用户下线时通知管理员
     * 
     * @param userId 下线用户ID
     */
    void notifyUserOffline(Long userId);
} 
package com.ubanillx.smartclass.constant;

/**
 * WebSocket消息类型常量
 */
public interface WebSocketMessageType {
    
    /**
     * 聊天消息
     */
    String CHAT = "chat";
    
    /**
     * 心跳消息
     */
    String HEARTBEAT = "heartbeat";
    
    /**
     * 命令消息
     */
    String COMMAND = "command";
    
    /**
     * 系统消息
     */
    String SYSTEM = "system";
    
    /**
     * 消息已读状态更新
     */
    String READ_STATUS = "read_status";
    
    /**
     * 未读消息计数更新
     */
    String UNREAD_COUNT = "unread_count";
    
    /**
     * 用户在线状态
     */
    String USER_STATUS = "user_status";
    
    /**
     * 用户正在输入
     */
    String TYPING = "typing";
    
    /**
     * 会话更新
     */
    String SESSION_UPDATE = "session_update";
    
    /**
     * 认证消息
     */
    String AUTH = "auth";
    
    /**
     * 认证成功
     */
    String AUTH_SUCCESS = "auth_success";
    
    /**
     * 认证失败
     */
    String AUTH_ERROR = "auth_error";
    
    /**
     * 错误消息
     */
    String ERROR = "error";
} 
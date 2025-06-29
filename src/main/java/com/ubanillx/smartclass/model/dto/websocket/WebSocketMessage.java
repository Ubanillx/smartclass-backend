package com.ubanillx.smartclass.model.dto.websocket;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * WebSocket消息模型
 * 用于标准化WebSocket消息的格式
 */
@Data
public class WebSocketMessage implements Serializable {
    /**
     * 消息类型
     * chat: 聊天消息
     * heartbeat: 心跳消息
     * command: 命令消息
     * system: 系统消息
     * read_status: 消息已读状态更新
     * unread_count: 未读消息计数
     * user_status: 用户在线状态
     * typing: 用户正在输入
     * session_update: 会话更新
     */
    private String type;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息发送者ID
     */
    private Long senderId;
    
    /**
     * 消息接收者ID
     */
    private Long receiverId;
    
    /**
     * 时间戳
     */
    private Long timestamp = System.currentTimeMillis();
    
    /**
     * 附加数据，可以是任意JSON对象
     */
    private Object data;
    
    /**
     * 消息ID，用于标识特定消息
     */
    private String messageId;
    
    /**
     * 消息状态：
     * 0 - 发送中
     * 1 - 已发送
     * 2 - 已送达
     * 3 - 已读
     * 4 - 发送失败
     */
    private Integer status;
    
    /**
     * 未读消息计数
     */
    private Integer unreadCount;
    
    /**
     * 会话相关的用户ID列表
     */
    private List<Long> userIds;
    
    private static final long serialVersionUID = 1L;
} 
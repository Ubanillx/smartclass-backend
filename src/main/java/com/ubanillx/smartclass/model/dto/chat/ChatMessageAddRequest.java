package com.ubanillx.smartclass.model.dto.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * 聊天消息添加请求
 */
@Data
public class ChatMessageAddRequest implements Serializable {
    
    /**
     * AI分身ID
     */
    private Long aiAvatarId;
    
    /**
     * 会话ID，如果为空则创建新会话
     */
    private String sessionId;
    
    /**
     * 消息类型：user/ai
     */
    private String messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    private static final long serialVersionUID = 1L;
} 
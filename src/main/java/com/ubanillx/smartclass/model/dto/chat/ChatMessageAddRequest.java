package com.ubanillx.smartclass.model.dto.chat;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    
    /**
     * 文件ID列表，用于多模态对话
     */
    private List<String> fileIds;
    
    /**
     * 是否结束对话，结束对话时会获取会话总结
     */
    private boolean endChat;
    
    private static final long serialVersionUID = 1L;
} 
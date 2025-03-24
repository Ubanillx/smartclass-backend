package com.ubanillx.smartclass.model.dto.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * 聊天会话更新请求
 */
@Data
public class ChatSessionUpdateRequest implements Serializable {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 会话名称
     */
    private String sessionName;
    
    private static final long serialVersionUID = 1L;
} 
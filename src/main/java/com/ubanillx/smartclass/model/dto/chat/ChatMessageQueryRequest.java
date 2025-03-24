package com.ubanillx.smartclass.model.dto.chat;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 聊天消息查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatMessageQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息类型：user/ai
     */
    private String messageType;
    
    /**
     * 消息内容（模糊搜索）
     */
    private String content;
    
    private static final long serialVersionUID = 1L;
} 
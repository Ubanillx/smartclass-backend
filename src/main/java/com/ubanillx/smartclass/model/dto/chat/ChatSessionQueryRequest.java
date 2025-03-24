package com.ubanillx.smartclass.model.dto.chat;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 聊天会话查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatSessionQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * AI分身ID
     */
    private Long aiAvatarId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 会话名称（模糊搜索）
     */
    private String sessionName;
    
    private static final long serialVersionUID = 1L;
} 
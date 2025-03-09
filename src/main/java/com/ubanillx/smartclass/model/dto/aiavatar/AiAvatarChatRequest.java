package com.ubanillx.smartclass.model.dto.aiavatar;

import lombok.Data;

import java.io.Serializable;

/**
 * AI分身聊天请求
 */
@Data
public class AiAvatarChatRequest implements Serializable {

    /**
     * AI分身id
     */
    private Long aiAvatarId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 自定义设置，JSON格式
     */
    private String customSettings;

    private static final long serialVersionUID = 1L;
} 
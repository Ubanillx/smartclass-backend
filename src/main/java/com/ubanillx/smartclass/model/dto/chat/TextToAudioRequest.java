package com.ubanillx.smartclass.model.dto.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * 文字转语音请求DTO
 */
@Data
public class TextToAudioRequest implements Serializable {
    
    /**
     * AI分身ID
     */
    private Long aiAvatarId;
    
    /**
     * 要转换的文本内容
     */
    private String text;
    
    /**
     * 消息ID，如果提供则优先使用（不需要传文本内容）
     */
    private String messageId;
    
    private static final long serialVersionUID = 1L;
} 
package com.ubanillx.smartclass.model.dto.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * 停止流式响应请求DTO
 */
@Data
public class StopStreamingRequest implements Serializable {
    
    /**
     * AI分身ID
     */
    private Long aiAvatarId;
    
    /**
     * 任务ID，从流式响应中获取
     */
    private String taskId;
    
    private static final long serialVersionUID = 1L;
} 
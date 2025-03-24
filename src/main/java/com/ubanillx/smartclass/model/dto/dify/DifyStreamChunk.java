package com.ubanillx.smartclass.model.dto.dify;

import lombok.Data;

import java.io.Serializable;

/**
 * Dify流式响应块DTO
 */
@Data
public class DifyStreamChunk implements Serializable {
    
    /**
     * 事件类型，如message
     */
    private String event;
    
    /**
     * 任务ID
     */
    private String task_id;
    
    /**
     * 消息ID
     */
    private String id;
    
    /**
     * 消息内容
     */
    private String answer;
    
    /**
     * 会话ID
     */
    private String conversation_id;
    
    /**
     * 创建时间
     */
    private Long created_at;
    
    private static final long serialVersionUID = 1L;
} 
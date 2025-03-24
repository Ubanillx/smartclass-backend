package com.ubanillx.smartclass.model.dto.dify;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Dify聊天请求DTO
 */
@Data
public class DifyChatRequest implements Serializable {
    
    /**
     * 用户输入/提问内容
     */
    private String query;
    
    /**
     * 应用变量值
     */
    private Map<String, Object> inputs;
    
    /**
     * 响应模式：streaming(流式)或blocking(阻塞)
     */
    private String response_mode;
    
    /**
     * 用户标识
     */
    private String user;
    
    /**
     * 会话ID
     */
    private String conversation_id;
    
    /**
     * 自动生成标题
     */
    private Boolean auto_generate_name;
    
    private static final long serialVersionUID = 1L;
} 
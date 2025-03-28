package com.ubanillx.smartclass.model.dto.dify;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
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
    
    /**
     * 文件列表，支持图片等多模态输入
     * 格式：[{"type":"image","transfer_method":"local_file","upload_file_id":"FILE_ID"}]
     */
    private List<Map<String, Object>> files;
    
    private static final long serialVersionUID = 1L;
} 
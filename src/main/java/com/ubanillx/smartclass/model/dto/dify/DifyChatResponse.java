package com.ubanillx.smartclass.model.dto.dify;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Dify聊天响应DTO
 */
@Data
public class DifyChatResponse implements Serializable {
    
    /**
     * 消息唯一ID
     */
    private String message_id;
    
    /**
     * 会话ID
     */
    private String conversation_id;
    
    /**
     * App模式
     */
    private String mode;
    
    /**
     * 完整回复内容
     */
    private String answer;
    
    /**
     * 元数据
     */
    private DifyChatMetadata metadata;
    
    /**
     * 消息创建时间戳
     */
    private Long created_at;
    
    /**
     * 错误代码
     */
    private String code;
    
    /**
     * 错误信息
     */
    private String message;
    
    /**
     * 错误参数
     */
    private String params;
    
    /**
     * 元数据信息
     */
    @Data
    public static class DifyChatMetadata implements Serializable {
        /**
         * 模型用量信息
         */
        private DifyUsage usage;
        
        private static final long serialVersionUID = 1L;
    }
    
    /**
     * 模型用量信息
     */
    @Data
    public static class DifyUsage implements Serializable {
        /**
         * 提示tokens数
         */
        private Integer prompt_tokens;
        
        /**
         * 返回tokens数
         */
        private Integer completion_tokens;
        
        /**
         * 总tokens数
         */
        private Integer total_tokens;
        
        private static final long serialVersionUID = 1L;
    }
    
    private static final long serialVersionUID = 1L;
} 
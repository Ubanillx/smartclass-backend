package com.ubanillx.smartclass.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Dify配置
 */
@Configuration
@ConfigurationProperties(prefix = "dify")
@Data
public class DifyConfig {
    
    /**
     * Dify API基础URL
     */
    private String baseUrl = "http://10.0.124.181/v1";
    
    /**
     * 聊天消息路径
     */
    private String chatMessagesPath = "/chat-messages";
    
    /**
     * 默认User标识前缀
     */
    private String userPrefix = "smartclass_";
    
    /**
     * 是否启用详细的流式日志记录
     */
    private boolean enableStreamingVerboseLog = false;
} 
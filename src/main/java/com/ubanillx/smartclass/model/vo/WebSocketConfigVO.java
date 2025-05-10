package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * WebSocket配置信息视图对象
 */
@Data
public class WebSocketConfigVO implements Serializable {
    
    /**
     * WebSocket服务器URL
     */
    private String wsUrl;
    
    /**
     * WebSocket服务器端口
     */
    private Integer port;
    
    /**
     * 心跳超时时间（秒）
     */
    private Integer heartbeatTimeout;
    
    /**
     * 连接鉴权超时时间（秒）
     */
    private Integer authTimeout;
    
    /**
     * 当前用户ID，用于WebSocket认证
     */
    private Long userId;
    
    private static final long serialVersionUID = 1L;
} 
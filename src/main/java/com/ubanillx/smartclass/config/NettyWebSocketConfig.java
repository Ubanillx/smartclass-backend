package com.ubanillx.smartclass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Netty WebSocket服务器配置
 */
@Configuration
@ConfigurationProperties(prefix = "netty.websocket")
public class NettyWebSocketConfig {
    // WebSocket服务器端口
    private int port = 12346;
    // 心跳超时时间（秒）
    private int heartbeatTimeout = 60;
    // 连接鉴权超时时间（秒）
    private int authTimeout = 10;
    // 单机最大连接数
    private int maxConnections = 10000;
    
    // getter和setter方法
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }
    
    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
    
    public int getAuthTimeout() {
        return authTimeout;
    }
    
    public void setAuthTimeout(int authTimeout) {
        this.authTimeout = authTimeout;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
} 
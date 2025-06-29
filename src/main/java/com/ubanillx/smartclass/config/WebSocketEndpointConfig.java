package com.ubanillx.smartclass.config;

import com.ubanillx.smartclass.constant.WebSocketMessageType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket接口注册配置
 */
@Configuration
public class WebSocketEndpointConfig {

    /**
     * 注册WebSocket消息类型和接口定义
     */
    @Bean
    public Map<String, WebSocketEndpointDefinition> webSocketEndpoints() {
        Map<String, WebSocketEndpointDefinition> endpoints = new HashMap<>();
        
        // 注册聊天消息接口
        endpoints.put(WebSocketMessageType.CHAT, new WebSocketEndpointDefinition(
            "发送聊天消息",
            "向指定用户或会话发送聊天消息",
            "{\"type\":\"chat\",\"content\":\"消息内容\",\"sessionId\":\"会话ID\",\"receiverId\":123}"
        ));
        
        // 注册心跳消息接口
        endpoints.put(WebSocketMessageType.HEARTBEAT, new WebSocketEndpointDefinition(
            "发送心跳消息",
            "向服务器发送心跳消息，保持连接活跃",
            "{\"type\":\"heartbeat\"}"
        ));
        
        // 注册系统消息接口
        endpoints.put(WebSocketMessageType.SYSTEM, new WebSocketEndpointDefinition(
            "系统通知",
            "系统向用户发送的通知消息",
            "{\"type\":\"system\",\"content\":\"系统通知内容\"}"
        ));
        
        // 注册已读状态消息接口
        endpoints.put(WebSocketMessageType.READ_STATUS, new WebSocketEndpointDefinition(
            "更新消息已读状态",
            "更新消息的已读状态",
            "{\"type\":\"read_status\",\"messageId\":\"msg123456\"}"
        ));
        
        // 注册命令消息接口
        endpoints.put(WebSocketMessageType.COMMAND, new WebSocketEndpointDefinition(
            "发送命令消息",
            "向服务器发送命令消息",
            "{\"type\":\"command\",\"content\":\"online_users\"}"
        ));
        
        return endpoints;
    }
    
    /**
     * WebSocket接口定义类
     */
    public static class WebSocketEndpointDefinition {
        private final String name;
        private final String description;
        private final String exampleRequest;
        
        public WebSocketEndpointDefinition(String name, String description, String exampleRequest) {
            this.name = name;
            this.description = description;
            this.exampleRequest = exampleRequest;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getExampleRequest() {
            return exampleRequest;
        }
    }
} 
package com.ubanillx.smartclass.event;

/**
 * WebSocket消息事件
 * 用于发送WebSocket消息，替代直接调用ChannelManager
 */
public class WebSocketMessageEvent extends WebSocketEvent {
    
    private final Long userId;
    private final String message;
    private final boolean isBroadcast;
    
    /**
     * 构造函数 - 发送给单个用户
     * 
     * @param userId 接收消息的用户ID
     * @param message 要发送的消息内容
     */
    public WebSocketMessageEvent(Long userId, String message) {
        super();
        this.userId = userId;
        this.message = message;
        this.isBroadcast = false;
    }
    
    /**
     * 构造函数 - 广播消息
     * 
     * @param message 要广播的消息
     * @param isBroadcast 是否广播
     */
    public WebSocketMessageEvent(String message, boolean isBroadcast) {
        super();
        this.userId = null;
        this.message = message;
        this.isBroadcast = true;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isBroadcast() {
        return isBroadcast;
    }
} 
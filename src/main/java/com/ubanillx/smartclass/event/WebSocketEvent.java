package com.ubanillx.smartclass.event;

/**
 * WebSocket事件基类
 * 所有WebSocket相关事件都应继承此类
 */
public abstract class WebSocketEvent {
    
    private final long timestamp;
    
    public WebSocketEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
} 
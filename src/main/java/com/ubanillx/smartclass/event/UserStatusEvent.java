package com.ubanillx.smartclass.event;

/**
 * 用户状态事件
 * 用于通知用户上线或下线
 */
public class UserStatusEvent extends WebSocketEvent {
    
    public enum Type {
        ONLINE,
        OFFLINE
    }
    
    private final Long userId;
    private final Type type;
    
    /**
     * 构造函数
     * 
     * @param userId 用户ID
     * @param type 状态类型：上线/下线
     */
    public UserStatusEvent(Long userId, Type type) {
        super();
        this.userId = userId;
        this.type = type;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Type getType() {
        return type;
    }
    
    public boolean isOnline() {
        return type == Type.ONLINE;
    }
} 
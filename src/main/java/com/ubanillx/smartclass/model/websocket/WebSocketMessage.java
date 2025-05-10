package com.ubanillx.smartclass.model.websocket;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * WebSocket消息模型
 */
@Data
public class WebSocketMessage implements Serializable {
    /**
     * 消息类型：
     * 0-认证消息
     * 1-私聊消息
     * 2-好友请求
     * 3-系统通知
     * 99-错误消息
     */
    private Integer type;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 附加数据，可以是任何类型的JSON对象
     */
    private Object data;
    
    /**
     * 发送时间
     */
    private Date sendTime;
    
    private static final long serialVersionUID = 1L;
} 
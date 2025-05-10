package com.ubanillx.smartclass.model.dto.privateMessage;

import lombok.Data;

import java.io.Serializable;

/**
 * 私聊消息添加请求
 */
@Data
public class PrivateMessageAddRequest implements Serializable {
    
    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
} 
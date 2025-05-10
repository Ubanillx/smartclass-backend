package com.ubanillx.smartclass.model.dto.privatechatsession;

import lombok.Data;

import java.io.Serializable;

/**
 * 私聊会话添加请求
 */
@Data
public class PrivateChatSessionAddRequest implements Serializable {
    
    /**
     * 用户1 ID
     */
    private Long userId1;

    /**
     * 用户2 ID
     */
    private Long userId2;

    private static final long serialVersionUID = 1L;
} 
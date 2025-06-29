package com.ubanillx.smartclass.model.dto.friendrequest;

import lombok.Data;

import java.io.Serializable;

/**
 * 好友申请添加请求
 */
@Data
public class FriendRequestAddRequest implements Serializable {
    
    /**
     * 发送者 ID，关联到user表
     */
    private Long senderId;

    /**
     * 接收者 ID，关联到user表
     */
    private Long receiverId;

    /**
     * 申请状态：pending/accepted/rejected
     */
    private String status;

    /**
     * 申请消息
     */
    private String message;

    private static final long serialVersionUID = 1L;
} 
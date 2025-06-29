package com.ubanillx.smartclass.model.dto.friendrequest;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 好友申请查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FriendRequestQueryRequest extends PageRequest implements Serializable {
    
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
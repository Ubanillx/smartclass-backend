package com.ubanillx.smartclass.model.dto.friendrelationship;

import lombok.Data;

import java.io.Serializable;

/**
 * 好友关系添加请求
 */
@Data
public class FriendRelationshipAddRequest implements Serializable {
    
    /**
     * 用户2 ID，关联到user表
     */
    private Long userId2;

    /**
     * 关系状态：pending/accepted/blocked
     */
    private String status;

    private static final long serialVersionUID = 1L;
} 
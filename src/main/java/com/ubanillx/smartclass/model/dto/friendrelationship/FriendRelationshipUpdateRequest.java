package com.ubanillx.smartclass.model.dto.friendrelationship;

import lombok.Data;

import java.io.Serializable;

/**
 * 好友关系更新请求
 */
@Data
public class FriendRelationshipUpdateRequest implements Serializable {
    
    /**
     * 主键
     */
    private Long id;
    
    /**
     * 关系状态：pending/accepted/blocked
     */
    private String status;

    private static final long serialVersionUID = 1L;
} 
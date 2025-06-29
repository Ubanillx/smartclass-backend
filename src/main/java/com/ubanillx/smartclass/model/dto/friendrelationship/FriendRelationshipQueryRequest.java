package com.ubanillx.smartclass.model.dto.friendrelationship;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 好友关系查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FriendRelationshipQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 用户1 ID，关联到user表
     */
    private Long userId1;

    /**
     * 用户2 ID，关联到user表
     */
    private Long userId2;

    /**
     * 关系状态：pending/accepted/blocked
     */
    private String status;

    /**
     * 用户ID(查询该用户的所有好友关系)
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
} 
package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友关系视图对象
 */
@Data
public class FriendRelationshipVO implements Serializable {
    
    /**
     * 主键
     */
    private Long id;

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
     * 好友用户信息
     */
    private UserVO friendUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
} 
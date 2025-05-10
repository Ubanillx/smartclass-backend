package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友申请视图对象
 */
@Data
public class FriendRequestVO implements Serializable {
    
    /**
     * 主键
     */
    private Long id;

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
     * 状态描述，更友好的展示
     */
    private String statusDescription;

    /**
     * 申请消息
     */
    private String message;

    /**
     * 发送者用户信息
     */
    private UserVO senderUser;

    /**
     * 接收者用户信息
     */
    private UserVO receiverUser;

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
package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 私聊会话视图对象
 */
@Data
public class PrivateChatSessionVO implements Serializable {
    
    /**
     * 会话ID
     */
    private Long id;

    /**
     * 用户1 ID
     */
    private Long userId1;

    /**
     * 用户2 ID
     */
    private Long userId2;
    
    /**
     * 用户1信息
     */
    private UserVO user1;
    
    /**
     * 用户2信息
     */
    private UserVO user2;
    
    /**
     * 对方用户信息（针对当前登录用户）
     */
    private UserVO targetUser;
    
    /**
     * 最后一条消息
     */
    private PrivateMessageVO lastMessage;

    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;
    
    /**
     * 未读消息数量
     */
    private Integer unreadCount;

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
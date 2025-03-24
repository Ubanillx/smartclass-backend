package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息视图对象
 */
@Data
public class ChatMessageVO implements Serializable {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * AI分身ID
     */
    private Long aiAvatarId;
    
    /**
     * AI分身名称
     */
    private String aiAvatarName;
    
    /**
     * AI分身头像
     */
    private String aiAvatarImgUrl;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息类型：user/ai
     */
    private String messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息token数
     */
    private Integer tokens;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    private static final long serialVersionUID = 1L;
} 
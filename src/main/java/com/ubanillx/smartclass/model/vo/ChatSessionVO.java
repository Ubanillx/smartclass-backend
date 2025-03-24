package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天会话视图对象
 */
@Data
public class ChatSessionVO implements Serializable {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 会话名称
     */
    private String sessionName;
    
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
     * 最后一条消息内容
     */
    private String lastMessage;
    
    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;
    
    /**
     * 会话消息数量
     */
    private Integer messageCount;
    
    private static final long serialVersionUID = 1L;
} 
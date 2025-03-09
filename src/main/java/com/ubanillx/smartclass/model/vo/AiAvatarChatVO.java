package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI分身聊天视图对象
 */
@Data
public class AiAvatarChatVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * AI分身id
     */
    private Long aiAvatarId;

    /**
     * AI分身名称
     */
    private String aiAvatarName;

    /**
     * AI分身头像URL
     */
    private String aiAvatarUrl;

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
package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天历史列表视图对象
 */
@Data
public class ChatHistoryListVO implements Serializable {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
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
     * 对话摘要 (最近的一条消息内容或会话标题)
     */
    private String summary;
    
    /**
     * 消息内容 (最后一条对话内容)
     */
    private String lastMessage;
    
    /**
     * 对话最后更新时间
     */
    private Date lastUpdateTime;
    
    /**
     * 对话消息总数
     */
    private Integer messageCount;
    
    private static final long serialVersionUID = 1L;
} 
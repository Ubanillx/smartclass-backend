package com.ubanillx.smartclass.model.dto.privatechatsession;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 私聊会话查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PrivateChatSessionQueryRequest extends PageRequest implements Serializable {
    
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
     * 用户ID，同时匹配userId1和userId2
     */
    private Long userId;

    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;

    private static final long serialVersionUID = 1L;
} 
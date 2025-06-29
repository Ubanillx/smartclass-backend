package com.ubanillx.smartclass.model.dto.privateMessage;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 私聊消息查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PrivateMessageQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读
     */
    private Integer isRead;
    
    /**
     * 起始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;

    private static final long serialVersionUID = 1L;
} 
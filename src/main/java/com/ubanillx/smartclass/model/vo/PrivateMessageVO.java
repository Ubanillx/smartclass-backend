package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 私聊消息视图对象
 */
@Data
public class PrivateMessageVO implements Serializable {
    
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
     * 消息内容
     */
    private String content;

    /**
     * 是否已读
     */
    private Integer isRead;
    
    /**
     * 发送者信息
     */
    private UserVO senderUser;

    /**
     * 接收者信息
     */
    private UserVO receiverUser;

    /**
     * 发送时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
} 
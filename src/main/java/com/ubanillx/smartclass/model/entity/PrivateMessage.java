package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 私聊消息
 * @TableName private_message
 */
@TableName(value ="private_message")
@Data
public class PrivateMessage implements Serializable {
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID，关联到user表
     */
    private Long senderId;

    /**
     * 接收者ID，关联到user表
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读：0-否，1-是
     */
    private Integer isRead;

    /**
     * 发送时间
     */
    private Date createTime;

    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
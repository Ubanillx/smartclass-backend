package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友申请表
 * @TableName friend_request
 */
@TableName(value ="friend_request")
@Data
public class FriendRequest implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 申请消息
     */
    private String message;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
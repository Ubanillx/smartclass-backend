package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友关系表
 * @TableName friend_relationship
 */
@TableName(value ="friend_relationship")
@Data
public class FriendRelationship implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户1 ID，关联到user表
     */
    private Long userId1;

    /**
     * 用户2 ID，关联到user表
     */
    private Long userId2;

    /**
     * 关系状态：pending/accepted/blocked
     */
    private String status;

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
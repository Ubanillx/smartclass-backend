package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子评论回复
 * @TableName post_comment_reply
 */
@TableName(value ="post_comment_reply")
@Data
public class PostCommentReply implements Serializable {
    /**
     * 回复ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID，关联到post表
     */
    private Long postId;

    /**
     * 评论ID，关联到post_comment表
     */
    private Long commentId;

    /**
     * 回复者ID，关联到user表
     */
    private Long userId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 国家
     */
    private String country;

    /**
     * 城市
     */
    private String city;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
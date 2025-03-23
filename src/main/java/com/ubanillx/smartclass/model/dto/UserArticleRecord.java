package com.ubanillx.smartclass.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户文章阅读记录
 * @TableName user_article_record
 */
@TableName(value ="user_article_record")
@Data
public class UserArticleRecord implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 阅读状态：0-未读，1-阅读中，2-已读完
     */
    private Integer readStatus;

    /**
     * 阅读进度(百分比)
     */
    private Integer readProgress;

    /**
     * 是否点赞
     */
    private Integer isLiked;

    /**
     * 用户笔记
     */
    private String userNotes;

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
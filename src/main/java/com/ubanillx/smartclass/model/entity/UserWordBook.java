package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户生词本
 * @TableName user_word_book
 */
@TableName(value ="user_word_book")
@Data
public class UserWordBook implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id，关联到user表
     */
    private Long userId;

    /**
     * 单词id，关联到daily_word表
     */
    private Long wordId;

    /**
     * 学习状态：0-未学习，1-已学习，2-已掌握
     */
    private Integer learningStatus;

    /**
     * 是否收藏：0-否，1-是
     */
    private Integer isCollected;

    /**
     * 收藏时间
     */
    private Date collectedTime;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;

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
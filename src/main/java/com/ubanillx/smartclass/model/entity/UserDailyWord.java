package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户与每日单词关联
 * @TableName user_daily_word
 */
@TableName(value ="user_daily_word")
@Data
public class UserDailyWord implements Serializable {
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
     * 单词id
     */
    private Long wordId;

    /**
     * 是否学习：0-否，1-是
     */
    private Integer isStudied;

    /**
     * 是否点赞：0-否，1-是
     */
    private Integer isLiked;

    /**
     * 点赞时间
     */
    private Date likeTime;

    /**
     * 学习时间
     */
    private Date studyTime;

    /**
     * 是否收藏：0-否，1-是
     */
    private Integer isCollected;

    /**
     * 收藏时间
     */
    private Date collectTime;

    /**
     * 笔记内容
     */
    private String noteContent;

    /**
     * 笔记时间
     */
    private Date noteTime;

    /**
     * 掌握程度：0-未知，1-生词，2-熟悉，3-掌握
     */
    private Integer masteryLevel;

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
package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 用户AI分身关联
 * @TableName user_ai_avatar
 */
@TableName(value ="user_ai_avatar")
@Data
public class UserAiAvatar implements Serializable {
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
     * AI分身id
     */
    private Long aiAvatarId;

    /**
     * 是否收藏：0-否，1-是
     */
    private Integer isFavorite;

    /**
     * 最后使用时间
     */
    private Date lastUseTime;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 用户评分，1-5分
     */
    private BigDecimal userRating;

    /**
     * 用户反馈
     */
    private String userFeedback;

    /**
     * 用户自定义设置，JSON格式
     */
    private String customSettings;

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
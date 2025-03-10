package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI分身视图对象
 */
@Data
public class AiAvatarVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * AI分身名称
     */
    private String name;

    /**
     * AI分身描述
     */
    private String description;

    /**
     * AI分身头像URL
     */
    private String avatarUrl;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 分类，如：学习助手、语言教练、职业顾问等
     */
    private String category;

    /**
     * 性格特点描述
     */
    private String personality;

    /**
     * 能力描述
     */
    private String abilities;

    /**
     * 提示词模板
     */
    private String promptTemplate;

    /**
     * 模型类型，如：GPT-4、Claude等
     */
    private String modelType;

    /**
     * 是否公开：0-否，1-是
     */
    private Integer isPublic;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 评分，1-5分
     */
    private BigDecimal rating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 创建者id
     */
    private Long creatorId;

    /**
     * 创建者类型：admin/user
     */
    private String creatorType;

    /**
     * 排序，数字越小排序越靠前
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户是否收藏
     */
    private Boolean isFavorite;

    /**
     * 用户使用次数
     */
    private Integer userUseCount;

    /**
     * 用户评分
     */
    private BigDecimal userRating;

    /**
     * 用户自定义设置
     */
    private String customSettings;

    private static final long serialVersionUID = 1L;
} 
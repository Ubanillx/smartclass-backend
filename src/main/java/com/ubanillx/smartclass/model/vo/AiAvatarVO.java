package com.ubanillx.smartclass.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

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
     * 请求地址
     */
    private String baseUrl;

    /**
     * AI分身描述
     */
    private String description;

    /**
     * AI分身头像URL
     */
    private String avatarImgUrl;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 性格特点描述
     */
    private String personality;

    /**
     * 能力描述
     */
    private String abilities;

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

    private static final long serialVersionUID = 1L;
} 
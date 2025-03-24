package com.ubanillx.smartclass.model.dto.useraiavatar;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户AI分身关联添加请求
 */
@Data
public class UserAiAvatarAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
} 
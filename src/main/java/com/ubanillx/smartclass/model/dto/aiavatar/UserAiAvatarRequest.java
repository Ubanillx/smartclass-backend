package com.ubanillx.smartclass.model.dto.aiavatar;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户AI分身交互请求
 */
@Data
public class UserAiAvatarRequest implements Serializable {

    /**
     * AI分身id
     */
    private Long aiAvatarId;

    /**
     * 用户评分，1-5分
     */
    private BigDecimal rating;

    /**
     * 用户反馈
     */
    private String feedback;

    /**
     * 用户自定义设置，JSON格式
     */
    private String customSettings;

    private static final long serialVersionUID = 1L;
} 
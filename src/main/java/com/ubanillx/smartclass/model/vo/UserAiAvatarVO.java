package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户AI分身关联视图对象
 */
@Data
public class UserAiAvatarVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * AI分身id
     */
    private Long aiAvatarId;
    
    /**
     * AI分身名称
     */
    private String aiAvatarName;
    
    /**
     * AI分身头像URL
     */
    private String aiAvatarImgUrl;
    
    /**
     * AI分身描述
     */
    private String aiAvatarDescription;

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

    private static final long serialVersionUID = 1L;
} 
package com.ubanillx.smartclass.model.dto.aiavatar;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * AI分身更新请求
 */
@Data
public class AiAvatarUpdateRequest implements Serializable {
    
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
     * AI分身鉴权，一串随机字符
     */
    private String avatarAuth;

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
     * 排序，数字越小排序越靠前
     */
    private Integer sort;

    private static final long serialVersionUID = 1L;
}
package com.ubanillx.smartclass.model.dto.aiavatar;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加AI分身请求
 */
@Data
public class AiAvatarAddRequest implements Serializable {

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
     * 模型配置，JSON格式
     */
    private String modelConfig;

    /**
     * 是否公开：0-否，1-是
     */
    private Integer isPublic;

    /**
     * 排序，数字越小排序越靠前
     */
    private Integer sort;

    private static final long serialVersionUID = 1L;
} 
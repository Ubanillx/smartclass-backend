package com.ubanillx.smartclass.model.dto.aiavatar;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询AI分身请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AiAvatarQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * AI分身名称
     */
    private String name;

    /**
     * 分类，如：学习助手、语言教练、职业顾问等
     */
    private String category;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

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
     * 创建者id
     */
    private Long creatorId;

    /**
     * 创建者类型：admin/user
     */
    private String creatorType;

    /**
     * 搜索关键词（同时搜索名称、描述、标签）
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
} 
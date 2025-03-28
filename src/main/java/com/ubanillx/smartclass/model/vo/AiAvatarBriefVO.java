package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI分身简要信息视图对象
 */
@Data
public class AiAvatarBriefVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * AI分身名称
     */
    private String name;

    /**
     * AI分身头像URL
     */
    private String avatarImgUrl;

    /**
     * AI分身描述
     */
    private String description;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 状态：0-禁用(离线)，1-启用(在线)
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
} 
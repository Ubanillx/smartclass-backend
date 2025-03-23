package com.ubanillx.smartclass.model.dto.userlevel;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户等级请求
 */
@Data
public class UserLevelUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    
    /**
     * 等级数值
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 等级图标URL
     */
    private String iconUrl;

    /**
     * 最小经验值
     */
    private Integer minExperience;

    /**
     * 最大经验值
     */
    private Integer maxExperience;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 等级特权，JSON格式
     */
    private String privileges;

    private static final long serialVersionUID = 1L;
} 
package com.ubanillx.smartclass.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户等级视图
 */
@Data
public class UserLevelVO implements Serializable {

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
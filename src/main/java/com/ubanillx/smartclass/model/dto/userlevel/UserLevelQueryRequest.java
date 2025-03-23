package com.ubanillx.smartclass.model.dto.userlevel;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询用户等级请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLevelQueryRequest extends PageRequest implements Serializable {
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
     * 最小经验值范围（起始值）
     */
    private Integer minExperienceStart;
    
    /**
     * 最小经验值范围（结束值）
     */
    private Integer minExperienceEnd;
    
    /**
     * 最大经验值范围（起始值）
     */
    private Integer maxExperienceStart;
    
    /**
     * 最大经验值范围（结束值）
     */
    private Integer maxExperienceEnd;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder;

    private static final long serialVersionUID = 1L;
} 
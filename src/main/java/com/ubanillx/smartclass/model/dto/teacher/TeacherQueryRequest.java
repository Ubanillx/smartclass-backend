package com.ubanillx.smartclass.model.dto.teacher;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询讲师请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    
    /**
     * 讲师姓名
     */
    private String name;

    /**
     * 讲师职称
     */
    private String title;

    /**
     * 专业领域
     */
    private String expertise;

    /**
     * 关联的用户id
     */
    private Long userId;

    /**
     * 创建管理员id
     */
    private Long adminId;

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
package com.ubanillx.smartclass.model.dto.teacher;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加讲师请求
 */
@Data
public class TeacherAddRequest implements Serializable {

    /**
     * 讲师姓名
     */
    private String name;

    /**
     * 讲师头像URL
     */
    private String avatar;

    /**
     * 讲师职称
     */
    private String title;

    /**
     * 讲师简介
     */
    private String introduction;

    /**
     * 专业领域，JSON数组格式
     */
    private String expertise;

    /**
     * 关联的用户id，如果讲师也是平台用户
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
} 
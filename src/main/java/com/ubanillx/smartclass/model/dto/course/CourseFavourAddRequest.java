package com.ubanillx.smartclass.model.dto.course;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程收藏请求
 */
@Data
public class CourseFavourAddRequest implements Serializable {

    /**
     * 课程id
     */
    private Long courseId;

    private static final long serialVersionUID = 1L;
} 
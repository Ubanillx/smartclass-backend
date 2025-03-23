package com.ubanillx.smartclass.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频上传请求
 */
@Data
public class UploadVideoRequest implements Serializable {

    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 课时ID
     */
    private Long sectionId;
    
    /**
     * 视频标题
     */
    private String title;
    
    /**
     * 视频描述
     */
    private String description;
    
    /**
     * 排序序号
     */
    private Integer sort;

    private static final long serialVersionUID = 1L;
} 
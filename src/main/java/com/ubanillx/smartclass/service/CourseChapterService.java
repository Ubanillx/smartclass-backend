package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.CourseChapter;

import java.util.List;

/**
* @author liulo
* @description 针对表【course_chapter(课程章节)】的数据库操作Service
* @createDate 2025-03-21 15:14:50
*/
public interface CourseChapterService extends IService<CourseChapter> {

    /**
     * 根据课程ID获取章节列表
     *
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<CourseChapter> getChaptersByCourseId(Long courseId);
    
    /**
     * 添加课程章节
     *
     * @param courseChapter 课程章节
     * @param adminId 管理员ID
     * @return 章节ID
     */
    long addCourseChapter(CourseChapter courseChapter, Long adminId);
    
    /**
     * 校验课程章节
     *
     * @param courseChapter 课程章节
     * @param add 是否为添加操作
     */
    void validCourseChapter(CourseChapter courseChapter, boolean add);
    
    /**
     * 获取查询条件
     *
     * @param courseId 课程ID
     * @return 查询条件
     */
    QueryWrapper<CourseChapter> getQueryWrapper(Long courseId);
}

package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.CourseSection;

import java.util.List;

/**
* @author liulo
* @description 针对表【course_section(课程小节)】的数据库操作Service
* @createDate 2025-03-21 15:14:50
*/
public interface CourseSectionService extends IService<CourseSection> {

    /**
     * 根据课程ID获取小节列表
     *
     * @param courseId 课程ID
     * @return 小节列表
     */
    List<CourseSection> getSectionsByCourseId(Long courseId);
    
    /**
     * 根据章节ID获取小节列表
     *
     * @param chapterId 章节ID
     * @return 小节列表
     */
    List<CourseSection> getSectionsByChapterId(Long chapterId);
    
    /**
     * 添加课程小节
     *
     * @param courseSection 课程小节
     * @param adminId 管理员ID
     * @return 小节ID
     */
    long addCourseSection(CourseSection courseSection, Long adminId);
    
    /**
     * 校验课程小节
     *
     * @param courseSection 课程小节
     * @param add 是否为添加操作
     */
    void validCourseSection(CourseSection courseSection, boolean add);
    
    /**
     * 获取查询条件
     *
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 查询条件
     */
    QueryWrapper<CourseSection> getQueryWrapper(Long courseId, Long chapterId);
    
    /**
     * 获取课程总时长（所有小节时长之和）
     *
     * @param courseId 课程ID
     * @return 总时长（秒）
     */
    int getTotalDuration(Long courseId);
    
    /**
     * 获取课程小节数量
     *
     * @param courseId 课程ID
     * @return 小节数量
     */
    int countSections(Long courseId);
}

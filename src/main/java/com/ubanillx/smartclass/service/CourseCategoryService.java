package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.CourseCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liulo
* @description 针对表【course_category(课程分类)】的数据库操作Service
* @createDate 2025-03-21 15:14:50
*/
public interface CourseCategoryService extends IService<CourseCategory> {

    /**
     * 添加课程分类
     *
     * @param courseCategory 课程分类
     * @param adminId 管理员ID
     * @return 分类ID
     */
    long addCourseCategory(CourseCategory courseCategory, Long adminId);

    /**
     * 获取一级分类列表
     *
     * @return 一级分类列表
     */
    List<CourseCategory> getTopCategories();

    /**
     * 获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<CourseCategory> getSubCategories(Long parentId);

    /**
     * 获取分类及其子分类
     *
     * @param categoryId 分类ID
     * @return 分类及其子分类列表
     */
    List<CourseCategory> getCategoryWithChildren(Long categoryId);
}

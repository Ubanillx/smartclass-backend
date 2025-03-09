package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.Course;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 课程服务接口
 * 针对表【course(课程)】的数据库操作Service
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 创建课程
     * @param course 课程信息
     * @return 课程ID
     */
    Long createCourse(Course course);
    
    /**
     * 更新课程
     * @param course 课程信息
     * @return 是否更新成功
     */
    boolean updateCourse(Course course);
    
    /**
     * 更新课程状态
     * @param courseId 课程ID
     * @param status 状态：0-未发布，1-已发布，2-已下架
     * @return 是否更新成功
     */
    boolean updateCourseStatus(Long courseId, Integer status);
    
    
    /**
     * 分页查询课程
     * @param condition 查询条件（可包含categoryId/teacherId/courseType/difficulty/keyword等）
     * @param page 分页参数
     * @return 课程分页列表
     */
    IPage<Course> pageCourses(Course condition, Page<Course> page);
    
    
    /**
     * 逻辑删除课程
     * @param courseId 课程ID
     * @return 是否删除成功
     */
    boolean deleteCourse(Long courseId);
}

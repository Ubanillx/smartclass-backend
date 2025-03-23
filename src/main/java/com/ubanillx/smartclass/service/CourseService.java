package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.course.CourseQueryRequest;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.CourseVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程服务
 */
public interface CourseService extends IService<Course> {

    /**
     * 创建课程
     *
     * @param course
     * @param adminId
     * @return
     */
    long addCourse(Course course, Long adminId);

    /**
     * 获取课程视图
     *
     * @param course
     * @param currentUser
     * @return
     */
    CourseVO getCourseVO(Course course, User currentUser);

    /**
     * 获取课程视图列表
     *
     * @param courseList
     * @param currentUser
     * @return
     */
    List<CourseVO> getCourseVO(List<Course> courseList, User currentUser);

    /**
     * 获取查询条件
     *
     * @param courseQueryRequest
     * @return
     */
    QueryWrapper<Course> getQueryWrapper(CourseQueryRequest courseQueryRequest);

    /**
     * 更新课程评分
     *
     * @param id 课程ID
     * @param score 评分值
     * @param count 评论数量
     * @return 是否成功
     */
    boolean updateCourseRating(Long id, BigDecimal score, Integer count);

    /**
     * 增加学习人数
     *
     * @param id
     * @return
     */
    boolean increaseStudentCount(Long id);

    /**
     * 根据讲师ID获取课程列表
     *
     * @param teacherId
     * @return
     */
    List<CourseVO> getCoursesByTeacher(Long teacherId, User currentUser);

    /**
     * 获取推荐课程
     *
     * @param categoryId
     * @param difficulty
     * @param limit
     * @param currentUser
     * @return
     */
    List<CourseVO> getRecommendCourses(Long categoryId, Integer difficulty, int limit, User currentUser);

    /**
     * 从 Course 对象构建 CourseVO 对象
     *
     * @param course
     * @return
     */
    CourseVO getCourseVO(Course course);

    /**
     * 从 Course 列表构建 CourseVO 列表
     *
     * @param courseList
     * @return
     */
    List<CourseVO> getCourseVO(List<Course> courseList);

    /**
     * 校验课程
     *
     * @param course
     * @param add
     */
    void validCourse(Course course, boolean add);
}

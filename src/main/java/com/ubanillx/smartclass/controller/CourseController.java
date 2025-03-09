package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 创建课程
     * @param course 课程信息
     * @return 课程ID
     */
    @PostMapping("/create")
    public Long createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    /**
     * 更新课程
     * @param course 课程信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public boolean updateCourse(@RequestBody Course course) {
        return courseService.updateCourse(course);
    }

    /**
     * 更新课程状态
     * @param courseId 课程ID
     * @param status 状态：0-未发布，1-已发布，2-已下架
     * @return 是否更新成功
     */
    @PostMapping("/updateStatus")
    public boolean updateCourseStatus(@RequestParam Long courseId, @RequestParam Integer status) {
        return courseService.updateCourseStatus(courseId, status);
    }

    /**
     * 根据ID获取课程
     * @param courseId 课程ID
     * @return 课程信息
     */
    @GetMapping("/get")
    public Course getCourseById(@RequestParam Long courseId) {
        return courseService.getById(courseId);
    }

    /**
     * 分页查询课程
     * @param current 当前页码
     * @param size 每页大小
     * @param condition 查询条件
     * @return 课程分页结果
     */
    @GetMapping("/page")
    public IPage<Course> pageCourses(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            Course condition) {
        Page<Course> page = new Page<>(current, size);
        return courseService.pageCourses(condition, page);
    }

    /**
     * 逻辑删除课程
     * @param courseId 课程ID
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public boolean deleteCourse(@RequestParam Long courseId) {
        return courseService.deleteCourse(courseId);
    }
}
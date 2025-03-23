package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.course.CourseAddRequest;
import com.ubanillx.smartclass.model.dto.course.CourseQueryRequest;
import com.ubanillx.smartclass.model.dto.course.CourseUpdateRequest;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.CourseVO;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 课程接口
 */
@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {

    @Resource
    private CourseService courseService;

    @Resource
    private UserService userService;

    /**
     * 创建课程
     *
     * @param courseAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCourse(@RequestBody CourseAddRequest courseAddRequest, HttpServletRequest request) {
        if (courseAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = new Course();
        BeanUtils.copyProperties(courseAddRequest, course);
        User loginUser = userService.getLoginUser(request);
        course.setAdminId(loginUser.getId());
        boolean result = courseService.save(course);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(course.getId());
    }

    /**
     * 删除课程
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCourse(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Course oldCourse = courseService.getById(id);
        if (oldCourse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldCourse.getAdminId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = courseService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新课程
     *
     * @param courseUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCourse(@RequestBody CourseUpdateRequest courseUpdateRequest,
                                              HttpServletRequest request) {
        if (courseUpdateRequest == null || courseUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = new Course();
        BeanUtils.copyProperties(courseUpdateRequest, course);
        User loginUser = userService.getLoginUser(request);
        long id = courseUpdateRequest.getId();
        // 判断是否存在
        Course oldCourse = courseService.getById(id);
        if (oldCourse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldCourse.getAdminId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = courseService.updateById(course);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Course> getCourseById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = courseService.getById(id);
        return ResultUtils.success(course);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param courseQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<Course>> listCourse(CourseQueryRequest courseQueryRequest, HttpServletRequest request) {
        Course courseQuery = new Course();
        if (courseQueryRequest != null) {
            BeanUtils.copyProperties(courseQueryRequest, courseQuery);
        }
        List<Course> courseList = courseService.list(courseService.getQueryWrapper(courseQueryRequest));
        return ResultUtils.success(courseList);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param courseQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page/vo")
    public BaseResponse<Page<CourseVO>> listCourseVOByPage(CourseQueryRequest courseQueryRequest,
                                                           HttpServletRequest request) {
        long current = courseQueryRequest.getCurrent();
        long size = courseQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Course> coursePage = courseService.page(new Page<>(current, size),
                courseService.getQueryWrapper(courseQueryRequest));
        Page<CourseVO> courseVOPage = new Page<>(current, size, coursePage.getTotal());
        List<CourseVO> courseVO = courseService.getCourseVO(coursePage.getRecords());
        courseVOPage.setRecords(courseVO);
        return ResultUtils.success(courseVOPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param courseQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list/page/vo")
    public BaseResponse<Page<CourseVO>> listMyCourseVOByPage(CourseQueryRequest courseQueryRequest,
                                                             HttpServletRequest request) {
        if (courseQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        courseQueryRequest.setUserId(null);
        long current = courseQueryRequest.getCurrent();
        long size = courseQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 构建查询条件
        QueryWrapper<Course> queryWrapper = courseService.getQueryWrapper(courseQueryRequest);
        queryWrapper.eq("admin_id", loginUser.getId());
        
        Page<Course> coursePage = courseService.page(new Page<>(current, size), queryWrapper);
        Page<CourseVO> courseVOPage = new Page<>(current, size, coursePage.getTotal());
        List<CourseVO> courseVO = courseService.getCourseVO(coursePage.getRecords());
        courseVOPage.setRecords(courseVO);
        return ResultUtils.success(courseVOPage);
    }

    /**
     * 根据 id 获取课程（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<CourseVO> getCourseVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Course course = courseService.getById(id);
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        CourseVO courseVO = courseService.getCourseVO(course, loginUser);
        
        // 如果是正常获取课程详情，则增加学习人数
        if (course.getStatus() == 1) {
            courseService.increaseStudentCount(id);
        }
        
        return ResultUtils.success(courseVO);
    }

    /**
     * 根据讲师ID获取课程列表
     *
     * @param teacherId
     * @param request
     * @return
     */
    @GetMapping("/teacher/{teacherId}")
    public BaseResponse<List<CourseVO>> getCoursesByTeacher(@PathVariable("teacherId") Long teacherId,
                                                         HttpServletRequest request) {
        if (teacherId == null || teacherId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<CourseVO> courseVOList = courseService.getCoursesByTeacher(teacherId, loginUser);
        return ResultUtils.success(courseVOList);
    }
    
    /**
     * 获取推荐课程
     *
     * @param categoryId
     * @param difficulty
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<List<CourseVO>> getRecommendCourses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        // 限制爬虫
        ThrowUtils.throwIf(limit > 20, ErrorCode.PARAMS_ERROR);
        
        User loginUser = userService.getLoginUser(request);
        List<CourseVO> courseVOList = courseService.getRecommendCourses(categoryId, difficulty, limit, loginUser);
        return ResultUtils.success(courseVOList);
    }
    
    /**
     * 给课程评分
     *
     * @param id
     * @param score
     * @param request
     * @return
     */
    @PostMapping("/rate/{id}")
    public BaseResponse<Boolean> rateCourse(@PathVariable("id") Long id,
                                          @RequestParam("score") Integer score,
                                          HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        if (score == null || score < 1 || score > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分必须在1-5分之间");
        }
        
        // 获取登录用户
        userService.getLoginUser(request);
        
        // 将用户评分转换为BigDecimal
        BigDecimal ratingScore = new BigDecimal(score);
        
        // 获取当前课程信息，用于计算新的评分
        Course course = courseService.getById(id);
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        }
        
        // 计算新的评分
        BigDecimal currentScore = course.getRatingScore();
        Integer currentCount = course.getRatingCount();
        BigDecimal newCount = new BigDecimal(currentCount + 1);
        BigDecimal totalScore = currentScore.multiply(new BigDecimal(currentCount)).add(ratingScore);
        BigDecimal newScore = totalScore.divide(newCount, 1, RoundingMode.HALF_UP);
        
        boolean result = courseService.updateCourseRating(id, newScore, currentCount + 1);
        return ResultUtils.success(result);
    }
    
} 
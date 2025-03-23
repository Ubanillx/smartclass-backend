package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.teacher.TeacherAddRequest;
import com.ubanillx.smartclass.model.dto.teacher.TeacherQueryRequest;
import com.ubanillx.smartclass.model.dto.teacher.TeacherUpdateRequest;
import com.ubanillx.smartclass.model.entity.Teacher;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.CourseVO;
import com.ubanillx.smartclass.model.vo.TeacherVO;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.TeacherService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 讲师接口
 */
@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    @Resource
    private UserService userService;
    
    @Resource
    private CourseService courseService;

    // region 增删改查

    /**
     * 创建讲师
     *
     * @param teacherAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addTeacher(@RequestBody TeacherAddRequest teacherAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(teacherAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherAddRequest, teacher);
        // 校验
        teacherService.validTeacher(teacher, true);
        User loginUser = userService.getLoginUser(request);
        
        // 添加讲师
        long teacherId = teacherService.addTeacher(teacher, loginUser.getId());
        return ResultUtils.success(teacherId);
    }

    /**
     * 删除讲师
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteTeacher(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        // 判断是否存在
        Teacher teacher = teacherService.getById(id);
        ThrowUtils.throwIf(teacher == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可删除
        boolean result = teacherService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新讲师
     *
     * @param teacherUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTeacher(@RequestBody TeacherUpdateRequest teacherUpdateRequest,
                                              HttpServletRequest request) {
        ThrowUtils.throwIf(teacherUpdateRequest == null || teacherUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherUpdateRequest, teacher);
        // 校验
        teacherService.validTeacher(teacher, false);
        
        // 判断是否存在
        long id = teacherUpdateRequest.getId();
        Teacher oldTeacher = teacherService.getById(id);
        ThrowUtils.throwIf(oldTeacher == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可更新
        boolean result = teacherService.updateById(teacher);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取讲师
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Teacher> getTeacherById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teacher);
    }

    /**
     * 根据 id 获取讲师（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<TeacherVO> getTeacherVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teacherService.getTeacherVO(teacher, request));
    }

    /**
     * 分页获取讲师列表
     *
     * @param teacherQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Teacher>> listTeacherByPage(@RequestBody TeacherQueryRequest teacherQueryRequest) {
        long current = teacherQueryRequest.getCurrent();
        long size = teacherQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<Teacher> teacherPage = teacherService.page(new Page<>(current, size),
                teacherService.getQueryWrapper(teacherQueryRequest));
        return ResultUtils.success(teacherPage);
    }

    /**
     * 分页获取讲师列表（封装类）
     *
     * @param teacherQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TeacherVO>> listTeacherVOByPage(@RequestBody TeacherQueryRequest teacherQueryRequest,
                                                          HttpServletRequest request) {
        long current = teacherQueryRequest.getCurrent();
        long size = teacherQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        // 查询讲师
        Page<Teacher> teacherPage = teacherService.page(new Page<>(current, size),
                teacherService.getQueryWrapper(teacherQueryRequest));
        
        // 获取封装
        Page<TeacherVO> teacherVOPage = teacherService.getTeacherVOPage(teacherPage, request);
        return ResultUtils.success(teacherVOPage);
    }

    // endregion

    /**
     * 获取讲师的课程列表
     *
     * @param teacherId
     * @param request
     * @return
     */
    @GetMapping("/courses/{teacherId}")
    public BaseResponse<List<CourseVO>> getTeacherCourses(@PathVariable Long teacherId, HttpServletRequest request) {
        if (teacherId == null || teacherId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        List<CourseVO> courseVOList = courseService.getCoursesByTeacher(teacherId, loginUser);
        return ResultUtils.success(courseVOList);
    }

    /**
     * 获取推荐讲师列表
     *
     * @param expertise 专业领域
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<List<TeacherVO>> getRecommendTeachers(String expertise, HttpServletRequest request) {
        // 默认获取前5位讲师
        int limit = 5;
        List<TeacherVO> teacherVOList = teacherService.getRecommendTeachers(expertise, limit, request);
        return ResultUtils.success(teacherVOList);
    }
} 
package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.entity.CourseChapter;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.CourseChapterService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 课程章节接口
 */
@RestController
@RequestMapping("course/chapter")
@Slf4j
public class CourseChapterController {

    @Resource
    private CourseChapterService courseChapterService;

    @Resource
    private UserService userService;

    /**
     * 创建课程章节
     *
     * @param courseChapter
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addChapter(@RequestBody CourseChapter courseChapter, HttpServletRequest request) {
        if (courseChapter == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        long id = courseChapterService.addCourseChapter(courseChapter, loginUser.getId());
        return ResultUtils.success(id);
    }

    /**
     * 删除课程章节
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteChapter(@RequestParam("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        CourseChapter oldCourseChapter = courseChapterService.getById(id);
        ThrowUtils.throwIf(oldCourseChapter == null, ErrorCode.NOT_FOUND_ERROR);
        
        boolean result = courseChapterService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新课程章节
     *
     * @param courseChapter
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChapter(@RequestBody CourseChapter courseChapter, HttpServletRequest request) {
        if (courseChapter == null || courseChapter.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        CourseChapter oldCourseChapter = courseChapterService.getById(courseChapter.getId());
        ThrowUtils.throwIf(oldCourseChapter == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 校验
        courseChapterService.validCourseChapter(courseChapter, false);
        
        boolean result = courseChapterService.updateById(courseChapter);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取课程章节
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<CourseChapter> getChapterById(@RequestParam("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        CourseChapter courseChapter = courseChapterService.getById(id);
        if (courseChapter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        return ResultUtils.success(courseChapter);
    }

    /**
     * 获取课程章节列表
     *
     * @param courseId
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<CourseChapter>> listChapters(@RequestParam("courseId") Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<CourseChapter> wrapper = courseChapterService.getQueryWrapper(courseId);
        List<CourseChapter> chapterList = courseChapterService.list(wrapper);
        return ResultUtils.success(chapterList);
    }

    /**
     * 分页获取课程章节列表
     *
     * @param courseId
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<CourseChapter>> listChaptersByPage(
            @RequestParam("courseId") Long courseId,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<CourseChapter> wrapper = courseChapterService.getQueryWrapper(courseId);
        Page<CourseChapter> chapterPage = courseChapterService.page(new Page<>(current, pageSize), wrapper);
        return ResultUtils.success(chapterPage);
    }
} 
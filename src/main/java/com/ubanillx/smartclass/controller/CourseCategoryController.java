package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.entity.CourseCategory;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.CourseCategoryService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 课程分类接口
 */
@RestController
@RequestMapping("course/category")
@Slf4j
public class CourseCategoryController {

    @Resource
    private CourseCategoryService courseCategoryService;

    @Resource
    private UserService userService;

    /**
     * 添加课程分类
     *
     * @param courseCategory 课程分类信息
     * @param request HTTP请求
     * @return 分类ID
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCategory(@RequestBody CourseCategory courseCategory,
                                         HttpServletRequest request) {
        if (courseCategory == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long categoryId = courseCategoryService.addCourseCategory(courseCategory, loginUser.getId());
        return ResultUtils.success(categoryId);
    }

    /**
     * 删除课程分类
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCategory(@RequestBody DeleteRequest deleteRequest,
                                              HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        // 判断是否存在
        CourseCategory oldCategory = courseCategoryService.getById(id);
        if (oldCategory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 检查是否有子分类
        List<CourseCategory> subCategories = courseCategoryService.getSubCategories(id);
        if (subCategories != null && !subCategories.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请先删除子分类");
        }
        
        boolean result = courseCategoryService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新课程分类
     *
     * @param courseCategory 更新信息
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCategory(@RequestBody CourseCategory courseCategory,
                                              HttpServletRequest request) {
        if (courseCategory == null || courseCategory.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        CourseCategory oldCategory = courseCategoryService.getById(courseCategory.getId());
        if (oldCategory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = courseCategoryService.updateById(courseCategory);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取课程分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/get")
    public BaseResponse<CourseCategory> getCategoryById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CourseCategory category = courseCategoryService.getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(category);
    }

    /**
     * 获取所有一级分类
     *
     * @return 一级分类列表
     */
    @GetMapping("/top")
    public BaseResponse<List<CourseCategory>> getTopCategories() {
        List<CourseCategory> topCategories = courseCategoryService.getTopCategories();
        return ResultUtils.success(topCategories);
    }

    /**
     * 获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/sub")
    public BaseResponse<List<CourseCategory>> getSubCategories(@RequestParam Long parentId) {
        if (parentId == null || parentId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<CourseCategory> subCategories = courseCategoryService.getSubCategories(parentId);
        return ResultUtils.success(subCategories);
    }

    /**
     * 获取分类及其子分类
     *
     * @param categoryId 分类ID
     * @return 分类及其子分类列表
     */
    @GetMapping("/with-children")
    public BaseResponse<List<CourseCategory>> getCategoryWithChildren(@RequestParam Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<CourseCategory> categories = courseCategoryService.getCategoryWithChildren(categoryId);
        return ResultUtils.success(categories);
    }
} 
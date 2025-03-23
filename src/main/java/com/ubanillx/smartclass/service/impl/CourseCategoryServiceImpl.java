package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.CourseCategoryMapper;
import com.ubanillx.smartclass.model.entity.CourseCategory;
import com.ubanillx.smartclass.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程分类服务实现类
 */
@Service
@Slf4j
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory>
        implements CourseCategoryService {

    /**
     * 添加课程分类
     *
     * @param courseCategory 课程分类
     * @param adminId 管理员ID
     * @return 分类ID
     */
    public long addCourseCategory(CourseCategory courseCategory, Long adminId) {
        if (courseCategory == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 校验课程分类
        validCourseCategory(courseCategory, true);

        // 设置管理员ID
        courseCategory.setAdminId(adminId);

        // 设置父分类，如果未设置则默认为一级分类
        if (courseCategory.getParentId() == null) {
            courseCategory.setParentId(0L);
        }

        // 设置排序，如果未设置则默认排序为0
        if (courseCategory.getSort() == null) {
            courseCategory.setSort(0);
        }

        // 保存课程分类
        boolean result = this.save(courseCategory);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return courseCategory.getId();
    }

    /**
     * 获取一级分类列表
     *
     * @return 一级分类列表
     */
    public List<CourseCategory> getTopCategories() {
        QueryWrapper<CourseCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parentId", 0);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByDesc("sort");
        return this.list(queryWrapper);
    }

    /**
     * 获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    public List<CourseCategory> getSubCategories(Long parentId) {
        if (parentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父分类ID不能为空");
        }

        QueryWrapper<CourseCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parentId", parentId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByDesc("sort");
        return this.list(queryWrapper);
    }

    /**
     * 获取分类及其子分类
     *
     * @param categoryId 分类ID
     * @return 分类及其子分类列表
     */
    public List<CourseCategory> getCategoryWithChildren(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }

        // 获取当前分类
        CourseCategory category = this.getById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 获取子分类
        List<CourseCategory> children = getSubCategories(categoryId);
        
        // 合并列表
        List<CourseCategory> result = new java.util.ArrayList<>();
        result.add(category);
        result.addAll(children);
        
        return result;
    }

    /**
     * 校验课程分类
     *
     * @param courseCategory 课程分类
     * @param add 是否为添加操作
     */
    private void validCourseCategory(CourseCategory courseCategory, boolean add) {
        if (courseCategory == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = courseCategory.getName();
        
        // 创建时，必须有分类名
        if (add) {
            if (StringUtils.isBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称不能为空");
            }
        }
        
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称过长");
        }
    }
} 
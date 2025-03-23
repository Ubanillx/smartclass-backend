package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.mapper.CourseChapterMapper;
import com.ubanillx.smartclass.model.entity.CourseChapter;
import com.ubanillx.smartclass.service.CourseChapterService;
import com.ubanillx.smartclass.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程章节服务实现
 */
@Service
@Slf4j
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements CourseChapterService {

    @Resource
    private CourseService courseService;

    @Override
    public List<CourseChapter> getChaptersByCourseId(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        QueryWrapper<CourseChapter> queryWrapper = getQueryWrapper(courseId);
        return this.list(queryWrapper);
    }

    @Override
    public long addCourseChapter(CourseChapter courseChapter, Long adminId) {
        if (courseChapter == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        if (adminId == null || adminId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员ID不合法");
        }
        
        // 校验
        validCourseChapter(courseChapter, true);
        
        // 课程ID校验
        Long courseId = courseChapter.getCourseId();
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        // 检查课程是否存在
        if (courseService.getById(courseId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程不存在");
        }
        
        // 设置管理员ID
        courseChapter.setAdminId(adminId);
        
        // 设置排序，如果未设置则默认为最后一个
        if (courseChapter.getSort() == null) {
            // 查询当前课程的最大排序值
            QueryWrapper<CourseChapter> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("courseId", courseId);
            queryWrapper.orderByDesc("sort");
            queryWrapper.last("LIMIT 1");
            
            CourseChapter lastChapter = this.getOne(queryWrapper);
            if (lastChapter != null) {
                courseChapter.setSort(lastChapter.getSort() + 1);
            } else {
                courseChapter.setSort(1);
            }
        }
        
        // 保存
        boolean result = this.save(courseChapter);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");
        
        return courseChapter.getId();
    }

    @Override
    public void validCourseChapter(CourseChapter courseChapter, boolean add) {
        if (courseChapter == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String title = courseChapter.getTitle();
        Long courseId = courseChapter.getCourseId();
        
        // 创建时必需参数
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR, "章节标题不能为空");
            ThrowUtils.throwIf(courseId == null || courseId <= 0, ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        // 有值则校验
        if (StringUtils.isNotBlank(title) && title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "章节标题过长");
        }
    }

    @Override
    public QueryWrapper<CourseChapter> getQueryWrapper(Long courseId) {
        QueryWrapper<CourseChapter> queryWrapper = new QueryWrapper<>();
        
        // 根据课程ID查询
        if (courseId != null && courseId > 0) {
            queryWrapper.eq("courseId", courseId);
        }
        
        // 未删除
        queryWrapper.eq("isDelete", 0);
        
        // 按排序字段升序
        queryWrapper.orderByAsc("sort");
        
        return queryWrapper;
    }
} 
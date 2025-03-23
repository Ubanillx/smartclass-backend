package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.mapper.CourseSectionMapper;
import com.ubanillx.smartclass.model.entity.CourseChapter;
import com.ubanillx.smartclass.model.entity.CourseSection;
import com.ubanillx.smartclass.service.CourseChapterService;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.CourseSectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程小节服务实现
 */
@Service
@Slf4j
public class CourseSectionServiceImpl extends ServiceImpl<CourseSectionMapper, CourseSection> implements CourseSectionService {

    @Resource
    private CourseService courseService;

    @Resource
    private CourseChapterService courseChapterService;

    @Override
    public List<CourseSection> getSectionsByCourseId(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        QueryWrapper<CourseSection> queryWrapper = getQueryWrapper(courseId, null);
        return this.list(queryWrapper);
    }

    @Override
    public List<CourseSection> getSectionsByChapterId(Long chapterId) {
        if (chapterId == null || chapterId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "章节ID不合法");
        }
        
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chapterId", chapterId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByAsc("sort");
        
        return this.list(queryWrapper);
    }

    @Override
    public long addCourseSection(CourseSection courseSection, Long adminId) {
        if (courseSection == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        if (adminId == null || adminId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员ID不合法");
        }
        
        // 校验
        validCourseSection(courseSection, true);
        
        // 课程ID校验
        Long courseId = courseSection.getCourseId();
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        // 章节ID校验
        Long chapterId = courseSection.getChapterId();
        if (chapterId == null || chapterId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "章节ID不合法");
        }
        
        // 检查课程是否存在
        if (courseService.getById(courseId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程不存在");
        }
        
        // 检查章节是否存在
        CourseChapter chapter = courseChapterService.getById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "章节不存在");
        }
        
        // 确保章节属于该课程
        if (!chapter.getCourseId().equals(courseId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "章节不属于该课程");
        }
        
        // 设置管理员ID
        courseSection.setAdminId(adminId);
        
        // 设置排序，如果未设置则默认为最后一个
        if (courseSection.getSort() == null) {
            // 查询当前章节的最大排序值
            QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("chapterId", chapterId);
            queryWrapper.orderByDesc("sort");
            queryWrapper.last("LIMIT 1");
            
            CourseSection lastSection = this.getOne(queryWrapper);
            if (lastSection != null) {
                courseSection.setSort(lastSection.getSort() + 1);
            } else {
                courseSection.setSort(1);
            }
        }
        
        // 保存
        boolean result = this.save(courseSection);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");
        
        return courseSection.getId();
    }

    @Override
    public void validCourseSection(CourseSection courseSection, boolean add) {
        if (courseSection == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String title = courseSection.getTitle();
        Long courseId = courseSection.getCourseId();
        Long chapterId = courseSection.getChapterId();
        String videoUrl = courseSection.getVideoUrl();
        Integer duration = courseSection.getDuration();
        
        // 创建时必需参数
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR, "小节标题不能为空");
            ThrowUtils.throwIf(courseId == null || courseId <= 0, ErrorCode.PARAMS_ERROR, "课程ID不合法");
            ThrowUtils.throwIf(chapterId == null || chapterId <= 0, ErrorCode.PARAMS_ERROR, "章节ID不合法");
            ThrowUtils.throwIf(StringUtils.isBlank(videoUrl), ErrorCode.PARAMS_ERROR, "视频地址不能为空");
        }
        
        // 有值则校验
        if (StringUtils.isNotBlank(title) && title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "小节标题过长");
        }
        
        if (StringUtils.isNotBlank(videoUrl) && videoUrl.length() > 255) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频地址过长");
        }
        
        if (duration != null && duration < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频时长不能为负数");
        }
    }

    @Override
    public QueryWrapper<CourseSection> getQueryWrapper(Long courseId, Long chapterId) {
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        
        // 根据课程ID查询
        if (courseId != null && courseId > 0) {
            queryWrapper.eq("courseId", courseId);
        }
        
        // 根据章节ID查询
        if (chapterId != null && chapterId > 0) {
            queryWrapper.eq("chapterId", chapterId);
        }
        
        // 未删除
        queryWrapper.eq("isDelete", 0);
        
        // 按章节ID和排序字段升序
        queryWrapper.orderByAsc("chapterId", "sort");
        
        return queryWrapper;
    }

    @Override
    public int getTotalDuration(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("courseId", courseId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.select("IFNULL(SUM(duration), 0) as total");
        
        // 使用selectOne查询总时长
        Integer totalDuration = this.baseMapper.selectOne(queryWrapper).getDuration();
        return totalDuration != null ? totalDuration : 0;
    }

    @Override
    public int countSections(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("courseId", courseId);
        queryWrapper.eq("isDelete", 0);
        
        return Math.toIntExact(this.count(queryWrapper));
    }
} 
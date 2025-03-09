package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.mapper.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author liulo
 * @description 针对表【course(课程)】的数据库操作Service实现
 * @createDate 2025-02-27 21:52:02
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService {

    @Override
    public Long createCourse(Course course) {
        // 设置初始状态为未发布
        course.setStatus(0);
        // 设置初始学习人数、评分人数为0
        course.setStudentCount(0);
        course.setRatingCount(0);
        // 设置创建和更新时间
        Date now = new Date();
        course.setCreateTime(now);
        course.setUpdateTime(now);
        // 设置未删除状态
        course.setIsDelete(0);
        
        // 保存课程并返回课程ID
        save(course);
        return course.getId();
    }

    @Override
    public boolean updateCourse(Course course) {
        // 更新时间
        course.setUpdateTime(new Date());
        return updateById(course);
    }

    @Override
    public boolean updateCourseStatus(Long courseId, Integer status) {
        // 构建更新条件
        LambdaUpdateWrapper<Course> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Course::getId, courseId)
                .set(Course::getStatus, status)
                .set(Course::getUpdateTime, new Date());
        
        return update(updateWrapper);
    }

    @Override
    public IPage<Course> pageCourses(Course condition, Page<Course> page) {
        // 构建查询条件
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加各种查询条件，只有当条件不为空时才添加
        queryWrapper.eq(condition.getCategoryId() != null, Course::getCategoryId, condition.getCategoryId())
                .eq(condition.getTeacherId() != null, Course::getTeacherId, condition.getTeacherId())
                .eq(condition.getCourseType() != null, Course::getCourseType, condition.getCourseType())
                .eq(condition.getDifficulty() != null, Course::getDifficulty, condition.getDifficulty())
                .eq(condition.getStatus() != null, Course::getStatus, condition.getStatus())
                .eq(Course::getIsDelete, 0); // 只查询未删除的课程
        
        // 如果有关键词，则在标题、副标题和描述中进行模糊查询
        if (StringUtils.hasText(condition.getTitle())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Course::getTitle, condition.getTitle())
                    .or()
                    .like(Course::getSubtitle, condition.getTitle())
                    .or()
                    .like(Course::getDescription, condition.getTitle()));
        }
        
        // 默认按更新时间降序排序
        queryWrapper.orderByDesc(Course::getUpdateTime);
        
        return page(page, queryWrapper);
    }

    @Override
    public boolean deleteCourse(Long courseId) {
        // 逻辑删除，更新isDelete字段为1
        LambdaUpdateWrapper<Course> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Course::getId, courseId)
                .set(Course::getIsDelete, 1)
                .set(Course::getUpdateTime, new Date());
        
        return update(updateWrapper);
    }
}
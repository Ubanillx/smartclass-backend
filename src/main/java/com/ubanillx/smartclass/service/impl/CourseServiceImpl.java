package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.CourseMapper;
import com.ubanillx.smartclass.model.dto.course.CourseQueryRequest;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.CourseVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author liulo
* @description 针对表【course(课程)】的数据库操作Service实现
* @createDate 2025-03-18 23:08:38
*/
@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService {

    @Resource
    private UserService userService;

    @Override
    public void validCourse(Course course, boolean add) {
        if (course == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = course.getTitle();
        // 创建时，参数不能为空
        if (add) {
            if (StringUtils.isBlank(title)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程标题不能为空");
            }
            if (course.getPrice() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程价格不能为空");
            }
            if (course.getCategoryId() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程分类不能为空");
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程标题过长");
        }
    }

    @Override
    public long addCourse(Course course, Long adminId) {
        if (course == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (StringUtils.isBlank(course.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程标题不能为空");
        }
        if (StringUtils.isBlank(course.getDescription())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程描述不能为空");
        }
        if (course.getTeacherId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "讲师不能为空");
        }
        // 初始化值
        course.setAdminId(adminId);
        course.setStudentCount(0);
        course.setRatingScore(BigDecimal.ZERO);
        course.setRatingCount(0);
        boolean result = this.save(course);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return course.getId();
    }

    @Override
    public CourseVO getCourseVO(Course course, User currentUser) {
        if (course == null) {
            return null;
        }
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);
        
        // 填充讲师信息
        Long teacherId = course.getTeacherId();
        if (teacherId != null) {
            User teacher = userService.getById(teacherId);
            if (teacher != null) {
                UserVO teacherVO = userService.getUserVO(teacher);
                courseVO.setTeacherVO(teacherVO);
            }
        }
        
        return courseVO;
    }

    @Override
    public List<CourseVO> getCourseVO(List<Course> courseList, User currentUser) {
        if (CollUtil.isEmpty(courseList)) {
            return new ArrayList<>();
        }

        // 获取所有讲师ID
        Set<Long> teacherIdSet = courseList.stream()
                .map(Course::getTeacherId)
                .filter(teacherId -> teacherId != null)
                .collect(Collectors.toSet());
        
        // 批量查询讲师信息
        Map<Long, List<User>> teacherIdUserListMap = new ArrayList<User>().stream()
                .collect(Collectors.groupingBy(User::getId));
        if (!CollUtil.isEmpty(teacherIdSet)) {
            List<User> userList = userService.listByIds(teacherIdSet);
            teacherIdUserListMap = userList.stream()
                    .collect(Collectors.groupingBy(User::getId));
        }
        
        // 填充讲师信息，转换为VO
        Map<Long, List<User>> finalTeacherIdUserListMap = teacherIdUserListMap;
        return courseList.stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtils.copyProperties(course, courseVO);
            
            // 填充讲师信息
            Long teacherId = course.getTeacherId();
            if (teacherId != null && finalTeacherIdUserListMap.containsKey(teacherId)) {
                User teacher = finalTeacherIdUserListMap.get(teacherId).get(0);
                UserVO teacherVO = userService.getUserVO(teacher);
                courseVO.setTeacherVO(teacherVO);
            }
            
            return courseVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CourseVO> getCourseVO(List<Course> courseList) {
        if (CollUtil.isEmpty(courseList)) {
            return new ArrayList<>();
        }
        // 获取所有讲师ID
        Set<Long> teacherIdSet = courseList.stream()
                .map(Course::getTeacherId)
                .filter(teacherId -> teacherId != null)
                .collect(Collectors.toSet());
        
        // 批量查询讲师信息
        Map<Long, List<User>> teacherIdUserListMap = new ArrayList<User>().stream()
                .collect(Collectors.groupingBy(User::getId));
        if (!CollUtil.isEmpty(teacherIdSet)) {
            List<User> userList = userService.listByIds(teacherIdSet);
            teacherIdUserListMap = userList.stream()
                    .collect(Collectors.groupingBy(User::getId));
        }
        
        // 填充讲师信息，转换为VO
        Map<Long, List<User>> finalTeacherIdUserListMap = teacherIdUserListMap;
        return courseList.stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtils.copyProperties(course, courseVO);
            
            // 填充讲师信息
            Long teacherId = course.getTeacherId();
            if (teacherId != null && finalTeacherIdUserListMap.containsKey(teacherId)) {
                User teacher = finalTeacherIdUserListMap.get(teacherId).get(0);
                UserVO teacherVO = userService.getUserVO(teacher);
                courseVO.setTeacherVO(teacherVO);
                courseVO.setTeacherName(teacher.getUserName());
                courseVO.setTeacherAvatar(teacher.getUserAvatar());
            }
            
            return courseVO;
        }).collect(Collectors.toList());
    }

    @Override
    public CourseVO getCourseVO(Course course) {
        if (course == null) {
            return null;
        }
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);
        
        // 填充讲师信息
        Long teacherId = course.getTeacherId();
        if (teacherId != null) {
            User teacher = userService.getById(teacherId);
            if (teacher != null) {
                UserVO teacherVO = userService.getUserVO(teacher);
                courseVO.setTeacherVO(teacherVO);
                courseVO.setTeacherName(teacher.getUserName());
                courseVO.setTeacherAvatar(teacher.getUserAvatar());
            }
        }
        
        return courseVO;
    }

    @Override
    public QueryWrapper<Course> getQueryWrapper(CourseQueryRequest courseQueryRequest) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (courseQueryRequest == null) {
            return queryWrapper;
        }
        Long id = courseQueryRequest.getId();
        String title = courseQueryRequest.getTitle();
        Integer courseType = courseQueryRequest.getCourseType();
        Integer difficulty = courseQueryRequest.getDifficulty();
        Integer status = courseQueryRequest.getStatus();
        Long categoryId = courseQueryRequest.getCategoryId();
        Long teacherId = courseQueryRequest.getTeacherId();
        Long userId = courseQueryRequest.getUserId();
        String tags = courseQueryRequest.getTags();
        BigDecimal minPrice = courseQueryRequest.getMinPrice();
        BigDecimal maxPrice = courseQueryRequest.getMaxPrice();
        String sortField = courseQueryRequest.getSortField();
        String sortOrder = courseQueryRequest.getSortOrder();
        
        // 拼接查询条件
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(courseType), "course_type", courseType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(categoryId), "category_id", categoryId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(teacherId), "teacher_id", teacherId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        if (StringUtils.isNotBlank(tags)) {
            queryWrapper.like("tags", tags);
        }
        if (ObjectUtils.isNotEmpty(minPrice)) {
            queryWrapper.ge("price", minPrice);
        }
        if (ObjectUtils.isNotEmpty(maxPrice)) {
            queryWrapper.le("price", maxPrice);
        }
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), 
                sortField);
        return queryWrapper;
    }

    @Override
    public boolean updateCourseRating(Long id, BigDecimal score, Integer count) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        if (score == null) {
            score = BigDecimal.ZERO;
        }
        
        if (count == null) {
            count = 0;
        }
        
        // 确保评分值合法
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("5")) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分值不合法");
        }
        
        // 构建更新条件
        UpdateWrapper<Course> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("ratingScore", score);
        updateWrapper.set("ratingCount", count);
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean increaseStudentCount(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不能为空");
        }
        
        UpdateWrapper<Course> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("student_count = student_count + 1");
        
        return this.update(updateWrapper);
    }

    @Override
    public List<CourseVO> getCoursesByTeacher(Long teacherId, User currentUser) {
        if (teacherId == null || teacherId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "讲师ID不能为空");
        }
        
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.eq("status", 1); // 只查询已发布的课程
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc("create_time");
        
        List<Course> courseList = this.list(queryWrapper);
        return this.getCourseVO(courseList, currentUser);
    }

    @Override
    public List<CourseVO> getRecommendCourses(Long categoryId, Integer difficulty, int limit, User currentUser) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询已发布的课程
        queryWrapper.eq("is_delete", 0);
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (difficulty != null) {
            queryWrapper.eq("difficulty", difficulty);
        }
        
        // 按照评分、学习人数排序，获取热门课程
        queryWrapper.orderByDesc("rating_score", "student_count");
        queryWrapper.last("LIMIT " + limit);
        
        List<Course> courseList = this.list(queryWrapper);
        return this.getCourseVO(courseList, currentUser);
    }
} 
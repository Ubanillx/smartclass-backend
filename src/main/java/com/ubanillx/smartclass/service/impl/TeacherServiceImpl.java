package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.TeacherMapper;
import com.ubanillx.smartclass.model.dto.teacher.TeacherQueryRequest;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.model.entity.Teacher;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.TeacherVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.TeacherService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 讲师服务实现
 */
@Service
@Slf4j
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Resource
    private UserService userService;
    
    @Resource
    private CourseService courseService;

    @Override
    public void validTeacher(Teacher teacher, boolean add) {
        if (teacher == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String name = teacher.getName();
        // 创建时，参数不能为空
        if (add) {
            if (StringUtils.isBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "讲师姓名不能为空");
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "讲师姓名过长");
        }
    }

    @Override
    public QueryWrapper<Teacher> getQueryWrapper(TeacherQueryRequest teacherQueryRequest) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        if (teacherQueryRequest == null) {
            return queryWrapper;
        }
        
        Long id = teacherQueryRequest.getId();
        String name = teacherQueryRequest.getName();
        String title = teacherQueryRequest.getTitle();
        String expertise = teacherQueryRequest.getExpertise();
        Long userId = teacherQueryRequest.getUserId();
        Long adminId = teacherQueryRequest.getAdminId();
        String sortField = teacherQueryRequest.getSortField();
        String sortOrder = teacherQueryRequest.getSortOrder();
        
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (StringUtils.isNotBlank(expertise)) {
            queryWrapper.like("expertise", expertise);
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "adminId", adminId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), 
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
                
        return queryWrapper;
    }

    @Override
    public TeacherVO getTeacherVO(Teacher teacher, HttpServletRequest request) {
        if (teacher == null) {
            return null;
        }
        
        TeacherVO teacherVO = new TeacherVO();
        BeanUtils.copyProperties(teacher, teacherVO);
        
        // 关联用户信息
        Long userId = teacher.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            if (user != null) {
                UserVO userVO = userService.getUserVO(user);
                teacherVO.setUserVO(userVO);
            }
        }
        
        // 查询讲师相关统计数据
        Long teacherId = teacher.getId();
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.eq("teacherId", teacherId);
        courseQueryWrapper.eq("isDelete", 0);
        courseQueryWrapper.eq("status", 1); // 已发布的课程
        
        // 统计课程数
        long courseCount = courseService.count(courseQueryWrapper);
        teacherVO.setCourseCount((int) courseCount);
        
        // 统计学生数和评分
        if (courseCount > 0) {
            List<Course> courseList = courseService.list(courseQueryWrapper);
            
            int totalStudentCount = courseList.stream()
                    .mapToInt(Course::getStudentCount)
                    .sum();
            teacherVO.setStudentCount(totalStudentCount);
            
            // 计算平均评分
            double totalRating = courseList.stream()
                    .filter(course -> course.getRatingCount() > 0)
                    .mapToDouble(course -> course.getRatingScore().doubleValue())
                    .sum();
                    
            int ratedCourseCount = (int) courseList.stream()
                    .filter(course -> course.getRatingCount() > 0)
                    .count();
                    
            if (ratedCourseCount > 0) {
                double averageRating = totalRating / ratedCourseCount;
                teacherVO.setAverageRating(BigDecimal.valueOf(averageRating)
                        .setScale(1, RoundingMode.HALF_UP).doubleValue());
            } else {
                teacherVO.setAverageRating(0.0);
            }
        } else {
            teacherVO.setStudentCount(0);
            teacherVO.setAverageRating(0.0);
        }
        
        return teacherVO;
    }

    @Override
    public List<TeacherVO> getTeacherVO(List<Teacher> teacherList, HttpServletRequest request) {
        if (CollUtil.isEmpty(teacherList)) {
            return new ArrayList<>();
        }
        
        // 获取关联的用户ID
        Set<Long> userIdSet = teacherList.stream()
                .map(Teacher::getUserId)
                .filter(userId -> userId != null)
                .collect(Collectors.toSet());
                
        // 批量查询用户信息
        Map<Long, List<User>> userIdUserListMap = new ArrayList<User>().stream()
                .collect(Collectors.groupingBy(User::getId));
        if (!CollUtil.isEmpty(userIdSet)) {
            List<User> userList = userService.listByIds(userIdSet);
            userIdUserListMap = userList.stream()
                    .collect(Collectors.groupingBy(User::getId));
        }
        
        // 获取所有讲师ID
        Set<Long> teacherIdSet = teacherList.stream()
                .map(Teacher::getId)
                .collect(Collectors.toSet());
                
        // 批量查询每个讲师的课程数、学生数和评分
        Map<Long, Integer> teacherCourseCountMap = new java.util.HashMap<>();
        Map<Long, Integer> teacherStudentCountMap = new java.util.HashMap<>();
        Map<Long, Double> teacherRatingMap = new java.util.HashMap<>();
        
        if (!CollUtil.isEmpty(teacherIdSet)) {
            // 查询所有讲师的课程
            QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
            courseQueryWrapper.in("teacherId", teacherIdSet);
            courseQueryWrapper.eq("isDelete", 0);
            courseQueryWrapper.eq("status", 1); // 已发布的课程
            List<Course> allCourseList = courseService.list(courseQueryWrapper);
            
            // 按讲师ID分组
            Map<Long, List<Course>> teacherCourseMap = allCourseList.stream()
                    .collect(Collectors.groupingBy(Course::getTeacherId));
                    
            // 统计每个讲师的数据
            teacherIdSet.forEach(teacherId -> {
                List<Course> courseList = teacherCourseMap.getOrDefault(teacherId, new ArrayList<>());
                
                // 课程数
                teacherCourseCountMap.put(teacherId, courseList.size());
                
                // 学生数
                int studentCount = courseList.stream()
                        .mapToInt(Course::getStudentCount)
                        .sum();
                teacherStudentCountMap.put(teacherId, studentCount);
                
                // 评分
                double totalRating = courseList.stream()
                        .filter(course -> course.getRatingCount() > 0)
                        .mapToDouble(course -> course.getRatingScore().doubleValue())
                        .sum();
                        
                int ratedCourseCount = (int) courseList.stream()
                        .filter(course -> course.getRatingCount() > 0)
                        .count();
                        
                if (ratedCourseCount > 0) {
                    double averageRating = totalRating / ratedCourseCount;
                    teacherRatingMap.put(teacherId, BigDecimal.valueOf(averageRating)
                            .setScale(1, RoundingMode.HALF_UP).doubleValue());
                } else {
                    teacherRatingMap.put(teacherId, 0.0);
                }
            });
        }
        
        // 填充VO
        Map<Long, List<User>> finalUserIdUserListMap = userIdUserListMap;
        return teacherList.stream().map(teacher -> {
            TeacherVO teacherVO = new TeacherVO();
            BeanUtils.copyProperties(teacher, teacherVO);
            
            // 填充用户信息
            Long userId = teacher.getUserId();
            if (userId != null && finalUserIdUserListMap.containsKey(userId)) {
                User user = finalUserIdUserListMap.get(userId).get(0);
                UserVO userVO = userService.getUserVO(user);
                teacherVO.setUserVO(userVO);
            }
            
            // 填充统计数据
            Long teacherId = teacher.getId();
            teacherVO.setCourseCount(teacherCourseCountMap.getOrDefault(teacherId, 0));
            teacherVO.setStudentCount(teacherStudentCountMap.getOrDefault(teacherId, 0));
            teacherVO.setAverageRating(teacherRatingMap.getOrDefault(teacherId, 0.0));
            
            return teacherVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<TeacherVO> getTeacherVOPage(Page<Teacher> teacherPage, HttpServletRequest request) {
        List<Teacher> teacherList = teacherPage.getRecords();
        Page<TeacherVO> teacherVOPage = new Page<>(teacherPage.getCurrent(), teacherPage.getSize(), teacherPage.getTotal());
        List<TeacherVO> teacherVOList = getTeacherVO(teacherList, request);
        teacherVOPage.setRecords(teacherVOList);
        return teacherVOPage;
    }

    @Override
    public long addTeacher(Teacher teacher, Long adminId) {
        if (teacher == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (StringUtils.isBlank(teacher.getName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "讲师姓名不能为空");
        }
        // 校验用户是否存在
        Long userId = teacher.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "关联用户不存在");
            }
        }
        
        // 设置管理员ID
        teacher.setAdminId(adminId);
        
        boolean result = this.save(teacher);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return teacher.getId();
    }

    @Override
    public List<TeacherVO> getRecommendTeachers(String expertise, int limit, HttpServletRequest request) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        
        if (StringUtils.isNotBlank(expertise)) {
            queryWrapper.like("expertise", expertise);
        }
        
        // 根据专业领域匹配，获取推荐讲师
        queryWrapper.last("LIMIT " + limit);
        
        List<Teacher> teacherList = this.list(queryWrapper);
        return this.getTeacherVO(teacherList, request);
    }
} 
package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.mapper.CourseReviewMapper;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.model.entity.CourseReview;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.CourseReviewVO;
import com.ubanillx.smartclass.service.CourseReviewService;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程评论服务实现
 */
@Service
@Slf4j
public class CourseReviewServiceImpl extends ServiceImpl<CourseReviewMapper, CourseReview> implements CourseReviewService {

    @Resource
    private CourseService courseService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addCourseReview(CourseReview courseReview, Long userId) {
        if (courseReview == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 检查课程ID
        Long courseId = courseReview.getCourseId();
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        // 检查内容
        String content = courseReview.getContent();
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        if (content.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容过长");
        }
        
        // 检查评分
        Integer rating = courseReview.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分必须在1-5之间");
        }
        
        // 检查课程是否存在
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程不存在");
        }
        
        // 检查用户是否已经评论过该课程
        QueryWrapper<CourseReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("courseId", courseId);
        queryWrapper.eq("isDelete", 0);
        
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已经评论过该课程");
        }
        
        // 设置用户ID
        courseReview.setUserId(userId);
        
        // 初始化其他字段
        courseReview.setLikeCount(0);
        courseReview.setReplyCount(0);
        courseReview.setAdminReply(null);
        courseReview.setAdminReplyTime(null);
        courseReview.setStatus(0); // 默认待审核
        
        // 保存评论
        boolean result = this.save(courseReview);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加评论失败");
        
        // 更新课程评分统计
        updateCourseRating(courseId);
        
        return courseReview.getId();
    }

    @Override
    public Page<CourseReviewVO> getReviewsByCourseId(Long courseId, long current, long pageSize) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        // 构建查询条件
        QueryWrapper<CourseReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("courseId", courseId);
        queryWrapper.eq("status", 1); // 只查询已发布的评论
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByDesc("createTime"); // 按时间倒序排列
        
        // 分页查询
        Page<CourseReview> reviewPage = this.page(new Page<>(current, pageSize), queryWrapper);
        
        // 转换为VO对象
        List<CourseReviewVO> reviewVOList = getReviewVO(reviewPage.getRecords());
        
        // 构建返回结果
        Page<CourseReviewVO> reviewVOPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        reviewVOPage.setRecords(reviewVOList);
        
        return reviewVOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeReview(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不合法");
        }
        
        // 检查评论是否存在
        CourseReview review = this.getById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
        }
        
        // 增加点赞数
        UpdateWrapper<CourseReview> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", reviewId);
        updateWrapper.setSql("likeCount = likeCount + 1");
        
        return this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replyReview(Long reviewId, String replyContent, Long adminId) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不合法");
        }
        
        if (StringUtils.isBlank(replyContent)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容不能为空");
        }
        
        if (replyContent.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容过长");
        }
        
        if (adminId == null || adminId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员ID不合法");
        }
        
        // 检查评论是否存在
        CourseReview review = this.getById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
        }
        
        // 更新回复内容
        UpdateWrapper<CourseReview> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", reviewId);
        updateWrapper.set("adminReply", replyContent);
        updateWrapper.set("adminReplyTime", new Date());
        
        return this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReviewStatus(Long reviewId, Integer status) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不合法");
        }
        
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态值不合法");
        }
        
        // 检查评论是否存在
        CourseReview review = this.getById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
        }
        
        // 如果状态从非已发布变为已发布，或从已发布变为非已发布，需要更新课程评分
        boolean needUpdateRating = (review.getStatus() != 1 && status == 1) || (review.getStatus() == 1 && status != 1);
        
        // 更新状态
        UpdateWrapper<CourseReview> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", reviewId);
        updateWrapper.set("status", status);
        
        boolean result = this.update(updateWrapper);
        
        // 更新课程评分
        if (result && needUpdateRating) {
            updateCourseRating(review.getCourseId());
        }
        
        return result;
    }

    @Override
    public QueryWrapper<CourseReview> getQueryWrapper(Long courseId, Long userId) {
        QueryWrapper<CourseReview> queryWrapper = new QueryWrapper<>();
        
        // 根据课程ID查询
        if (courseId != null && courseId > 0) {
            queryWrapper.eq("courseId", courseId);
        }
        
        // 根据用户ID查询
        if (userId != null && userId > 0) {
            queryWrapper.eq("userId", userId);
        }
        
        // 未删除
        queryWrapper.eq("isDelete", 0);
        
        return queryWrapper;
    }

    @Override
    public Map<String, Object> getCourseRatingStats(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        
        // 查询已发布的评论
        QueryWrapper<CourseReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("courseId", courseId);
        queryWrapper.eq("status", 1); // 只统计已发布的评论
        queryWrapper.eq("isDelete", 0);
        queryWrapper.select("COUNT(*) as count", "AVG(rating) as avgRating");
        
        Map<String, Object> statMap = this.getMap(queryWrapper);
        
        // 如果没有评论，返回默认值
        if (statMap == null || statMap.isEmpty()) {
            statMap = new HashMap<>();
            statMap.put("count", 0);
            statMap.put("avgRating", 0);
        }
        
        return statMap;
    }

    @Override
    public CourseReviewVO getReviewVO(CourseReview courseReview) {
        if (courseReview == null) {
            return null;
        }
        
        CourseReviewVO reviewVO = new CourseReviewVO();
        
        // 复制基本属性
        reviewVO.setId(courseReview.getId());
        reviewVO.setUserId(courseReview.getUserId());
        reviewVO.setCourseId(courseReview.getCourseId());
        reviewVO.setContent(courseReview.getContent());
        reviewVO.setRating(courseReview.getRating());
        reviewVO.setLikeCount(courseReview.getLikeCount());
        reviewVO.setReplyCount(courseReview.getReplyCount());
        reviewVO.setAdminReply(courseReview.getAdminReply());
        reviewVO.setAdminReplyTime(courseReview.getAdminReplyTime());
        reviewVO.setStatus(courseReview.getStatus());
        reviewVO.setCreateTime(courseReview.getCreateTime());
        reviewVO.setUpdateTime(courseReview.getUpdateTime());
        
        // 获取用户信息
        User user = userService.getById(courseReview.getUserId());
        if (user != null) {
            reviewVO.setUserName(user.getUserName());
            reviewVO.setUserAvatar(user.getUserAvatar());
        }
        
        // 获取课程信息
        Course course = courseService.getById(courseReview.getCourseId());
        if (course != null) {
            reviewVO.setCourseTitle(course.getTitle());
        }
        
        return reviewVO;
    }

    @Override
    public List<CourseReviewVO> getReviewVO(List<CourseReview> reviewList) {
        if (reviewList == null || reviewList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有用户ID
        Set<Long> userIds = reviewList.stream()
                .map(CourseReview::getUserId)
                .collect(Collectors.toSet());
        
        // 批量查询用户信息
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        }
        
        // 获取所有课程ID
        Set<Long> courseIds = reviewList.stream()
                .map(CourseReview::getCourseId)
                .collect(Collectors.toSet());
        
        // 批量查询课程信息
        Map<Long, Course> courseMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            List<Course> courses = courseService.listByIds(courseIds);
            courseMap = courses.stream().collect(Collectors.toMap(Course::getId, course -> course));
        }
        
        // 转换为VO
        final Map<Long, User> finalUserMap = userMap;
        final Map<Long, Course> finalCourseMap = courseMap;
        
        return reviewList.stream().map(review -> {
            CourseReviewVO reviewVO = new CourseReviewVO();
            
            // 复制基本属性
            reviewVO.setId(review.getId());
            reviewVO.setUserId(review.getUserId());
            reviewVO.setCourseId(review.getCourseId());
            reviewVO.setContent(review.getContent());
            reviewVO.setRating(review.getRating());
            reviewVO.setLikeCount(review.getLikeCount());
            reviewVO.setReplyCount(review.getReplyCount());
            reviewVO.setAdminReply(review.getAdminReply());
            reviewVO.setAdminReplyTime(review.getAdminReplyTime());
            reviewVO.setStatus(review.getStatus());
            reviewVO.setCreateTime(review.getCreateTime());
            reviewVO.setUpdateTime(review.getUpdateTime());
            
            // 设置用户信息
            User user = finalUserMap.get(review.getUserId());
            if (user != null) {
                reviewVO.setUserName(user.getUserName());
                reviewVO.setUserAvatar(user.getUserAvatar());
            }
            
            // 设置课程信息
            Course course = finalCourseMap.get(review.getCourseId());
            if (course != null) {
                reviewVO.setCourseTitle(course.getTitle());
            }
            
            return reviewVO;
        }).collect(Collectors.toList());
    }
    
    /**
     * 更新课程评分统计
     *
     * @param courseId 课程ID
     */
    private void updateCourseRating(Long courseId) {
        Map<String, Object> ratingStats = getCourseRatingStats(courseId);
        
        Long count = ((Number) ratingStats.get("count")).longValue();
        Double avgRating = ((Number) ratingStats.get("avgRating")).doubleValue();
        
        // 四舍五入到一位小数
        BigDecimal ratingScore = BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP);
        
        // 更新课程评分
        courseService.updateCourseRating(courseId, ratingScore, count.intValue());
    }
} 
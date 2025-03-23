package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.CourseReview;
import com.ubanillx.smartclass.model.vo.CourseReviewVO;

import java.util.List;
import java.util.Map;

/**
* @author liulo
* @description 针对表【course_review(课程评价)】的数据库操作Service
* @createDate 2025-03-21 15:14:50
*/
public interface CourseReviewService extends IService<CourseReview> {

    /**
     * 添加课程评论
     *
     * @param courseReview 课程评论
     * @param userId 用户ID
     * @return 评论ID
     */
    long addCourseReview(CourseReview courseReview, Long userId);
    
    /**
     * 根据课程ID获取评论列表
     *
     * @param courseId 课程ID
     * @param current 当前页
     * @param pageSize 每页数量
     * @return 评论列表
     */
    Page<CourseReviewVO> getReviewsByCourseId(Long courseId, long current, long pageSize);
    
    /**
     * 对评论进行点赞
     *
     * @param reviewId 评论ID
     * @return 是否成功
     */
    boolean likeReview(Long reviewId);
    
    /**
     * 管理员回复评论
     *
     * @param reviewId 评论ID
     * @param replyContent 回复内容
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean replyReview(Long reviewId, String replyContent, Long adminId);
    
    /**
     * 修改评论状态
     *
     * @param reviewId 评论ID
     * @param status 状态：0-待审核，1-已发布，2-已拒绝
     * @return 是否成功
     */
    boolean updateReviewStatus(Long reviewId, Integer status);
    
    /**
     * 获取查询条件
     *
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 查询条件
     */
    QueryWrapper<CourseReview> getQueryWrapper(Long courseId, Long userId);
    
    /**
     * 获取课程评分统计
     *
     * @param courseId 课程ID
     * @return 统计结果，包含平均分和评分人数
     */
    Map<String, Object> getCourseRatingStats(Long courseId);
    
    /**
     * 将CourseReview转为CourseReviewVO
     *
     * @param courseReview 课程评论
     * @return 视图对象
     */
    CourseReviewVO getReviewVO(CourseReview courseReview);
    
    /**
     * 将CourseReview列表转为CourseReviewVO列表
     *
     * @param reviewList 课程评论列表
     * @return 视图对象列表
     */
    List<CourseReviewVO> getReviewVO(List<CourseReview> reviewList);
}

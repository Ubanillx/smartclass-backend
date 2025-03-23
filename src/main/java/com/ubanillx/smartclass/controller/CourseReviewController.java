package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.entity.CourseReview;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.CourseReviewVO;
import com.ubanillx.smartclass.service.CourseReviewService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 课程评论接口
 */
@RestController
@RequestMapping("course/review")
@Slf4j
public class CourseReviewController {

    @Resource
    private CourseReviewService courseReviewService;

    @Resource
    private UserService userService;

    /**
     * 创建课程评论
     *
     * @param courseReview
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addReview(@RequestBody CourseReview courseReview, HttpServletRequest request) {
        if (courseReview == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        long id = courseReviewService.addCourseReview(courseReview, loginUser.getId());
        return ResultUtils.success(id);
    }

    /**
     * 删除课程评论
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteReview(@RequestParam("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        CourseReview oldCourseReview = courseReviewService.getById(id);
        ThrowUtils.throwIf(oldCourseReview == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        User loginUser = userService.getLoginUser(request);
        if (!oldCourseReview.getUserId().equals(loginUser.getId()) 
                && !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = courseReviewService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新评论状态（管理员）
     *
     * @param reviewId
     * @param status
     * @param request
     * @return
     */
    @PostMapping("/admin/status")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateReviewStatus(
            @RequestParam("reviewId") Long reviewId,
            @RequestParam("status") Integer status,
            HttpServletRequest request) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态值不合法");
        }
        
        boolean result = courseReviewService.updateReviewStatus(reviewId, status);
        return ResultUtils.success(result);
    }

    /**
     * 管理员回复评论
     *
     * @param reviewId
     * @param replyContent
     * @param request
     * @return
     */
    @PostMapping("/admin/reply")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> replyReview(
            @RequestParam("reviewId") Long reviewId,
            @RequestParam("replyContent") String replyContent,
            HttpServletRequest request) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        if (replyContent == null || replyContent.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean result = courseReviewService.replyReview(reviewId, replyContent, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 点赞评论
     *
     * @param reviewId
     * @param request
     * @return
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> likeReview(@RequestParam("reviewId") Long reviewId, HttpServletRequest request) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 登录校验
        userService.getLoginUser(request);
        
        boolean result = courseReviewService.likeReview(reviewId);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取评论
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<CourseReviewVO> getReviewById(@RequestParam("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        CourseReview courseReview = courseReviewService.getById(id);
        if (courseReview == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        CourseReviewVO reviewVO = courseReviewService.getReviewVO(courseReview);
        return ResultUtils.success(reviewVO);
    }

    /**
     * 分页获取课程评论列表
     *
     * @param courseId
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<CourseReviewVO>> listReviewsByPage(
            @RequestParam("courseId") Long courseId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<CourseReviewVO> reviewPage = courseReviewService.getReviewsByCourseId(courseId, current, pageSize);
        return ResultUtils.success(reviewPage);
    }

    /**
     * 获取课程评分统计
     *
     * @param courseId
     * @return
     */
    @GetMapping("/stats")
    public BaseResponse<Map<String, Object>> getRatingStats(@RequestParam("courseId") Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Map<String, Object> stats = courseReviewService.getCourseRatingStats(courseId);
        return ResultUtils.success(stats);
    }
} 
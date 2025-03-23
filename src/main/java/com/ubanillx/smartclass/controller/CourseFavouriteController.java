package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.dto.course.CourseFavourAddRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.CourseFavouriteService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 课程收藏接口
 */
@RestController
@RequestMapping("course/favourite")
@Slf4j
public class CourseFavouriteController {

    @Resource
    private CourseFavouriteService courseFavouriteService;

    @Resource
    private UserService userService;

    /**
     * 收藏课程
     *
     * @param favourAddRequest 收藏请求
     * @param request HTTP请求
     * @return 收藏ID
     */
    @PostMapping("/add")
    public BaseResponse<Long> favourCourse(@RequestBody CourseFavourAddRequest favourAddRequest,
                                        HttpServletRequest request) {
        if (favourAddRequest == null || favourAddRequest.getCourseId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能收藏
        User loginUser = userService.getLoginUser(request);
        Long courseId = favourAddRequest.getCourseId();
        long favourId = courseFavouriteService.favourCourse(loginUser.getId(), courseId);
        return ResultUtils.success(favourId);
    }

    /**
     * 取消收藏
     *
     * @param courseId 课程ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/cancel")
    public BaseResponse<Boolean> unfavourCourse(@RequestParam Long courseId,
                                              HttpServletRequest request) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能取消收藏
        User loginUser = userService.getLoginUser(request);
        boolean result = courseFavouriteService.unfavourCourse(loginUser.getId(), courseId);
        return ResultUtils.success(result);
    }

    /**
     * 判断当前用户是否已收藏
     *
     * @param courseId 课程ID
     * @param request HTTP请求
     * @return 是否已收藏
     */
    @GetMapping("/check")
    public BaseResponse<Boolean> checkFavoured(@RequestParam Long courseId,
                                             HttpServletRequest request) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = courseFavouriteService.hasFavoured(loginUser.getId(), courseId);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户收藏的课程ID列表
     *
     * @param request HTTP请求
     * @return 课程ID列表
     */
    @GetMapping("/my/list")
    public BaseResponse<List<Long>> getMyFavourList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<Long> favouriteCourseIds = courseFavouriteService.getUserFavouriteCourseIds(loginUser.getId());
        return ResultUtils.success(favouriteCourseIds);
    }

    /**
     * 获取用户收藏数量
     *
     * @param request HTTP请求
     * @return 收藏数量
     */
    @GetMapping("/my/count")
    public BaseResponse<Long> getMyFavourCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long count = courseFavouriteService.getUserFavouriteCount(loginUser.getId());
        return ResultUtils.success(count);
    }
} 
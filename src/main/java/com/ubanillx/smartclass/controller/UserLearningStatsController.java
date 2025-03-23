package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserLearningStats;
import com.ubanillx.smartclass.service.UserLearningStatsService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 用户学习统计接口
 */
@RestController
@RequestMapping("/user/stats")
@Slf4j
public class UserLearningStatsController {

    @Resource
    private UserLearningStatsService userLearningStatsService;

    @Resource
    private UserService userService;

    /**
     * 获取当前用户的学习统计信息
     *
     * @param request
     * @return
     */
    @GetMapping("/my")
    public BaseResponse<UserLearningStats> getMyLearningStats(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        UserLearningStats userLearningStats = userLearningStatsService.getUserLearningStatsByUserId(loginUser.getId());
        return ResultUtils.success(userLearningStats);
    }
    
    /**
     * 获取指定用户的学习统计信息（管理员）
     *
     * @param userId
     * @param request
     * @return
     */
    @GetMapping("/admin/user")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserLearningStats> getUserLearningStatsByAdmin(@RequestParam Long userId, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不合法");
        UserLearningStats userLearningStats = userLearningStatsService.getUserLearningStatsByUserId(userId);
        return ResultUtils.success(userLearningStats);
    }
    
    /**
     * 用户打卡
     *
     * @param request
     * @return
     */
    @PostMapping("/check-in")
    public BaseResponse<Map<String, Object>> userCheckIn(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Map<String, Object> result = userLearningStatsService.userCheckIn(loginUser.getId());
        return ResultUtils.success(result);
    }
    
    /**
     * 增加用户积分（管理员）
     *
     * @param userId
     * @param points
     * @param request
     * @return
     */
    @PostMapping("/admin/add/points")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addUserPointsByAdmin(@RequestParam Long userId, @RequestParam Integer points, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不合法");
        ThrowUtils.throwIf(points == null || points <= 0, ErrorCode.PARAMS_ERROR, "积分必须为正数");
        
        boolean result = userLearningStatsService.addUserPoints(userId, points);
        return ResultUtils.success(result);
    }
    
    /**
     * 增加用户经验值（管理员）
     *
     * @param userId
     * @param experience
     * @param request
     * @return
     */
    @PostMapping("/admin/add/experience")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addUserExperienceByAdmin(@RequestParam Long userId, @RequestParam Integer experience, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不合法");
        ThrowUtils.throwIf(experience == null || experience <= 0, ErrorCode.PARAMS_ERROR, "经验值必须为正数");
        
        boolean result = userLearningStatsService.addUserExperience(userId, experience);
        return ResultUtils.success(result);
    }
    
    /**
     * 增加用户徽章数量（管理员）
     *
     * @param userId
     * @param count
     * @param request
     * @return
     */
    @PostMapping("/admin/add/badge")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addUserBadgeByAdmin(@RequestParam Long userId, @RequestParam(required = false) Integer count, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不合法");
        
        boolean result = userLearningStatsService.addUserBadgeCount(userId, count);
        return ResultUtils.success(result);
    }
    
    /**
     * 更新用户学习天数
     *
     * @param request
     * @return
     */
    @PostMapping("/update/learning-day")
    public BaseResponse<Boolean> updateLearningDay(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = userLearningStatsService.updateLearningDays(loginUser.getId(), new Date());
        return ResultUtils.success(result);
    }
    
    /**
     * 更新用户等级（管理员）
     *
     * @param userId
     * @param newLevel
     * @param nextLevelExp
     * @param request
     * @return
     */
    @PostMapping("/admin/update/level")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserLevelByAdmin(@RequestParam Long userId, @RequestParam Integer newLevel, 
                                                    @RequestParam(required = false) Integer nextLevelExp, 
                                                    HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不合法");
        ThrowUtils.throwIf(newLevel == null || newLevel < 0, ErrorCode.PARAMS_ERROR, "等级不能为负数");
        
        boolean result = userLearningStatsService.updateUserLevel(userId, newLevel, nextLevelExp);
        return ResultUtils.success(result);
    }
    
    /**
     * 初始化用户学习统计（管理员）
     *
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/admin/init")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> initUserLearningStatsByAdmin(@RequestParam Long userId, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不合法");
        
        long statsId = userLearningStatsService.initUserLearningStats(userId);
        return ResultUtils.success(statsId);
    }
} 
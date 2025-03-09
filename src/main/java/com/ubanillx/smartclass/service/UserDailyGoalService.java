package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserDailyGoal;

import java.util.Date;

/**
* @author liulo
* @description 针对表【user_daily_goal(用户每日目标)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface UserDailyGoalService extends BaseRelationService<UserDailyGoal> {

    /**
     * 获取用户当日学习目标
     * @param userId 用户ID
     * @return 用户每日目标
     */
    UserDailyGoal getUserDailyGoal(Long userId);
    
    /**
     * 获取用户指定日期的学习目标
     * @param userId 用户ID
     * @param date 日期
     * @return 用户每日目标
     */
    UserDailyGoal getUserDailyGoalByDate(Long userId, Date date);
    
    /**
     * 创建用户每日学习目标
     * @param userId 用户ID
     * @param targetMinutes 目标学习时间（分钟）
     * @return 用户每日目标
     */
    UserDailyGoal createUserDailyGoal(Long userId, Integer targetMinutes);
    
    /**
     * 更新用户学习时间
     * @param userId 用户ID
     * @param minutesSpent 学习时间（分钟）
     * @return 是否完成目标
     */
    boolean updateLearningTime(Long userId, int minutesSpent);
    
    /**
     * 检查用户每日目标是否完成
     * @param userId 用户ID
     * @return 是否完成
     */
    boolean isDailyGoalCompleted(Long userId);
    
    /**
     * 获取用户连续完成每日目标的天数
     * @param userId 用户ID
     * @return 连续天数
     */
    int getConsecutiveCompletedDays(Long userId);
}

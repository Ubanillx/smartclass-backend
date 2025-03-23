package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserLearningStats;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.Map;

/**
* @author liulo
* @description 针对表【user_learning_stats(用户学习统计)】的数据库操作Service
* @createDate 2025-03-21 15:14:50
*/
public interface UserLearningStatsService extends IService<UserLearningStats> {

    /**
     * 获取用户学习统计信息
     * @param userId 用户ID
     * @return 用户学习统计信息
     */
    UserLearningStats getUserLearningStatsByUserId(Long userId);
    
    /**
     * 初始化用户学习统计数据
     * @param userId 用户ID
     * @return 新创建的用户学习统计ID
     */
    long initUserLearningStats(Long userId);
    
    /**
     * 增加用户经验值
     * @param userId 用户ID
     * @param experience 经验值
     * @return 是否更新成功
     */
    boolean addUserExperience(Long userId, Integer experience);
    
    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param points 积分
     * @return 是否更新成功
     */
    boolean addUserPoints(Long userId, Integer points);
    
    /**
     * 用户打卡
     * @param userId 用户ID
     * @return 打卡结果，包含连续打卡天数、奖励积分等信息
     */
    Map<String, Object> userCheckIn(Long userId);
    
    /**
     * 更新用户学习天数
     * @param userId 用户ID
     * @param learningDate 学习日期
     * @return 是否更新成功
     */
    boolean updateLearningDays(Long userId, Date learningDate);
    
    /**
     * 更新用户等级
     * @param userId 用户ID
     * @param newLevel 新等级
     * @param nextLevelExp 下一级所需经验值
     * @return 是否更新成功
     */
    boolean updateUserLevel(Long userId, Integer newLevel, Integer nextLevelExp);
    
    /**
     * 增加用户徽章数量
     * @param userId 用户ID
     * @param count 增加数量，默认为1
     * @return 是否更新成功
     */
    boolean addUserBadgeCount(Long userId, Integer count);
}

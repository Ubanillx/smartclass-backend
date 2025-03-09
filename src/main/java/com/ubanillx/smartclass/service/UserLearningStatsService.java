package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserLearningStats;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liulo
* @description 针对表【user_learning_stats(用户学习统计)】的数据库操作Service
* @createDate 2025-02-27 21:52:02
*/
public interface UserLearningStatsService extends IService<UserLearningStats> {

    /**
     * 获取用户学习统计数据
     * @param userId 用户ID
     * @return 学习统计数据
     */
    UserLearningStats getUserLearningStats(Long userId);
    
    /**
     * 创建用户学习统计数据
     * @param userId 用户ID
     * @return 学习统计数据
     */
    UserLearningStats createUserLearningStats(Long userId);
    
    /**
     * 更新用户学习统计数据
     * @param userId 用户ID
     * @param minutesSpent 学习时间（分钟）
     * @param wordsLearned 学习单词数
     * @param articlesRead 阅读文章数
     * @return 是否更新成功
     */
    boolean updateLearningStats(Long userId, int minutesSpent, int wordsLearned, int articlesRead);
    
    /**
     * 获取用户连续学习天数
     * @param userId 用户ID
     * @return 连续学习天数
     */
    int getConsecutiveLearningDays(Long userId);
    
    /**
     * 增加用户经验值
     * @param userId 用户ID
     * @param experience 经验值
     * @return 是否升级
     */
    boolean addExperience(Long userId, int experience);
    
    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param points 积分
     * @return 是否成功
     */
    boolean addPoints(Long userId, int points);
}

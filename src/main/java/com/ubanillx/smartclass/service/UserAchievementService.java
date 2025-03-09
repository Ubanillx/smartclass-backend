package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserAchievement;

import java.util.List;

/**
* @author liulo
* @description 针对表【user_achievement(用户成就)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface UserAchievementService extends BaseRelationService<UserAchievement> {

    /**
     * 获取用户成就列表
     * @param userId 用户ID
     * @return 用户成就列表
     */
    List<UserAchievement> getUserAchievements(Long userId);
    
    /**
     * 获取用户已完成的成就列表
     * @param userId 用户ID
     * @return 已完成的成就列表
     */
    List<UserAchievement> getUserCompletedAchievements(Long userId);
    
    /**
     * 获取用户未完成的成就列表
     * @param userId 用户ID
     * @return 未完成的成就列表
     */
    List<UserAchievement> getUserUncompletedAchievements(Long userId);
    
    /**
     * 获取用户特定成就记录
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @return 用户成就记录
     */
    UserAchievement getUserAchievement(Long userId, Long achievementId);
}

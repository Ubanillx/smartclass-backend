package com.ubanillx.smartclass.manager;

import com.ubanillx.smartclass.model.entity.Achievement;
import com.ubanillx.smartclass.model.entity.AchievementMilestone;
import com.ubanillx.smartclass.model.entity.UserAchievement;
import com.ubanillx.smartclass.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 成就管理类
 */
@Component
public class AchievementManager {

    @Autowired
    private AchievementService achievementService;

    /**
     * 检查成就是否可用
     * @param achievement 成就
     * @return 是否可用
     */
    public boolean isAchievementAvailable(Achievement achievement) {
        if (achievement == null) {
            return false;
        }
        // 检查是否被删除
        if (achievement.getIsDelete() != null && achievement.getIsDelete() == 1) {
            return false;
        }
        return true;
    }

    /**
     * 检查用户是否已获得成就
     * @param achievement 成就
     * @param userAchievements 用户成就列表
     * @return 是否已获得
     */
    public boolean hasUserAchieved(Achievement achievement, List<UserAchievement> userAchievements) {
        if (achievement == null || userAchievements == null || userAchievements.isEmpty()) {
            return false;
        }
        
        Long achievementId = achievement.getId();
        for (UserAchievement userAchievement : userAchievements) {
            if (achievementId.equals(userAchievement.getAchievementId()) && 
                userAchievement.getIsCompleted() != null && 
                userAchievement.getIsCompleted() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取成就当前进度
     * @param achievement 成就
     * @param userAchievement 用户成就
     * @return 进度百分比（0-100）
     */
    public int getAchievementProgress(Achievement achievement, UserAchievement userAchievement) {
        if (achievement == null || userAchievement == null) {
            return 0;
        }
        
        // 如果已完成，直接返回100%
        if (userAchievement.getIsCompleted() != null && userAchievement.getIsCompleted() == 1) {
            return 100;
        }
        
        // 获取当前进度和目标值
        Integer currentProgress = userAchievement.getProgress();
        Integer targetValue = userAchievement.getProgressMax();
        
        if (currentProgress == null || targetValue == null || targetValue == 0) {
            return 0;
        }
        
        // 计算百分比，最大为100%
        int progressPercentage = (int) (((double) currentProgress / targetValue) * 100);
        return Math.min(progressPercentage, 100);
    }

    /**
     * 检查是否达成成就里程碑
     * @param userAchievement 用户成就
     * @param milestones 里程碑列表
     * @return 达成的里程碑，如果没有达成则返回null
     */
    public AchievementMilestone checkMilestoneAchieved(UserAchievement userAchievement, List<AchievementMilestone> milestones) {
        if (userAchievement == null || milestones == null || milestones.isEmpty()) {
            return null;
        }
        
        Integer currentProgress = userAchievement.getProgress();
        if (currentProgress == null) {
            return null;
        }
        
        // 按照里程碑所需点数从大到小排序，找到第一个达成的里程碑
        milestones.sort((m1, m2) -> {
            if (m1.getRequiredPoints() == null || m2.getRequiredPoints() == null) {
                return 0;
            }
            return m2.getRequiredPoints().compareTo(m1.getRequiredPoints());
        });
        
        for (AchievementMilestone milestone : milestones) {
            Integer requiredPoints = milestone.getRequiredPoints();
            if (requiredPoints != null && currentProgress >= requiredPoints) {
                return milestone;
            }
        }
        
        return null;
    }

    /**
     * 更新用户成就进度
     * @param userAchievement 用户成就
     * @param achievement 成就
     * @param progressIncrement 进度增量
     * @return 是否完成成就
     */
    public boolean updateAchievementProgress(UserAchievement userAchievement, Achievement achievement, int progressIncrement) {
        if (userAchievement == null || achievement == null) {
            return false;
        }
        
        // 如果已经完成，不再更新
        if (userAchievement.getIsCompleted() != null && userAchievement.getIsCompleted() == 1) {
            return true;
        }
        
        // 更新进度
        Integer currentProgress = userAchievement.getProgress();
        if (currentProgress == null) {
            currentProgress = 0;
        }
        
        int newProgress = currentProgress + progressIncrement;
        userAchievement.setProgress(newProgress);
        
        // 获取目标值
        Integer targetValue = userAchievement.getProgressMax();
        if (targetValue == null) {
            // 如果用户成就中没有设置目标值，则使用成就的条件值
            targetValue = achievement.getConditionValue();
            userAchievement.setProgressMax(targetValue);
        }
        
        // 更新进度百分比
        if (targetValue != null && targetValue > 0) {
            int progressPercent = Math.min((int) (((double) newProgress / targetValue) * 100), 100);
            userAchievement.setProgressPercent(progressPercent);
        }
        
        // 检查是否完成
        if (targetValue != null && newProgress >= targetValue) {
            userAchievement.setIsCompleted(1);
            userAchievement.setCompletedTime(new Date());
            return true;
        }
        
        return false;
    }
    
    /**
     * 创建用户成就记录
     * @param userId 用户ID
     * @param achievement 成就
     * @return 用户成就记录
     */
    public UserAchievement createUserAchievement(Long userId, Achievement achievement) {
        if (userId == null || achievement == null) {
            return null;
        }
        
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUserId(userId);
        userAchievement.setAchievementId(achievement.getId());
        userAchievement.setProgress(0);
        userAchievement.setProgressMax(achievement.getConditionValue());
        userAchievement.setProgressPercent(0);
        userAchievement.setIsCompleted(0);
        userAchievement.setIsRewarded(0);
        userAchievement.setCreateTime(new Date());
        userAchievement.setUpdateTime(new Date());
        
        return userAchievement;
    }
    
    /**
     * 发放成就奖励
     * @param userAchievement 用户成就
     * @param achievement 成就
     * @return 是否发放成功
     */
    public boolean grantAchievementReward(UserAchievement userAchievement, Achievement achievement) {
        if (userAchievement == null || achievement == null) {
            return false;
        }
        
        // 检查是否已完成且未发放奖励
        if (userAchievement.getIsCompleted() == null || userAchievement.getIsCompleted() != 1 ||
            userAchievement.getIsRewarded() != null && userAchievement.getIsRewarded() == 1) {
            return false;
        }
        
        // TODO: 根据成就的奖励类型和奖励值发放奖励
        // 这里需要根据具体业务逻辑实现，如积分奖励、徽章奖励等
        
        // 标记为已发放奖励
        userAchievement.setIsRewarded(1);
        userAchievement.setRewardTime(new Date());
        
        return true;
    }
    
    /**
     * 根据条件类型获取成就列表
     * @param conditionType 条件类型
     * @return 成就列表
     */
    public List<Achievement> getAchievementsByConditionType(String conditionType) {
        if (conditionType == null || conditionType.isEmpty()) {
            return null;
        }
        
        Achievement condition = new Achievement();
        condition.setConditionType(conditionType);
        condition.setIsDelete(0);
        
        return achievementService.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getConditionType, conditionType)
                .eq(Achievement::getIsDelete, 0)
                .orderByAsc(Achievement::getSort));
    }
} 
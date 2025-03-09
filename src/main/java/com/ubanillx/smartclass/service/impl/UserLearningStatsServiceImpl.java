package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.manager.LearningManager;
import com.ubanillx.smartclass.mapper.UserLearningStatsMapper;
import com.ubanillx.smartclass.model.entity.UserLearningStats;
import com.ubanillx.smartclass.service.UserLearningStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* @author liulo
* @description 针对表【user_learning_stats(用户学习统计)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:02
*/
@Service
public class UserLearningStatsServiceImpl extends ServiceImpl<UserLearningStatsMapper, UserLearningStats>
    implements UserLearningStatsService {

    @Autowired
    private LearningManager learningManager;
    
    @Override
    public UserLearningStats getUserLearningStats(Long userId) {
        if (userId == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserLearningStats> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLearningStats::getUserId, userId);
        
        UserLearningStats stats = getOne(queryWrapper);
        if (stats == null) {
            // 如果不存在，创建一个新的统计记录
            stats = createUserLearningStats(userId);
        }
        
        return stats;
    }
    
    @Override
    public UserLearningStats createUserLearningStats(Long userId) {
        if (userId == null) {
            return null;
        }
        
        // 检查是否已存在
        LambdaQueryWrapper<UserLearningStats> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLearningStats::getUserId, userId);
        
        UserLearningStats existingStats = getOne(queryWrapper);
        if (existingStats != null) {
            return existingStats;
        }
        
        // 创建新的统计记录
        UserLearningStats stats = new UserLearningStats();
        stats.setUserId(userId);
        stats.setLevel(1);
        stats.setExperience(0);
        stats.setNextLevelExp(100); // 初始下一级所需经验值
        stats.setLearningDays(0);
        stats.setContinuousCheckIn(0); // 使用正确的字段名
        stats.setTotalPoints(0);
        stats.setTotalBadges(0);
        stats.setLastCheckInTime(new Date()); // 使用正确的字段名
        stats.setCreateTime(new Date());
        stats.setUpdateTime(new Date());
        
        save(stats);
        return stats;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLearningStats(Long userId, int minutesSpent, int wordsLearned, int articlesRead) {
        if (userId == null) {
            return false;
        }
        
        // 获取用户学习统计数据
        UserLearningStats stats = getUserLearningStats(userId);
        if (stats == null) {
            return false;
        }
        
        // 使用LearningManager更新统计数据
        learningManager.updateLearningStats(stats, minutesSpent, wordsLearned, articlesRead);
        
        // 更新学习天数
        Integer learningDays = stats.getLearningDays();
        if (learningDays == null) {
            learningDays = 0;
        }
        stats.setLearningDays(learningDays + 1);
        
        // 更新数据库
        stats.setUpdateTime(new Date());
        return updateById(stats);
    }
    
    @Override
    public int getConsecutiveLearningDays(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        UserLearningStats stats = getUserLearningStats(userId);
        if (stats == null) {
            return 0;
        }
        
        Integer consecutiveDays = stats.getContinuousCheckIn(); // 使用正确的字段名
        return consecutiveDays != null ? consecutiveDays : 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addExperience(Long userId, int experience) {
        if (userId == null || experience <= 0) {
            return false;
        }
        
        // 获取用户学习统计数据
        UserLearningStats stats = getUserLearningStats(userId);
        if (stats == null) {
            return false;
        }
        
        // 更新经验值
        Integer currentExp = stats.getExperience();
        Integer nextLevelExp = stats.getNextLevelExp();
        Integer currentLevel = stats.getLevel();
        
        if (currentExp == null) {
            currentExp = 0;
        }
        if (nextLevelExp == null) {
            nextLevelExp = 100;
        }
        if (currentLevel == null) {
            currentLevel = 1;
        }
        
        int newExp = currentExp + experience;
        stats.setExperience(newExp);
        
        // 检查是否升级
        boolean isLevelUp = false;
        while (newExp >= nextLevelExp) {
            // 升级
            currentLevel++;
            newExp -= nextLevelExp;
            
            // 计算下一级所需经验值（每级增加20%）
            nextLevelExp = (int) (nextLevelExp * 1.2);
            
            isLevelUp = true;
        }
        
        if (isLevelUp) {
            stats.setLevel(currentLevel);
            stats.setExperience(newExp);
            stats.setNextLevelExp(nextLevelExp);
        }
        
        // 更新数据库
        stats.setUpdateTime(new Date());
        updateById(stats);
        
        return isLevelUp;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, int points) {
        if (userId == null || points <= 0) {
            return false;
        }
        
        // 获取用户学习统计数据
        UserLearningStats stats = getUserLearningStats(userId);
        if (stats == null) {
            return false;
        }
        
        // 更新积分
        Integer currentPoints = stats.getTotalPoints();
        if (currentPoints == null) {
            currentPoints = 0;
        }
        
        stats.setTotalPoints(currentPoints + points);
        
        // 更新数据库
        stats.setUpdateTime(new Date());
        return updateById(stats);
    }
}





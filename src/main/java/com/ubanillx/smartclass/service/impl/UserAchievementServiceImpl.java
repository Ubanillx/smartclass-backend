package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubanillx.smartclass.mapper.UserAchievementMapper;
import com.ubanillx.smartclass.model.entity.UserAchievement;
import com.ubanillx.smartclass.service.UserAchievementService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liulo
* @description 针对表【user_achievement(用户成就)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserAchievementServiceImpl extends BaseRelationServiceImpl<UserAchievementMapper, UserAchievement>
    implements UserAchievementService {
    
    public UserAchievementServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("achievementId");
    }
    
    @Override
    public List<UserAchievement> getUserAchievements(Long userId) {
        if (userId == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserAchievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAchievement::getUserId, userId);
        
        return list(queryWrapper);
    }
    
    @Override
    public List<UserAchievement> getUserCompletedAchievements(Long userId) {
        if (userId == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserAchievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAchievement::getUserId, userId)
                .eq(UserAchievement::getIsCompleted, 1);
        
        return list(queryWrapper);
    }
    
    @Override
    public List<UserAchievement> getUserUncompletedAchievements(Long userId) {
        if (userId == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserAchievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAchievement::getUserId, userId)
                .eq(UserAchievement::getIsCompleted, 0);
        
        return list(queryWrapper);
    }
    
    @Override
    public UserAchievement getUserAchievement(Long userId, Long achievementId) {
        if (userId == null || achievementId == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserAchievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAchievement::getUserId, userId)
                .eq(UserAchievement::getAchievementId, achievementId);
        
        return getOne(queryWrapper);
    }
}





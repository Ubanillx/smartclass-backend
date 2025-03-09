package com.ubanillx.smartclass.service.impl;

import com.ubanillx.smartclass.mapper.UserAchievementDisplayMapper;
import com.ubanillx.smartclass.model.entity.UserAchievementDisplay;
import com.ubanillx.smartclass.service.UserAchievementDisplayService;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【user_achievement_display(用户成就展示)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserAchievementDisplayServiceImpl extends BaseRelationServiceImpl<UserAchievementDisplayMapper, UserAchievementDisplay>
    implements UserAchievementDisplayService {
    
    public UserAchievementDisplayServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("achievementDisplayId");
    }
}





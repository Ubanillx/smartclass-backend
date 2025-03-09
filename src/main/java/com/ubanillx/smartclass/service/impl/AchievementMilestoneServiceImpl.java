package com.ubanillx.smartclass.service.impl;

import com.ubanillx.smartclass.mapper.AchievementMilestoneMapper;
import com.ubanillx.smartclass.model.entity.AchievementMilestone;
import com.ubanillx.smartclass.service.AchievementMilestoneService;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【achievement_milestone(成就里程碑)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class AchievementMilestoneServiceImpl extends BaseRelationServiceImpl<AchievementMilestoneMapper, AchievementMilestone>
    implements AchievementMilestoneService {
    
    public AchievementMilestoneServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("achievementId");
    }
}





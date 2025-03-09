package com.ubanillx.smartclass.service.impl;

import com.ubanillx.smartclass.mapper.UserMilestoneMapper;
import com.ubanillx.smartclass.model.entity.UserMilestone;
import com.ubanillx.smartclass.service.UserMilestoneService;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【user_milestone(用户里程碑)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserMilestoneServiceImpl extends BaseRelationServiceImpl<UserMilestoneMapper, UserMilestone>
    implements UserMilestoneService {
    
    public UserMilestoneServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("milestoneId");
    }
}





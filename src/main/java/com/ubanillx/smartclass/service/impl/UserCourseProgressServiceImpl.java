package com.ubanillx.smartclass.service.impl;

import com.ubanillx.smartclass.mapper.UserCourseProgressMapper;
import com.ubanillx.smartclass.model.entity.UserCourseProgress;
import com.ubanillx.smartclass.service.UserCourseProgressService;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【user_course_progress(用户学习进度)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserCourseProgressServiceImpl extends BaseRelationServiceImpl<UserCourseProgressMapper, UserCourseProgress>
    implements UserCourseProgressService {
    
    public UserCourseProgressServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("courseId");
    }
}





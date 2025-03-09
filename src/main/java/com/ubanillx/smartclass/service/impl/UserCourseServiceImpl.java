package com.ubanillx.smartclass.service.impl;

import com.ubanillx.smartclass.mapper.UserCourseMapper;
import com.ubanillx.smartclass.model.entity.UserCourse;
import com.ubanillx.smartclass.service.UserCourseService;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【user_course(用户课程关联)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserCourseServiceImpl extends BaseRelationServiceImpl<UserCourseMapper, UserCourse>
    implements UserCourseService {
    
    public UserCourseServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("courseId");
    }
}





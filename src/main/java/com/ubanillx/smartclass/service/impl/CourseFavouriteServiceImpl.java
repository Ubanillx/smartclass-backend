package com.ubanillx.smartclass.service.impl;

import com.ubanillx.smartclass.mapper.CourseFavouriteMapper;
import com.ubanillx.smartclass.model.entity.CourseFavourite;
import com.ubanillx.smartclass.service.CourseFavouriteService;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【course_favourite(课程收藏)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class CourseFavouriteServiceImpl extends BaseRelationServiceImpl<CourseFavouriteMapper, CourseFavourite>
    implements CourseFavouriteService {
    
    public CourseFavouriteServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("courseId");
    }
}





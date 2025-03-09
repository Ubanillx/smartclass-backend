package com.ubanillx.smartclass.service.impl;

import com.ubanillx.smartclass.model.entity.AnnouncementRead;
import com.ubanillx.smartclass.service.AnnouncementReadService;
import com.ubanillx.smartclass.mapper.AnnouncementReadMapper;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【announcement_read(公告阅读记录)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class AnnouncementReadServiceImpl extends BaseRelationServiceImpl<AnnouncementReadMapper, AnnouncementRead>
    implements AnnouncementReadService {
    
    public AnnouncementReadServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("announcementId");
    }
}





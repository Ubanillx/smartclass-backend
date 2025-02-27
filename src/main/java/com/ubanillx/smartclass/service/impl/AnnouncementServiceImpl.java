package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.model.entity.Announcement;
import com.ubanillx.smartclass.service.AnnouncementService;
import com.ubanillx.smartclass.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【announcement(系统公告)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
    implements AnnouncementService{

}





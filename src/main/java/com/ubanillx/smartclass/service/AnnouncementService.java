package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.announcement.AnnouncementQueryRequest;
import com.ubanillx.smartclass.model.entity.Announcement;
import com.ubanillx.smartclass.model.vo.AnnouncementVO;

import java.util.List;

/**
* @author liulo
* @description 针对表【announcement(系统公告)】的数据库操作Service
* @createDate 2025-03-18 23:08:38
*/
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 创建公告
     *
     * @param announcement
     * @param adminId
     * @return
     */
    long addAnnouncement(Announcement announcement, Long adminId);

    /**
     * 获取查询条件
     *
     * @param announcementQueryRequest
     * @return
     */
    QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest);

    /**
     * 获取公告封装
     *
     * @param announcement
     * @return
     */
    AnnouncementVO getAnnouncementVO(Announcement announcement);

    /**
     * 获取公告列表封装
     *
     * @param announcementList
     * @return
     */
    List<AnnouncementVO> getAnnouncementVO(List<Announcement> announcementList);

    /**
     * 分页获取有效公告（未删除、已发布、在有效期内的公告）
     *
     * @param current
     * @param size
     * @return
     */
    Page<AnnouncementVO> listValidAnnouncements(long current, long size);

    /**
     * 增加公告查看次数
     *
     * @param id
     * @return
     */
    boolean increaseViewCount(Long id);

    /**
     * 已读公告
     *
     * @param announcementId
     * @param userId
     * @return
     */
    boolean readAnnouncement(Long announcementId, Long userId);

    /**
     * 检查用户是否已读公告
     *
     * @param announcementId
     * @param userId
     * @return
     */
    boolean hasReadAnnouncement(Long announcementId, Long userId);
}

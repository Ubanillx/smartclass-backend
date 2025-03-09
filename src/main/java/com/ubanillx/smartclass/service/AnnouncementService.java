package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ubanillx.smartclass.model.dto.annoucement.AnnouncementQueryRequest;
import com.ubanillx.smartclass.model.dto.user.UserQueryRequest;
import com.ubanillx.smartclass.model.entity.Achievement;
import com.ubanillx.smartclass.model.entity.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.LoginUserVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author liulo
* @description 针对表【announcement(系统公告)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface AnnouncementService extends IService<Announcement> {
    /**
     * 添加公告
     *
     * @param title   标题
     * @param content 内容
     * @param status  状态
     * @param request
     * @return
     */
    Announcement addAnnouncement(String title, String content, int status, HttpServletRequest request);


    /**
     * 更新公告
     *
     * @param title   标题
     * @param content 内容
     * @param status  状态
     * @param request
     * @return
     */
    Announcement updateAnnouncement(String title, String content, int status, HttpServletRequest request);


    /**
     * 删除公告
     *
     * @param id
     * @param request
     * @return 是否删除
     */
    boolean deleteAnnouncement(long id, HttpServletRequest request);

    /**
     * 查询公告
     *
     * @param AnnouncementQueryRequest
     * @return
     */
    QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest AnnouncementQueryRequest);




}

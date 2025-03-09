package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.config.WxOpenConfig;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.annoucement.AnnouncementAddRequest;
import com.ubanillx.smartclass.model.dto.annoucement.AnnouncementQueryRequest;
import com.ubanillx.smartclass.model.dto.annoucement.AnnouncementUpdateRequest;
import com.ubanillx.smartclass.model.dto.user.*;
import com.ubanillx.smartclass.model.entity.Announcement;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.LoginUserVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.AnnouncementService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static com.ubanillx.smartclass.service.impl.UserServiceImpl.SALT;


/**
 * 用户接口
 *

 */
@RestController
@RequestMapping("/announcement")
@Slf4j
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    /**
     * 创建公告
     *
     * @param announcementAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addAnnouncement(@RequestBody AnnouncementAddRequest announcementAddRequest, HttpServletRequest request) {
        if (announcementAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementAddRequest, announcement);
        // 校验
        ThrowUtils.throwIf(announcement.getTitle() == null, ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(announcement.getContent() == null, ErrorCode.PARAMS_ERROR, "内容不能为空");
        
        Announcement result = announcementService.addAnnouncement(
                announcement.getTitle(), 
                announcement.getContent(), 
                announcement.getStatus() != null ? announcement.getStatus() : 0, 
                request
        );
        
        return ResultUtils.success(result.getId());
    }

    /**
     * 删除公告
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAnnouncement(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = announcementService.deleteAnnouncement(deleteRequest.getId(), request);
        return ResultUtils.success(b);
    }

    /**
     * 更新公告
     *
     * @param announcementUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAnnouncement(@RequestBody AnnouncementUpdateRequest announcementUpdateRequest,
                                            HttpServletRequest request) {
        if (announcementUpdateRequest == null || announcementUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 先获取原有公告
        Announcement oldAnnouncement = announcementService.getById(announcementUpdateRequest.getId());
        if (oldAnnouncement == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 更新公告
        Announcement newAnnouncement = new Announcement();
        BeanUtils.copyProperties(announcementUpdateRequest, newAnnouncement);
        
        // 设置请求参数，因为service层需要从request中获取id
        request.setAttribute("id", announcementUpdateRequest.getId().toString());
        
        Announcement result = announcementService.updateAnnouncement(
                newAnnouncement.getTitle(),
                newAnnouncement.getContent(),
                newAnnouncement.getStatus() != null ? newAnnouncement.getStatus() : oldAnnouncement.getStatus(),
                request
        );
        
        return ResultUtils.success(result != null);
    }

    /**
     * 根据 id 获取公告
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Announcement> getAnnouncementById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = announcementService.getById(id);
        ThrowUtils.throwIf(announcement == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 可以在这里增加浏览量统计
        announcement.setViewCount(announcement.getViewCount() + 1);
        announcementService.updateById(announcement);
        
        return ResultUtils.success(announcement);
    }

    /**
     * 分页获取公告列表
     *
     * @param announcementQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Announcement>> listAnnouncementByPage(@RequestBody AnnouncementQueryRequest announcementQueryRequest,
                                                   HttpServletRequest request) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size),
                announcementService.getQueryWrapper(announcementQueryRequest));
                
        return ResultUtils.success(announcementPage);
    }
    
    /**
     * 获取最新公告列表（首页展示）
     *
     * @param request
     * @return
     */
    @GetMapping("/latest")
    public BaseResponse<List<Announcement>> getLatestAnnouncements(HttpServletRequest request) {
        // 创建一个查询，获取最新的且状态为"已发布"的公告
        AnnouncementQueryRequest queryRequest = new AnnouncementQueryRequest();
        queryRequest.setStatus(1); // 1-已发布
        
        // 按优先级降序、创建时间降序
        QueryWrapper<Announcement> queryWrapper = announcementService.getQueryWrapper(queryRequest);
        queryWrapper.orderByDesc("priority", "create_time");
        queryWrapper.last("limit 5"); // 限制只返回5条
        
        List<Announcement> announcementList = announcementService.list(queryWrapper);
        return ResultUtils.success(announcementList);
    }
    
    /**
     * 获取有效的公告（在展示时间范围内的公告）
     *
     * @param request
     * @return
     */
    @GetMapping("/active")
    public BaseResponse<List<Announcement>> getActiveAnnouncements(HttpServletRequest request) {
        Date now = new Date();
        
        // 创建一个查询，获取当前时间段内有效的且状态为"已发布"的公告
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 1-已发布
        queryWrapper.le("start_time", now); // 开始时间小于等于当前时间
        queryWrapper.ge("end_time", now);   // 结束时间大于等于当前时间
        queryWrapper.eq("is_delete", 0);    // 未删除
        queryWrapper.orderByDesc("priority", "create_time");
        
        List<Announcement> announcementList = announcementService.list(queryWrapper);
        return ResultUtils.success(announcementList);
    }
}

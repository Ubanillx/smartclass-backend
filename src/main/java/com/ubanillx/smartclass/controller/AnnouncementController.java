package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.announcement.AnnouncementAddRequest;
import com.ubanillx.smartclass.model.dto.announcement.AnnouncementQueryRequest;
import com.ubanillx.smartclass.model.dto.announcement.AnnouncementUpdateRequest;
import com.ubanillx.smartclass.model.entity.Announcement;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.AnnouncementVO;
import com.ubanillx.smartclass.service.AnnouncementService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统公告接口
 */
@RestController
@RequestMapping("/announcement")
@Slf4j
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建公告（仅管理员）
     *
     * @param announcementAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addAnnouncement(@RequestBody AnnouncementAddRequest announcementAddRequest,
                                         HttpServletRequest request) {
        if (announcementAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementAddRequest, announcement);
        User loginUser = userService.getLoginUser(request);
        Long adminId = loginUser.getId();
        long id = announcementService.addAnnouncement(announcement, adminId);
        return ResultUtils.success(id);
    }

    /**
     * 删除公告（仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAnnouncement(@RequestBody DeleteRequest deleteRequest,
                                              HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = announcementService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新公告（仅管理员）
     *
     * @param announcementUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAnnouncement(@RequestBody AnnouncementUpdateRequest announcementUpdateRequest,
                                              HttpServletRequest request) {
        if (announcementUpdateRequest == null || announcementUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementUpdateRequest, announcement);
        // 判断是否存在
        long id = announcementUpdateRequest.getId();
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = announcementService.updateById(announcement);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取公告（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AnnouncementVO> getAnnouncementVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 获取封装
        AnnouncementVO announcementVO = announcementService.getAnnouncementVO(announcement);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 如果用户已登录，查询是否已读
        if (loginUser != null) {
            boolean hasRead = announcementService.hasReadAnnouncement(id, loginUser.getId());
            announcementVO.setHasRead(hasRead);
            
            // 如果未读，增加公告查看次数并标记为已读
            if (!hasRead) {
                announcementService.increaseViewCount(id);
                announcementService.readAnnouncement(id, loginUser.getId());
            }
        } else {
            // 未登录用户增加查看次数
            announcementService.increaseViewCount(id);
        }
        
        return ResultUtils.success(announcementVO);
    }

    /**
     * 分页获取公告列表（仅管理员）
     *
     * @param announcementQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Announcement>> listAnnouncementByPage(@RequestBody AnnouncementQueryRequest announcementQueryRequest) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Announcement> queryWrapper = announcementService.getQueryWrapper(announcementQueryRequest);
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(announcementPage);
    }

    /**
     * 分页获取公告列表（封装类）
     *
     * @param announcementQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AnnouncementVO>> listAnnouncementVOByPage(@RequestBody AnnouncementQueryRequest announcementQueryRequest,
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
        
        List<AnnouncementVO> announcementVOList = announcementService.getAnnouncementVO(announcementPage.getRecords());
        Page<AnnouncementVO> announcementVOPage = new Page<>(current, size, announcementPage.getTotal());
        announcementVOPage.setRecords(announcementVOList);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 如果用户已登录，查询是否已读
        if (loginUser != null) {
            Long userId = loginUser.getId();
            for (AnnouncementVO announcementVO : announcementVOList) {
                boolean hasRead = announcementService.hasReadAnnouncement(announcementVO.getId(), userId);
                announcementVO.setHasRead(hasRead);
            }
        }
        
        return ResultUtils.success(announcementVOPage);
    }

    /**
     * 获取有效公告列表（未删除、已发布、在有效期内的公告）
     *
     * @param current
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/list/valid")
    public BaseResponse<Page<AnnouncementVO>> listValidAnnouncements(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest request) {
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<AnnouncementVO> announcementVOPage = announcementService.listValidAnnouncements(current, size);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 如果用户已登录，查询是否已读
        if (loginUser != null) {
            Long userId = loginUser.getId();
            for (AnnouncementVO announcementVO : announcementVOPage.getRecords()) {
                boolean hasRead = announcementService.hasReadAnnouncement(announcementVO.getId(), userId);
                announcementVO.setHasRead(hasRead);
            }
        }
        
        return ResultUtils.success(announcementVOPage);
    }

    /**
     * 标记公告为已读
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/read/{id}")
    public BaseResponse<Boolean> readAnnouncement(@PathVariable long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 判断是否已读
        boolean hasRead = announcementService.hasReadAnnouncement(id, loginUser.getId());
        
        // 如果未读，则增加查看次数
        if (!hasRead) {
            announcementService.increaseViewCount(id);
        }
        
        // 标记为已读
        boolean result = announcementService.readAnnouncement(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 检查公告是否已读
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/has-read/{id}")
    public BaseResponse<Boolean> hasReadAnnouncement(@PathVariable long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 查询是否已读
        boolean result = announcementService.hasReadAnnouncement(id, loginUser.getId());
        return ResultUtils.success(result);
    }
} 
package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.AnnouncementMapper;
import com.ubanillx.smartclass.model.dto.annoucement.AnnouncementQueryRequest;
import com.ubanillx.smartclass.model.entity.Announcement;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author liulo
 * @description 针对表【announcement(系统公告)】的数据库操作Service实现
 * @createDate 2025-02-27 21:52:01
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Resource
    private UserService userService;

    @Override
    public Announcement addAnnouncement(String title, String content, int status, HttpServletRequest request) {
        // 1. 校验参数
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (title.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        
        // 2. 获取当前用户（管理员）
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 仅管理员可添加公告
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 3. 创建公告对象
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setStatus(status);
        announcement.setAdminId(loginUser.getId());
        announcement.setPriority(0); // 默认优先级
        announcement.setViewCount(0); // 默认查看次数为0
        Date now = new Date();
        announcement.setCreateTime(now);
        announcement.setUpdateTime(now);
        announcement.setIsDelete(0); // 默认未删除
        
        // 4. 保存到数据库
        boolean result = this.save(announcement);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加公告失败");
        }
        
        return announcement;
    }

    @Override
    public Announcement updateAnnouncement(String title, String content, int status, HttpServletRequest request) {
        // 1. 校验参数
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (title.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        
        // 2. 获取当前用户（管理员）
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 仅管理员可更新公告
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 3. 获取要更新的公告对象
        Long id = request.getParameter("id") != null ? Long.parseLong(request.getParameter("id")) : null;
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "公告ID不能为空");
        }
        
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "公告不存在");
        }
        
        // 4. 更新公告对象
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setStatus(status);
        announcement.setUpdateTime(new Date());
        
        // 5. 保存到数据库
        boolean result = this.updateById(announcement);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新公告失败");
        }
        
        return announcement;
    }

    @Override
    public boolean deleteAnnouncement(long id, HttpServletRequest request) {
        // 1. 校验参数
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 2. 获取当前用户（管理员）
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 仅管理员可删除公告
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 3. 查询公告是否存在
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 4. 删除公告（逻辑删除）
        announcement.setIsDelete(1);
        announcement.setUpdateTime(new Date());
        return this.updateById(announcement);
    }

    @Override
    public QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest) {
        if (announcementQueryRequest == null) {
            return new QueryWrapper<>();
        }
        
        Long id = announcementQueryRequest.getId();
        String title = announcementQueryRequest.getTitle();
        String content = announcementQueryRequest.getContent();
        Integer status = announcementQueryRequest.getStatus();
        Integer priority = announcementQueryRequest.getPriority();
        Long adminId = announcementQueryRequest.getAdminId();
        String sortField = announcementQueryRequest.getSortField();
        String sortOrder = announcementQueryRequest.getSortOrder();
        
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq(priority != null, "priority", priority);
        queryWrapper.eq(adminId != null, "admin_id", adminId);
        

        // 未删除
        queryWrapper.eq("is_delete", 0);
        
        // 排序处理
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), 
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), 
                sortField);
        
        return queryWrapper;
    }
}

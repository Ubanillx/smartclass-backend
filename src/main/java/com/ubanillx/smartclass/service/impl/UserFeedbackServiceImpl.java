package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.mapper.UserFeedbackMapper;
import com.ubanillx.smartclass.model.dto.userfeedback.UserFeedbackQueryRequest;
import com.ubanillx.smartclass.model.entity.UserFeedback;
import com.ubanillx.smartclass.service.UserFeedbackService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 用户反馈Service实现类
 */
@Service
@Slf4j
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback> implements UserFeedbackService {

    @Resource
    private UserService userService;

    @Override
    public void validUserFeedback(UserFeedback userFeedback, boolean add) {
        if (userFeedback == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = userFeedback.getTitle();
        String content = userFeedback.getContent();
        String feedbackType = userFeedback.getFeedbackType();
        
        // 创建时参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, feedbackType), ErrorCode.PARAMS_ERROR);
        }
        
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }
    
    @Override
    public long addUserFeedback(UserFeedback userFeedback) {
        // 校验参数
        validUserFeedback(userFeedback, true);
        
        // 设置默认状态
        userFeedback.setStatus(0);  // 0-待处理
        userFeedback.setCreateTime(new Date());
        userFeedback.setUpdateTime(new Date());
        userFeedback.setIsDelete(0);
        
        // 保存到数据库
        boolean result = this.save(userFeedback);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "反馈创建失败");
        }
        
        return userFeedback.getId();
    }
    
    @Override
    public boolean updateUserFeedback(UserFeedback userFeedback) {
        // 校验参数
        validUserFeedback(userFeedback, false);
        
        // 判断是否存在
        long id = userFeedback.getId();
        UserFeedback oldFeedback = this.getById(id);
        ThrowUtils.throwIf(oldFeedback == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新
        userFeedback.setUpdateTime(new Date());
        return this.updateById(userFeedback);
    }
    
    @Override
    public boolean processFeedback(Long id, Long adminId, Integer status) {
        // 判断是否存在
        UserFeedback userFeedback = this.getById(id);
        ThrowUtils.throwIf(userFeedback == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 更新处理状态
        userFeedback.setStatus(status);
        userFeedback.setAdminId(adminId);
        userFeedback.setProcessTime(new Date());
        userFeedback.setUpdateTime(new Date());
        
        return this.updateById(userFeedback);
    }
    
    @Override
    public Page<UserFeedback> getUserFeedbackPage(UserFeedbackQueryRequest userFeedbackQueryRequest) {
        // 获取参数
        long current = userFeedbackQueryRequest.getCurrent();
        long size = userFeedbackQueryRequest.getPageSize();
        Integer status = userFeedbackQueryRequest.getStatus();
        Long userId = userFeedbackQueryRequest.getUserId();
        String feedbackType = userFeedbackQueryRequest.getFeedbackType();
        String title = userFeedbackQueryRequest.getTitle();
        
        // 构建查询条件
        QueryWrapper<UserFeedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        if (userId != null) {
            queryWrapper.eq("userId", userId);
        }
        
        if (StringUtils.isNotBlank(feedbackType)) {
            queryWrapper.eq("feedbackType", feedbackType);
        }
        
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        
        queryWrapper.orderByDesc("createTime");
        
        return this.page(new Page<>(current, size), queryWrapper);
    }
} 
package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserFeedback;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.dto.userfeedback.UserFeedbackQueryRequest;

/**
* @author rog
* @description 针对表【user_feedback(用户反馈)】的数据库操作Service
* @createDate 2025-05-02 14:35:52
*/
public interface UserFeedbackService extends IService<UserFeedback> {
    
    /**
     * 校验用户反馈参数
     * @param userFeedback 用户反馈对象
     * @param add 是否为添加操作
     */
    void validUserFeedback(UserFeedback userFeedback, boolean add);
    
    /**
     * 添加用户反馈
     * @param userFeedback 用户反馈对象
     * @return 反馈ID
     */
    long addUserFeedback(UserFeedback userFeedback);
    
    /**
     * 更新用户反馈
     * @param userFeedback 用户反馈对象
     * @return 是否成功
     */
    boolean updateUserFeedback(UserFeedback userFeedback);
    
    /**
     * 处理用户反馈
     * @param id 反馈ID
     * @param adminId 管理员ID
     * @param status 处理状态：1-处理中，2-已处理
     * @return 是否成功
     */
    boolean processFeedback(Long id, Long adminId, Integer status);
    
    /**
     * 获取用户反馈分页
     * @param userFeedbackQueryRequest 查询条件
     * @return 分页数据
     */
    Page<UserFeedback> getUserFeedbackPage(UserFeedbackQueryRequest userFeedbackQueryRequest);
}

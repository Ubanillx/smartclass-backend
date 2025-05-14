package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.userfeedback.UserFeedbackAddRequest;
import com.ubanillx.smartclass.model.dto.userfeedback.UserFeedbackProcessRequest;
import com.ubanillx.smartclass.model.dto.userfeedback.UserFeedbackQueryRequest;
import com.ubanillx.smartclass.model.dto.userfeedback.UserFeedbackUpdateRequest;
import com.ubanillx.smartclass.model.dto.userfeedbackreply.UserFeedbackReplyAddRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserFeedback;
import com.ubanillx.smartclass.model.entity.UserFeedbackReply;
import com.ubanillx.smartclass.service.UserFeedbackReplyService;
import com.ubanillx.smartclass.service.UserFeedbackService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 用户反馈接口
 */
@RestController
@RequestMapping("/user-feedbacks")
@Slf4j
public class UserFeedbackController {

    @Resource
    private UserFeedbackService userFeedbackService;

    @Resource
    private UserService userService;
    
    @Resource
    private UserFeedbackReplyService userFeedbackReplyService;

    /**
     * 创建用户反馈
     *
     * @param userFeedbackAddRequest 用户反馈创建请求体，包含反馈标题、内容、类型等信息
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 创建成功的反馈ID
     * @throws BusinessException 参数错误时抛出异常
     */
    @PostMapping
    public BaseResponse<Long> addUserFeedback(@RequestBody UserFeedbackAddRequest userFeedbackAddRequest,
                                        HttpServletRequest request) {
        if (userFeedbackAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        UserFeedback userFeedback = new UserFeedback();
        BeanUtils.copyProperties(userFeedbackAddRequest, userFeedback);
        userFeedback.setUserId(loginUser.getId());
        
        long feedbackId = userFeedbackService.addUserFeedback(userFeedback);
        return ResultUtils.success(feedbackId);
    }

    /**
     * 删除用户反馈
     *
     * @param id 要删除的反馈ID
     * @param request HTTP请求对象，用于获取当前登录用户信息和权限验证
     * @return 删除操作结果，true表示成功，false表示失败
     * @throws BusinessException 参数错误、未找到数据或无权限时抛出异常
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteUserFeedback(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        // 判断是否存在
        UserFeedback oldUserFeedback = userFeedbackService.getById(id);
        ThrowUtils.throwIf(oldUserFeedback == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserFeedback.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userFeedbackService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新用户反馈（仅管理员和自己）
     *
     * @param id 要更新的反馈ID
     * @param userFeedbackUpdateRequest 用户反馈更新请求体，包含要更新的反馈信息
     * @param request HTTP请求对象，用于获取当前登录用户信息和权限验证
     * @return 更新操作结果，true表示成功，false表示失败
     * @throws BusinessException 参数错误、未找到数据或无权限时抛出异常
     */
    @PutMapping("/{id}")
    public BaseResponse<Boolean> updateUserFeedback(@PathVariable("id") Long id, 
                                      @RequestBody UserFeedbackUpdateRequest userFeedbackUpdateRequest,
                                      HttpServletRequest request) {
        if (userFeedbackUpdateRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        UserFeedback userFeedback = new UserFeedback();
        BeanUtils.copyProperties(userFeedbackUpdateRequest, userFeedback);
        userFeedback.setId(id);
        
        // 判断是否存在
        UserFeedback oldUserFeedback = userFeedbackService.getById(id);
        ThrowUtils.throwIf(oldUserFeedback == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可更新
        if (!oldUserFeedback.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = userFeedbackService.updateUserFeedback(userFeedback);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取用户反馈详情
     *
     * @param id 反馈ID
     * @param request HTTP请求对象，用于获取当前登录用户信息和权限验证
     * @return 用户反馈详情
     * @throws BusinessException 参数错误、未找到数据或无权限时抛出异常
     */
    @GetMapping("/{id}")
    public BaseResponse<UserFeedback> getUserFeedbackById(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserFeedback userFeedback = userFeedbackService.getById(id);
        if (userFeedback == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        User user = userService.getLoginUser(request);
        // 仅本人或管理员可查看
        if (!userFeedback.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        return ResultUtils.success(userFeedback);
    }

    /**
     * 分页获取用户反馈列表
     *
     * @param userFeedbackQueryRequest 查询条件，包含分页参数、排序、过滤条件等
     * @param request HTTP请求对象，用于获取当前登录用户信息和权限验证
     * @return 分页用户反馈列表
     * @throws BusinessException 参数错误或分页参数超出限制时抛出异常
     */
    @GetMapping("/page")
    public BaseResponse<Page<UserFeedback>> listUserFeedbackByPage(UserFeedbackQueryRequest userFeedbackQueryRequest,
                                             HttpServletRequest request) {
        if (userFeedbackQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        // 非管理员只能查看自己的反馈
        if (!userService.isAdmin(request)) {
            userFeedbackQueryRequest.setUserId(loginUser.getId());
        }
        
        // 验证分页参数
        int current = (int)userFeedbackQueryRequest.getCurrent();
        int size = (int)userFeedbackQueryRequest.getPageSize();
        
        // 检查分页参数合法性
        if (current <= 0) {
            current = 1;
            userFeedbackQueryRequest.setCurrent(current);
        }
        if (size <= 0) {
            size = 10;
            userFeedbackQueryRequest.setPageSize(size);
        }
        
        // 限制爬虫，防止请求过大分页
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR, "页面大小不能超过20");
        
        // 构建分页对象
        Page<UserFeedback> page = new Page<>(current, size);
        
        // 执行分页查询
        Page<UserFeedback> userFeedbackPage = userFeedbackService.getUserFeedbackPage(userFeedbackQueryRequest);
        
        return ResultUtils.success(userFeedbackPage);
    }

    /**
     * 管理员处理用户反馈
     *
     * @param id 要处理的反馈ID
     * @param userFeedbackProcessRequest 处理请求体，包含处理状态等信息
     * @param request HTTP请求对象，用于获取当前管理员信息
     * @return 处理操作结果，true表示成功，false表示失败
     * @throws BusinessException 参数错误、权限不足或反馈不存在时抛出异常
     */
    @PutMapping("/{id}/process")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> processUserFeedback(@PathVariable("id") Long id,
                                      @RequestBody UserFeedbackProcessRequest userFeedbackProcessRequest,
                                      HttpServletRequest request) {
        if (userFeedbackProcessRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User admin = userService.getLoginUser(request);
        Integer status = userFeedbackProcessRequest.getStatus();
        
        boolean result = userFeedbackService.processFeedback(id, admin.getId(), status);
        return ResultUtils.success(result);
    }
    
    /**
     * 管理员处理用户反馈并回复
     *
     * @param id 要处理的反馈ID
     * @param userFeedbackReplyAddRequest 回复请求体，包含回复内容等信息
     * @param request HTTP请求对象，用于获取当前管理员信息
     * @return 创建的回复ID
     * @throws BusinessException 参数错误、权限不足或反馈不存在时抛出异常
     */
    @PostMapping("/{id}/reply")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> processAndReply(@PathVariable("id") Long id,
                                        @RequestBody UserFeedbackReplyAddRequest userFeedbackReplyAddRequest,
                                        HttpServletRequest request) {
        if (userFeedbackReplyAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User admin = userService.getLoginUser(request);
        userFeedbackReplyAddRequest.setFeedbackId(id);
        
        // 校验反馈是否存在
        UserFeedback userFeedback = userFeedbackService.getById(id);
        ThrowUtils.throwIf(userFeedback == null, ErrorCode.NOT_FOUND_ERROR, "反馈不存在");
        
        userFeedback.setAdminId(admin.getId());
        userFeedback.setProcessTime(new Date());
        userFeedback.setUpdateTime(new Date());
        userFeedbackService.updateById(userFeedback);
        
        // 添加回复
        UserFeedbackReply userFeedbackReply = new UserFeedbackReply();
        BeanUtils.copyProperties(userFeedbackReplyAddRequest, userFeedbackReply);
        userFeedbackReply.setSenderId(admin.getId());
        userFeedbackReply.setSenderRole(1); // 1-管理员
        
        long replyId = userFeedbackReplyService.addReply(userFeedbackReply);
        return ResultUtils.success(replyId);
    }
    
    /**
     * 获取用户未读回复数量
     *
     * @param request HTTP请求对象，用于获取当前登录用户信息和角色判断
     * @return 未读回复数量
     * @throws BusinessException 用户未登录时抛出异常
     */
    @GetMapping("/unread-count")
    public BaseResponse<Long> getUnreadCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        // 查询条件构建
        QueryWrapper<UserFeedback> feedbackQueryWrapper = new QueryWrapper<>();
        
        // 管理员查看所有未处理的反馈
        if (userService.isAdmin(request)) {
            feedbackQueryWrapper.eq("status", 0);
            feedbackQueryWrapper.eq("isDelete", 0);
            long adminUnreadCount = userFeedbackService.count(feedbackQueryWrapper);
            
            // 查询管理员未读的用户回复
            QueryWrapper<UserFeedbackReply> replyQueryWrapper = new QueryWrapper<>();
            replyQueryWrapper.eq("senderRole", 0); // 用户发送的
            replyQueryWrapper.eq("isRead", 0); // 未读
            replyQueryWrapper.eq("isDelete", 0);
            long adminUnreadReplyCount = userFeedbackReplyService.count(replyQueryWrapper);
            
            return ResultUtils.success(adminUnreadCount + adminUnreadReplyCount);
        } 
        // 普通用户只查看自己相关的未读回复
        else {
            // 查询用户的反馈ID
            feedbackQueryWrapper.eq("userId", loginUser.getId());
            feedbackQueryWrapper.eq("isDelete", 0);
            List<UserFeedback> userFeedbacks = userFeedbackService.list(feedbackQueryWrapper);
            
            if (userFeedbacks.isEmpty()) {
                return ResultUtils.success(0L);
            }
            
            // 获取用户所有反馈的ID
            List<Long> feedbackIds = userFeedbacks.stream().map(UserFeedback::getId).collect(java.util.stream.Collectors.toList());
            
            // 查询管理员对这些反馈的未读回复
            QueryWrapper<UserFeedbackReply> replyQueryWrapper = new QueryWrapper<>();
            replyQueryWrapper.in("feedbackId", feedbackIds);
            replyQueryWrapper.eq("senderRole", 1); // 管理员发送的
            replyQueryWrapper.eq("isRead", 0); // 未读
            replyQueryWrapper.eq("isDelete", 0);
            
            long userUnreadReplyCount = userFeedbackReplyService.count(replyQueryWrapper);
            return ResultUtils.success(userUnreadReplyCount);
        }
    }
} 
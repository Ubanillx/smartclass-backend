package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.userfeedbackreply.UserFeedbackReplyAddRequest;
import com.ubanillx.smartclass.model.dto.userfeedbackreply.UserFeedbackReplyQueryRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserFeedback;
import com.ubanillx.smartclass.model.entity.UserFeedbackReply;
import com.ubanillx.smartclass.model.vo.UserFeedbackReplyVO;
import com.ubanillx.smartclass.service.UserFeedbackReplyService;
import com.ubanillx.smartclass.service.UserFeedbackService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户反馈回复接口
 */
@RestController
@RequestMapping("/user-feedback-replies")
@Slf4j
public class UserFeedbackReplyController {

    @Resource
    private UserFeedbackReplyService userFeedbackReplyService;

    @Resource
    private UserFeedbackService userFeedbackService;

    @Resource
    private UserService userService;

    /**
     * 添加反馈回复
     *
     * @param userFeedbackReplyAddRequest 反馈回复创建请求，包含反馈ID和回复内容
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 新创建的反馈回复ID
     */
    @PostMapping("")
    public BaseResponse<Long> addReply(@RequestBody UserFeedbackReplyAddRequest userFeedbackReplyAddRequest,
                                  HttpServletRequest request) {
        if (userFeedbackReplyAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        Long feedbackId = userFeedbackReplyAddRequest.getFeedbackId();
        
        // 校验反馈是否存在
        UserFeedback userFeedback = userFeedbackService.getById(feedbackId);
        ThrowUtils.throwIf(userFeedback == null, ErrorCode.NOT_FOUND_ERROR, "反馈不存在");
        
        UserFeedbackReply userFeedbackReply = new UserFeedbackReply();
        BeanUtils.copyProperties(userFeedbackReplyAddRequest, userFeedbackReply);
        
        // 设置发送者ID和角色
        userFeedbackReply.setSenderId(loginUser.getId());
        
        // 根据当前用户角色确定发送者角色
        Integer senderRole = userService.isAdmin(request) ? 1 : 0; // 1-管理员，0-用户
        userFeedbackReply.setSenderRole(senderRole);
        
        // 管理员回复时，更新反馈状态为处理中（如果当前是待处理状态）
        if (senderRole == 1 && userFeedback.getStatus() == 0) {
            userFeedback.setStatus(1); // 设置为处理中
            userFeedback.setAdminId(loginUser.getId());
            userFeedbackService.updateById(userFeedback);
        }
        
        // 检查权限：普通用户只能回复自己的反馈
        if (senderRole == 0 && !userFeedback.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        long replyId = userFeedbackReplyService.addReply(userFeedbackReply);
        return ResultUtils.success(replyId);
    }

    /**
     * 获取反馈的所有回复
     *
     * @param feedbackId 反馈ID，用于查询指定反馈下的所有回复
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 反馈回复VO列表，包含发送者信息和回复内容
     */
    @GetMapping("")
    public BaseResponse<List<UserFeedbackReplyVO>> listReplies(@RequestParam("feedbackId") Long feedbackId,
                                                      HttpServletRequest request) {
        if (feedbackId == null || feedbackId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 校验反馈是否存在
        UserFeedback userFeedback = userFeedbackService.getById(feedbackId);
        ThrowUtils.throwIf(userFeedback == null, ErrorCode.NOT_FOUND_ERROR, "反馈不存在");
        
        // 检查权限：普通用户只能查看自己的反馈回复
        if (!userService.isAdmin(request) && !userFeedback.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 获取回复列表
        List<UserFeedbackReplyVO> replyVOList = userFeedbackReplyService.listRepliesByFeedbackId(feedbackId);
        
        // 如果是用户查看，标记管理员的回复为已读
        if (!userService.isAdmin(request)) {
            userFeedbackReplyService.markFeedbackRepliesAsRead(feedbackId, 1);
        } 
        // 如果是管理员查看，标记用户的回复为已读
        else {
            userFeedbackReplyService.markFeedbackRepliesAsRead(feedbackId, 0);
        }
        
        return ResultUtils.success(replyVOList);
    }

    /**
     * 分页获取反馈回复
     *
     * @param userFeedbackReplyQueryRequest 查询请求，包含分页参数和过滤条件
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 反馈回复VO分页结果，包含回复列表和分页信息
     */
    @GetMapping("/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserFeedbackReplyVO>> listReplyByPage(UserFeedbackReplyQueryRequest userFeedbackReplyQueryRequest,
                                                  HttpServletRequest request) {
        if (userFeedbackReplyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long current = userFeedbackReplyQueryRequest.getCurrent();
        long size = userFeedbackReplyQueryRequest.getPageSize();
        Long feedbackId = userFeedbackReplyQueryRequest.getFeedbackId();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<UserFeedbackReplyVO> replyVOPage = userFeedbackReplyService.getReplyVOPage(current, size, feedbackId);
        return ResultUtils.success(replyVOPage);
    }

    /**
     * 标记反馈回复为已读
     *
     * @param replyId 需要标记为已读的反馈回复ID
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 标记操作是否成功
     */
    @PutMapping("/{replyId}/read")
    public BaseResponse<Boolean> markAsRead(@PathVariable("replyId") Long replyId, HttpServletRequest request) {
        if (replyId == null || replyId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 获取回复
        UserFeedbackReply reply = userFeedbackReplyService.getById(replyId);
        ThrowUtils.throwIf(reply == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 获取反馈
        UserFeedback feedback = userFeedbackService.getById(reply.getFeedbackId());
        ThrowUtils.throwIf(feedback == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 检查权限：普通用户只能操作自己的反馈相关回复
        boolean isAdmin = userService.isAdmin(request);
        if (!isAdmin && !feedback.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 管理员可以标记用户回复为已读，用户可以标记管理员回复为已读
        Integer userRole = isAdmin ? 1 : 0;
        if (reply.getSenderRole().equals(userRole)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能标记自己的回复为已读");
        }
        
        boolean result = userFeedbackReplyService.markAsRead(replyId);
        return ResultUtils.success(result);
    }
} 
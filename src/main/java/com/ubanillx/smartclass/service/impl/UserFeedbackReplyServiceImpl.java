package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserFeedbackReplyMapper;
import com.ubanillx.smartclass.model.entity.UserFeedback;
import com.ubanillx.smartclass.model.entity.UserFeedbackReply;
import com.ubanillx.smartclass.model.vo.UserFeedbackReplyVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.UserFeedbackReplyService;
import com.ubanillx.smartclass.service.UserFeedbackService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户反馈回复Service实现类
 */
@Service
@Slf4j
public class UserFeedbackReplyServiceImpl extends ServiceImpl<UserFeedbackReplyMapper, UserFeedbackReply> implements UserFeedbackReplyService {

    @Resource
    private UserFeedbackService userFeedbackService;

    @Resource
    private UserService userService;

    @Override
    public void validUserFeedbackReply(UserFeedbackReply userFeedbackReply, boolean add) {
        if (userFeedbackReply == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add) {
            if (userFeedbackReply.getFeedbackId() == null || userFeedbackReply.getFeedbackId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "反馈ID不能为空");
            }
            
            if (StringUtils.isBlank(userFeedbackReply.getContent())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容不能为空");
            }
            
            // 校验反馈是否存在
            UserFeedback userFeedback = userFeedbackService.getById(userFeedbackReply.getFeedbackId());
            if (userFeedback == null || userFeedback.getIsDelete() == 1) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "反馈不存在");
            }
        }
        
        // 内容长度限制
        if (StringUtils.isNotBlank(userFeedbackReply.getContent()) && userFeedbackReply.getContent().length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容过长");
        }
    }

    @Override
    public long addReply(UserFeedbackReply userFeedbackReply) {
        // 校验参数
        validUserFeedbackReply(userFeedbackReply, true);
        
        // 设置默认值
        userFeedbackReply.setIsRead(0); // 默认未读
        userFeedbackReply.setCreateTime(new Date());
        userFeedbackReply.setUpdateTime(new Date());
        userFeedbackReply.setIsDelete(0);
        
        // 保存
        boolean result = this.save(userFeedbackReply);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "回复创建失败");
        }
        
        return userFeedbackReply.getId();
    }

    @Override
    public List<UserFeedbackReplyVO> listRepliesByFeedbackId(Long feedbackId) {
        if (feedbackId == null || feedbackId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取所有回复
        QueryWrapper<UserFeedbackReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("feedbackId", feedbackId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByAsc("createTime"); // 按时间正序排列
        
        List<UserFeedbackReply> replyList = this.list(queryWrapper);
        if (replyList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为VO
        List<UserFeedbackReplyVO> replyVOList = replyList.stream()
                .map(UserFeedbackReplyVO::objToVo)
                .collect(Collectors.toList());
        
        // 设置发送者信息
        setReplySenderInfo(replyVOList);
        
        return replyVOList;
    }

    @Override
    public Page<UserFeedbackReplyVO> getReplyVOPage(long current, long size, Long feedbackId) {
        // 构建查询条件
        QueryWrapper<UserFeedbackReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        
        if (feedbackId != null) {
            queryWrapper.eq("feedbackId", feedbackId);
        }
        
        queryWrapper.orderByDesc("createTime");
        
        // 分页查询
        Page<UserFeedbackReply> replyPage = this.page(new Page<>(current, size), queryWrapper);
        Page<UserFeedbackReplyVO> replyVOPage = new Page<>(replyPage.getCurrent(), replyPage.getSize(), replyPage.getTotal());
        
        // 获取记录
        List<UserFeedbackReply> replyList = replyPage.getRecords();
        if (replyList.isEmpty()) {
            return replyVOPage;
        }
        
        // 转换为VO
        List<UserFeedbackReplyVO> replyVOList = replyList.stream()
                .map(UserFeedbackReplyVO::objToVo)
                .collect(Collectors.toList());
        
        // 设置发送者信息
        setReplySenderInfo(replyVOList);
        
        replyVOPage.setRecords(replyVOList);
        
        return replyVOPage;
    }
    
    @Override
    public boolean markAsRead(Long replyId) {
        if (replyId == null || replyId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取回复
        UserFeedbackReply reply = this.getById(replyId);
        if (reply == null || reply.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 已读则不处理
        if (reply.getIsRead() == 1) {
            return true;
        }
        
        // 设置为已读
        reply.setIsRead(1);
        reply.setUpdateTime(new Date());
        
        return this.updateById(reply);
    }
    
    @Override
    public boolean markFeedbackRepliesAsRead(Long feedbackId, Integer senderRole) {
        if (feedbackId == null || feedbackId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询该反馈下指定角色发送的未读回复
        QueryWrapper<UserFeedbackReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("feedbackId", feedbackId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.eq("isRead", 0); // 未读
        
        // 如果指定了发送者角色，则只标记该角色的消息为已读
        if (senderRole != null) {
            queryWrapper.eq("senderRole", senderRole);
        }
        
        List<UserFeedbackReply> replyList = this.list(queryWrapper);
        if (replyList.isEmpty()) {
            return true;
        }
        
        // 批量更新为已读
        for (UserFeedbackReply reply : replyList) {
            reply.setIsRead(1);
            reply.setUpdateTime(new Date());
        }
        
        return this.updateBatchById(replyList);
    }
    
    /**
     * 设置回复发送者信息
     *
     * @param replyVOList 回复VO列表
     */
    private void setReplySenderInfo(List<UserFeedbackReplyVO> replyVOList) {
        if (replyVOList.isEmpty()) {
            return;
        }
        
        // 获取所有发送者ID
        Set<Long> senderIds = replyVOList.stream()
                .map(UserFeedbackReplyVO::getSenderId)
                .collect(Collectors.toSet());
        
        // 批量查询用户信息
        Map<Long, UserVO> userMap = new HashMap<>();
        for (Long senderId : senderIds) {
            UserVO userVO = userService.getUserVOById(senderId);
            if (userVO != null) {
                userMap.put(senderId, userVO);
            }
        }
        
        // 设置发送者信息
        for (UserFeedbackReplyVO replyVO : replyVOList) {
            Long senderId = replyVO.getSenderId();
            if (userMap.containsKey(senderId)) {
                replyVO.setSender(userMap.get(senderId));
            }
        }
    }
} 
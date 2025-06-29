package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserFeedbackReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.vo.UserFeedbackReplyVO;

import java.util.List;

/**
* @author rog
* @description 针对表【user_feedback_reply(反馈回复)】的数据库操作Service
* @createDate 2025-05-02 14:51:01
*/
public interface UserFeedbackReplyService extends IService<UserFeedbackReply> {

    /**
     * 校验用户反馈回复参数
     * @param userFeedbackReply 用户反馈回复对象
     * @param add 是否为添加操作
     */
    void validUserFeedbackReply(UserFeedbackReply userFeedbackReply, boolean add);
    
    /**
     * 添加反馈回复
     * @param userFeedbackReply 反馈回复对象
     * @return 回复ID
     */
    long addReply(UserFeedbackReply userFeedbackReply);
    
    /**
     * 获取某一反馈的所有回复
     * @param feedbackId 反馈ID
     * @return 回复VO列表
     */
    List<UserFeedbackReplyVO> listRepliesByFeedbackId(Long feedbackId);
    
    /**
     * 分页获取反馈回复VO
     * @param current 当前页
     * @param size 每页大小
     * @param feedbackId 反馈ID
     * @return 回复VO分页
     */
    Page<UserFeedbackReplyVO> getReplyVOPage(long current, long size, Long feedbackId);
    
    /**
     * 标记回复为已读
     * @param replyId 回复ID
     * @return 是否成功
     */
    boolean markAsRead(Long replyId);
    
    /**
     * 标记某一反馈下的所有回复为已读
     * @param feedbackId 反馈ID
     * @param senderRole 发送者角色，可为null表示所有角色
     * @return 是否成功
     */
    boolean markFeedbackRepliesAsRead(Long feedbackId, Integer senderRole);
}

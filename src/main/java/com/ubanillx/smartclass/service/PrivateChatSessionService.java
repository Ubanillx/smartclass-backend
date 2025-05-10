package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.privatechatsession.PrivateChatSessionQueryRequest;
import com.ubanillx.smartclass.model.dto.privateMessage.PrivateMessageAddRequest;
import com.ubanillx.smartclass.model.entity.PrivateChatSession;
import com.ubanillx.smartclass.model.entity.PrivateMessage;
import com.ubanillx.smartclass.model.vo.PrivateChatSessionVO;
import com.ubanillx.smartclass.model.vo.PrivateMessageVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 私聊会话服务
 */
public interface PrivateChatSessionService extends IService<PrivateChatSession> {

    /**
     * 创建私聊会话
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 会话ID
     */
    long createChatSession(Long userId1, Long userId2);

    /**
     * 获取用户的所有聊天会话
     *
     * @param userId 用户ID
     * @param request 请求
     * @return 聊天会话列表
     */
    List<PrivateChatSessionVO> listUserChatSessions(Long userId, HttpServletRequest request);

    /**
     * 获取或创建两个用户之间的聊天会话
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 聊天会话
     */
    PrivateChatSession getOrCreateChatSession(Long userId1, Long userId2);

    /**
     * 发送私聊消息
     *
     * @param senderId 发送者ID
     * @param request 消息请求
     * @param httpRequest HTTP请求
     * @return 消息ID
     */
    long sendPrivateMessage(Long senderId, PrivateMessageAddRequest request, HttpServletRequest httpRequest);

    /**
     * 获取会话中的消息列表
     *
     * @param sessionId 会话ID
     * @param current 当前页
     * @param size 页大小
     * @param request HTTP请求
     * @return 消息列表分页
     */
    Page<PrivateMessageVO> listSessionMessages(Long sessionId, long current, long size, HttpServletRequest request);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param userId 用户ID
     */
    void markMessageAsRead(Long messageId, Long userId);

    /**
     * 标记会话中的所有消息为已读
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllMessagesAsRead(Long sessionId, Long userId);

    /**
     * 获取会话的未读消息数量
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadMessageCount(Long sessionId, Long userId);

    /**
     * 获取用户的所有未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getTotalUnreadMessageCount(Long userId);

    /**
     * 获取两个用户之间的聊天会话
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 聊天会话
     */
    PrivateChatSession getChatSession(Long userId1, Long userId2);

    /**
     * 获取私聊会话VO
     *
     * @param chatSession 聊天会话
     * @param currentUserId 当前用户ID
     * @return 私聊会话VO
     */
    PrivateChatSessionVO getPrivateChatSessionVO(PrivateChatSession chatSession, Long currentUserId);

    /**
     * 获取私聊消息VO
     *
     * @param message 私聊消息
     * @return 私聊消息VO
     */
    PrivateMessageVO getPrivateMessageVO(PrivateMessage message);
    
    /**
     * 获取查询条件
     *
     * @param request 查询请求
     * @return 查询条件
     */
    QueryWrapper<PrivateChatSession> getQueryWrapper(PrivateChatSessionQueryRequest request);
}

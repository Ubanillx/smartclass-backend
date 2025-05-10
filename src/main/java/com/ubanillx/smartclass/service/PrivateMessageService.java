package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.privateMessage.PrivateMessageAddRequest;
import com.ubanillx.smartclass.model.dto.privateMessage.PrivateMessageQueryRequest;
import com.ubanillx.smartclass.model.entity.PrivateMessage;
import com.ubanillx.smartclass.model.vo.PrivateMessageVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 私聊消息服务
 */
public interface PrivateMessageService extends IService<PrivateMessage> {

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
     * 获取用户的所有私聊消息
     *
     * @param userId 用户ID
     * @param request HTTP请求
     * @return 消息列表
     */
    List<PrivateMessageVO> listUserMessages(Long userId, HttpServletRequest request);

    /**
     * 获取两个用户之间的私聊消息
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @param current 当前页
     * @param size 页大小
     * @param request HTTP请求
     * @return 消息分页
     */
    Page<PrivateMessageVO> listMessagesBetweenUsers(Long userId1, Long userId2, long current, long size, HttpServletRequest request);

    /**
     * 分页获取用户的未读消息
     *
     * @param userId 用户ID
     * @param current 当前页
     * @param size 页大小
     * @param request HTTP请求
     * @return 未读消息分页
     */
    Page<PrivateMessageVO> listUnreadMessages(Long userId, long current, long size, HttpServletRequest request);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean markMessageAsRead(Long messageId, HttpServletRequest request);

    /**
     * 批量标记消息为已读
     *
     * @param messageIds 消息ID列表
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean markMessagesAsRead(List<Long> messageIds, HttpServletRequest request);

    /**
     * 标记用户所有消息为已读
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllMessagesAsRead(Long userId);

    /**
     * 获取私聊消息VO
     *
     * @param message 私聊消息
     * @return 私聊消息VO
     */
    PrivateMessageVO getPrivateMessageVO(PrivateMessage message);

    /**
     * 获取私聊消息VO列表
     *
     * @param messageList 私聊消息列表
     * @return 私聊消息VO列表
     */
    List<PrivateMessageVO> getPrivateMessageVOList(List<PrivateMessage> messageList);

    /**
     * 获取查询条件
     *
     * @param request 查询请求
     * @return 查询条件
     */
    QueryWrapper<PrivateMessage> getQueryWrapper(PrivateMessageQueryRequest request);
}

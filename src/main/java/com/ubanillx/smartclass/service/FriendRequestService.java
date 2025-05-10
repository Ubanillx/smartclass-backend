package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.friendrequest.FriendRequestQueryRequest;
import com.ubanillx.smartclass.model.entity.FriendRequest;
import com.ubanillx.smartclass.model.vo.FriendRequestVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 好友申请服务
 */
public interface FriendRequestService extends IService<FriendRequest> {

    /**
     * 创建好友申请
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param message 申请消息
     * @return 好友申请ID
     */
    long addFriendRequest(Long senderId, Long receiverId, String message);

    /**
     * 接受好友申请
     *
     * @param id 好友申请ID
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean acceptFriendRequest(Long id, HttpServletRequest request);

    /**
     * 拒绝好友申请
     *
     * @param id 好友申请ID
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean rejectFriendRequest(Long id, HttpServletRequest request);

    /**
     * 更新好友申请状态
     *
     * @param id 好友申请ID
     * @param status 申请状态
     * @return 是否成功
     */
    boolean updateFriendRequestStatus(Long id, String status);

    /**
     * 获取发送给指定用户的好友申请列表
     *
     * @param receiverId 接收者ID
     * @param status 申请状态，可为null
     * @return 好友申请列表
     */
    List<FriendRequestVO> listFriendRequestByReceiverId(Long receiverId, String status);

    /**
     * 获取指定用户发送的好友申请列表
     *
     * @param senderId 发送者ID
     * @param status 申请状态，可为null
     * @return 好友申请列表
     */
    List<FriendRequestVO> listFriendRequestBySenderId(Long senderId, String status);

    /**
     * 获取好友申请
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 好友申请
     */
    FriendRequest getFriendRequest(Long senderId, Long receiverId);

    /**
     * 检查是否已经存在好友申请
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 是否存在
     */
    boolean existsFriendRequest(Long senderId, Long receiverId);

    /**
     * 获取查询条件
     *
     * @param friendRequestQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper<FriendRequest> getQueryWrapper(FriendRequestQueryRequest friendRequestQueryRequest);

    /**
     * 分页获取好友申请VO
     *
     * @param friendRequestPage 好友申请分页
     * @param request 请求
     * @return 好友申请VO分页
     */
    Page<FriendRequestVO> getFriendRequestVOPage(Page<FriendRequest> friendRequestPage, HttpServletRequest request);

    /**
     * 获取好友申请VO
     *
     * @param friendRequest 好友申请
     * @return 好友申请VO
     */
    FriendRequestVO getFriendRequestVO(FriendRequest friendRequest);

    /**
     * 获取好友申请VO列表
     *
     * @param friendRequestList 好友申请列表
     * @return 好友申请VO列表
     */
    List<FriendRequestVO> getFriendRequestVOList(List<FriendRequest> friendRequestList);
}

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
     * @param senderId 发送者ID，当前登录用户的ID
     * @param receiverId 接收者ID，要添加的好友的用户ID
     * @param message 申请消息，添加好友的附加说明信息，可为空
     * @return 好友申请ID，成功创建后的申请记录ID
     */
    long addFriendRequest(Long senderId, Long receiverId, String message);

    /**
     * 接受好友申请
     *
     * @param id 好友申请ID，要接受的申请记录ID
     * @param request HTTP请求，用于获取当前登录用户信息和权限验证
     * @return 是否成功接受申请
     */
    boolean acceptFriendRequest(Long id, HttpServletRequest request);

    /**
     * 拒绝好友申请
     *
     * @param id 好友申请ID，要拒绝的申请记录ID
     * @param request HTTP请求，用于获取当前登录用户信息和权限验证
     * @return 是否成功拒绝申请
     */
    boolean rejectFriendRequest(Long id, HttpServletRequest request);

    /**
     * 获取发送给指定用户的好友申请列表
     *
     * @param receiverId 接收者ID，要查询收到的申请的用户ID
     * @param status 申请状态，可为null，为null时查询所有状态的申请
     * @return 好友申请VO列表，带有发送者和接收者详细信息的申请列表
     */
    List<FriendRequestVO> listFriendRequestByReceiverId(Long receiverId, String status);

    /**
     * 获取指定用户发送的好友申请列表
     *
     * @param senderId 发送者ID，要查询发出的申请的用户ID
     * @param status 申请状态，可为null，为null时查询所有状态的申请
     * @return 好友申请VO列表，带有发送者和接收者详细信息的申请列表
     */
    List<FriendRequestVO> listFriendRequestBySenderId(Long senderId, String status);

    /**
     * 获取好友申请
     *
     * @param senderId 发送者ID，申请发起人ID
     * @param receiverId 接收者ID，申请接收人ID
     * @return 好友申请实体，如果不存在则返回null
     */
    FriendRequest getFriendRequest(Long senderId, Long receiverId);

    /**
     * 检查是否已经存在好友申请
     *
     * @param senderId 发送者ID，申请发起人ID
     * @param receiverId 接收者ID，申请接收人ID
     * @return 是否存在申请记录，true表示已存在，false表示不存在
     */
    boolean existsFriendRequest(Long senderId, Long receiverId);

    /**
     * 获取查询条件
     *
     * @param friendRequestQueryRequest 查询参数，包含发送者ID、接收者ID、状态等过滤条件
     * @return 查询条件包装器，用于执行数据库查询
     */
    QueryWrapper<FriendRequest> getQueryWrapper(FriendRequestQueryRequest friendRequestQueryRequest);

    /**
     * 分页获取好友申请VO
     *
     * @param friendRequestPage 好友申请分页结果，原始实体数据
     * @param request 请求对象，用于获取当前登录用户信息
     * @return 好友申请VO分页结果，包含用户详细信息的VO对象
     */
    Page<FriendRequestVO> getFriendRequestVOPage(Page<FriendRequest> friendRequestPage, HttpServletRequest request);

    /**
     * 获取好友申请VO
     *
     * @param friendRequest 好友申请实体对象
     * @return 好友申请VO对象，包含发送者和接收者的详细信息
     */
    FriendRequestVO getFriendRequestVO(FriendRequest friendRequest);

    /**
     * 获取好友申请VO列表
     *
     * @param friendRequestList 好友申请实体列表
     * @return 好友申请VO列表，每个VO对象包含发送者和接收者的详细信息
     */
    List<FriendRequestVO> getFriendRequestVOList(List<FriendRequest> friendRequestList);
}

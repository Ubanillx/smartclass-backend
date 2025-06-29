package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.friendrelationship.FriendRelationshipQueryRequest;
import com.ubanillx.smartclass.model.entity.FriendRelationship;
import com.ubanillx.smartclass.model.vo.FriendRelationshipVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 好友关系服务
 */
public interface FriendRelationshipService extends IService<FriendRelationship> {

    /**
     * 创建好友关系
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @param status 关系状态
     * @return 好友关系ID
     */
    long addFriendRelationship(Long userId1, Long userId2, String status);

    /**
     * 更新好友关系状态
     *
     * @param id 好友关系ID
     * @param status 关系状态
     * @return 是否成功
     */
    boolean updateFriendRelationshipStatus(Long id, String status);

    /**
     * 获取好友关系
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 好友关系
     */
    FriendRelationship getFriendRelationship(Long userId1, Long userId2);

    /**
     * 检查两个用户是否为好友关系
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 是否为好友
     */
    boolean isFriend(Long userId1, Long userId2);

    /**
     * 获取用户的好友列表
     *
     * @param userId 用户ID
     * @return 好友列表
     */
    List<FriendRelationshipVO> listUserFriends(Long userId);

    /**
     * 获取用户的好友列表
     *
     * @param userId 用户ID
     * @param status 关系状态
     * @return 好友列表
     */
    List<FriendRelationshipVO> listUserFriendsByStatus(Long userId, String status);

    /**
     * 删除好友关系
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 是否成功
     */
    boolean deleteFriendRelationship(Long userId1, Long userId2);

    /**
     * 获取查询条件
     *
     * @param friendRelationshipQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper<FriendRelationship> getQueryWrapper(FriendRelationshipQueryRequest friendRelationshipQueryRequest);

    /**
     * 分页获取好友关系VO
     *
     * @param friendRelationshipPage 好友关系分页
     * @param request 请求
     * @return 好友关系VO分页
     */
    Page<FriendRelationshipVO> getFriendRelationshipVOPage(Page<FriendRelationship> friendRelationshipPage, HttpServletRequest request);

}

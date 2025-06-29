package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.FriendRelationshipConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.FriendRelationshipMapper;
import com.ubanillx.smartclass.model.dto.friendrelationship.FriendRelationshipQueryRequest;
import com.ubanillx.smartclass.model.entity.FriendRelationship;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.FriendRelationshipVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.FriendRelationshipService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友关系服务实现类
 */
@Service
@Slf4j
public class FriendRelationshipServiceImpl extends ServiceImpl<FriendRelationshipMapper, FriendRelationship>
        implements FriendRelationshipService {

    @Resource
    private UserService userService;

    @Override
    public long addFriendRelationship(Long userId1, Long userId2, String status) {
        // 参数校验
        if (userId1 == null || userId2 == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 用户不能与自己建立好友关系
        if (userId1.equals(userId2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能与自己建立好友关系");
        }
        
        // 确保用户存在
        User user1 = userService.getById(userId1);
        User user2 = userService.getById(userId2);
        if (user1 == null || user2 == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        
        // 确保好友关系不存在
        FriendRelationship existRelationship = getFriendRelationship(userId1, userId2);
        if (existRelationship != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "好友关系已存在");
        }
        
        // 创建好友关系
        FriendRelationship friendRelationship = new FriendRelationship();
        friendRelationship.setUserId1(userId1);
        friendRelationship.setUserId2(userId2);
        friendRelationship.setStatus(status);
        
        // 保存
        boolean saveResult = save(friendRelationship);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "好友关系创建失败");
        }
        
        return friendRelationship.getId();
    }

    @Override
    public boolean updateFriendRelationshipStatus(Long id, String status) {
        // 参数校验
        if (id == null || StringUtils.isBlank(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        // 获取好友关系
        FriendRelationship friendRelationship = getById(id);
        if (friendRelationship == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友关系不存在");
        }
        
        // 更新状态
        friendRelationship.setStatus(status);
        return updateById(friendRelationship);
    }

    @Override
    public FriendRelationship getFriendRelationship(Long userId1, Long userId2) {
        // 参数校验
        if (userId1 == null || userId2 == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 查询条件，两种排列都要查询
        QueryWrapper<FriendRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(qw -> qw
                .eq("userId1", userId1).eq("userId2", userId2)
                .or()
                .eq("userId1", userId2).eq("userId2", userId1)
        );
        
        return getOne(queryWrapper);
    }

    @Override
    public boolean isFriend(Long userId1, Long userId2) {
        // 查询好友关系
        FriendRelationship relationship = getFriendRelationship(userId1, userId2);
        if (relationship == null) {
            return false;
        }
        
        // 只有状态为已接受才算是好友
        return FriendRelationshipConstant.STATUS_ACCEPTED.equals(relationship.getStatus());
    }

    @Override
    public List<FriendRelationshipVO> listUserFriends(Long userId) {
        return listUserFriendsByStatus(userId, FriendRelationshipConstant.STATUS_ACCEPTED);
    }

    @Override
    public List<FriendRelationshipVO> listUserFriendsByStatus(Long userId, String status) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 构建查询条件
        QueryWrapper<FriendRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(qw -> qw
                .eq("userId1", userId)
                .or()
                .eq("userId2", userId)
        );
        
        // 如果提供了状态，需要增加状态筛选
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
        
        // 获取好友关系列表
        List<FriendRelationship> relationships = list(queryWrapper);
        
        // 使用Stream API转换为VO并填充好友用户信息
        return relationships.stream().map(relationship -> {
            FriendRelationshipVO vo = new FriendRelationshipVO();
            BeanUtils.copyProperties(relationship, vo);
            
            // 判断好友是哪个用户
            Long friendUserId = userId.equals(relationship.getUserId1()) ? 
                    relationship.getUserId2() : relationship.getUserId1();
            
            // 获取好友用户信息
            vo.setFriendUser(userService.getUserVOById(friendUserId));
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean deleteFriendRelationship(Long userId1, Long userId2) {
        // 获取好友关系 - getFriendRelationship内部已包含参数校验
        FriendRelationship relationship = getFriendRelationship(userId1, userId2);
        
        // 关系不存在视为删除成功
        return relationship == null || removeById(relationship.getId());
    }

    @Override
    public QueryWrapper<FriendRelationship> getQueryWrapper(FriendRelationshipQueryRequest friendRelationshipQueryRequest) {
        if (friendRelationshipQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        
        QueryWrapper<FriendRelationship> queryWrapper = new QueryWrapper<>();
        
        // 添加基本条件
        Long userId1 = friendRelationshipQueryRequest.getUserId1();
        Long userId2 = friendRelationshipQueryRequest.getUserId2();
        Long userId = friendRelationshipQueryRequest.getUserId();
        String status = friendRelationshipQueryRequest.getStatus();
        
        // 精确匹配条件
        queryWrapper.eq(userId1 != null, "userId1", userId1);
        queryWrapper.eq(userId2 != null, "userId2", userId2);
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);
        
        // 如果提供了userId，则查询与该用户相关的所有好友关系
        if (userId != null) {
            queryWrapper.and(qw -> qw
                    .eq("userId1", userId)
                    .or()
                    .eq("userId2", userId)
            );
        }
        
        // 排序处理
        String sortField = friendRelationshipQueryRequest.getSortField();
        String sortOrder = friendRelationshipQueryRequest.getSortOrder();
        queryWrapper.orderBy(
            StringUtils.isNotBlank(sortField), 
            "asc".equals(sortOrder), 
            StringUtils.isNotBlank(sortField) ? sortField : "createTime"
        );
        
        return queryWrapper;
    }

    @Override
    public Page<FriendRelationshipVO> getFriendRelationshipVOPage(Page<FriendRelationship> friendRelationshipPage, HttpServletRequest request) {
        List<FriendRelationship> friendRelationshipList = friendRelationshipPage.getRecords();
        
        // 创建VO分页对象
        Page<FriendRelationshipVO> friendRelationshipVOPage = new Page<>(
                friendRelationshipPage.getCurrent(),
                friendRelationshipPage.getSize(),
                friendRelationshipPage.getTotal()
        );
        
        // 如果好友关系为空，返回空分页
        if (friendRelationshipList.isEmpty()) {
            friendRelationshipVOPage.setRecords(new ArrayList<>());
            return friendRelationshipVOPage;
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        
        // 转换为VO列表
        List<FriendRelationshipVO> friendRelationshipVOList = friendRelationshipList.stream()
            .map(friendRelationship -> {
                FriendRelationshipVO vo = new FriendRelationshipVO();
                BeanUtils.copyProperties(friendRelationship, vo);
                
                // 确定好友ID
                Long friendUserId;
                if (loginUserId.equals(friendRelationship.getUserId1())) {
                    friendUserId = friendRelationship.getUserId2();
                } else if (loginUserId.equals(friendRelationship.getUserId2())) {
                    friendUserId = friendRelationship.getUserId1();
                } else {
                    // 管理员查看，默认展示用户2
                    friendUserId = friendRelationship.getUserId2();
                }
                
                // 设置好友信息
                vo.setFriendUser(userService.getUserVOById(friendUserId));
                
                return vo;
            })
            .collect(Collectors.toList());
        
        friendRelationshipVOPage.setRecords(friendRelationshipVOList);
        return friendRelationshipVOPage;
    }
} 
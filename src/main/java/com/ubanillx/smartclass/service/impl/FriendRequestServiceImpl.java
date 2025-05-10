package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.FriendRelationshipConstant;
import com.ubanillx.smartclass.constant.FriendRequestConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.FriendRequestMapper;
import com.ubanillx.smartclass.model.dto.friendrequest.FriendRequestQueryRequest;
import com.ubanillx.smartclass.model.entity.FriendRelationship;
import com.ubanillx.smartclass.model.entity.FriendRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.FriendRequestVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.FriendRelationshipService;
import com.ubanillx.smartclass.service.FriendRequestService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友申请服务实现类
 */
@Service
@Slf4j
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest>
        implements FriendRequestService {

    @Resource
    private UserService userService;

    @Resource
    private FriendRelationshipService friendRelationshipService;

    @Override
    public long addFriendRequest(Long senderId, Long receiverId, String message) {
        // 参数校验
        if (senderId == null || receiverId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 不能向自己发送好友申请
        if (senderId.equals(receiverId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能向自己发送好友申请");
        }
        
        // 确保用户存在
        User sender = userService.getById(senderId);
        User receiver = userService.getById(receiverId);
        if (sender == null || receiver == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        
        // 检查是否已经是好友
        if (friendRelationshipService.isFriend(senderId, receiverId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经是好友关系，无需再次申请");
        }
        
        // 检查是否已存在待处理的申请
        FriendRequest existRequest = getFriendRequest(senderId, receiverId);
        if (existRequest != null) {
            if (FriendRequestConstant.STATUS_PENDING.equals(existRequest.getStatus())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "已存在待处理的好友申请");
            } else if (FriendRequestConstant.STATUS_REJECTED.equals(existRequest.getStatus())) {
                // 如果之前被拒绝，可以更新为新的申请
                existRequest.setStatus(FriendRequestConstant.STATUS_PENDING);
                existRequest.setMessage(message);
                updateById(existRequest);
                return existRequest.getId();
            }
        }
        
        // 创建新的好友申请
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(senderId);
        friendRequest.setReceiverId(receiverId);
        friendRequest.setStatus(FriendRequestConstant.STATUS_PENDING);
        friendRequest.setMessage(message);
        
        // 保存
        boolean saveResult = save(friendRequest);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "好友申请创建失败");
        }
        
        return friendRequest.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptFriendRequest(Long id, HttpServletRequest request) {
        // 参数校验
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友申请ID不能为空");
        }
        
        // 获取好友申请
        FriendRequest friendRequest = getById(id);
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友申请不存在");
        }
        
        // 只有待处理的申请可以被接受
        if (!FriendRequestConstant.STATUS_PENDING.equals(friendRequest.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该好友申请已被处理");
        }
        
        // 获取当前登录用户，非管理员只能处理自己收到的好友申请
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(friendRequest.getReceiverId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权处理该好友申请");
        }
        
        // 更新好友申请状态为已接受
        boolean updateResult = updateFriendRequestStatus(id, FriendRequestConstant.STATUS_ACCEPTED);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接受好友申请失败");
        }
        
        // 创建好友关系
        try {
            friendRelationshipService.addFriendRelationship(
                    friendRequest.getSenderId(),
                    friendRequest.getReceiverId(),
                    FriendRelationshipConstant.STATUS_ACCEPTED
            );
            return true;
        } catch (Exception e) {
            log.error("创建好友关系失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建好友关系失败");
        }
    }

    @Override
    public boolean rejectFriendRequest(Long id, HttpServletRequest request) {
        // 参数校验
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友申请ID不能为空");
        }
        
        // 获取好友申请
        FriendRequest friendRequest = getById(id);
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友申请不存在");
        }
        
        // 只有待处理的申请可以被拒绝
        if (!FriendRequestConstant.STATUS_PENDING.equals(friendRequest.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该好友申请已被处理");
        }
        
        // 获取当前登录用户，非管理员只能处理自己收到的好友申请
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(friendRequest.getReceiverId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权处理该好友申请");
        }
        
        // 更新好友申请状态为已拒绝
        return updateFriendRequestStatus(id, FriendRequestConstant.STATUS_REJECTED);
    }

    @Override
    public boolean updateFriendRequestStatus(Long id, String status) {
        // 参数校验
        if (id == null || StringUtils.isBlank(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        // 获取好友申请
        FriendRequest friendRequest = getById(id);
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友申请不存在");
        }
        
        // 更新状态
        friendRequest.setStatus(status);
        return updateById(friendRequest);
    }

    @Override
    public List<FriendRequestVO> listFriendRequestByReceiverId(Long receiverId, String status) {
        // 参数校验
        if (receiverId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接收者ID不能为空");
        }
        
        // 构建查询条件
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiverId", receiverId);
        
        // 如果提供了状态，需要增加状态筛选
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc("createTime");
        
        // 获取好友申请列表
        List<FriendRequest> friendRequests = list(queryWrapper);
        
        // 转换为VO列表
        return getFriendRequestVOList(friendRequests);
    }

    @Override
    public List<FriendRequestVO> listFriendRequestBySenderId(Long senderId, String status) {
        // 参数校验
        if (senderId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发送者ID不能为空");
        }
        
        // 构建查询条件
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("senderId", senderId);
        
        // 如果提供了状态，需要增加状态筛选
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc("createTime");
        
        // 获取好友申请列表
        List<FriendRequest> friendRequests = list(queryWrapper);
        
        // 转换为VO列表
        return getFriendRequestVOList(friendRequests);
    }

    @Override
    public FriendRequest getFriendRequest(Long senderId, Long receiverId) {
        // 参数校验
        if (senderId == null || receiverId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 查询条件
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("senderId", senderId)
                .eq("receiverId", receiverId);
        
        return getOne(queryWrapper);
    }

    @Override
    public boolean existsFriendRequest(Long senderId, Long receiverId) {
        return getFriendRequest(senderId, receiverId) != null;
    }

    @Override
    public QueryWrapper<FriendRequest> getQueryWrapper(FriendRequestQueryRequest friendRequestQueryRequest) {
        if (friendRequestQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        
        Long senderId = friendRequestQueryRequest.getSenderId();
        Long receiverId = friendRequestQueryRequest.getReceiverId();
        String status = friendRequestQueryRequest.getStatus();
        String message = friendRequestQueryRequest.getMessage();
        String sortField = friendRequestQueryRequest.getSortField();
        String sortOrder = friendRequestQueryRequest.getSortOrder();
        
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(senderId != null, "senderId", senderId);
        queryWrapper.eq(receiverId != null, "receiverId", receiverId);
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);
        queryWrapper.like(StringUtils.isNotBlank(message), "message", message);
        
        // 排序
        if (StringUtils.isNotBlank(sortField)) {
            queryWrapper.orderBy(true, "asc".equals(sortOrder), sortField);
        } else {
            queryWrapper.orderByDesc("createTime");
        }
        
        return queryWrapper;
    }

    @Override
    public Page<FriendRequestVO> getFriendRequestVOPage(Page<FriendRequest> friendRequestPage, HttpServletRequest request) {
        List<FriendRequest> friendRequestList = friendRequestPage.getRecords();
        Page<FriendRequestVO> friendRequestVOPage = new Page<>(
                friendRequestPage.getCurrent(),
                friendRequestPage.getSize(),
                friendRequestPage.getTotal()
        );
        
        // 如果好友申请为空，返回空分页
        if (friendRequestList.isEmpty()) {
            friendRequestVOPage.setRecords(new ArrayList<>());
            return friendRequestVOPage;
        }
        
        // 转换为VO列表
        List<FriendRequestVO> friendRequestVOList = getFriendRequestVOList(friendRequestList);
        
        friendRequestVOPage.setRecords(friendRequestVOList);
        return friendRequestVOPage;
    }

    @Override
    public FriendRequestVO getFriendRequestVO(FriendRequest friendRequest) {
        if (friendRequest == null) {
            return null;
        }
        
        FriendRequestVO friendRequestVO = new FriendRequestVO();
        BeanUtils.copyProperties(friendRequest, friendRequestVO);
        
        // 获取发送者和接收者信息
        UserVO senderVO = userService.getUserVOById(friendRequest.getSenderId());
        UserVO receiverVO = userService.getUserVOById(friendRequest.getReceiverId());
        
        friendRequestVO.setSenderUser(senderVO);
        friendRequestVO.setReceiverUser(receiverVO);
        
        // 设置状态描述
        String status = friendRequest.getStatus();
        if (FriendRequestConstant.STATUS_PENDING.equals(status)) {
            friendRequestVO.setStatusDescription("待处理");
        } else if (FriendRequestConstant.STATUS_ACCEPTED.equals(status)) {
            friendRequestVO.setStatusDescription("已接受");
        } else if (FriendRequestConstant.STATUS_REJECTED.equals(status)) {
            friendRequestVO.setStatusDescription("已拒绝");
        } else {
            friendRequestVO.setStatusDescription("未知状态");
        }
        
        return friendRequestVO;
    }

    @Override
    public List<FriendRequestVO> getFriendRequestVOList(List<FriendRequest> friendRequestList) {
        if (friendRequestList == null || friendRequestList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return friendRequestList.stream()
                .map(this::getFriendRequestVO)
                .collect(Collectors.toList());
    }
} 
package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.FriendRequestConstant;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.friendrequest.FriendRequestAddRequest;
import com.ubanillx.smartclass.model.dto.friendrequest.FriendRequestQueryRequest;
import com.ubanillx.smartclass.model.dto.friendrequest.FriendRequestUpdateRequest;
import com.ubanillx.smartclass.model.entity.FriendRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.FriendRequestVO;
import com.ubanillx.smartclass.service.FriendRequestService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 好友申请接口
 */
@RestController
@RequestMapping("/friend/request")
@Slf4j
public class FriendRequestController {

    @Resource
    private FriendRequestService friendRequestService;

    @Resource
    private UserService userService;

    /**
     * 发送好友申请
     *
     * @param friendRequestAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addFriendRequest(@RequestBody FriendRequestAddRequest friendRequestAddRequest,
                                         HttpServletRequest request) {
        if (friendRequestAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 参数校验
        Long receiverId = friendRequestAddRequest.getReceiverId();
        String message = friendRequestAddRequest.getMessage();
        
        if (receiverId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接收者ID不能为空");
        }
        
        // 获取当前登录用户作为发送者
        User loginUser = userService.getLoginUser(request);
        Long senderId = loginUser.getId();
        
        // 创建好友申请
        long id = friendRequestService.addFriendRequest(senderId, receiverId, message);
        return ResultUtils.success(id);
    }

    /**
     * 接受好友申请
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/accept")
    public BaseResponse<Boolean> acceptFriendRequest(@RequestParam Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        boolean result = friendRequestService.acceptFriendRequest(id, request);
        return ResultUtils.success(result);
    }

    /**
     * 拒绝好友申请
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/reject")
    public BaseResponse<Boolean> rejectFriendRequest(@RequestParam Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        boolean result = friendRequestService.rejectFriendRequest(id, request);
        return ResultUtils.success(result);
    }

    /**
     * 更新好友申请
     *
     * @param friendRequestUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateFriendRequest(@RequestBody FriendRequestUpdateRequest friendRequestUpdateRequest,
                                              HttpServletRequest request) {
        if (friendRequestUpdateRequest == null || friendRequestUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 参数校验
        Long id = friendRequestUpdateRequest.getId();
        String status = friendRequestUpdateRequest.getStatus();
        
        // 权限校验
        FriendRequest friendRequest = friendRequestService.getById(id);
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 获取当前登录用户，非管理员只能处理自己收到的好友申请
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(friendRequest.getReceiverId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权更新该好友申请");
        }
        
        boolean result = friendRequestService.updateFriendRequestStatus(id, status);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取好友申请
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<FriendRequestVO> getFriendRequestById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        FriendRequest friendRequest = friendRequestService.getById(id);
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 权限校验：只有发送者、接收者和管理员可以查看
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        if (!userService.isAdmin(loginUser) && 
            !loginUserId.equals(friendRequest.getSenderId()) && 
            !loginUserId.equals(friendRequest.getReceiverId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看该好友申请");
        }
        
        FriendRequestVO friendRequestVO = friendRequestService.getFriendRequestVO(friendRequest);
        return ResultUtils.success(friendRequestVO);
    }

    /**
     * 获取我收到的好友申请
     *
     * @param status
     * @param request
     * @return
     */
    @GetMapping("/received")
    public BaseResponse<List<FriendRequestVO>> getReceivedFriendRequests(
            @RequestParam(required = false) String status, 
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<FriendRequestVO> friendRequests = friendRequestService.listFriendRequestByReceiverId(
                loginUser.getId(), status);
        return ResultUtils.success(friendRequests);
    }

    /**
     * 获取我发送的好友申请
     *
     * @param status
     * @param request
     * @return
     */
    @GetMapping("/sent")
    public BaseResponse<List<FriendRequestVO>> getSentFriendRequests(
            @RequestParam(required = false) String status, 
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<FriendRequestVO> friendRequests = friendRequestService.listFriendRequestBySenderId(
                loginUser.getId(), status);
        return ResultUtils.success(friendRequests);
    }

    /**
     * 分页查询好友申请
     *
     * @param friendRequestQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<FriendRequest>> listFriendRequestByPage(@RequestBody FriendRequestQueryRequest friendRequestQueryRequest,
                                                    HttpServletRequest request) {
        long current = friendRequestQueryRequest.getCurrent();
        long size = friendRequestQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<FriendRequest> friendRequestPage = friendRequestService.page(
                new Page<>(current, size),
                friendRequestService.getQueryWrapper(friendRequestQueryRequest)
        );
        
        return ResultUtils.success(friendRequestPage);
    }

    /**
     * 分页获取好友申请VO
     *
     * @param friendRequestQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<FriendRequestVO>> listFriendRequestVOByPage(@RequestBody FriendRequestQueryRequest friendRequestQueryRequest,
                                                      HttpServletRequest request) {
        if (friendRequestQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long current = friendRequestQueryRequest.getCurrent();
        long size = friendRequestQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        // 非管理员只能查看自己相关的好友申请
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            // 如果是普通用户，只能查询与自己相关的申请
            if (friendRequestQueryRequest.getSenderId() != null && 
                !friendRequestQueryRequest.getSenderId().equals(loginUser.getId()) && 
                (friendRequestQueryRequest.getReceiverId() == null || 
                 !friendRequestQueryRequest.getReceiverId().equals(loginUser.getId()))) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只能查询与自己相关的好友申请");
            }
            
            if (friendRequestQueryRequest.getReceiverId() != null && 
                !friendRequestQueryRequest.getReceiverId().equals(loginUser.getId()) && 
                (friendRequestQueryRequest.getSenderId() == null || 
                 !friendRequestQueryRequest.getSenderId().equals(loginUser.getId()))) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只能查询与自己相关的好友申请");
            }
            
            // 如果两个ID都没有指定，则默认查询与自己相关的所有申请
            if (friendRequestQueryRequest.getSenderId() == null && 
                friendRequestQueryRequest.getReceiverId() == null) {
                friendRequestQueryRequest.setSenderId(loginUser.getId());
                friendRequestQueryRequest.setReceiverId(loginUser.getId());
                // 使用自定义SQL处理OR条件查询
            }
        }
        
        Page<FriendRequest> friendRequestPage = friendRequestService.page(
                new Page<>(current, size),
                friendRequestService.getQueryWrapper(friendRequestQueryRequest)
        );
        
        Page<FriendRequestVO> friendRequestVOPage = friendRequestService.getFriendRequestVOPage(
                friendRequestPage, request);
        
        return ResultUtils.success(friendRequestVOPage);
    }

    /**
     * 删除好友申请
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFriendRequest(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long id = deleteRequest.getId();
        
        // 权限校验
        FriendRequest friendRequest = friendRequestService.getById(id);
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        
        // 只有发送者、接收者和管理员可以删除申请
        if (!userService.isAdmin(loginUser) && 
            !loginUserId.equals(friendRequest.getSenderId()) && 
            !loginUserId.equals(friendRequest.getReceiverId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权删除该好友申请");
        }
        
        boolean result = friendRequestService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return ResultUtils.success(true);
    }

    /**
     * 获取用户收到的待处理好友申请数量
     *
     * @param request
     * @return
     */
    @GetMapping("/count/pending")
    public BaseResponse<Long> getPendingRequestCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        // 查询条件：当前用户收到的待处理申请
        FriendRequestQueryRequest queryRequest = new FriendRequestQueryRequest();
        queryRequest.setReceiverId(loginUser.getId());
        queryRequest.setStatus(FriendRequestConstant.STATUS_PENDING);
        
        long count = friendRequestService.count(friendRequestService.getQueryWrapper(queryRequest));
        return ResultUtils.success(count);
    }
} 
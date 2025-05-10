package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.FriendRelationshipConstant;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.friendrelationship.FriendRelationshipAddRequest;
import com.ubanillx.smartclass.model.dto.friendrelationship.FriendRelationshipQueryRequest;
import com.ubanillx.smartclass.model.dto.friendrelationship.FriendRelationshipUpdateRequest;
import com.ubanillx.smartclass.model.entity.FriendRelationship;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.FriendRelationshipVO;
import com.ubanillx.smartclass.service.FriendRelationshipService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 好友关系接口
 */
@RestController
@RequestMapping("/friend/relationship")
@Slf4j
public class FriendRelationshipController {

    @Resource
    private FriendRelationshipService friendRelationshipService;

    @Resource
    private UserService userService;

    /**
     * 创建好友关系
     *
     * @param friendRelationshipAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addFriendRelationship(@RequestBody FriendRelationshipAddRequest friendRelationshipAddRequest,
                                              HttpServletRequest request) {
        if (friendRelationshipAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 参数校验
        Long userId1 = friendRelationshipAddRequest.getUserId1();
        Long userId2 = friendRelationshipAddRequest.getUserId2();
        String status = friendRelationshipAddRequest.getStatus();
        
        if (userId1 == null || userId2 == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 获取当前登录用户，非管理员只能创建自己的好友关系
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(userId1) && !loginUser.getId().equals(userId2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权创建他人的好友关系");
        }
        
        long id = friendRelationshipService.addFriendRelationship(userId1, userId2, status);
        return ResultUtils.success(id);
    }

    /**
     * 更新好友关系
     *
     * @param friendRelationshipUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateFriendRelationship(@RequestBody FriendRelationshipUpdateRequest friendRelationshipUpdateRequest,
                                                HttpServletRequest request) {
        if (friendRelationshipUpdateRequest == null || friendRelationshipUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 参数校验
        Long id = friendRelationshipUpdateRequest.getId();
        String status = friendRelationshipUpdateRequest.getStatus();
        
        // 权限校验
        FriendRelationship friendRelationship = friendRelationshipService.getById(id);
        if (friendRelationship == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 获取当前登录用户，非管理员只能更新自己的好友关系
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        if (!userService.isAdmin(loginUser) && 
            !loginUserId.equals(friendRelationship.getUserId1()) && 
            !loginUserId.equals(friendRelationship.getUserId2())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权更新他人的好友关系");
        }
        
        boolean result = friendRelationshipService.updateFriendRelationshipStatus(id, status);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取好友关系
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<FriendRelationship> getFriendRelationshipById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        FriendRelationship friendRelationship = friendRelationshipService.getById(id);
        if (friendRelationship == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 权限校验：只有关系中的用户和管理员可以查看
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        if (!userService.isAdmin(loginUser) && 
            !loginUserId.equals(friendRelationship.getUserId1()) && 
            !loginUserId.equals(friendRelationship.getUserId2())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看他人的好友关系");
        }
        
        return ResultUtils.success(friendRelationship);
    }

    /**
     * 根据用户获取好友关系
     *
     * @param userId1
     * @param userId2
     * @param request
     * @return
     */
    @GetMapping("/getByUsers")
    public BaseResponse<FriendRelationship> getFriendRelationshipByUsers(@RequestParam("userId1") long userId1,
                                                      @RequestParam("userId2") long userId2,
                                                      HttpServletRequest request) {
        if (userId1 <= 0 || userId2 <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 权限校验：只有关系中的用户和管理员可以查看
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        if (!userService.isAdmin(loginUser) && 
            !loginUserId.equals(userId1) && 
            !loginUserId.equals(userId2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看他人的好友关系");
        }
        
        FriendRelationship friendRelationship = friendRelationshipService.getFriendRelationship(userId1, userId2);
        return ResultUtils.success(friendRelationship);
    }

    /**
     * 检查是否为好友关系
     *
     * @param userId1
     * @param userId2
     * @param request
     * @return
     */
    @GetMapping("/isFriend")
    public BaseResponse<Boolean> isFriend(@RequestParam("userId1") long userId1,
                                   @RequestParam("userId2") long userId2,
                                   HttpServletRequest request) {
        if (userId1 <= 0 || userId2 <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        boolean isFriend = friendRelationshipService.isFriend(userId1, userId2);
        return ResultUtils.success(isFriend);
    }

    /**
     * 获取用户的好友列表
     *
     * @param userId
     * @param request
     * @return
     */
    @GetMapping("/listFriends")
    public BaseResponse<List<FriendRelationshipVO>> listUserFriends(long userId, HttpServletRequest request) {
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 权限校验：只有自己和管理员可以查看好友列表
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看他人的好友列表");
        }
        
        List<FriendRelationshipVO> friendList = friendRelationshipService.listUserFriends(userId);
        return ResultUtils.success(friendList);
    }

    /**
     * 获取自己的好友列表
     *
     * @param request
     * @return
     */
    @GetMapping("/my/list")
    public BaseResponse<List<FriendRelationshipVO>> listMyFriends(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<FriendRelationshipVO> friendList = friendRelationshipService.listUserFriends(loginUser.getId());
        return ResultUtils.success(friendList);
    }

    /**
     * 分页查询好友关系
     *
     * @param friendRelationshipQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<FriendRelationship>> listFriendRelationshipByPage(@RequestBody FriendRelationshipQueryRequest friendRelationshipQueryRequest,
                                                         HttpServletRequest request) {
        long current = friendRelationshipQueryRequest.getCurrent();
        long size = friendRelationshipQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<FriendRelationship> friendRelationshipPage = friendRelationshipService.page(
                new Page<>(current, size),
                friendRelationshipService.getQueryWrapper(friendRelationshipQueryRequest)
        );
        
        return ResultUtils.success(friendRelationshipPage);
    }

    /**
     * 分页获取好友关系VO
     *
     * @param friendRelationshipQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<FriendRelationshipVO>> listFriendRelationshipVOByPage(@RequestBody FriendRelationshipQueryRequest friendRelationshipQueryRequest,
                                                           HttpServletRequest request) {
        if (friendRelationshipQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long current = friendRelationshipQueryRequest.getCurrent();
        long size = friendRelationshipQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        // 非管理员只能查看自己的好友关系
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            friendRelationshipQueryRequest.setUserId(loginUser.getId());
        }
        
        Page<FriendRelationship> friendRelationshipPage = friendRelationshipService.page(
                new Page<>(current, size),
                friendRelationshipService.getQueryWrapper(friendRelationshipQueryRequest)
        );
        
        Page<FriendRelationshipVO> friendRelationshipVOPage = friendRelationshipService.getFriendRelationshipVOPage(
                friendRelationshipPage, request);
        
        return ResultUtils.success(friendRelationshipVOPage);
    }

    /**
     * 删除好友关系
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFriendRelationship(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long id = deleteRequest.getId();
        
        // 权限校验
        FriendRelationship friendRelationship = friendRelationshipService.getById(id);
        if (friendRelationship == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 获取当前登录用户，非管理员只能删除自己的好友关系
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        if (!userService.isAdmin(loginUser) && 
            !loginUserId.equals(friendRelationship.getUserId1()) && 
            !loginUserId.equals(friendRelationship.getUserId2())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权删除他人的好友关系");
        }
        
        boolean result = friendRelationshipService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return ResultUtils.success(true);
    }

    /**
     * 删除与指定用户的好友关系
     *
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/delete/friend")
    public BaseResponse<Boolean> deleteFriendByUserId(@RequestParam("userId") long userId, HttpServletRequest request) {
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        
        boolean result = friendRelationshipService.deleteFriendRelationship(loginUserId, userId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return ResultUtils.success(true);
    }

    /**
     * 屏蔽好友
     *
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/block")
    public BaseResponse<Boolean> blockFriend(@RequestParam("userId") long userId, HttpServletRequest request) {
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        
        // 获取好友关系
        FriendRelationship relationship = friendRelationshipService.getFriendRelationship(loginUserId, userId);
        if (relationship == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友关系不存在");
        }
        
        // 更新状态为屏蔽
        boolean result = friendRelationshipService.updateFriendRelationshipStatus(
                relationship.getId(), 
                FriendRelationshipConstant.STATUS_BLOCKED
        );
        
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return ResultUtils.success(true);
    }
} 
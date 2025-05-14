package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友关系接口
 */
@RestController
@RequestMapping("/friends")
@Slf4j
public class FriendRelationshipController {

    @Resource
    private FriendRelationshipService friendRelationshipService;

    @Resource
    private UserService userService;

    /**
     * 创建好友关系
     *
     * @param friendRelationshipAddRequest 好友关系创建请求，包含好友ID和关系状态
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 新创建的好友关系ID
     */
    @PostMapping
    public BaseResponse<Long> addFriendRelationship(@RequestBody FriendRelationshipAddRequest friendRelationshipAddRequest,
                                              HttpServletRequest request) {
        if (friendRelationshipAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 参数校验
        Long userId2 = friendRelationshipAddRequest.getUserId2();
        String status = friendRelationshipAddRequest.getStatus();

        if (userId2 == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友ID不能为空");
        }

        // 获取当前登录用户ID作为userId1
        User loginUser = userService.getLoginUser(request);
        Long userId1 = loginUser.getId();

        // 非管理员只能创建自己的好友关系
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(userId1)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权创建他人的好友关系");
        }

        long id = friendRelationshipService.addFriendRelationship(userId1, userId2, status);
        return ResultUtils.success(id);
    }

    /**
     * 更新好友关系
     *
     * @param friendRelationshipUpdateRequest 好友关系更新请求，包含关系ID、用户ID和状态
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 更新结果，true表示更新成功，false表示更新失败
     */
    @PutMapping
    public BaseResponse<Boolean> updateFriendRelationship(@RequestBody FriendRelationshipUpdateRequest friendRelationshipUpdateRequest,
                                                HttpServletRequest request) {
        if (friendRelationshipUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();

        Long id = friendRelationshipUpdateRequest.getId();
        Long userId = friendRelationshipUpdateRequest.getUserId();
        String status = friendRelationshipUpdateRequest.getStatus();

        FriendRelationship friendRelationship;
        // 根据ID或对方用户ID查找好友关系
        if (id != null) {
            // 根据ID更新
            friendRelationship = friendRelationshipService.getById(id);
            if (friendRelationship == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友关系不存在");
            }

            // 权限校验，非管理员只能更新自己的好友关系
            if (!userService.isAdmin(loginUser) &&
                !loginUserId.equals(friendRelationship.getUserId1()) &&
                !loginUserId.equals(friendRelationship.getUserId2())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权更新他人的好友关系");
            }
        } else if (userId != null) {
            // 根据对方用户ID和当前用户ID查找好友关系
            friendRelationship = friendRelationshipService.getFriendRelationship(loginUserId, userId);
            if (friendRelationship == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友关系不存在");
            }
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "缺少好友关系ID或用户ID");
        }

        boolean result = friendRelationshipService.updateFriendRelationshipStatus(friendRelationship.getId(), status);
        return ResultUtils.success(result);
    }

    /**
     * 获取好友关系
     *
     * @param id 关系ID，如果提供则通过ID查询
     * @param userId 用户ID，如果提供则查询当前用户与该用户的关系
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 好友关系信息，包含是否为好友状态
     */
    @GetMapping("/relation")
    public BaseResponse<Object> getFriendRelationship(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "userId", required = false) Long userId,
            HttpServletRequest request) {

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();

        // 通过ID查询好友关系
        if (id != null && id > 0) {
            FriendRelationship friendRelationship = friendRelationshipService.getById(id);
            if (friendRelationship == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "好友关系不存在");
            }

            // 权限校验：只有关系中的用户和管理员可以查看
            if (!userService.isAdmin(loginUser) &&
                !loginUserId.equals(friendRelationship.getUserId1()) &&
                !loginUserId.equals(friendRelationship.getUserId2())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看他人的好友关系");
            }

            return ResultUtils.success(friendRelationship);
        }
        // 通过用户ID查询与当前用户的好友关系
        else if (userId != null && userId > 0) {
            FriendRelationship relationship = friendRelationshipService.getFriendRelationship(loginUserId, userId);

            // 如果关系不存在，返回关系不存在和不是好友
            if (relationship == null) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("exists", false);
                resultMap.put("isFriend", false);
                return ResultUtils.success(resultMap);
            }

            // 如果关系存在，返回关系信息和是否为好友
            boolean isFriend = FriendRelationshipConstant.STATUS_ACCEPTED.equals(relationship.getStatus());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("exists", true);
            resultMap.put("isFriend", isFriend);
            resultMap.put("relationship", relationship);
            return ResultUtils.success(resultMap);
        }
        // 参数错误
        else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不正确，需要提供关系ID或用户ID");
        }
    }

    /**
     * 获取用户的好友列表
     *
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 当前用户的好友列表，包含好友信息
     */
    @GetMapping
    public BaseResponse<List<FriendRelationshipVO>> listUserFriends(HttpServletRequest request) {
        // 获取当前登录用户ID
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();

        List<FriendRelationshipVO> friendList = friendRelationshipService.listUserFriends(userId);
        return ResultUtils.success(friendList);
    }

    /**
     * 分页查询好友关系（管理员）
     *
     * @param friendRelationshipQueryRequest 查询参数，包含分页信息和筛选条件
     * @return 分页好友关系结果
     */
    @GetMapping("/admin/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<FriendRelationship>> listFriendRelationshipByPage(FriendRelationshipQueryRequest friendRelationshipQueryRequest) {
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
     * @param friendRelationshipQueryRequest 查询参数，包含分页信息和筛选条件
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 分页好友关系VO结果，包含详细的好友信息
     */
    @GetMapping("/page")
    public BaseResponse<Page<FriendRelationshipVO>> listFriendRelationshipVOByPage(FriendRelationshipQueryRequest friendRelationshipQueryRequest,
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
     * @param id 要删除的好友关系ID
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 删除结果，true表示删除成功，false表示删除失败
     */
    @DeleteMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteFriendRelationship(@PathVariable("id") long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

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
     * @param userId 要删除关系的好友用户ID
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 删除结果，true表示删除成功，false表示删除失败
     */
    @DeleteMapping("/user/{userId}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteFriendByUserId(@PathVariable("userId") long userId, HttpServletRequest request) {
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
} 
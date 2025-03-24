package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.model.dto.DeleteRequest;
import com.ubanillx.smartclass.model.dto.useraiavatar.UserAiAvatarAddRequest;
import com.ubanillx.smartclass.model.dto.useraiavatar.UserAiAvatarQueryRequest;
import com.ubanillx.smartclass.model.dto.useraiavatar.UserAiAvatarUpdateRequest;
import com.ubanillx.smartclass.model.entity.UserAiAvatar;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.UserAiAvatarVO;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户AI分身关联接口
 */
@RestController
@RequestMapping("/user_ai_avatar")
@Slf4j
public class UserAiAvatarController {

    @Resource
    private UserAiAvatarService userAiAvatarService;

    @Resource
    private UserService userService;

    /**
     * 创建用户与AI分身的关联
     *
     * @param userAiAvatarAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserAiAvatar(@RequestBody UserAiAvatarAddRequest userAiAvatarAddRequest, HttpServletRequest request) {
        if (userAiAvatarAddRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        UserAiAvatar userAiAvatar = new UserAiAvatar();
        BeanUtils.copyProperties(userAiAvatarAddRequest, userAiAvatar);
        // 校验
        User loginUser = userService.getLoginUser(request);
        if (userAiAvatarAddRequest.getUserId() == null) {
            userAiAvatar.setUserId(loginUser.getId());
        } else {
            // 如果传入了userId，则需要管理员权限
            if (!userService.isAdmin(loginUser)) {
                return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
            }
        }
        boolean result = userAiAvatarService.save(userAiAvatar);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(userAiAvatar.getId());
    }

    /**
     * 删除用户与AI分身的关联
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserAiAvatar(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserAiAvatar userAiAvatar = userAiAvatarService.getById(id);
        if (userAiAvatar == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!userAiAvatar.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userAiAvatarService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新用户与AI分身的关联
     *
     * @param userAiAvatarUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUserAiAvatar(@RequestBody UserAiAvatarUpdateRequest userAiAvatarUpdateRequest,
            HttpServletRequest request) {
        if (userAiAvatarUpdateRequest == null || userAiAvatarUpdateRequest.getId() <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        UserAiAvatar userAiAvatar = new UserAiAvatar();
        BeanUtils.copyProperties(userAiAvatarUpdateRequest, userAiAvatar);
        // 参数校验
        User loginUser = userService.getLoginUser(request);
        long id = userAiAvatarUpdateRequest.getId();
        // 判断是否存在
        UserAiAvatar oldUserAiAvatar = userAiAvatarService.getById(id);
        if (oldUserAiAvatar == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldUserAiAvatar.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userAiAvatarService.updateById(userAiAvatar);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户与AI分身的关联
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserAiAvatarVO> getUserAiAvatarById(long id, HttpServletRequest request) {
        if (id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        UserAiAvatar userAiAvatar = userAiAvatarService.getById(id);
        if (userAiAvatar == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可查看
        if (!userAiAvatar.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        
        UserAiAvatarVO userAiAvatarVO = new UserAiAvatarVO();
        BeanUtils.copyProperties(userAiAvatar, userAiAvatarVO);
        
        // 获取用户和AI分身的详细信息
        // 这里简化处理，实际实现应该调用service层方法
        User user = userService.getById(userAiAvatar.getUserId());
        if (user != null) {
            userAiAvatarVO.setUserName(user.getUserName());
            userAiAvatarVO.setUserAvatar(user.getUserAvatar());
        }
        
        return ResultUtils.success(userAiAvatarVO);
    }

    /**
     * 获取登录用户的所有AI分身关联列表
     *
     * @param request
     * @return
     */
    @GetMapping("/my/list")
    public BaseResponse<List<UserAiAvatarVO>> listMyUserAiAvatars(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<UserAiAvatarVO> userAiAvatarVOList = userAiAvatarService.getUserFavoriteAiAvatars(loginUser.getId());
        return ResultUtils.success(userAiAvatarVOList);
    }

    /**
     * 分页获取登录用户的AI分身关联列表
     *
     * @param userAiAvatarQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list/page")
    public BaseResponse<Page<UserAiAvatarVO>> listMyUserAiAvatarsByPage(UserAiAvatarQueryRequest userAiAvatarQueryRequest, HttpServletRequest request) {
        if (userAiAvatarQueryRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        userAiAvatarQueryRequest.setUserId(loginUser.getId());
        
        long current = userAiAvatarQueryRequest.getCurrent();
        long size = userAiAvatarQueryRequest.getPageSize();
        
        // 限制爬虫
        if (size > 50) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        Page<UserAiAvatarVO> userAiAvatarVOPage = userAiAvatarService.getUserAiAvatarPage(loginUser.getId(), current, size);
        return ResultUtils.success(userAiAvatarVOPage);
    }
    
    /**
     * 收藏/取消收藏AI分身
     *
     * @param aiAvatarId AI分身id
     * @param isFavorite 是否收藏：0-否，1-是
     * @param request
     * @return
     */
    @PostMapping("/favorite")
    public BaseResponse<Boolean> favoriteAiAvatar(@RequestParam Long aiAvatarId, @RequestParam Integer isFavorite, HttpServletRequest request) {
        if (aiAvatarId == null || aiAvatarId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        if (isFavorite == null || (isFavorite != 0 && isFavorite != 1)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "收藏状态参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = userAiAvatarService.favoriteAiAvatar(loginUser.getId(), aiAvatarId, isFavorite);
        return ResultUtils.success(result);
    }
    
    /**
     * 使用AI分身，记录使用次数
     *
     * @param aiAvatarId AI分身id
     * @param request
     * @return
     */
    @PostMapping("/use")
    public BaseResponse<Boolean> useAiAvatar(@RequestParam Long aiAvatarId, HttpServletRequest request) {
        if (aiAvatarId == null || aiAvatarId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = userAiAvatarService.useAiAvatar(loginUser.getId(), aiAvatarId);
        return ResultUtils.success(result);
    }
    
    /**
     * 用户对AI分身评分和反馈
     *
     * @param aiAvatarId AI分身id
     * @param rating 评分（1-5分）
     * @param feedback 反馈内容
     * @param request
     * @return
     */
    @PostMapping("/rate")
    public BaseResponse<Boolean> rateAiAvatar(@RequestParam Long aiAvatarId, @RequestParam java.math.BigDecimal rating, 
                                     @RequestParam(required = false) String feedback, HttpServletRequest request) {
        if (aiAvatarId == null || aiAvatarId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        if (rating == null || rating.compareTo(java.math.BigDecimal.ONE) < 0 || rating.compareTo(new java.math.BigDecimal(5)) > 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "评分必须在1-5分之间");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = userAiAvatarService.rateAiAvatar(loginUser.getId(), aiAvatarId, rating, feedback);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取用户收藏的AI分身列表
     *
     * @param request
     * @return
     */
    @GetMapping("/favorite/list")
    public BaseResponse<List<UserAiAvatarVO>> listFavoriteAiAvatars(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<UserAiAvatarVO> userAiAvatarVOList = userAiAvatarService.getUserFavoriteAiAvatars(loginUser.getId());
        return ResultUtils.success(userAiAvatarVOList);
    }
} 
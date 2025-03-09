package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.aiavatar.UserAiAvatarRequest;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserAiAvatar;
import com.ubanillx.smartclass.model.vo.AiAvatarVO;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户AI分身交互接口
 */
@RestController
@RequestMapping("/user/ai/avatar")
@Slf4j
public class UserAiAvatarController {

    @Resource
    private UserAiAvatarService userAiAvatarService;

    @Resource
    private AiAvatarService aiAvatarService;

    @Resource
    private UserService userService;

    /**
     * 收藏AI分身
     *
     * @param aiAvatarId
     * @param request
     * @return
     */
    @PostMapping("/favorite")
    public BaseResponse<Boolean> favoriteAiAvatar(@RequestParam Long aiAvatarId, HttpServletRequest request) {
        if (aiAvatarId == null || aiAvatarId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断AI分身是否存在
        AiAvatar aiAvatar = aiAvatarService.getAiAvatarById(aiAvatarId);
        ThrowUtils.throwIf(aiAvatar == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 收藏AI分身
        boolean result = userAiAvatarService.favoriteAiAvatar(loginUser.getId(), aiAvatarId);
        return ResultUtils.success(result);
    }

    /**
     * 取消收藏AI分身
     *
     * @param aiAvatarId
     * @param request
     * @return
     */
    @PostMapping("/unfavorite")
    public BaseResponse<Boolean> unfavoriteAiAvatar(@RequestParam Long aiAvatarId, HttpServletRequest request) {
        if (aiAvatarId == null || aiAvatarId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 取消收藏AI分身
        boolean result = userAiAvatarService.unfavoriteAiAvatar(loginUser.getId(), aiAvatarId);
        return ResultUtils.success(result);
    }

    /**
     * 评分AI分身
     *
     * @param userAiAvatarRequest
     * @param request
     * @return
     */
    @PostMapping("/rate")
    public BaseResponse<Boolean> rateAiAvatar(@RequestBody UserAiAvatarRequest userAiAvatarRequest, HttpServletRequest request) {
        if (userAiAvatarRequest == null || userAiAvatarRequest.getAiAvatarId() == null || userAiAvatarRequest.getRating() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 评分AI分身
        boolean result = userAiAvatarService.rateAiAvatar(
                loginUser.getId(), 
                userAiAvatarRequest.getAiAvatarId(), 
                userAiAvatarRequest.getRating(), 
                userAiAvatarRequest.getFeedback()
        );
        return ResultUtils.success(result);
    }

    /**
     * 更新用户自定义设置
     *
     * @param userAiAvatarRequest
     * @param request
     * @return
     */
    @PostMapping("/settings")
    public BaseResponse<Boolean> updateCustomSettings(@RequestBody UserAiAvatarRequest userAiAvatarRequest, HttpServletRequest request) {
        if (userAiAvatarRequest == null || userAiAvatarRequest.getAiAvatarId() == null || userAiAvatarRequest.getCustomSettings() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 更新自定义设置
        boolean result = userAiAvatarService.updateCustomSettings(
                loginUser.getId(), 
                userAiAvatarRequest.getAiAvatarId(), 
                userAiAvatarRequest.getCustomSettings()
        );
        return ResultUtils.success(result);
    }

    /**
     * 获取用户收藏的AI分身列表
     *
     * @param current
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/favorite/list")
    public BaseResponse<Page<AiAvatarVO>> listUserFavoriteAiAvatars(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 分页获取用户收藏的AI分身
        Page<UserAiAvatar> userAiAvatarPage = userAiAvatarService.listUserFavoriteAiAvatars(loginUser.getId(), current, size);
        
        // 转换为VO
        Page<AiAvatarVO> aiAvatarVOPage = new Page<>(current, size, userAiAvatarPage.getTotal());
        List<AiAvatarVO> aiAvatarVOList = userAiAvatarPage.getRecords().stream()
                .map(userAiAvatar -> {
                    // 获取AI分身信息
                    AiAvatar aiAvatar = aiAvatarService.getById(userAiAvatar.getAiAvatarId());
                    if (aiAvatar == null || aiAvatar.getIsDelete() == 1) {
                        return null;
                    }
                    
                    // 转换为VO
                    AiAvatarVO aiAvatarVO = new AiAvatarVO();
                    BeanUtils.copyProperties(aiAvatar, aiAvatarVO);
                    
                    // 设置用户相关信息
                    aiAvatarVO.setIsFavorite(userAiAvatar.getIsFavorite() == 1);
                    aiAvatarVO.setUserUseCount(userAiAvatar.getUseCount());
                    aiAvatarVO.setUserRating(userAiAvatar.getUserRating());
                    aiAvatarVO.setCustomSettings(userAiAvatar.getCustomSettings());
                    
                    return aiAvatarVO;
                })
                .filter(aiAvatarVO -> aiAvatarVO != null)
                .collect(Collectors.toList());
        aiAvatarVOPage.setRecords(aiAvatarVOList);
        
        return ResultUtils.success(aiAvatarVOPage);
    }

    /**
     * 获取用户最近使用的AI分身列表
     *
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/recent")
    public BaseResponse<List<AiAvatarVO>> listUserRecentAiAvatars(
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 获取用户最近使用的AI分身
        List<UserAiAvatar> userAiAvatarList = userAiAvatarService.listUserRecentAiAvatars(loginUser.getId(), limit);
        
        // 转换为VO
        List<AiAvatarVO> aiAvatarVOList = userAiAvatarList.stream()
                .map(userAiAvatar -> {
                    // 获取AI分身信息
                    AiAvatar aiAvatar = aiAvatarService.getById(userAiAvatar.getAiAvatarId());
                    if (aiAvatar == null || aiAvatar.getIsDelete() == 1) {
                        return null;
                    }
                    
                    // 转换为VO
                    AiAvatarVO aiAvatarVO = new AiAvatarVO();
                    BeanUtils.copyProperties(aiAvatar, aiAvatarVO);
                    
                    // 设置用户相关信息
                    aiAvatarVO.setIsFavorite(userAiAvatar.getIsFavorite() == 1);
                    aiAvatarVO.setUserUseCount(userAiAvatar.getUseCount());
                    aiAvatarVO.setUserRating(userAiAvatar.getUserRating());
                    aiAvatarVO.setCustomSettings(userAiAvatar.getCustomSettings());
                    
                    return aiAvatarVO;
                })
                .filter(aiAvatarVO -> aiAvatarVO != null)
                .collect(Collectors.toList());
        
        return ResultUtils.success(aiAvatarVOList);
    }
} 
package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.userlevel.UserLevelAddRequest;
import com.ubanillx.smartclass.model.dto.userlevel.UserLevelQueryRequest;
import com.ubanillx.smartclass.model.dto.userlevel.UserLevelUpdateRequest;
import com.ubanillx.smartclass.model.entity.UserLevel;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.UserLevelVO;
import com.ubanillx.smartclass.service.UserLevelService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户等级接口
 */
@RestController
@RequestMapping("/user/level")
@Slf4j
public class UserLevelController {

    @Resource
    private UserLevelService userLevelService;

    @Resource
    private UserService userService;

    // region 管理员接口 - 增删改查

    /**
     * 创建用户等级（仅管理员）
     *
     * @param userLevelAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserLevel(@RequestBody UserLevelAddRequest userLevelAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLevelAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        UserLevel userLevel = new UserLevel();
        BeanUtils.copyProperties(userLevelAddRequest, userLevel);
        // 校验
        userLevelService.validUserLevel(userLevel, true);
        
        // 添加用户等级
        long userLevelId = userLevelService.addUserLevel(userLevel);
        return ResultUtils.success(userLevelId);
    }

    /**
     * 删除用户等级（仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserLevel(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        long id = deleteRequest.getId();
        
        // 判断是否存在
        UserLevel userLevel = userLevelService.getById(id);
        ThrowUtils.throwIf(userLevel == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可删除
        boolean result = userLevelService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户等级（仅管理员）
     *
     * @param userLevelUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserLevel(@RequestBody UserLevelUpdateRequest userLevelUpdateRequest,
                                              HttpServletRequest request) {
        ThrowUtils.throwIf(userLevelUpdateRequest == null || userLevelUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        UserLevel userLevel = new UserLevel();
        BeanUtils.copyProperties(userLevelUpdateRequest, userLevel);
        
        // 校验
        userLevelService.validUserLevel(userLevel, false);
        
        // 判断是否存在
        long id = userLevelUpdateRequest.getId();
        UserLevel oldUserLevel = userLevelService.getById(id);
        ThrowUtils.throwIf(oldUserLevel == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可更新
        boolean result = userLevelService.updateById(userLevel);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户等级（仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserLevel> getUserLevelById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLevel userLevel = userLevelService.getById(id);
        if (userLevel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userLevel);
    }

    /**
     * 分页获取用户等级列表（仅管理员）
     *
     * @param userLevelQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserLevel>> listUserLevelByPage(@RequestBody UserLevelQueryRequest userLevelQueryRequest) {
        long current = userLevelQueryRequest.getCurrent();
        long size = userLevelQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<UserLevel> userLevelPage = userLevelService.page(new Page<>(current, size),
                userLevelService.getQueryWrapper(userLevelQueryRequest));
        return ResultUtils.success(userLevelPage);
    }

    /**
     * 分页获取用户等级列表（封装类）（仅管理员）
     *
     * @param userLevelQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserLevelVO>> listUserLevelVOByPage(@RequestBody UserLevelQueryRequest userLevelQueryRequest,
                                                          HttpServletRequest request) {
        long current = userLevelQueryRequest.getCurrent();
        long size = userLevelQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        // 查询用户等级
        Page<UserLevel> userLevelPage = userLevelService.page(new Page<>(current, size),
                userLevelService.getQueryWrapper(userLevelQueryRequest));
        
        // 获取封装
        Page<UserLevelVO> userLevelVOPage = userLevelService.getUserLevelVOPage(userLevelPage, request);
        return ResultUtils.success(userLevelVOPage);
    }

    // endregion

    // region 普通用户接口

    /**
     * 根据 id 获取用户等级（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserLevelVO> getUserLevelVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLevel userLevel = userLevelService.getById(id);
        if (userLevel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userLevelService.getUserLevelVO(userLevel, request));
    }

    /**
     * 获取所有用户等级列表
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserLevelVO>> getAllUserLevels(HttpServletRequest request) {
        List<UserLevelVO> userLevelVOList = userLevelService.getAllUserLevels(request);
        return ResultUtils.success(userLevelVOList);
    }
    
    /**
     * 根据经验值获取用户等级
     *
     * @param experience
     * @param request
     * @return
     */
    @GetMapping("/by-experience")
    public BaseResponse<UserLevelVO> getUserLevelByExperience(int experience, HttpServletRequest request) {
        if (experience < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "经验值不能为负数");
        }
        
        UserLevel userLevel = userLevelService.getUserLevelByExperience(experience);
        UserLevelVO userLevelVO = userLevelService.getUserLevelVO(userLevel, request);
        return ResultUtils.success(userLevelVO);
    }
    
    /**
     * 获取下一级用户等级
     *
     * @param currentLevel
     * @param request
     * @return
     */
    @GetMapping("/next")
    public BaseResponse<UserLevelVO> getNextUserLevel(int currentLevel, HttpServletRequest request) {
        if (currentLevel < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级不能为负数");
        }
        
        UserLevel nextUserLevel = userLevelService.getNextUserLevel(currentLevel);
        if (nextUserLevel == null) {
            return ResultUtils.success(null);
        }
        
        UserLevelVO userLevelVO = userLevelService.getUserLevelVO(nextUserLevel, request);
        return ResultUtils.success(userLevelVO);
    }

    // endregion
} 
package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.DailyWordThumbService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 每日单词点赞接口
 */
@RestController
@RequestMapping("/daily/word/thumb")
@Slf4j
public class DailyWordThumbController {

    @Resource
    private DailyWordThumbService dailyWordThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞单词
     *
     * @param wordId 单词ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 1-点赞成功；0-操作失败
     */
    @PostMapping("/do/{wordId}")
    public BaseResponse<Integer> thumbWord(@PathVariable("wordId") long wordId,
                                               HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 执行点赞操作
        int result = dailyWordThumbService.thumbWord(wordId, loginUser);
        return ResultUtils.success(result);
    }
    
    /**
     * 取消点赞单词
     *
     * @param wordId 单词ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 1-取消点赞成功；0-操作失败
     */
    @PostMapping("/cancel/{wordId}")
    public BaseResponse<Integer> cancelThumbWord(@PathVariable("wordId") long wordId,
                                               HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 执行取消点赞操作
        int result = dailyWordThumbService.cancelThumbWord(wordId, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 查询当前用户是否点赞了单词
     *
     * @param wordId 单词ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 是否点赞
     */
    @GetMapping("/is-thumb/{wordId}")
    public BaseResponse<Boolean> isThumbWord(@PathVariable("wordId") long wordId,
                                              HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 查询是否点赞
        boolean result = dailyWordThumbService.isThumbWord(wordId, loginUser.getId());
        return ResultUtils.success(result);
    }
} 
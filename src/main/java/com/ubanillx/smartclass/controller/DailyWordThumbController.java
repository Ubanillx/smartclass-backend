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
     * 点赞/取消点赞单词
     *
     * @param wordId
     * @param request
     * @return 1-点赞成功；-1-取消点赞成功；0-操作失败
     */
    @PostMapping("/{wordId}")
    public BaseResponse<Integer> doWordThumb(@PathVariable("wordId") long wordId,
                                               HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 执行点赞操作
        int result = dailyWordThumbService.doWordThumb(wordId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 查询当前用户是否点赞了单词
     *
     * @param wordId
     * @param request
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
package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyWord;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.service.UserDailyWordService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 每日单词学习进度接口
 */
@RestController
@RequestMapping("/daily/word/learning")
@Slf4j
public class DailyWordLearningController {

    @Resource
    private UserDailyWordService userDailyWordService;

    @Resource
    private DailyWordService dailyWordService;

    @Resource
    private UserService userService;

    /**
     * 标记单词为已学习
     *
     * @param wordId
     * @param request
     * @return
     */
    @PostMapping("/{wordId}/study-status")
    public BaseResponse<Boolean> markWordAsStudied(@PathVariable("wordId") long wordId,
                                                  HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断单词是否存在
        DailyWord dailyWord = dailyWordService.getById(wordId);
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 标记为已学习
        boolean result = userDailyWordService.markWordAsStudied(wordId, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 取消标记单词为已学习
     *
     * @param wordId
     * @param request
     * @return
     */
    @DeleteMapping("/{wordId}/study-status")
    public BaseResponse<Boolean> cancelWordStudied(@PathVariable("wordId") long wordId,
                                                  HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断单词是否存在
        DailyWord dailyWord = dailyWordService.getById(wordId);
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 取消标记为已学习
        boolean result = userDailyWordService.cancelWordStudied(wordId, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新单词掌握程度
     *
     * @param wordId
     * @param masteryLevel 掌握程度：1-完全不认识，2-有点印象，3-认识但不熟练，4-熟练掌握，5-完全掌握
     * @param request
     * @return
     */
    @PostMapping("/{wordId}/mastery")
    public BaseResponse<Boolean> updateMasteryLevel(@PathVariable("wordId") long wordId,
                                                   @RequestParam("masteryLevel") int masteryLevel,
                                                   HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (masteryLevel < 1 || masteryLevel > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "掌握程度参数错误");
        }
        // 判断单词是否存在
        DailyWord dailyWord = dailyWordService.getById(wordId);
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 更新掌握程度
        boolean result = userDailyWordService.updateMasteryLevel(wordId, loginUser.getId(), masteryLevel);
        return ResultUtils.success(result);
    }

    /**
     * 保存单词学习笔记
     *
     * @param wordId
     * @param noteContent
     * @param request
     * @return
     */
    @PostMapping("/{wordId}/note")
    public BaseResponse<Boolean> saveWordNote(@PathVariable("wordId") long wordId,
                                              @RequestParam("noteContent") String noteContent,
                                              HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (noteContent == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记内容不能为空");
        }
        // 判断单词是否存在
        DailyWord dailyWord = dailyWordService.getById(wordId);
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 保存笔记
        boolean result = userDailyWordService.saveWordNote(wordId, loginUser.getId(), noteContent);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户单词学习记录
     *
     * @param wordId
     * @param request
     * @return
     */
    @GetMapping("/{wordId}")
    public BaseResponse<UserDailyWord> getUserDailyWord(@PathVariable("wordId") long wordId,
                                                       HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 获取学习记录
        UserDailyWord userDailyWord = userDailyWordService.getUserDailyWord(wordId, loginUser.getId());
        return ResultUtils.success(userDailyWord);
    }
} 
package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.DailyWordVO;
import com.ubanillx.smartclass.service.DailyWordFavourService;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 每日单词收藏接口
 */
@RestController
@RequestMapping("/dailyWord/favour")
@Slf4j
public class DailyWordFavourController {

    @Resource
    private DailyWordFavourService dailyWordFavourService;

    @Resource
    private DailyWordService dailyWordService;

    @Resource
    private UserService userService;

    /**
     * 收藏/取消收藏单词
     *
     * @param wordId
     * @param request
     * @return
     */
    @PostMapping("/{wordId}")
    public BaseResponse<Integer> doWordFavour(@PathVariable("wordId") long wordId,
                                             HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        int result = dailyWordFavourService.doWordFavour(wordId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户收藏的单词列表
     *
     * @param dailyWordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/my")
    public BaseResponse<Page<DailyWordVO>> listMyFavourWordByPage(@RequestBody DailyWordQueryRequest dailyWordQueryRequest,
                                                        HttpServletRequest request) {
        if (dailyWordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        dailyWordQueryRequest.setPageSize(Math.min(20, dailyWordQueryRequest.getPageSize()));
        long current = dailyWordQueryRequest.getCurrent();
        long size = dailyWordQueryRequest.getPageSize();
        // 构造查询条件
        QueryWrapper<DailyWord> queryWrapper = dailyWordService.getQueryWrapper(dailyWordQueryRequest);
        // 获取收藏单词分页数据
        Page<DailyWordVO> wordPage = dailyWordFavourService.listFavourWordByPage(new Page<>(current, size),
                queryWrapper, loginUser.getId());
        return ResultUtils.success(wordPage);
    }

    /**
     * 检查用户是否收藏了单词
     *
     * @param wordId
     * @param request
     * @return
     */
    @GetMapping("/check/{wordId}")
    public BaseResponse<Boolean> isFavourWord(@PathVariable("wordId") long wordId,
                                            HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 查询是否收藏
        boolean isFavour = dailyWordFavourService.isFavourWord(wordId, loginUser.getId());
        return ResultUtils.success(isFavour);
    }
    
    /**
     * 更新单词掌握程度
     *
     * @param wordId
     * @param masteryLevel
     * @param request
     * @return
     */
    @PostMapping("/mastery/{wordId}/{masteryLevel}")
    public BaseResponse<Boolean> updateMasteryLevel(@PathVariable("wordId") long wordId,
                                               @PathVariable("masteryLevel") int masteryLevel,
                                               HttpServletRequest request) {
        if (wordId <= 0 || masteryLevel < 0 || masteryLevel > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 更新掌握程度
        boolean result = dailyWordFavourService.updateMasteryLevel(wordId, loginUser.getId(), masteryLevel);
        return ResultUtils.success(result);
    }
    
    /**
     * 保存单词笔记
     *
     * @param wordId
     * @param noteContent
     * @param request
     * @return
     */
    @PostMapping("/note/{wordId}")
    public BaseResponse<Boolean> saveWordNote(@PathVariable("wordId") long wordId,
                                         @RequestParam String noteContent,
                                         HttpServletRequest request) {
        if (wordId <= 0 || noteContent == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 保存笔记
        boolean result = dailyWordFavourService.saveWordNote(wordId, loginUser.getId(), noteContent);
        return ResultUtils.success(result);
    }
    
    /**
     * 标记单词为已学习
     *
     * @param wordId
     * @param request
     * @return
     */
    @PostMapping("/study/{wordId}")
    public BaseResponse<Boolean> markWordAsStudied(@PathVariable("wordId") long wordId,
                                              HttpServletRequest request) {
        if (wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 标记为已学习
        boolean result = dailyWordFavourService.markWordAsStudied(wordId, loginUser.getId());
        return ResultUtils.success(result);
    }
} 
package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.userwordbook.*;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserWordBook;
import com.ubanillx.smartclass.model.vo.UserWordBookVO;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.service.UserWordBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户生词本接口
 */
@RestController
@RequestMapping("/word-books")
@Slf4j
public class UserWordBookController {

    @Resource
    private UserWordBookService userWordBookService;

    @Resource
    private UserService userService;

    /**
     * 添加单词到生词本
     *
     * @param addRequest 添加请求
     * @param request    HTTP请求
     * @return 是否添加成功
     */
    @PostMapping("")
    public BaseResponse<Boolean> addToWordBook(@RequestBody UserWordBookAddRequest addRequest,
                                              HttpServletRequest request) {
        if (addRequest == null || addRequest.getWordId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        Long wordId = addRequest.getWordId();
        Integer difficulty = addRequest.getDifficulty();
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        boolean result = userWordBookService.addToWordBook(userId, wordId, difficulty);
        return ResultUtils.success(result);
    }

    /**
     * 从生词本中移除单词
     *
     * @param wordId  单词ID
     * @param request HTTP请求
     * @return 是否移除成功
     */
    @DeleteMapping("/{wordId}")
    public BaseResponse<Boolean> removeFromWordBook(@PathVariable Long wordId,
                                                 HttpServletRequest request) {
        if (wordId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        boolean result = userWordBookService.removeFromWordBook(userId, wordId);
        return ResultUtils.success(result);
    }

    /**
     * 更新单词学习状态
     *
     * @param wordId 单词ID
     * @param updateStatusRequest 更新状态请求
     * @param request            HTTP请求
     * @return 是否更新成功
     */
    @PutMapping("/{wordId}/status")
    public BaseResponse<Boolean> updateLearningStatus(@PathVariable Long wordId,
                                                  @RequestBody UserWordBookUpdateStatusRequest updateStatusRequest,
                                                  HttpServletRequest request) {
        if (updateStatusRequest == null || updateStatusRequest.getLearningStatus() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        Integer learningStatus = updateStatusRequest.getLearningStatus();
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        boolean result = userWordBookService.updateLearningStatus(userId, wordId, learningStatus);
        return ResultUtils.success(result);
    }

    /**
     * 更新单词难度
     *
     * @param wordId 单词ID
     * @param difficultyRequest 难度请求
     * @param request          HTTP请求
     * @return 是否更新成功
     */
    @PutMapping("/{wordId}/difficulty")
    public BaseResponse<Boolean> updateDifficulty(@PathVariable Long wordId,
                                              @RequestBody UserWordBookUpdateDifficultyRequest difficultyRequest,
                                              HttpServletRequest request) {
        if (difficultyRequest == null || difficultyRequest.getDifficulty() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        Integer difficulty = difficultyRequest.getDifficulty();
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        boolean result = userWordBookService.updateDifficulty(userId, wordId, difficulty);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户生词本列表（分页）
     *
     * @param userWordBookQueryRequest 查询请求
     * @param request                 HTTP请求
     * @return 生词本分页列表
     */
    @GetMapping("/page")
    public BaseResponse<Page<UserWordBookVO>> listUserWordBookByPage(UserWordBookQueryRequest userWordBookQueryRequest,
                                                                HttpServletRequest request) {
        if (userWordBookQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        userWordBookQueryRequest.setUserId(loginUser.getId());
        
        long current = userWordBookQueryRequest.getCurrent();
        long size = userWordBookQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        QueryWrapper<UserWordBook> queryWrapper = userWordBookService.getQueryWrapper(userWordBookQueryRequest);
        Page<UserWordBook> userWordBookPage = userWordBookService.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        Page<UserWordBookVO> userWordBookVOPage = new Page<>(current, size, userWordBookPage.getTotal());
        List<UserWordBookVO> userWordBookVOList = userWordBookService.getUserWordBookVO(userWordBookPage.getRecords());
        userWordBookVOPage.setRecords(userWordBookVOList);
        
        return ResultUtils.success(userWordBookVOPage);
    }

    /**
     * 获取用户生词本统计信息
     *
     * @param request HTTP请求
     * @return 统计信息 [总收藏数，已学习数，待复习数]
     */
    @GetMapping("/statistics")
    public BaseResponse<int[]> getUserWordBookStatistics(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        int[] statistics = userWordBookService.getUserWordBookStatistics(userId);
        return ResultUtils.success(statistics);
    }

    /**
     * 判断单词是否在用户的生词本中
     *
     * @param wordId  单词ID
     * @param request HTTP请求
     * @return 是否在生词本中
     */
    @GetMapping("/{wordId}/exists")
    public BaseResponse<Boolean> isWordInUserBook(@PathVariable Long wordId,
                                           HttpServletRequest request) {
        if (wordId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        boolean result = userWordBookService.isWordInUserBook(userId, wordId);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户生词本列表（按学习状态和收藏状态筛选）
     *
     * @param learningStatus 学习状态：0-未学习，1-已学习，2-已掌握，不传则表示全部
     * @param isCollected    是否收藏：0-否，1-是，不传则表示全部
     * @param request        HTTP请求
     * @return 生词本列表
     */
    @GetMapping("")
    public BaseResponse<List<UserWordBookVO>> getUserWordBookList(
            @RequestParam(required = false) Integer learningStatus,
            @RequestParam(required = false) Integer isCollected,
            HttpServletRequest request) {
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        List<UserWordBookVO> userWordBookVOList = userWordBookService.getUserWordBookList(userId, learningStatus, isCollected);
        return ResultUtils.success(userWordBookVOList);
    }
} 
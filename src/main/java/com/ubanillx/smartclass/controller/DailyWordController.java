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
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordAddRequest;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordQueryRequest;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordUpdateRequest;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserWordRecord;
import com.ubanillx.smartclass.model.vo.DailyWordVO;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.service.UserWordRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日单词接口
 */
@RestController
@RequestMapping("/daily/word")
@Slf4j
public class DailyWordController {

    @Resource
    private DailyWordService dailyWordService;

    @Resource
    private UserService userService;

    @Resource
    private UserWordRecordService userWordRecordService;

    /**
     * 创建每日单词
     *
     * @param dailyWordAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addDailyWord(@RequestBody DailyWordAddRequest dailyWordAddRequest, HttpServletRequest request) {
        if (dailyWordAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyWord dailyWord = new DailyWord();
        BeanUtils.copyProperties(dailyWordAddRequest, dailyWord);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        dailyWord.setAdminId(loginUser.getId());
        
        // 校验
        dailyWordService.validDailyWord(dailyWord, true);
        
        long id = dailyWordService.addDailyWord(dailyWord);
        return ResultUtils.success(id);
    }

    /**
     * 删除每日单词
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDailyWord(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        // 判断是否存在
        DailyWord oldDailyWord = dailyWordService.getById(id);
        ThrowUtils.throwIf(oldDailyWord == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldDailyWord.getAdminId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean b = dailyWordService.deleteDailyWord(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新每日单词
     *
     * @param dailyWordUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDailyWord(@RequestBody DailyWordUpdateRequest dailyWordUpdateRequest, HttpServletRequest request) {
        if (dailyWordUpdateRequest == null || dailyWordUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyWord dailyWord = new DailyWord();
        BeanUtils.copyProperties(dailyWordUpdateRequest, dailyWord);
        
        // 参数校验
        dailyWordService.validDailyWord(dailyWord, false);
        
        // 判断是否存在
        long id = dailyWordUpdateRequest.getId();
        DailyWord oldDailyWord = dailyWordService.getById(id);
        ThrowUtils.throwIf(oldDailyWord == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可修改
        User user = userService.getLoginUser(request);
        if (!oldDailyWord.getAdminId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = dailyWordService.updateDailyWord(dailyWord);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取每日单词
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<DailyWordVO> getDailyWordById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyWord dailyWord = dailyWordService.getDailyWordById(id);
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        DailyWordVO dailyWordVO = getDailyWordVO(dailyWord, request);
        return ResultUtils.success(dailyWordVO);
    }

    /**
     * 分页获取每日单词列表
     *
     * @param dailyWordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<DailyWordVO>> listDailyWordByPage(@RequestBody DailyWordQueryRequest dailyWordQueryRequest, HttpServletRequest request) {
        if (dailyWordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取分页参数
        long current = dailyWordQueryRequest.getCurrent();
        long size = dailyWordQueryRequest.getPageSize();
        
        // 构建查询条件
        String category = dailyWordQueryRequest.getCategory();
        Integer difficulty = dailyWordQueryRequest.getDifficulty();
        String searchText = dailyWordQueryRequest.getSearchText();
        
        // 分页查询
        Page<DailyWord> dailyWordPage = dailyWordService.listDailyWordByPage(category, difficulty, searchText, (int) current, (int) size);
        
        // 转换为VO
        Page<DailyWordVO> dailyWordVOPage = new Page<>(current, size, dailyWordPage.getTotal());
        List<DailyWordVO> dailyWordVOList = dailyWordPage.getRecords().stream()
                .map(dailyWord -> getDailyWordVO(dailyWord, request))
                .collect(Collectors.toList());
        dailyWordVOPage.setRecords(dailyWordVOList);
        
        return ResultUtils.success(dailyWordVOPage);
    }

    /**
     * 获取今日单词
     *
     * @param request
     * @return
     */
    @GetMapping("/today")
    public BaseResponse<List<DailyWordVO>> getTodayWords(HttpServletRequest request) {
        List<DailyWord> dailyWordList = dailyWordService.getTodayWords();
        List<DailyWordVO> dailyWordVOList = dailyWordList.stream()
                .map(dailyWord -> getDailyWordVO(dailyWord, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyWordVOList);
    }

    /**
     * 获取指定日期的单词
     *
     * @param date
     * @param request
     * @return
     */
    @GetMapping("/date")
    public BaseResponse<List<DailyWordVO>> getDailyWordsByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, HttpServletRequest request) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyWord> dailyWordList = dailyWordService.getDailyWordsByDate(date);
        List<DailyWordVO> dailyWordVOList = dailyWordList.stream()
                .map(dailyWord -> getDailyWordVO(dailyWord, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyWordVOList);
    }

    /**
     * 根据分类获取单词列表
     *
     * @param category
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/category")
    public BaseResponse<List<DailyWordVO>> listDailyWordsByCategory(@RequestParam String category, 
                                                                   @RequestParam(defaultValue = "10") int limit, 
                                                                   HttpServletRequest request) {
        if (category == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyWord> dailyWordList = dailyWordService.listDailyWordsByCategory(category, limit);
        List<DailyWordVO> dailyWordVOList = dailyWordList.stream()
                .map(dailyWord -> getDailyWordVO(dailyWord, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyWordVOList);
    }

    /**
     * 根据难度等级获取单词列表
     *
     * @param difficulty
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/difficulty")
    public BaseResponse<List<DailyWordVO>> listDailyWordsByDifficulty(@RequestParam Integer difficulty, 
                                                                     @RequestParam(defaultValue = "10") int limit, 
                                                                     HttpServletRequest request) {
        if (difficulty == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyWord> dailyWordList = dailyWordService.listDailyWordsByDifficulty(difficulty, limit);
        List<DailyWordVO> dailyWordVOList = dailyWordList.stream()
                .map(dailyWord -> getDailyWordVO(dailyWord, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyWordVOList);
    }

    /**
     * 获取单词VO
     *
     * @param dailyWord
     * @param request
     * @return
     */
    private DailyWordVO getDailyWordVO(DailyWord dailyWord, HttpServletRequest request) {
        if (dailyWord == null) {
            return null;
        }
        
        DailyWordVO dailyWordVO = new DailyWordVO();
        BeanUtils.copyProperties(dailyWord, dailyWordVO);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取用户学习记录
            UserWordRecord userWordRecord = userWordRecordService.getUserWordRecord(loginUser.getId(), dailyWord.getId());
            if (userWordRecord != null) {
                // 设置用户学习状态
                dailyWordVO.setHasLearned(true);
                dailyWordVO.setLearningProgress(userWordRecord.getLearningProgress());
            } else {
                dailyWordVO.setHasLearned(false);
                dailyWordVO.setLearningProgress(0);
            }
        } else {
            dailyWordVO.setHasLearned(false);
            dailyWordVO.setLearningProgress(0);
        }
        
        return dailyWordVO;
    }
} 
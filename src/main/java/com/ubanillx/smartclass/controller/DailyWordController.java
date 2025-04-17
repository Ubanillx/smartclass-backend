package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.ubanillx.smartclass.model.vo.DailyWordVO;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 每日单词接口
 */
@RestController
@RequestMapping("/dailyWord")
@Slf4j
public class DailyWordController {

    @Resource
    private DailyWordService dailyWordService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建每日单词（仅管理员）
     *
     * @param dailyWordAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addDailyWord(@RequestBody DailyWordAddRequest dailyWordAddRequest,
                                         HttpServletRequest request) {
        if (dailyWordAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyWord dailyWord = new DailyWord();
        BeanUtils.copyProperties(dailyWordAddRequest, dailyWord);
        User loginUser = userService.getLoginUser(request);
        Long adminId = loginUser.getId();
        long id = dailyWordService.addDailyWord(dailyWord, adminId);
        return ResultUtils.success(id);
    }

    /**
     * 删除每日单词（仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDailyWord(@RequestBody DeleteRequest deleteRequest,
                                              HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        DailyWord oldDailyWord = dailyWordService.getById(id);
        ThrowUtils.throwIf(oldDailyWord == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = dailyWordService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新每日单词（仅管理员）
     *
     * @param dailyWordUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDailyWord(@RequestBody DailyWordUpdateRequest dailyWordUpdateRequest,
                                              HttpServletRequest request) {
        if (dailyWordUpdateRequest == null || dailyWordUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyWord dailyWord = new DailyWord();
        BeanUtils.copyProperties(dailyWordUpdateRequest, dailyWord);
        // 判断是否存在
        long id = dailyWordUpdateRequest.getId();
        DailyWord oldDailyWord = dailyWordService.getById(id);
        ThrowUtils.throwIf(oldDailyWord == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = dailyWordService.updateById(dailyWord);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取每日单词
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<DailyWordVO> getDailyWordVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyWord dailyWord = dailyWordService.getById(id);
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 返回单词详情
        return ResultUtils.success(dailyWordService.getDailyWordVO(dailyWord));
    }

    /**
     * 分页获取单词列表（仅管理员）
     *
     * @param dailyWordQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<DailyWord>> listDailyWordByPage(@RequestBody DailyWordQueryRequest dailyWordQueryRequest) {
        if (dailyWordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = dailyWordQueryRequest.getCurrent();
        long size = dailyWordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<DailyWord> queryWrapper = dailyWordService.getQueryWrapper(dailyWordQueryRequest);
        Page<DailyWord> dailyWordPage = dailyWordService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(dailyWordPage);
    }

    /**
     * 分页获取单词列表（封装VO）
     *
     * @param dailyWordQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<DailyWordVO>> listDailyWordVOByPage(@RequestBody DailyWordQueryRequest dailyWordQueryRequest) {
        if (dailyWordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = dailyWordQueryRequest.getCurrent();
        long size = dailyWordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<DailyWord> dailyWordPage = dailyWordService.page(new Page<>(current, size),
                dailyWordService.getQueryWrapper(dailyWordQueryRequest));
        Page<DailyWordVO> dailyWordVOPage = new Page<>(current, size, dailyWordPage.getTotal());
        List<DailyWordVO> dailyWordVOList = dailyWordService.getDailyWordVO(dailyWordPage.getRecords());
        dailyWordVOPage.setRecords(dailyWordVOList);
        return ResultUtils.success(dailyWordVOPage);
    }

    /**
     * 获取特定日期的单词
     *
     * @param date 日期 yyyy-MM-dd
     * @return
     */
    @GetMapping("/date")
    public BaseResponse<List<DailyWordVO>> getDailyWordByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyWordVO> dailyWordVOList = dailyWordService.getDailyWordByDate(date);
        return ResultUtils.success(dailyWordVOList);
    }

    /**
     * 获取今日单词
     *
     * @return 随机返回一个最新的单词
     */
    @GetMapping("/today")
    public BaseResponse<DailyWordVO> getTodayWord() {
        DailyWordVO randomLatestWord = dailyWordService.getRandomLatestWord();
        if (randomLatestWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到单词");
        }
        return ResultUtils.success(randomLatestWord);
    }

    /**
     * 获取随机单词
     *
     * @param difficulty 难度等级，可选参数
     * @return
     */
    @GetMapping("/random")
    public BaseResponse<DailyWordVO> getRandomWord(
            @RequestParam(required = false) Integer difficulty) {
        DailyWordVO dailyWordVO = dailyWordService.getRandomDailyWord(difficulty);
        return ResultUtils.success(dailyWordVO);
    }
} 
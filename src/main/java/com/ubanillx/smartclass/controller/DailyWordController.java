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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 每日单词接口
 */
@RestController
@RequestMapping("/daily-words")
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
     * @param dailyWordAddRequest 单词创建请求体，包含单词、解释、例句等信息
     * @param request HTTP请求，用于获取当前登录用户
     * @return 新增单词的ID
     */
    @PostMapping("")
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
        // 设置管理员ID
        dailyWord.setAdminId(adminId);
        // 使用saveDailyWord方法，同步到ES
        boolean result = dailyWordService.saveDailyWord(dailyWord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(dailyWord.getId());
    }

    /**
     * 删除每日单词（仅管理员）
     *
     * @param id 要删除单词的ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDailyWord(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        DailyWord oldDailyWord = dailyWordService.getById(id);
        ThrowUtils.throwIf(oldDailyWord == null, ErrorCode.NOT_FOUND_ERROR);
        // 使用deleteDailyWord方法，同步删除ES中的数据
        boolean b = dailyWordService.deleteDailyWord(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新每日单词（仅管理员）
     *
     * @param id 单词ID
     * @param dailyWordUpdateRequest 单词更新请求，包含需要更新的单词信息
     * @return 是否更新成功
     */
    @PutMapping("/{id}/admin")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDailyWord(@PathVariable("id") Long id, 
                                              @RequestBody DailyWordUpdateRequest dailyWordUpdateRequest) {
        if (dailyWordUpdateRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 设置ID
        dailyWordUpdateRequest.setId(id);
        DailyWord dailyWord = new DailyWord();
        BeanUtils.copyProperties(dailyWordUpdateRequest, dailyWord);
        // 判断是否存在
        DailyWord oldDailyWord = dailyWordService.getById(id);
        ThrowUtils.throwIf(oldDailyWord == null, ErrorCode.NOT_FOUND_ERROR);
        // 使用updateDailyWord方法，同步更新ES中的数据
        boolean result = dailyWordService.updateDailyWord(dailyWord);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取每日单词
     *
     * @param id 单词ID
     * @return 单词视图对象
     */
    @GetMapping("/{id}")
    public BaseResponse<DailyWordVO> getDailyWordVOById(@PathVariable("id") Long id) {
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
     * @param dailyWordQueryRequest 单词查询请求，包含分页参数和查询条件
     * @return 单词分页列表
     */
    @GetMapping("/admin/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<DailyWord>> listDailyWordByPage(DailyWordQueryRequest dailyWordQueryRequest) {
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
     * @param dailyWordQueryRequest 单词查询请求，包含分页参数和查询条件
     * @return 单词视图对象分页列表
     */
    @GetMapping("/page")
    public BaseResponse<Page<DailyWordVO>> listDailyWordVOByPage(DailyWordQueryRequest dailyWordQueryRequest) {
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
     * 获取今日单词
     *
     * @param difficulty 难度等级，可选参数，用于筛选特定难度的单词
     * @return 随机单词
     */
    @GetMapping("/today")
    public BaseResponse<DailyWordVO> getTodayWord(
            @RequestParam(required = false) Integer difficulty) {
        DailyWordVO dailyWordVO = dailyWordService.getRandomDailyWord(difficulty);
        if (dailyWordVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到单词");
        }
        return ResultUtils.success(dailyWordVO);
    }

    /**
     * 从ES搜索单词
     *
     * @param searchText 搜索关键词，服务层会自动匹配单词的所有相关字段
     * @return 符合搜索条件的单词视图对象分页列表
     */
    @GetMapping("/search")
    public BaseResponse<Page<DailyWordVO>> searchDailyWord(@RequestParam String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
        }
        // 调用服务层方法进行搜索，service层负责匹配所有字段
        Page<DailyWord> dailyWordPage = dailyWordService.searchFromEs(searchText);
        Page<DailyWordVO> dailyWordVOPage = new Page<>(dailyWordPage.getCurrent(), 
                dailyWordPage.getSize(), dailyWordPage.getTotal());
        List<DailyWordVO> dailyWordVOList = dailyWordService.getDailyWordVO(dailyWordPage.getRecords());
        dailyWordVOPage.setRecords(dailyWordVOList);
        return ResultUtils.success(dailyWordVOPage);
    }
} 
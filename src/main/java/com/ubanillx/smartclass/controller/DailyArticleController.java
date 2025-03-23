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
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleAddRequest;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleQueryRequest;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleUpdateRequest;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;
import com.ubanillx.smartclass.service.DailyArticleService;
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
 * 每日文章接口
 */
@RestController
@RequestMapping("/dailyArticle")
@Slf4j
public class DailyArticleController {

    @Resource
    private DailyArticleService dailyArticleService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建每日文章（仅管理员）
     *
     * @param dailyArticleAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addDailyArticle(@RequestBody DailyArticleAddRequest dailyArticleAddRequest,
                                         HttpServletRequest request) {
        if (dailyArticleAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyArticle dailyArticle = new DailyArticle();
        BeanUtils.copyProperties(dailyArticleAddRequest, dailyArticle);
        User loginUser = userService.getLoginUser(request);
        Long adminId = loginUser.getId();
        long id = dailyArticleService.addDailyArticle(dailyArticle, adminId);
        return ResultUtils.success(id);
    }

    /**
     * 删除每日文章（仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDailyArticle(@RequestBody DeleteRequest deleteRequest,
                                              HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        DailyArticle oldDailyArticle = dailyArticleService.getById(id);
        ThrowUtils.throwIf(oldDailyArticle == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = dailyArticleService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新每日文章（仅管理员）
     *
     * @param dailyArticleUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDailyArticle(@RequestBody DailyArticleUpdateRequest dailyArticleUpdateRequest,
                                              HttpServletRequest request) {
        if (dailyArticleUpdateRequest == null || dailyArticleUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyArticle dailyArticle = new DailyArticle();
        BeanUtils.copyProperties(dailyArticleUpdateRequest, dailyArticle);
        // 判断是否存在
        long id = dailyArticleUpdateRequest.getId();
        DailyArticle oldDailyArticle = dailyArticleService.getById(id);
        ThrowUtils.throwIf(oldDailyArticle == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = dailyArticleService.updateById(dailyArticle);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取每日文章
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<DailyArticleVO> getDailyArticleVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyArticle dailyArticle = dailyArticleService.getById(id);
        if (dailyArticle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 增加文章查看次数
        dailyArticleService.increaseViewCount(id);
        // 返回文章详情
        return ResultUtils.success(dailyArticleService.getDailyArticleVO(dailyArticle));
    }

    /**
     * 分页获取文章列表（仅管理员）
     *
     * @param dailyArticleQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<DailyArticle>> listDailyArticleByPage(@RequestBody DailyArticleQueryRequest dailyArticleQueryRequest) {
        if (dailyArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = dailyArticleQueryRequest.getCurrent();
        long size = dailyArticleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<DailyArticle> queryWrapper = dailyArticleService.getQueryWrapper(dailyArticleQueryRequest);
        Page<DailyArticle> dailyArticlePage = dailyArticleService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(dailyArticlePage);
    }

    /**
     * 分页获取文章列表（封装VO）
     *
     * @param dailyArticleQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<DailyArticleVO>> listDailyArticleVOByPage(@RequestBody DailyArticleQueryRequest dailyArticleQueryRequest) {
        if (dailyArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = dailyArticleQueryRequest.getCurrent();
        long size = dailyArticleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<DailyArticle> dailyArticlePage = dailyArticleService.page(new Page<>(current, size),
                dailyArticleService.getQueryWrapper(dailyArticleQueryRequest));
        Page<DailyArticleVO> dailyArticleVOPage = new Page<>(current, size, dailyArticlePage.getTotal());
        List<DailyArticleVO> dailyArticleVOList = dailyArticleService.getDailyArticleVO(dailyArticlePage.getRecords());
        dailyArticleVOPage.setRecords(dailyArticleVOList);
        return ResultUtils.success(dailyArticleVOPage);
    }

    /**
     * 获取特定日期的文章
     *
     * @param date 日期 yyyy-MM-dd
     * @return
     */
    @GetMapping("/date")
    public BaseResponse<List<DailyArticleVO>> getDailyArticleByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyArticleVO> dailyArticleVOList = dailyArticleService.getDailyArticleByDate(date);
        return ResultUtils.success(dailyArticleVOList);
    }

    /**
     * 获取今日文章
     *
     * @return
     */
    @GetMapping("/today")
    public BaseResponse<List<DailyArticleVO>> getTodayArticle() {
        List<DailyArticleVO> dailyArticleVOList = dailyArticleService.getDailyArticleByDate(new Date());
        return ResultUtils.success(dailyArticleVOList);
    }

    /**
     * 获取推荐文章
     *
     * @param category 分类
     * @param difficulty 难度
     * @param limit 返回数量限制
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<List<DailyArticleVO>> getRecommendArticles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<DailyArticleVO> dailyArticleVOList = dailyArticleService.getRecommendArticles(category, difficulty, limit);
        return ResultUtils.success(dailyArticleVOList);
    }

} 
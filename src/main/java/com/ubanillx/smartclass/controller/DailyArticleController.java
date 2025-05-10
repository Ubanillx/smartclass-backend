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
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleEsDTO;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

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

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

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
        // 设置管理员ID
        dailyArticle.setAdminId(adminId);
        // 使用saveDailyArticle方法，同步到ES
        boolean result = dailyArticleService.saveDailyArticle(dailyArticle);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(dailyArticle.getId());
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
        // 使用deleteDailyArticle方法，同步删除ES中的数据
        boolean b = dailyArticleService.deleteDailyArticle(id);
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
        // 使用updateDailyArticle方法，同步更新ES中的数据
        boolean result = dailyArticleService.updateDailyArticle(dailyArticle);
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
     * 获取今日文章
     *
     * @return 随机返回一篇最新的文章
     */
    @GetMapping("/today")
    public BaseResponse<DailyArticleVO> getTodayArticle() {
        DailyArticleVO randomLatestArticle = dailyArticleService.getRandomLatestArticle();
        if (randomLatestArticle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到文章");
        }
        // 增加文章查看次数
        dailyArticleService.increaseViewCount(randomLatestArticle.getId());
        return ResultUtils.success(randomLatestArticle);
    }

    /**
     * 从ES搜索美文
     *
     * @param dailyArticleQueryRequest
     * @return
     */
    @PostMapping("/search/es")
    public BaseResponse<Page<DailyArticleVO>> searchDailyArticle(@RequestBody DailyArticleQueryRequest dailyArticleQueryRequest) {
        if (dailyArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long size = dailyArticleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<DailyArticle> dailyArticlePage = dailyArticleService.searchFromEs(dailyArticleQueryRequest);
        Page<DailyArticleVO> dailyArticleVOPage = new Page<>(dailyArticlePage.getCurrent(), size, dailyArticlePage.getTotal());
        List<DailyArticleVO> dailyArticleVOList = dailyArticleService.getDailyArticleVO(dailyArticlePage.getRecords());
        dailyArticleVOPage.setRecords(dailyArticleVOList);
        return ResultUtils.success(dailyArticleVOPage);
    }
    
    /**
     * 测试ES索引
     *
     * @return
     */
    @GetMapping("/es/test")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> testEsIndex() {
        boolean existsIndex = elasticsearchRestTemplate.indexOps(DailyArticleEsDTO.class).exists();
        if (!existsIndex) {
            boolean createIndex = elasticsearchRestTemplate.indexOps(DailyArticleEsDTO.class).create();
            if (!createIndex) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建ES索引失败");
            }
        }
        return ResultUtils.success(true);
    }

} 
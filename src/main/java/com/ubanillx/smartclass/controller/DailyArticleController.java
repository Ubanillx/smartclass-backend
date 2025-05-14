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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 每日文章接口
 */
@RestController
@RequestMapping("/daily-articles")
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
     * @param dailyArticleAddRequest 每日文章创建请求体，包含文章标题、内容、类型等信息
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 新创建的每日文章ID
     */
    @PostMapping("")
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
     * @param id 要删除的文章ID
     * @return 删除操作是否成功
     */
    @DeleteMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDailyArticle(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
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
     * @param id 文章ID
     * @param dailyArticleUpdateRequest 文章更新请求体，包含需要更新的字段
     * @return 更新操作是否成功
     */
    @PutMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDailyArticle(@PathVariable("id") Long id, 
                                               @RequestBody DailyArticleUpdateRequest dailyArticleUpdateRequest) {
        if (dailyArticleUpdateRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        dailyArticleUpdateRequest.setId(id);
        DailyArticle dailyArticle = new DailyArticle();
        BeanUtils.copyProperties(dailyArticleUpdateRequest, dailyArticle);
        // 判断是否存在
        DailyArticle oldDailyArticle = dailyArticleService.getById(id);
        ThrowUtils.throwIf(oldDailyArticle == null, ErrorCode.NOT_FOUND_ERROR);
        // 使用updateDailyArticle方法，同步更新ES中的数据
        boolean result = dailyArticleService.updateDailyArticle(dailyArticle);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取每日文章
     *
     * @param id 文章ID
     * @return 文章视图对象，包含文章详细信息
     */
    @GetMapping("/{id}")
    public BaseResponse<DailyArticleVO> getDailyArticleVOById(@PathVariable("id") Long id) {
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
     * @param dailyArticleQueryRequest 查询请求参数，包含页码、每页大小、排序字段等
     * @return 每日文章分页数据
     */
    @GetMapping("/admin/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<DailyArticle>> listDailyArticleByPage(DailyArticleQueryRequest dailyArticleQueryRequest) {
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
     * @param dailyArticleQueryRequest 查询请求参数，包含页码、每页大小、排序字段、搜索关键词等
     * @return 每日文章VO对象的分页数据，包含更多展示信息
     */
    @GetMapping("/page")
    public BaseResponse<Page<DailyArticleVO>> listDailyArticleVOByPage(DailyArticleQueryRequest dailyArticleQueryRequest) {
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
     * @return 随机选择的最新文章视图对象
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
     * @param searchText 搜索关键词，服务层会自动匹配文章的所有相关字段
     * @return 符合搜索条件的文章视图对象分页结果
     */
    @GetMapping("/search")
    public BaseResponse<Page<DailyArticleVO>> searchDailyArticle(@RequestParam String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
        }
        // 调用服务层方法进行搜索，service层负责匹配所有字段
        Page<DailyArticle> dailyArticlePage = dailyArticleService.searchFromEs(searchText);
        Page<DailyArticleVO> dailyArticleVOPage = new Page<>(dailyArticlePage.getCurrent(), 
                dailyArticlePage.getSize(), dailyArticlePage.getTotal());
        List<DailyArticleVO> dailyArticleVOList = dailyArticleService.getDailyArticleVO(dailyArticlePage.getRecords());
        dailyArticleVOPage.setRecords(dailyArticleVOList);
        return ResultUtils.success(dailyArticleVOPage);
    }
} 
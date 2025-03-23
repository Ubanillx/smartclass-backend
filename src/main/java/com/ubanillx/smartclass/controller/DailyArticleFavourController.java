package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;
import com.ubanillx.smartclass.service.DailyArticleFavourService;
import com.ubanillx.smartclass.service.DailyArticleService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 每日文章收藏接口
 */
@RestController
@RequestMapping("/dailyArticle/favour")
@Slf4j
public class DailyArticleFavourController {

    @Resource
    private DailyArticleFavourService dailyArticleFavourService;

    @Resource
    private DailyArticleService dailyArticleService;

    @Resource
    private UserService userService;

    /**
     * 收藏/取消收藏文章
     *
     * @param articleId
     * @param request
     * @return
     */
    @PostMapping("/{articleId}")
    public BaseResponse<Integer> doArticleFavour(@PathVariable("articleId") long articleId,
                                             HttpServletRequest request) {
        if (articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        int result = dailyArticleFavourService.doArticleFavour(articleId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户收藏的文章列表
     *
     * @param dailyArticleQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/my")
    public BaseResponse<Page<DailyArticleVO>> listMyFavourArticleByPage(@RequestBody DailyArticleQueryRequest dailyArticleQueryRequest,
                                                        HttpServletRequest request) {
        if (dailyArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        dailyArticleQueryRequest.setPageSize(Math.min(20, dailyArticleQueryRequest.getPageSize()));
        long current = dailyArticleQueryRequest.getCurrent();
        long size = dailyArticleQueryRequest.getPageSize();
        // 构造查询条件
        QueryWrapper<DailyArticle> queryWrapper = dailyArticleService.getQueryWrapper(dailyArticleQueryRequest);
        // 获取收藏文章分页数据
        Page<DailyArticleVO> articlePage = dailyArticleFavourService.listFavourArticleByPage(new Page<>(current, size),
                queryWrapper, loginUser.getId());
        return ResultUtils.success(articlePage);
    }

    /**
     * 检查用户是否收藏了文章
     *
     * @param articleId
     * @param request
     * @return
     */
    @GetMapping("/check/{articleId}")
    public BaseResponse<Boolean> isFavourArticle(@PathVariable("articleId") long articleId,
                                            HttpServletRequest request) {
        if (articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 查询是否收藏
        boolean isFavour = dailyArticleFavourService.isFavourArticle(articleId, loginUser.getId());
        return ResultUtils.success(isFavour);
    }
} 
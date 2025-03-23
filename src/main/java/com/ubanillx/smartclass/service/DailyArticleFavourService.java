package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyArticle;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;

/**
 * 每日文章收藏服务
*/
public interface DailyArticleFavourService extends IService<UserDailyArticle> {

    /**
     * 收藏/取消收藏每日文章
     *
     * @param articleId
     * @param loginUser
     * @return 1-收藏，-1-取消收藏，0-失败
     */
    int doArticleFavour(long articleId, User loginUser);

    /**
     * 分页获取用户收藏的每日文章列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<DailyArticleVO> listFavourArticleByPage(IPage<DailyArticle> page, Wrapper<DailyArticle> queryWrapper,
            long favourUserId);

    /**
     * 判断用户是否收藏了文章
     *
     * @param articleId
     * @param userId
     * @return
     */
    boolean isFavourArticle(long articleId, long userId);

    /**
     * 收藏/取消收藏每日文章（内部事务方法）
     *
     * @param userId
     * @param articleId
     * @return 1-收藏，-1-取消收藏，0-失败
     */
    int doArticleFavourInner(long userId, long articleId);
} 
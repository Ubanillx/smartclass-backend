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
 * 每日文章点赞服务
*/
public interface DailyArticleThumbService extends IService<UserDailyArticle> {

    /**
     * 点赞/取消点赞文章
     *
     * @param articleId
     * @param loginUser
     * @return 1-点赞，-1-取消点赞，0-失败
     */
    int doArticleThumb(long articleId, User loginUser);
    
    /**
     * 取消点赞文章
     *
     * @param articleId 文章id
     * @param userId 用户id
     * @return 是否成功
     */
    int cancelArticleThumb(long articleId, long userId);

    /**
     * 文章点赞（内部事务方法）
     *
     * @param userId
     * @param articleId
     * @return 1-点赞，-1-取消点赞，0-失败
     */
    int doArticleThumbInner(long userId, long articleId);
    
    /**
     * 分页获取用户点赞的每日文章列表
     *
     * @param page
     * @param queryWrapper
     * @param thumbUserId
     * @return
     */
    Page<DailyArticleVO> listThumbArticleByPage(IPage<DailyArticle> page, Wrapper<DailyArticle> queryWrapper,
            long thumbUserId);

    /**
     * 判断用户是否点赞了文章
     *
     * @param articleId
     * @param userId
     * @return
     */
    boolean isThumbArticle(long articleId, long userId);
} 
package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_article(每日文章)】的数据库操作Service
* @createDate 2025-03-19 00:03:09
*/
public interface DailyArticleService extends IService<DailyArticle> {

    /**
     * 创建每日文章
     *
     * @param dailyArticle
     * @param adminId
     * @return
     */
    long addDailyArticle(DailyArticle dailyArticle, Long adminId);

    /**
     * 获取每日文章视图
     *
     * @param dailyArticle
     * @return
     */
    DailyArticleVO getDailyArticleVO(DailyArticle dailyArticle);

    /**
     * 获取每日文章视图列表
     *
     * @param dailyArticleList
     * @return
     */
    List<DailyArticleVO> getDailyArticleVO(List<DailyArticle> dailyArticleList);

    /**
     * 获取查询条件
     *
     * @param dailyArticleQueryRequest
     * @return
     */
    QueryWrapper<DailyArticle> getQueryWrapper(DailyArticleQueryRequest dailyArticleQueryRequest);

    /**
     * 增加文章查看次数
     *
     * @param id
     * @return
     */
    boolean increaseViewCount(Long id);

    /**
     * 增加文章点赞次数
     *
     * @param id
     * @return
     */
    boolean increaseLikeCount(Long id);

    /**
     * 减少文章点赞次数
     *
     * @param id
     * @return
     */
    boolean decreaseLikeCount(Long id);

    /**
     * 随机获取一篇最新的文章
     *
     * @return 随机选择的最新文章
     */
    DailyArticleVO getRandomLatestArticle();
    
    /**
     * 从ES中搜索每日美文
     *
     * @param dailyArticleQueryRequest 查询条件
     * @return 分页结果
     */
    Page<DailyArticle> searchFromEs(DailyArticleQueryRequest dailyArticleQueryRequest);
    
    /**
     * 保存每日美文并同步到ES
     *
     * @param dailyArticle 每日美文
     * @return 是否成功
     */
    boolean saveDailyArticle(DailyArticle dailyArticle);
    
    /**
     * 更新每日美文并同步到ES
     *
     * @param dailyArticle 每日美文
     * @return 是否成功
     */
    boolean updateDailyArticle(DailyArticle dailyArticle);
    
    /**
     * 删除每日美文并同步到ES
     *
     * @param id 每日美文ID
     * @return 是否成功
     */
    boolean deleteDailyArticle(Long id);
}

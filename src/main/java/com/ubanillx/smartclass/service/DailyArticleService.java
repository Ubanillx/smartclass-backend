package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_article(每日文章)】的数据库操作Service
* @createDate 2025-02-27 21:52:02
*/
public interface DailyArticleService extends IService<DailyArticle> {

    /**
     * 添加每日文章
     * @param dailyArticle 每日文章信息
     * @return 新增文章的ID
     */
    long addDailyArticle(DailyArticle dailyArticle);

    /**
     * 删除每日文章（逻辑删除）
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteDailyArticle(long id);

    /**
     * 更新每日文章信息
     * @param dailyArticle 文章信息
     * @return 是否成功
     */
    boolean updateDailyArticle(DailyArticle dailyArticle);

    /**
     * 根据ID获取每日文章信息
     * @param id 文章ID
     * @return 文章信息
     */
    DailyArticle getDailyArticleById(long id);

    /**
     * 获取指定日期的每日文章
     * @param date 日期
     * @return 文章列表
     */
    List<DailyArticle> getDailyArticlesByDate(Date date);

    /**
     * 获取今日文章
     * @return 文章列表
     */
    List<DailyArticle> getTodayArticles();

    /**
     * 分页查询每日文章列表
     * @param category 分类
     * @param difficulty 难度等级
     * @param keyword 关键词
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<DailyArticle> listDailyArticleByPage(String category, Integer difficulty, String keyword, int current, int size);

    /**
     * 根据分类获取每日文章列表
     * @param category 分类
     * @param limit 限制数量
     * @return 文章列表
     */
    List<DailyArticle> listDailyArticlesByCategory(String category, int limit);

    /**
     * 根据难度等级获取每日文章列表
     * @param difficulty 难度等级
     * @param limit 限制数量
     * @return 文章列表
     */
    List<DailyArticle> listDailyArticlesByDifficulty(Integer difficulty, int limit);

    /**
     * 增加文章查看次数
     * @param id 文章ID
     * @return 是否成功
     */
    boolean incrViewCount(long id);

    /**
     * 增加文章点赞次数
     * @param id 文章ID
     * @return 是否成功
     */
    boolean incrLikeCount(long id);

    /**
     * 校验每日文章信息
     * @param dailyArticle 文章信息
     * @param add 是否为新增操作
     */
    void validDailyArticle(DailyArticle dailyArticle, boolean add);
}

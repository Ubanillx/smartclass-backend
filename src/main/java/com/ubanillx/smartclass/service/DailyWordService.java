package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_word(每日单词)】的数据库操作Service
* @createDate 2025-02-27 21:52:02
*/
public interface DailyWordService extends IService<DailyWord> {

    /**
     * 添加每日单词
     * @param dailyWord 每日单词信息
     * @return 新增单词的ID
     */
    long addDailyWord(DailyWord dailyWord);

    /**
     * 删除每日单词（逻辑删除）
     * @param id 单词ID
     * @return 是否成功
     */
    boolean deleteDailyWord(long id);

    /**
     * 更新每日单词信息
     * @param dailyWord 单词信息
     * @return 是否成功
     */
    boolean updateDailyWord(DailyWord dailyWord);

    /**
     * 根据ID获取每日单词信息
     * @param id 单词ID
     * @return 单词信息
     */
    DailyWord getDailyWordById(long id);

    /**
     * 获取指定日期的每日单词
     * @param date 日期
     * @return 单词列表
     */
    List<DailyWord> getDailyWordsByDate(Date date);

    /**
     * 获取今日单词
     * @return 单词列表
     */
    List<DailyWord> getTodayWords();

    /**
     * 分页查询每日单词列表
     * @param category 分类
     * @param difficulty 难度等级
     * @param keyword 关键词
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<DailyWord> listDailyWordByPage(String category, Integer difficulty, String keyword, int current, int size);

    /**
     * 根据分类获取每日单词列表
     * @param category 分类
     * @param limit 限制数量
     * @return 单词列表
     */
    List<DailyWord> listDailyWordsByCategory(String category, int limit);

    /**
     * 根据难度等级获取每日单词列表
     * @param difficulty 难度等级
     * @param limit 限制数量
     * @return 单词列表
     */
    List<DailyWord> listDailyWordsByDifficulty(Integer difficulty, int limit);

    /**
     * 校验每日单词信息
     * @param dailyWord 单词信息
     * @param add 是否为新增操作
     */
    void validDailyWord(DailyWord dailyWord, boolean add);
}

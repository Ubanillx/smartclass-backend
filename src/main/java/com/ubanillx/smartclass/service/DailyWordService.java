package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.vo.DailyWordVO;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_word(每日单词)】的数据库操作Service
* @createDate 2025-03-19 00:03:09
*/
public interface DailyWordService extends IService<DailyWord> {

    /**
     * 创建每日单词
     *
     * @param dailyWord
     * @param adminId
     * @return
     */
    long addDailyWord(DailyWord dailyWord, Long adminId);

    /**
     * 获取指定日期的每日单词
     *
     * @param date
     * @return
     */
    List<DailyWordVO> getDailyWordByDate(Date date);

    /**
     * 获取每日单词视图
     *
     * @param dailyWord
     * @return
     */
    DailyWordVO getDailyWordVO(DailyWord dailyWord);

    /**
     * 获取每日单词视图列表
     *
     * @param dailyWordList
     * @return
     */
    List<DailyWordVO> getDailyWordVO(List<DailyWord> dailyWordList);

    /**
     * 获取查询条件
     *
     * @param dailyWordQueryRequest
     * @return
     */
    QueryWrapper<DailyWord> getQueryWrapper(DailyWordQueryRequest dailyWordQueryRequest);

    /**
     * 随机获取一个指定难度的单词
     * 
     * @param difficulty
     * @return
     */
    DailyWordVO getRandomDailyWord(Integer difficulty);
    
    /**
     * 增加单词点赞次数
     *
     * @param id
     * @return
     */
    boolean increaseLikeCount(Long id);

    /**
     * 减少单词点赞次数
     *
     * @param id
     * @return
     */
    boolean decreaseLikeCount(Long id);
    
    /**
     * 随机获取一个最新的单词
     *
     * @return 随机选择的最新单词
     */
    DailyWordVO getRandomLatestWord();
}

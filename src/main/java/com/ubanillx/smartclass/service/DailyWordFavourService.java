package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyWord;
import com.ubanillx.smartclass.model.vo.DailyWordVO;

/**
 * 每日单词收藏服务
*/
public interface DailyWordFavourService extends IService<UserDailyWord> {

    /**
     * 收藏/取消收藏每日单词
     *
     * @param wordId
     * @param loginUser
     * @return 1-收藏，-1-取消收藏，0-失败
     */
    int doWordFavour(long wordId, User loginUser);

    /**
     * 分页获取用户收藏的每日单词列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<DailyWordVO> listFavourWordByPage(IPage<DailyWord> page, Wrapper<DailyWord> queryWrapper,
            long favourUserId);

    /**
     * 判断用户是否收藏了单词
     *
     * @param wordId
     * @param userId
     * @return
     */
    boolean isFavourWord(long wordId, long userId);

    /**
     * 更新单词掌握程度
     * 
     * @param wordId
     * @param userId
     * @param masteryLevel 掌握程度：0-未知，1-生词，2-熟悉，3-掌握
     * @return
     */
    boolean updateMasteryLevel(long wordId, long userId, int masteryLevel);

    /**
     * 保存单词笔记
     * 
     * @param wordId
     * @param userId
     * @param noteContent
     * @return
     */
    boolean saveWordNote(long wordId, long userId, String noteContent);

    /**
     * 标记单词为已学习
     * 
     * @param wordId
     * @param userId
     * @return
     */
    boolean markWordAsStudied(long wordId, long userId);

    /**
     * 收藏/取消收藏每日单词（内部事务方法）
     *
     * @param userId
     * @param wordId
     * @return 1-收藏，-1-取消收藏，0-失败
     */
    int doWordFavourInner(long userId, long wordId);
} 
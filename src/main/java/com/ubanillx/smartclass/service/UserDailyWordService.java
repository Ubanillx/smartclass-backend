package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.UserDailyWord;

/**
 * 用户与每日单词关联服务
 */
public interface UserDailyWordService extends IService<UserDailyWord> {

    /**
     * 标记单词为已学习
     *
     * @param wordId
     * @param userId
     * @return
     */
    boolean markWordAsStudied(long wordId, long userId);

    /**
     * 更新单词掌握程度
     *
     * @param wordId
     * @param userId
     * @param masteryLevel
     * @return
     */
    boolean updateMasteryLevel(long wordId, long userId, int masteryLevel);

    /**
     * 保存单词学习笔记
     *
     * @param wordId
     * @param userId
     * @param noteContent
     * @return
     */
    boolean saveWordNote(long wordId, long userId, String noteContent);

    /**
     * 获取用户单词学习记录
     *
     * @param wordId
     * @param userId
     * @return
     */
    UserDailyWord getUserDailyWord(long wordId, long userId);
} 
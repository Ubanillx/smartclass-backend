package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyWord;

/**
 * 每日单词点赞服务
*/
public interface DailyWordThumbService extends IService<UserDailyWord> {

    /**
     * 点赞/取消点赞单词
     *
     * @param wordId
     * @param loginUser
     * @return 1-点赞，-1-取消点赞，0-失败
     */
    int doWordThumb(long wordId, User loginUser);

    /**
     * 单词点赞（内部事务方法）
     *
     * @param userId
     * @param wordId
     * @return 1-点赞，-1-取消点赞，0-失败
     */
    int doWordThumbInner(long userId, long wordId);

    /**
     * 判断用户是否点赞了单词
     *
     * @param wordId
     * @param userId
     * @return
     */
    boolean isThumbWord(long wordId, long userId);
} 
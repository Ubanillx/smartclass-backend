package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyWord;

/**
 * 每日单词点赞服务
*/
public interface DailyWordThumbService extends IService<UserDailyWord> {
    
    /**
     * 点赞单词
     *
     * @param wordId 单词id
     * @param loginUser 当前登录用户
     * @return 1-点赞成功，0-操作失败
     */
    int thumbWord(long wordId, User loginUser);
    
    /**
     * 取消点赞单词
     *
     * @param wordId 单词id
     * @param userId 用户id
     * @return 1-取消点赞成功，0-操作失败
     */
    int cancelThumbWord(long wordId, long userId);

    /**
     * 判断用户是否点赞了单词
     *
     * @param wordId
     * @param userId
     * @return
     */
    boolean isThumbWord(long wordId, long userId);
} 
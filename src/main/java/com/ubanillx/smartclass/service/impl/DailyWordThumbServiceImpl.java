package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserDailyWordMapper;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyWord;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.service.DailyWordThumbService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 每日单词点赞服务实现
*/
@Service
public class DailyWordThumbServiceImpl extends ServiceImpl<UserDailyWordMapper, UserDailyWord>
        implements DailyWordThumbService {

    @Resource
    private DailyWordService dailyWordService;

    /**
     * 点赞/取消点赞单词
     *
     * @param wordId
     * @param loginUser
     * @return
     */
    @Override
    public int doWordThumb(long wordId, User loginUser) {
        // 判断单词是否存在
        DailyWord word = dailyWordService.getById(wordId);
        if (word == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        // 获取用户ID
        long userId = loginUser.getId();
        // 每个用户串行点赞，避免并发问题
        // 锁必须要包裹住事务方法
        DailyWordThumbService wordThumbService = (DailyWordThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return wordThumbService.doWordThumbInner(userId, wordId);
        }
    }

    /**
     * 单词点赞（内部事务方法）
     *
     * @param userId
     * @param wordId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doWordThumbInner(long userId, long wordId) {
        // 查询用户与单词的关联记录
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        UserDailyWord userDailyWord = this.getOne(queryWrapper);
        
        // 当前时间
        Date now = new Date();
        
        // 如果关联记录不存在，创建新记录
        if (userDailyWord == null) {
            userDailyWord = new UserDailyWord();
            userDailyWord.setUserId(userId);
            userDailyWord.setWordId(wordId);
            userDailyWord.setIsLiked(1); // 设置为已点赞
            userDailyWord.setLikeTime(now);
            userDailyWord.setCreateTime(now);
            boolean result = this.save(userDailyWord);
            if (result) {
                // 更新单词点赞数 +1
                dailyWordService.increaseLikeCount(wordId);
                return 1;
            } else {
                return 0;
            }
        }
        
        // 如果关联记录存在
        Integer isLiked = userDailyWord.getIsLiked();
        
        // 如果已点赞，则取消点赞
        if (isLiked != null && isLiked == 1) {
            userDailyWord.setIsLiked(0);
            userDailyWord.setLikeTime(null);
            userDailyWord.setUpdateTime(now);
            boolean result = this.updateById(userDailyWord);
            if (result) {
                // 更新单词点赞数 -1
                dailyWordService.decreaseLikeCount(wordId);
                return -1;
            } else {
                return 0;
            }
        } 
        // 如果未点赞，则设置为点赞
        else {
            userDailyWord.setIsLiked(1);
            userDailyWord.setLikeTime(now);
            userDailyWord.setUpdateTime(now);
            boolean result = this.updateById(userDailyWord);
            if (result) {
                // 更新单词点赞数 +1
                dailyWordService.increaseLikeCount(wordId);
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 判断用户是否点赞了单词
     *
     * @param wordId
     * @param userId
     * @return
     */
    @Override
    public boolean isThumbWord(long wordId, long userId) {
        // 查询是否存在点赞记录
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        queryWrapper.eq("isLiked", 1);
        return this.count(queryWrapper) > 0;
    }
} 
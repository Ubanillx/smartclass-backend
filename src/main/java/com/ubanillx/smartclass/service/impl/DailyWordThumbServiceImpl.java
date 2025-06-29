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
     * 点赞单词
     *
     * @param wordId 单词id
     * @param loginUser 当前登录用户
     * @return 1-点赞成功，0-操作失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int thumbWord(long wordId, User loginUser) {
        // 判断单词是否存在
        DailyWord word = dailyWordService.getById(wordId);
        if (word == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        
        // 获取用户ID
        long userId = loginUser.getId();
        
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
        
        // 如果关联记录存在但未点赞，则设置为点赞
        Integer isLiked = userDailyWord.getIsLiked();
        if (isLiked == null || isLiked == 0) {
            userDailyWord.setIsLiked(1);
            userDailyWord.setLikeTime(now);
            userDailyWord.setUpdateTime(now);
            boolean result = this.updateById(userDailyWord);
            if (result) {
                // 更新单词点赞数 +1
                dailyWordService.increaseLikeCount(wordId);
                return 1;
            }
        }
        
        // 如果已经点赞，不做操作
        return 0;
    }
    
    /**
     * 取消点赞单词
     *
     * @param wordId 单词id
     * @param userId 用户id
     * @return 1-取消点赞成功，0-操作失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelThumbWord(long wordId, long userId) {
        // 判断单词是否存在
        DailyWord word = dailyWordService.getById(wordId);
        if (word == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        
        // 查询用户与单词的关联记录
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        UserDailyWord userDailyWord = this.getOne(queryWrapper);
        
        // 如果关联记录不存在或已经是未点赞状态，返回失败
        if (userDailyWord == null || userDailyWord.getIsLiked() == 0) {
            return 0;
        }
        
        // 设置为未点赞
        userDailyWord.setIsLiked(0);
        userDailyWord.setLikeTime(null);
        userDailyWord.setUpdateTime(new Date());
        boolean result = this.updateById(userDailyWord);
        if (result) {
            // 更新单词点赞数 -1
            dailyWordService.decreaseLikeCount(wordId);
            return 1;
        }
        return 0;
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
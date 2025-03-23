package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.mapper.UserDailyWordMapper;
import com.ubanillx.smartclass.model.entity.UserDailyWord;
import com.ubanillx.smartclass.service.UserDailyWordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户与每日单词关联服务实现
 */
@Service
public class UserDailyWordServiceImpl extends ServiceImpl<UserDailyWordMapper, UserDailyWord>
        implements UserDailyWordService {

    @Override
    public boolean markWordAsStudied(long wordId, long userId) {
        // 查询关联记录是否存在
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        UserDailyWord userDailyWord = this.getOne(queryWrapper);
        
        Date now = new Date();
        
        // 如果不存在，则创建新记录
        if (userDailyWord == null) {
            userDailyWord = new UserDailyWord();
            userDailyWord.setUserId(userId);
            userDailyWord.setWordId(wordId);
            userDailyWord.setIsStudied(1); // 设置为已学习
            userDailyWord.setStudyTime(now);
            userDailyWord.setCreateTime(now);
            return this.save(userDailyWord);
        } 
        
        // 如果存在，则更新为已学习
        userDailyWord.setIsStudied(1); // 设置为已学习
        userDailyWord.setStudyTime(now);
        userDailyWord.setUpdateTime(now);
        return this.updateById(userDailyWord);
    }

    @Override
    public boolean updateMasteryLevel(long wordId, long userId, int masteryLevel) {
        // 查询关联记录是否存在
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        UserDailyWord userDailyWord = this.getOne(queryWrapper);
        
        Date now = new Date();
        
        // 如果不存在，则创建新记录
        if (userDailyWord == null) {
            userDailyWord = new UserDailyWord();
            userDailyWord.setUserId(userId);
            userDailyWord.setWordId(wordId);
            userDailyWord.setMasteryLevel(masteryLevel);
            userDailyWord.setIsStudied(1); // 设置为已学习
            userDailyWord.setStudyTime(now);
            userDailyWord.setCreateTime(now);
            return this.save(userDailyWord);
        } 
        
        // 如果存在，则更新掌握程度
        userDailyWord.setMasteryLevel(masteryLevel);
        
        // 如果未标记为已学习，则同时标记为已学习
        if (userDailyWord.getIsStudied() == null || userDailyWord.getIsStudied() != 1) {
            userDailyWord.setIsStudied(1);
            userDailyWord.setStudyTime(now);
        }
        
        userDailyWord.setUpdateTime(now);
        return this.updateById(userDailyWord);
    }

    @Override
    public boolean saveWordNote(long wordId, long userId, String noteContent) {
        // 查询关联记录是否存在
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        UserDailyWord userDailyWord = this.getOne(queryWrapper);
        
        Date now = new Date();
        
        // 如果不存在，则创建新记录
        if (userDailyWord == null) {
            userDailyWord = new UserDailyWord();
            userDailyWord.setUserId(userId);
            userDailyWord.setWordId(wordId);
            userDailyWord.setNoteContent(noteContent);
            userDailyWord.setNoteTime(now);
            userDailyWord.setCreateTime(now);
            return this.save(userDailyWord);
        } 
        
        // 如果存在，则更新笔记内容
        userDailyWord.setNoteContent(noteContent);
        userDailyWord.setNoteTime(now);
        userDailyWord.setUpdateTime(now);
        return this.updateById(userDailyWord);
    }

    @Override
    public UserDailyWord getUserDailyWord(long wordId, long userId) {
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        return this.getOne(queryWrapper);
    }
} 
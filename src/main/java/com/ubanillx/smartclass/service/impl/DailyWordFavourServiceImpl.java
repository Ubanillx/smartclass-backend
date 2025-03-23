package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserDailyWordMapper;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyWord;
import com.ubanillx.smartclass.model.vo.DailyWordVO;
import com.ubanillx.smartclass.service.DailyWordFavourService;
import com.ubanillx.smartclass.service.DailyWordService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 每日单词收藏服务实现
*/
@Service
public class DailyWordFavourServiceImpl extends ServiceImpl<UserDailyWordMapper, UserDailyWord>
        implements DailyWordFavourService {

    @Resource
    private DailyWordService dailyWordService;

    /**
     * 收藏/取消收藏每日单词
     *
     * @param wordId
     * @param loginUser
     * @return
     */
    @Override
    public int doWordFavour(long wordId, User loginUser) {
        // 判断单词是否存在
        DailyWord word = dailyWordService.getById(wordId);
        if (word == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单词不存在");
        }
        // 获取用户ID
        long userId = loginUser.getId();
        // 每个用户串行收藏，避免并发问题
        // 锁必须要包裹住事务方法
        DailyWordFavourService dailyWordFavourService = (DailyWordFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return dailyWordFavourService.doWordFavourInner(userId, wordId);
        }
    }

    /**
     * 分页获取用户收藏的每日单词列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    @Override
    public Page<DailyWordVO> listFavourWordByPage(IPage<DailyWord> page, Wrapper<DailyWord> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        
        // 使用Mapper中的自定义方法查询收藏的单词
        Page<DailyWord> wordPage = baseMapper.listFavourWordByPage(page, queryWrapper, favourUserId);
        
        // 将单词实体转换为VO
        List<DailyWordVO> wordVOList = dailyWordService.getDailyWordVO(wordPage.getRecords());
        
        // 构建返回结果
        Page<DailyWordVO> wordVOPage = new Page<>(page.getCurrent(), page.getSize(), wordPage.getTotal());
        wordVOPage.setRecords(wordVOList);
        
        return wordVOPage;
    }

    /**
     * 判断用户是否收藏了单词
     *
     * @param wordId
     * @param userId
     * @return
     */
    @Override
    public boolean isFavourWord(long wordId, long userId) {
        // 查询是否存在收藏记录
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        queryWrapper.eq("isCollected", 1);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 更新单词掌握程度
     * 
     * @param wordId
     * @param userId
     * @param masteryLevel 掌握程度：0-未知，1-生词，2-熟悉，3-掌握
     * @return
     */
    @Override
    public boolean updateMasteryLevel(long wordId, long userId, int masteryLevel) {
        if (masteryLevel < 0 || masteryLevel > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "掌握程度值无效");
        }
        
        // 查询用户与单词的关联记录
        UserDailyWord userDailyWord = getUserDailyWord(wordId, userId);
        
        // 更新掌握程度
        userDailyWord.setMasteryLevel(masteryLevel);
        userDailyWord.setUpdateTime(new Date());
        
        return this.updateById(userDailyWord);
    }

    /**
     * 保存单词笔记
     * 
     * @param wordId
     * @param userId
     * @param noteContent
     * @return
     */
    @Override
    public boolean saveWordNote(long wordId, long userId, String noteContent) {
        if (noteContent == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记内容不能为空");
        }
        
        // 查询用户与单词的关联记录
        UserDailyWord userDailyWord = getUserDailyWord(wordId, userId);
        
        // 更新笔记内容和时间
        Date now = new Date();
        userDailyWord.setNoteContent(noteContent);
        userDailyWord.setNoteTime(now);
        userDailyWord.setUpdateTime(now);
        
        return this.updateById(userDailyWord);
    }

    /**
     * 标记单词为已学习
     * 
     * @param wordId
     * @param userId
     * @return
     */
    @Override
    public boolean markWordAsStudied(long wordId, long userId) {
        // 查询用户与单词的关联记录
        UserDailyWord userDailyWord = getUserDailyWord(wordId, userId);
        
        // 标记为已学习
        Date now = new Date();
        userDailyWord.setIsStudied(1);
        userDailyWord.setStudyTime(now);
        userDailyWord.setUpdateTime(now);
        
        // 如果掌握程度为未知(0)，则默认设置为生词(1)
        if (userDailyWord.getMasteryLevel() == null || userDailyWord.getMasteryLevel() == 0) {
            userDailyWord.setMasteryLevel(1);
        }
        
        return this.updateById(userDailyWord);
    }

    /**
     * 收藏/取消收藏每日单词（内部事务方法）
     *
     * @param userId
     * @param wordId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doWordFavourInner(long userId, long wordId) {
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
            userDailyWord.setIsCollected(1); // 设置为已收藏
            userDailyWord.setCollectTime(now);
            userDailyWord.setCreateTime(now);
            userDailyWord.setMasteryLevel(0); // 初始掌握程度为未知
            boolean result = this.save(userDailyWord);
            return result ? 1 : 0;
        }
        
        // 如果关联记录存在
        Integer isCollected = userDailyWord.getIsCollected();
        
        // 如果已收藏，则取消收藏
        if (isCollected != null && isCollected == 1) {
            userDailyWord.setIsCollected(0);
            userDailyWord.setCollectTime(null);
            userDailyWord.setUpdateTime(now);
            boolean result = this.updateById(userDailyWord);
            return result ? -1 : 0;
        } 
        // 如果未收藏，则设置为收藏
        else {
            userDailyWord.setIsCollected(1);
            userDailyWord.setCollectTime(now);
            userDailyWord.setUpdateTime(now);
            boolean result = this.updateById(userDailyWord);
            return result ? 1 : 0;
        }
    }
    
    /**
     * 获取用户与单词的关联记录，如果不存在则创建
     * 
     * @param wordId
     * @param userId
     * @return
     */
    private UserDailyWord getUserDailyWord(long wordId, long userId) {
        // 查询用户与单词的关联记录
        QueryWrapper<UserDailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        UserDailyWord userDailyWord = this.getOne(queryWrapper);
        
        // 如果关联记录不存在，创建新记录
        if (userDailyWord == null) {
            Date now = new Date();
            userDailyWord = new UserDailyWord();
            userDailyWord.setUserId(userId);
            userDailyWord.setWordId(wordId);
            userDailyWord.setIsCollected(0);
            userDailyWord.setIsStudied(0);
            userDailyWord.setMasteryLevel(0);
            userDailyWord.setCreateTime(now);
            this.save(userDailyWord);
        }
        
        return userDailyWord;
    }
} 
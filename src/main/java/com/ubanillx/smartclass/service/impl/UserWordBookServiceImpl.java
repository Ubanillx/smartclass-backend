package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.DailyWordMapper;
import com.ubanillx.smartclass.mapper.UserWordBookMapper;
import com.ubanillx.smartclass.model.dto.userwordbook.UserWordBookQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.entity.UserWordBook;
import com.ubanillx.smartclass.model.vo.UserWordBookVO;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.service.UserWordBookService;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author rog
* @description 针对表【user_word_book(用户生词本)】的数据库操作Service实现
* @createDate 2025-04-17 22:49:58
*/
@Service
@Slf4j
public class UserWordBookServiceImpl extends ServiceImpl<UserWordBookMapper, UserWordBook>
    implements UserWordBookService {

    @Resource
    private DailyWordMapper dailyWordMapper;

    @Resource
    private DailyWordService dailyWordService;

    @Override
    public boolean addToWordBook(Long userId, Long wordId, Integer difficulty) {
        if (userId == null || wordId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 先查询是否已存在
        QueryWrapper<UserWordBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        UserWordBook userWordBook = this.getOne(queryWrapper);

        // 如果已存在且未删除，则不重复添加
        if (userWordBook != null && userWordBook.getIsDeleted() == 0) {
            return true;
        }

        // 设置默认难度
        if (difficulty == null) {
            difficulty = 1; // 默认简单
        }

        // 如果已存在但被标记为删除，则更新状态
        if (userWordBook != null) {
            userWordBook.setIsDeleted(0);
            userWordBook.setDifficulty(difficulty);
            userWordBook.setLearningStatus(0);
            userWordBook.setUpdateTime(new Date());
            return this.updateById(userWordBook);
        }

        // 如果不存在，创建新记录
        UserWordBook newUserWordBook = new UserWordBook();
        newUserWordBook.setUserId(userId);
        newUserWordBook.setWordId(wordId);
        newUserWordBook.setLearningStatus(0); // 未学习
        newUserWordBook.setIsCollected(0); // 未收藏
        newUserWordBook.setDifficulty(difficulty);
        newUserWordBook.setIsDeleted(0);
        newUserWordBook.setCreateTime(new Date());
        newUserWordBook.setUpdateTime(new Date());

        return this.save(newUserWordBook);
    }

    @Override
    public boolean removeFromWordBook(Long userId, Long wordId) {
        if (userId == null || wordId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 使用逻辑删除
        UpdateWrapper<UserWordBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.eq("wordId", wordId);
        updateWrapper.set("isDeleted", 1);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateLearningStatus(Long userId, Long wordId, Integer learningStatus) {
        if (userId == null || wordId == null || learningStatus == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        UpdateWrapper<UserWordBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.eq("wordId", wordId);
        updateWrapper.eq("isDeleted", 0);
        updateWrapper.set("learningStatus", learningStatus);
        updateWrapper.set("updateTime", new Date());

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateCollectionStatus(Long userId, Long wordId, Integer isCollected) {
        if (userId == null || wordId == null || isCollected == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        UpdateWrapper<UserWordBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.eq("wordId", wordId);
        updateWrapper.eq("isDeleted", 0);
        updateWrapper.set("isCollected", isCollected);

        // 如果是收藏，设置收藏时间
        if (isCollected == 1) {
            updateWrapper.set("collectedTime", new Date());
        }
        
        updateWrapper.set("updateTime", new Date());

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateDifficulty(Long userId, Long wordId, Integer difficulty) {
        if (userId == null || wordId == null || difficulty == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        UpdateWrapper<UserWordBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.eq("wordId", wordId);
        updateWrapper.eq("isDeleted", 0);
        updateWrapper.set("difficulty", difficulty);
        updateWrapper.set("updateTime", new Date());

        return this.update(updateWrapper);
    }

    @Override
    public List<UserWordBookVO> getUserWordBookList(Long userId, Integer learningStatus, Integer isCollected) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        QueryWrapper<UserWordBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("isDeleted", 0);
        
        if (learningStatus != null) {
            queryWrapper.eq("learningStatus", learningStatus);
        }
        
        if (isCollected != null) {
            queryWrapper.eq("isCollected", isCollected);
        }
        
        queryWrapper.orderByDesc("updateTime");
        
        List<UserWordBook> userWordBookList = this.list(queryWrapper);
        return this.getUserWordBookVO(userWordBookList);
    }

    @Override
    public QueryWrapper<UserWordBook> getQueryWrapper(UserWordBookQueryRequest userWordBookQueryRequest) {
        if (userWordBookQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = userWordBookQueryRequest.getId();
        Long userId = userWordBookQueryRequest.getUserId();
        Long wordId = userWordBookQueryRequest.getWordId();
        Integer learningStatus = userWordBookQueryRequest.getLearningStatus();
        Integer isCollected = userWordBookQueryRequest.getIsCollected();
        Integer difficulty = userWordBookQueryRequest.getDifficulty();
        String word = userWordBookQueryRequest.getWord();
        Date createTime = userWordBookQueryRequest.getCreateTime();
        Date createTimeStart = userWordBookQueryRequest.getCreateTimeStart();
        Date createTimeEnd = userWordBookQueryRequest.getCreateTimeEnd();
        String sortField = userWordBookQueryRequest.getSortField();
        String sortOrder = userWordBookQueryRequest.getSortOrder();

        QueryWrapper<UserWordBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.eq(wordId != null, "wordId", wordId);
        queryWrapper.eq(learningStatus != null, "learningStatus", learningStatus);
        queryWrapper.eq(isCollected != null, "isCollected", isCollected);
        queryWrapper.eq(difficulty != null, "difficulty", difficulty);
        queryWrapper.eq(createTime != null, "createTime", createTime);
        queryWrapper.ge(createTimeStart != null, "createTime", createTimeStart);
        queryWrapper.le(createTimeEnd != null, "createTime", createTimeEnd);
        queryWrapper.eq("isDeleted", 0);

        // 处理按单词内容模糊查询的情况
        if (StringUtils.isNotBlank(word)) {
            List<Long> wordIds = getWordIdsByContent(word);
            if (!wordIds.isEmpty()) {
                queryWrapper.in("wordId", wordIds);
            } else {
                // 如果没有匹配的单词，设置一个不可能的条件
                queryWrapper.eq("id", -1);
            }
        }

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), 
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), 
                sortField);
                
        return queryWrapper;
    }

    /**
     * 根据单词内容模糊查询获取单词ID列表
     */
    private List<Long> getWordIdsByContent(String word) {
        if (StringUtils.isBlank(word)) {
            return new ArrayList<>();
        }
        QueryWrapper<DailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("word", word);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.select("id");
        
        return dailyWordMapper.selectList(queryWrapper)
                .stream()
                .map(DailyWord::getId)
                .collect(Collectors.toList());
    }

    @Override
    public UserWordBookVO getUserWordBookVO(UserWordBook userWordBook) {
        if (userWordBook == null) {
            return null;
        }
        
        UserWordBookVO userWordBookVO = new UserWordBookVO();
        BeanUtils.copyProperties(userWordBook, userWordBookVO);
        
        // 获取单词详细信息
        Long wordId = userWordBook.getWordId();
        if (wordId != null) {
            DailyWord dailyWord = dailyWordMapper.selectById(wordId);
            if (dailyWord != null) {
                userWordBookVO.setWord(dailyWord.getWord());
                userWordBookVO.setTranslation(dailyWord.getTranslation());
                userWordBookVO.setPhonetic(dailyWord.getPronunciation());  // 修改为使用pronunciation字段
                userWordBookVO.setPronunciation(dailyWord.getAudioUrl());  // 修改为使用audioUrl字段
                userWordBookVO.setExample(dailyWord.getExample());
            } else {
                log.warn("找不到单词详细信息，wordId={}", wordId);
                // 设置一个默认值，避免前端显示为null
                userWordBookVO.setWord("未知单词");
                userWordBookVO.setTranslation("暂无翻译");
            }
        }
        
        return userWordBookVO;
    }

    @Override
    public List<UserWordBookVO> getUserWordBookVO(List<UserWordBook> userWordBookList) {
        if (CollUtil.isEmpty(userWordBookList)) {
            return new ArrayList<>();
        }
        
        return userWordBookList.stream()
                .map(this::getUserWordBookVO)
                .collect(Collectors.toList());
    }

    @Override
    public int[] getUserWordBookStatistics(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 返回值：[总收藏数，已学习数，待复习数]
        int[] stats = new int[3];
        
        // 总收藏数
        QueryWrapper<UserWordBook> totalQuery = new QueryWrapper<>();
        totalQuery.eq("userId", userId);
        totalQuery.eq("isDeleted", 0);
        stats[0] = Math.toIntExact(this.count(totalQuery));  // 使用Math.toIntExact进行安全转换
        
        // 已学习数（学习状态为已学习和已掌握的）
        QueryWrapper<UserWordBook> learnedQuery = new QueryWrapper<>();
        learnedQuery.eq("userId", userId);
        learnedQuery.eq("isDeleted", 0);
        learnedQuery.eq("learningStatus", 1); // 已学习
        stats[1] = Math.toIntExact(this.count(learnedQuery));  // 使用Math.toIntExact进行安全转换
        
        // 待复习数（已添加但还未掌握的）
        QueryWrapper<UserWordBook> toReviewQuery = new QueryWrapper<>();
        toReviewQuery.eq("userId", userId);
        toReviewQuery.eq("isDeleted", 0);
        toReviewQuery.ne("learningStatus", 2); // 非已掌握
        stats[2] = Math.toIntExact(this.count(toReviewQuery));  // 使用Math.toIntExact进行安全转换
        
        return stats;
    }

    @Override
    public boolean isWordInUserBook(Long userId, Long wordId) {
        if (userId == null || wordId == null) {
            return false;
        }
        
        QueryWrapper<UserWordBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("wordId", wordId);
        queryWrapper.eq("isDeleted", 0);
        
        return this.count(queryWrapper) > 0;
    }
} 
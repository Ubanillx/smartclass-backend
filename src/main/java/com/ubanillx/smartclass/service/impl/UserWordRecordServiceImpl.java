package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserWordRecordMapper;
import com.ubanillx.smartclass.model.entity.UserWordRecord;
import com.ubanillx.smartclass.service.UserWordRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author liulo
* @description 针对表【user_word_record(用户单词学习记录)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserWordRecordServiceImpl extends BaseRelationServiceImpl<UserWordRecordMapper, UserWordRecord>
    implements UserWordRecordService {
    
    public UserWordRecordServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("wordId");
    }

    @Override
    public UserWordRecord getUserWordRecord(Long userId, Long wordId) {
        // 参数校验
        if (userId == null || wordId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        LambdaQueryWrapper<UserWordRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWordRecord::getUserId, userId)
                .eq(UserWordRecord::getWordId, wordId);
        
        return getOne(queryWrapper);
    }

    @Override
    public boolean updateUserWordRecord(Long userId, Long wordId, Integer learningStatus, Integer learningProgress) {
        // 参数校验
        if (userId == null || wordId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询记录是否存在
        UserWordRecord userWordRecord = getUserWordRecord(userId, wordId);
        if (userWordRecord == null) {
            // 不存在则创建新记录
            userWordRecord = new UserWordRecord();
            userWordRecord.setUserId(userId);
            userWordRecord.setWordId(wordId);
            userWordRecord.setLearningStatus(learningStatus != null ? learningStatus : 0);
            userWordRecord.setLearningProgress(learningProgress != null ? learningProgress : 0);
            userWordRecord.setReviewCount(0);
            userWordRecord.setLastReviewTime(new Date());
            userWordRecord.setCreateTime(new Date());
            userWordRecord.setUpdateTime(new Date());
            return save(userWordRecord);
        } else {
            // 存在则更新记录
            LambdaUpdateWrapper<UserWordRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserWordRecord::getUserId, userId)
                    .eq(UserWordRecord::getWordId, wordId);
            
            if (learningStatus != null) {
                updateWrapper.set(UserWordRecord::getLearningStatus, learningStatus);
            }
            if (learningProgress != null) {
                updateWrapper.set(UserWordRecord::getLearningProgress, learningProgress);
            }
            
            updateWrapper.set(UserWordRecord::getLastReviewTime, new Date());
            updateWrapper.set(UserWordRecord::getReviewCount, userWordRecord.getReviewCount() + 1);
            updateWrapper.set(UserWordRecord::getUpdateTime, new Date());
            
            return update(updateWrapper);
        }
    }

    @Override
    public boolean addUserWordRecord(UserWordRecord userWordRecord) {
        // 参数校验
        if (userWordRecord == null || userWordRecord.getUserId() == null || userWordRecord.getWordId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询记录是否存在
        UserWordRecord existRecord = getUserWordRecord(userWordRecord.getUserId(), userWordRecord.getWordId());
        if (existRecord != null) {
            // 已存在则更新
            userWordRecord.setId(existRecord.getId());
            userWordRecord.setReviewCount(existRecord.getReviewCount() + 1);
            userWordRecord.setLastReviewTime(new Date());
            userWordRecord.setUpdateTime(new Date());
            return updateById(userWordRecord);
        } else {
            // 不存在则创建
            if (userWordRecord.getLearningStatus() == null) {
                userWordRecord.setLearningStatus(0);
            }
            if (userWordRecord.getLearningProgress() == null) {
                userWordRecord.setLearningProgress(0);
            }
            if (userWordRecord.getReviewCount() == null) {
                userWordRecord.setReviewCount(0);
            }
            userWordRecord.setLastReviewTime(new Date());
            userWordRecord.setCreateTime(new Date());
            userWordRecord.setUpdateTime(new Date());
            return save(userWordRecord);
        }
    }
}





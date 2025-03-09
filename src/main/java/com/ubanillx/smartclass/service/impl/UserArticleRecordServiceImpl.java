package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserArticleRecordMapper;
import com.ubanillx.smartclass.model.entity.UserArticleRecord;
import com.ubanillx.smartclass.service.UserArticleRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author liulo
* @description 针对表【user_article_record(用户文章阅读记录)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserArticleRecordServiceImpl extends BaseRelationServiceImpl<UserArticleRecordMapper, UserArticleRecord>
    implements UserArticleRecordService {
    
    public UserArticleRecordServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("articleId");
    }

    @Override
    public UserArticleRecord getUserArticleRecord(Long userId, Long articleId) {
        // 参数校验
        if (userId == null || articleId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        LambdaQueryWrapper<UserArticleRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserArticleRecord::getUserId, userId)
                .eq(UserArticleRecord::getArticleId, articleId);
        
        return getOne(queryWrapper);
    }

    @Override
    public boolean updateUserArticleRecord(Long userId, Long articleId, Integer readStatus, Integer readingProgress, Integer hasLiked) {
        // 参数校验
        if (userId == null || articleId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询记录是否存在
        UserArticleRecord userArticleRecord = getUserArticleRecord(userId, articleId);
        if (userArticleRecord == null) {
            // 不存在则创建新记录
            userArticleRecord = new UserArticleRecord();
            userArticleRecord.setUserId(userId);
            userArticleRecord.setArticleId(articleId);
            userArticleRecord.setReadStatus(readStatus != null ? readStatus : 0);
            userArticleRecord.setReadProgress(readingProgress != null ? readingProgress : 0);
            userArticleRecord.setIsLiked(hasLiked != null ? hasLiked : 0);
            userArticleRecord.setCreateTime(new Date());
            userArticleRecord.setUpdateTime(new Date());
            return save(userArticleRecord);
        } else {
            // 存在则更新记录
            LambdaUpdateWrapper<UserArticleRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserArticleRecord::getUserId, userId)
                    .eq(UserArticleRecord::getArticleId, articleId);
            
            if (readStatus != null) {
                updateWrapper.set(UserArticleRecord::getReadStatus, readStatus);
            }
            if (readingProgress != null) {
                updateWrapper.set(UserArticleRecord::getReadProgress, readingProgress);
            }
            if (hasLiked != null) {
                updateWrapper.set(UserArticleRecord::getIsLiked, hasLiked);
            }
            
            updateWrapper.set(UserArticleRecord::getUpdateTime, new Date());
            
            return update(updateWrapper);
        }
    }

    @Override
    public boolean addUserArticleRecord(UserArticleRecord userArticleRecord) {
        // 参数校验
        if (userArticleRecord == null || userArticleRecord.getUserId() == null || userArticleRecord.getArticleId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询记录是否存在
        UserArticleRecord existRecord = getUserArticleRecord(userArticleRecord.getUserId(), userArticleRecord.getArticleId());
        if (existRecord != null) {
            // 已存在则更新
            userArticleRecord.setId(existRecord.getId());
            userArticleRecord.setUpdateTime(new Date());
            return updateById(userArticleRecord);
        } else {
            // 不存在则创建
            if (userArticleRecord.getReadStatus() == null) {
                userArticleRecord.setReadStatus(0);
            }
            if (userArticleRecord.getReadProgress() == null) {
                userArticleRecord.setReadProgress(0);
            }
            if (userArticleRecord.getIsLiked() == null) {
                userArticleRecord.setIsLiked(0);
            }
            userArticleRecord.setCreateTime(new Date());
            userArticleRecord.setUpdateTime(new Date());
            return save(userArticleRecord);
        }
    }
}





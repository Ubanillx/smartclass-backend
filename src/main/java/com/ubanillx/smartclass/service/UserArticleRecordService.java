package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserArticleRecord;

/**
* @author liulo
* @description 针对表【user_article_record(用户文章阅读记录)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface UserArticleRecordService extends BaseRelationService<UserArticleRecord> {

    /**
     * 获取用户文章阅读记录
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 阅读记录
     */
    UserArticleRecord getUserArticleRecord(Long userId, Long articleId);
    
    /**
     * 更新用户文章阅读记录
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param readStatus 阅读状态
     * @param readingProgress 阅读进度
     * @param hasLiked 是否点赞
     * @return 是否成功
     */
    boolean updateUserArticleRecord(Long userId, Long articleId, Integer readStatus, Integer readingProgress, Integer hasLiked);
    
    /**
     * 添加用户文章阅读记录
     * @param userArticleRecord 阅读记录
     * @return 是否成功
     */
    boolean addUserArticleRecord(UserArticleRecord userArticleRecord);
}

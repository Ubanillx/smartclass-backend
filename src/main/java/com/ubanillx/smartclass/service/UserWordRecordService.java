package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.UserWordRecord;

/**
* @author liulo
* @description 针对表【user_word_record(用户单词学习记录)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface UserWordRecordService extends BaseRelationService<UserWordRecord> {

    /**
     * 获取用户单词学习记录
     * @param userId 用户ID
     * @param wordId 单词ID
     * @return 学习记录
     */
    UserWordRecord getUserWordRecord(Long userId, Long wordId);
    
    /**
     * 更新用户单词学习记录
     * @param userId 用户ID
     * @param wordId 单词ID
     * @param learningStatus 学习状态
     * @param learningProgress 学习进度
     * @return 是否成功
     */
    boolean updateUserWordRecord(Long userId, Long wordId, Integer learningStatus, Integer learningProgress);
    
    /**
     * 添加用户单词学习记录
     * @param userWordRecord 学习记录
     * @return 是否成功
     */
    boolean addUserWordRecord(UserWordRecord userWordRecord);
}

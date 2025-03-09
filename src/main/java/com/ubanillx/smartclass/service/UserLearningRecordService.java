package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【user_learning_record(用户学习记录)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface UserLearningRecordService extends BaseRelationService<UserLearningRecord> {

    /**
     * 创建学习记录
     * @param userId 用户ID
     * @param recordType 记录类型
     * @param relatedId 关联ID
     * @param duration 学习时长（秒）
     * @param count 学习数量
     * @param points 获得积分
     * @param experience 获得经验
     * @return 学习记录
     */
    UserLearningRecord createLearningRecord(Long userId, String recordType, Long relatedId, 
                                           Integer duration, Integer count, Integer points, Integer experience);
    
    /**
     * 创建课程学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param sectionId 小节ID
     * @param minutesSpent 学习时间（分钟）
     * @return 学习记录
     */
    UserLearningRecord createCourseLearningRecord(Long userId, Long courseId, Long sectionId, int minutesSpent);
    
    /**
     * 创建单词学习记录
     * @param userId 用户ID
     * @param wordId 单词ID
     * @param count 学习数量
     * @return 学习记录
     */
    UserLearningRecord createWordLearningRecord(Long userId, Long wordId, int count);
    
    /**
     * 创建文章阅读记录
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param duration 阅读时长（秒）
     * @return 学习记录
     */
    UserLearningRecord createArticleReadingRecord(Long userId, Long articleId, int duration);
    
    /**
     * 获取用户学习记录
     * @param userId 用户ID
     * @param page 分页参数
     * @return 学习记录分页列表
     */
    IPage<UserLearningRecord> getUserLearningRecords(Long userId, Page<UserLearningRecord> page);
    
    /**
     * 获取用户指定日期的学习记录
     * @param userId 用户ID
     * @param date 日期
     * @return 学习记录列表
     */
    List<UserLearningRecord> getUserLearningRecordsByDate(Long userId, Date date);
    
    /**
     * 获取用户指定类型的学习记录
     * @param userId 用户ID
     * @param recordType 记录类型
     * @param page 分页参数
     * @return 学习记录分页列表
     */
    IPage<UserLearningRecord> getUserLearningRecordsByType(Long userId, String recordType, Page<UserLearningRecord> page);
    
    /**
     * 获取用户指定关联ID的学习记录
     * @param userId 用户ID
     * @param relatedId 关联ID
     * @return 学习记录列表
     */
    List<UserLearningRecord> getUserLearningRecordsByRelatedId(Long userId, Long relatedId);
}

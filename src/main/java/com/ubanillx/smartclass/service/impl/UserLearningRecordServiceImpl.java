package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.manager.LearningManager;
import com.ubanillx.smartclass.mapper.UserLearningRecordMapper;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;
import com.ubanillx.smartclass.service.UserLearningRecordService;
import com.ubanillx.smartclass.service.UserLearningStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【user_learning_record(用户学习记录)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserLearningRecordServiceImpl extends BaseRelationServiceImpl<UserLearningRecordMapper, UserLearningRecord>
    implements UserLearningRecordService {
    
    @Autowired
    private LearningManager learningManager;
    
    @Autowired
    private UserLearningStatsService userLearningStatsService;
    
    public UserLearningRecordServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("relatedId");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLearningRecord createLearningRecord(Long userId, String recordType, Long relatedId, 
                                                 Integer duration, Integer count, Integer points, Integer experience) {
        if (userId == null || recordType == null || relatedId == null) {
            return null;
        }
        
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRecordType(recordType);
        record.setRelatedId(relatedId);
        record.setRecordDate(new Date());
        record.setDuration(duration != null ? duration : 0);
        record.setCount(count != null ? count : 0);
        record.setPoints(points != null ? points : 0);
        record.setExperience(experience != null ? experience : 0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        save(record);
        
        // 更新用户学习统计数据
        if (points != null && points > 0) {
            userLearningStatsService.addPoints(userId, points);
        }
        
        if (experience != null && experience > 0) {
            userLearningStatsService.addExperience(userId, experience);
        }
        
        return record;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLearningRecord createCourseLearningRecord(Long userId, Long courseId, Long sectionId, int minutesSpent) {
        if (userId == null || courseId == null || sectionId == null || minutesSpent <= 0) {
            return null;
        }
        
        // 使用LearningManager创建学习记录
        UserLearningRecord record = learningManager.createLearningRecord(userId, courseId, sectionId, minutesSpent);
        if (record == null) {
            return null;
        }
        
        // 设置记录类型和其他字段
        record.setRecordType("course");
        record.setRelatedId(courseId);
        record.setDuration(minutesSpent * 60); // 转换为秒
        record.setRecordDate(new Date());
        
        // 计算获得的积分和经验值（示例：每分钟1积分，2经验）
        int points = minutesSpent;
        int experience = minutesSpent * 2;
        
        record.setPoints(points);
        record.setExperience(experience);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        save(record);
        
        // 更新用户学习统计数据
        userLearningStatsService.updateLearningStats(userId, minutesSpent, 0, 0);
        userLearningStatsService.addPoints(userId, points);
        userLearningStatsService.addExperience(userId, experience);
        
        return record;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLearningRecord createWordLearningRecord(Long userId, Long wordId, int count) {
        if (userId == null || wordId == null || count <= 0) {
            return null;
        }
        
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRecordType("word");
        record.setRelatedId(wordId);
        record.setRecordDate(new Date());
        record.setCount(count);
        
        // 计算获得的积分和经验值（示例：每个单词2积分，3经验）
        int points = count * 2;
        int experience = count * 3;
        
        record.setPoints(points);
        record.setExperience(experience);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        save(record);
        
        // 更新用户学习统计数据
        userLearningStatsService.updateLearningStats(userId, 0, count, 0);
        userLearningStatsService.addPoints(userId, points);
        userLearningStatsService.addExperience(userId, experience);
        
        return record;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLearningRecord createArticleReadingRecord(Long userId, Long articleId, int duration) {
        if (userId == null || articleId == null || duration <= 0) {
            return null;
        }
        
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRecordType("article");
        record.setRelatedId(articleId);
        record.setRecordDate(new Date());
        record.setDuration(duration);
        record.setCount(1); // 一篇文章
        
        // 计算获得的积分和经验值（示例：每分钟1积分，2经验）
        int minutes = duration / 60;
        int points = minutes;
        int experience = minutes * 2;
        
        record.setPoints(points);
        record.setExperience(experience);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        save(record);
        
        // 更新用户学习统计数据
        userLearningStatsService.updateLearningStats(userId, minutes, 0, 1);
        userLearningStatsService.addPoints(userId, points);
        userLearningStatsService.addExperience(userId, experience);
        
        return record;
    }
    
    @Override
    public IPage<UserLearningRecord> getUserLearningRecords(Long userId, Page<UserLearningRecord> page) {
        if (userId == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserLearningRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLearningRecord::getUserId, userId)
                .orderByDesc(UserLearningRecord::getCreateTime);
        
        return page(page, queryWrapper);
    }
    
    @Override
    public List<UserLearningRecord> getUserLearningRecordsByDate(Long userId, Date date) {
        if (userId == null || date == null) {
            return null;
        }
        
        // 设置日期的时分秒为0，只比较日期部分
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = calendar.getTime();
        
        // 查询指定日期的学习记录
        LambdaQueryWrapper<UserLearningRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLearningRecord::getUserId, userId)
                .ge(UserLearningRecord::getRecordDate, startOfDay)
                .lt(UserLearningRecord::getRecordDate, endOfDay)
                .orderByDesc(UserLearningRecord::getCreateTime);
        
        return list(queryWrapper);
    }
    
    @Override
    public IPage<UserLearningRecord> getUserLearningRecordsByType(Long userId, String recordType, Page<UserLearningRecord> page) {
        if (userId == null || recordType == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserLearningRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLearningRecord::getUserId, userId)
                .eq(UserLearningRecord::getRecordType, recordType)
                .orderByDesc(UserLearningRecord::getCreateTime);
        
        return page(page, queryWrapper);
    }
    
    @Override
    public List<UserLearningRecord> getUserLearningRecordsByRelatedId(Long userId, Long relatedId) {
        if (userId == null || relatedId == null) {
            return null;
        }
        
        LambdaQueryWrapper<UserLearningRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLearningRecord::getUserId, userId)
                .eq(UserLearningRecord::getRelatedId, relatedId)
                .orderByDesc(UserLearningRecord::getCreateTime);
        
        return list(queryWrapper);
    }
}





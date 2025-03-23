package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.learningrecord.UserLearningRecordQueryRequest;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;
import com.ubanillx.smartclass.model.vo.UserLearningRecordVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户学习记录服务
 */
public interface UserLearningRecordService extends IService<UserLearningRecord> {

    /**
     * 校验用户学习记录
     *
     * @param userLearningRecord
     * @param add 是否为添加校验
     */
    void validUserLearningRecord(UserLearningRecord userLearningRecord, boolean add);

    /**
     * 获取查询条件
     *
     * @param userLearningRecordQueryRequest
     * @return
     */
    QueryWrapper<UserLearningRecord> getQueryWrapper(UserLearningRecordQueryRequest userLearningRecordQueryRequest);

    /**
     * 获取用户学习记录视图
     *
     * @param userLearningRecord
     * @param request
     * @return
     */
    UserLearningRecordVO getUserLearningRecordVO(UserLearningRecord userLearningRecord, HttpServletRequest request);

    /**
     * 获取用户学习记录视图列表
     *
     * @param userLearningRecordList
     * @param request
     * @return
     */
    List<UserLearningRecordVO> getUserLearningRecordVO(List<UserLearningRecord> userLearningRecordList, HttpServletRequest request);

    /**
     * 分页获取用户学习记录视图
     *
     * @param userLearningRecordPage
     * @param request
     * @return
     */
    Page<UserLearningRecordVO> getUserLearningRecordVOPage(Page<UserLearningRecord> userLearningRecordPage, HttpServletRequest request);

    /**
     * 添加学习记录
     *
     * @param userLearningRecord
     * @param userId
     * @return
     */
    long addUserLearningRecord(UserLearningRecord userLearningRecord, Long userId);

    /**
     * 获取用户在指定日期范围内的各类型学习时长统计
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String, Object>> getUserLearningDurationStats(Long userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的各类型学习数量统计
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String, Object>> getUserLearningCountStats(Long userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的积分和经验值统计
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    Map<String, Object> getUserPointsAndExperienceStats(Long userId, Date startDate, Date endDate);

    /**
     * 获取用户每日学习记录数量统计
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String, Object>> getUserDailyLearningStats(Long userId, Date startDate, Date endDate);

    /**
     * 记录课程学习
     *
     * @param userId
     * @param courseId
     * @param sectionId
     * @param duration
     * @param progress
     * @return
     */
    long recordCourseStudy(Long userId, Long courseId, Long sectionId, Integer duration, Integer progress);

    /**
     * 记录单词学习
     *
     * @param userId
     * @param wordId
     * @param count
     * @param accuracy
     * @return
     */
    long recordWordStudy(Long userId, Long wordId, Integer count, Double accuracy);

    /**
     * 记录听力练习
     *
     * @param userId
     * @param listeningId
     * @param duration
     * @param accuracy
     * @return
     */
    long recordListeningPractice(Long userId, Long listeningId, Integer duration, Double accuracy);

    /**
     * 记录阅读练习
     *
     * @param userId
     * @param articleId
     * @param duration
     * @param accuracy
     * @return
     */
    long recordReadingPractice(Long userId, Long articleId, Integer duration, Double accuracy);
}

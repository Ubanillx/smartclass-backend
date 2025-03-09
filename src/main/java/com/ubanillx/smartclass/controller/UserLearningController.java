package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.model.entity.UserDailyGoal;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;
import com.ubanillx.smartclass.model.entity.UserLearningStats;
import com.ubanillx.smartclass.service.UserDailyGoalService;
import com.ubanillx.smartclass.service.UserLearningRecordService;
import com.ubanillx.smartclass.service.UserLearningStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 用户学习控制器
 */
@RestController
@RequestMapping("/api/learning")
public class UserLearningController {

    @Autowired
    private UserDailyGoalService userDailyGoalService;

    @Autowired
    private UserLearningStatsService userLearningStatsService;

    @Autowired
    private UserLearningRecordService userLearningRecordService;

    // ==================== 每日目标相关接口 ====================

    /**
     * 获取用户当日学习目标
     * @param userId 用户ID
     * @return 用户每日目标
     */
    @GetMapping("/daily-goal")
    public BaseResponse<UserDailyGoal> getUserDailyGoal(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        UserDailyGoal dailyGoal = userDailyGoalService.getUserDailyGoal(userId);
        return ResultUtils.success(dailyGoal);
    }

    /**
     * 创建用户每日学习目标
     * @param userId 用户ID
     * @param targetMinutes 目标学习时间（分钟）
     * @return 用户每日目标
     */
    @PostMapping("/daily-goal/create")
    public BaseResponse<UserDailyGoal> createUserDailyGoal(@RequestParam Long userId, @RequestParam Integer targetMinutes) {
        if (userId == null || targetMinutes == null || targetMinutes <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UserDailyGoal dailyGoal = userDailyGoalService.createUserDailyGoal(userId, targetMinutes);
        return ResultUtils.success(dailyGoal);
    }

    /**
     * 更新用户学习时间
     * @param userId 用户ID
     * @param minutesSpent 学习时间（分钟）
     * @return 是否完成目标
     */
    @PostMapping("/daily-goal/update-time")
    public BaseResponse<Boolean> updateLearningTime(@RequestParam Long userId, @RequestParam Integer minutesSpent) {
        if (userId == null || minutesSpent == null || minutesSpent <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        boolean isCompleted = userDailyGoalService.updateLearningTime(userId, minutesSpent);
        return ResultUtils.success(isCompleted);
    }

    /**
     * 检查用户每日目标是否完成
     * @param userId 用户ID
     * @return 是否完成
     */
    @GetMapping("/daily-goal/is-completed")
    public BaseResponse<Boolean> isDailyGoalCompleted(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        boolean isCompleted = userDailyGoalService.isDailyGoalCompleted(userId);
        return ResultUtils.success(isCompleted);
    }

    /**
     * 获取用户连续完成每日目标的天数
     * @param userId 用户ID
     * @return 连续天数
     */
    @GetMapping("/daily-goal/consecutive-days")
    public BaseResponse<Integer> getConsecutiveCompletedDays(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        int consecutiveDays = userDailyGoalService.getConsecutiveCompletedDays(userId);
        return ResultUtils.success(consecutiveDays);
    }

    // ==================== 学习统计相关接口 ====================

    /**
     * 获取用户学习统计数据
     * @param userId 用户ID
     * @return 学习统计数据
     */
    @GetMapping("/stats")
    public BaseResponse<UserLearningStats> getUserLearningStats(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        UserLearningStats stats = userLearningStatsService.getUserLearningStats(userId);
        return ResultUtils.success(stats);
    }

    /**
     * 更新用户学习统计数据
     * @param userId 用户ID
     * @param minutesSpent 学习时间（分钟）
     * @param wordsLearned 学习单词数
     * @param articlesRead 阅读文章数
     * @return 是否更新成功
     */
    @PostMapping("/stats/update")
    public BaseResponse<Boolean> updateLearningStats(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") Integer minutesSpent,
            @RequestParam(defaultValue = "0") Integer wordsLearned,
            @RequestParam(defaultValue = "0") Integer articlesRead) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        boolean result = userLearningStatsService.updateLearningStats(userId, minutesSpent, wordsLearned, articlesRead);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户连续学习天数
     * @param userId 用户ID
     * @return 连续学习天数
     */
    @GetMapping("/stats/consecutive-days")
    public BaseResponse<Integer> getConsecutiveLearningDays(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        int consecutiveDays = userLearningStatsService.getConsecutiveLearningDays(userId);
        return ResultUtils.success(consecutiveDays);
    }

    /**
     * 增加用户经验值
     * @param userId 用户ID
     * @param experience 经验值
     * @return 是否升级
     */
    @PostMapping("/stats/add-experience")
    public BaseResponse<Boolean> addExperience(@RequestParam Long userId, @RequestParam Integer experience) {
        if (userId == null || experience == null || experience <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        boolean isLevelUp = userLearningStatsService.addExperience(userId, experience);
        return ResultUtils.success(isLevelUp);
    }

    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param points 积分
     * @return 是否成功
     */
    @PostMapping("/stats/add-points")
    public BaseResponse<Boolean> addPoints(@RequestParam Long userId, @RequestParam Integer points) {
        if (userId == null || points == null || points <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        boolean result = userLearningStatsService.addPoints(userId, points);
        return ResultUtils.success(result);
    }

    // ==================== 学习记录相关接口 ====================

    /**
     * 创建课程学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param sectionId 小节ID
     * @param minutesSpent 学习时间（分钟）
     * @return 学习记录
     */
    @PostMapping("/record/course")
    public BaseResponse<UserLearningRecord> createCourseLearningRecord(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam Long sectionId,
            @RequestParam Integer minutesSpent) {
        if (userId == null || courseId == null || sectionId == null || minutesSpent == null || minutesSpent <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UserLearningRecord record = userLearningRecordService.createCourseLearningRecord(userId, courseId, sectionId, minutesSpent);
        return ResultUtils.success(record);
    }

    /**
     * 创建单词学习记录
     * @param userId 用户ID
     * @param wordId 单词ID
     * @param count 学习数量
     * @return 学习记录
     */
    @PostMapping("/record/word")
    public BaseResponse<UserLearningRecord> createWordLearningRecord(
            @RequestParam Long userId,
            @RequestParam Long wordId,
            @RequestParam Integer count) {
        if (userId == null || wordId == null || count == null || count <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UserLearningRecord record = userLearningRecordService.createWordLearningRecord(userId, wordId, count);
        return ResultUtils.success(record);
    }

    /**
     * 创建文章阅读记录
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param duration 阅读时长（秒）
     * @return 学习记录
     */
    @PostMapping("/record/article")
    public BaseResponse<UserLearningRecord> createArticleReadingRecord(
            @RequestParam Long userId,
            @RequestParam Long articleId,
            @RequestParam Integer duration) {
        if (userId == null || articleId == null || duration == null || duration <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UserLearningRecord record = userLearningRecordService.createArticleReadingRecord(userId, articleId, duration);
        return ResultUtils.success(record);
    }

    /**
     * 获取用户学习记录
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 学习记录分页列表
     */
    @GetMapping("/record/page")
    public BaseResponse<IPage<UserLearningRecord>> getUserLearningRecords(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        Page<UserLearningRecord> page = new Page<>(current, size);
        IPage<UserLearningRecord> recordPage = userLearningRecordService.getUserLearningRecords(userId, page);
        return ResultUtils.success(recordPage);
    }

    /**
     * 获取用户指定日期的学习记录
     * @param userId 用户ID
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 学习记录列表
     */
    @GetMapping("/record/date")
    public BaseResponse<List<UserLearningRecord>> getUserLearningRecordsByDate(
            @RequestParam Long userId,
            @RequestParam Date date) {
        if (userId == null || date == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        List<UserLearningRecord> records = userLearningRecordService.getUserLearningRecordsByDate(userId, date);
        return ResultUtils.success(records);
    }

    /**
     * 获取用户指定类型的学习记录
     * @param userId 用户ID
     * @param recordType 记录类型
     * @param current 当前页码
     * @param size 每页大小
     * @return 学习记录分页列表
     */
    @GetMapping("/record/type")
    public BaseResponse<IPage<UserLearningRecord>> getUserLearningRecordsByType(
            @RequestParam Long userId,
            @RequestParam String recordType,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        if (userId == null || recordType == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        Page<UserLearningRecord> page = new Page<>(current, size);
        IPage<UserLearningRecord> recordPage = userLearningRecordService.getUserLearningRecordsByType(userId, recordType, page);
        return ResultUtils.success(recordPage);
    }

    /**
     * 获取用户指定关联ID的学习记录
     * @param userId 用户ID
     * @param relatedId 关联ID
     * @return 学习记录列表
     */
    @GetMapping("/record/related")
    public BaseResponse<List<UserLearningRecord>> getUserLearningRecordsByRelatedId(
            @RequestParam Long userId,
            @RequestParam Long relatedId) {
        if (userId == null || relatedId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        List<UserLearningRecord> records = userLearningRecordService.getUserLearningRecordsByRelatedId(userId, relatedId);
        return ResultUtils.success(records);
    }
} 
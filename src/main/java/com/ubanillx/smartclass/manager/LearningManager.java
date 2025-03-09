package com.ubanillx.smartclass.manager;

import com.ubanillx.smartclass.model.entity.UserDailyGoal;
import com.ubanillx.smartclass.model.entity.UserLearningStats;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 学习管理类
 */
@Component
public class LearningManager {

    /**
     * 检查每日目标是否完成
     * @param dailyGoal 每日目标
     * @return 是否完成
     */
    public boolean isDailyGoalCompleted(UserDailyGoal dailyGoal) {
        if (dailyGoal == null) {
            return false;
        }
        
        Integer targetMinutes = dailyGoal.getTargetMinutes();
        Integer completedMinutes = dailyGoal.getCompletedMinutes();
        
        if (targetMinutes == null || completedMinutes == null) {
            return false;
        }
        
        return completedMinutes >= targetMinutes;
    }

    /**
     * 更新每日学习时间
     * @param dailyGoal 每日目标
     * @param minutesSpent 学习时间（分钟）
     * @return 是否完成目标
     */
    public boolean updateDailyLearningTime(UserDailyGoal dailyGoal, int minutesSpent) {
        if (dailyGoal == null || minutesSpent <= 0) {
            return false;
        }
        
        Integer completedMinutes = dailyGoal.getCompletedMinutes();
        if (completedMinutes == null) {
            completedMinutes = 0;
        }
        
        int newCompletedMinutes = completedMinutes + minutesSpent;
        dailyGoal.setCompletedMinutes(newCompletedMinutes);
        
        // 检查是否完成目标
        Integer targetMinutes = dailyGoal.getTargetMinutes();
        if (targetMinutes != null && newCompletedMinutes >= targetMinutes) {
            dailyGoal.setIsCompleted(1);
            return true;
        }
        
        return false;
    }

    /**
     * 更新学习统计数据
     * @param stats 学习统计
     * @param minutesSpent 学习时间（分钟）
     * @param wordsLearned 学习单词数
     * @param articlesRead 阅读文章数
     */
    public void updateLearningStats(UserLearningStats stats, int minutesSpent, int wordsLearned, int articlesRead) {
        if (stats == null) {
            return;
        }
        
        // 由于实体类中没有这些字段，我们只更新可用的字段
        
        // 更新最后学习时间
        stats.setLastCheckInTime(new Date());
        
        // 更新连续学习天数
        updateConsecutiveDays(stats);
    }

    /**
     * 更新连续学习天数
     * @param stats 学习统计
     */
    private void updateConsecutiveDays(UserLearningStats stats) {
        if (stats == null) {
            return;
        }
        
        Date lastLearningTime = stats.getLastCheckInTime();
        if (lastLearningTime == null) {
            // 首次学习，设置为1天
            stats.setContinuousCheckIn(1);
            return;
        }
        
        // 获取当前日期和最后学习日期
        Calendar currentCal = Calendar.getInstance();
        Calendar lastCal = Calendar.getInstance();
        lastCal.setTime(lastLearningTime);
        
        // 清除时分秒，只比较日期
        clearTimeFields(currentCal);
        clearTimeFields(lastCal);
        
        // 计算日期差
        long diffMillis = currentCal.getTimeInMillis() - lastCal.getTimeInMillis();
        long diffDays = diffMillis / (24 * 60 * 60 * 1000);
        
        Integer consecutiveDays = stats.getContinuousCheckIn();
        if (consecutiveDays == null) {
            consecutiveDays = 0;
        }
        
        if (diffDays == 0) {
            // 同一天，不更新连续天数
            return;
        } else if (diffDays == 1) {
            // 连续一天，增加连续天数
            stats.setContinuousCheckIn(consecutiveDays + 1);
        } else {
            // 中断，重置为1天
            stats.setContinuousCheckIn(1);
        }
    }

    /**
     * 清除时分秒
     * @param calendar 日历对象
     */
    private void clearTimeFields(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 创建学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param sectionId 小节ID
     * @param minutesSpent 学习时间（分钟）
     * @return 学习记录
     */
    public UserLearningRecord createLearningRecord(Long userId, Long courseId, Long sectionId, int minutesSpent) {
        if (userId == null || courseId == null || sectionId == null || minutesSpent <= 0) {
            return null;
        }
        
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRelatedId(courseId); // 使用正确的字段名
        record.setRecordType("course"); // 设置记录类型
        record.setDuration(minutesSpent * 60); // 转换为秒
        record.setCount(1); // 设置数量为1
        record.setRecordDate(new Date());
        
        return record;
    }
} 
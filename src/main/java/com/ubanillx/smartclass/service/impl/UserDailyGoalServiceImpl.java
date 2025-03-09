package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubanillx.smartclass.manager.LearningManager;
import com.ubanillx.smartclass.mapper.UserDailyGoalMapper;
import com.ubanillx.smartclass.model.entity.UserDailyGoal;
import com.ubanillx.smartclass.service.UserDailyGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【user_daily_goal(用户每日目标)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class UserDailyGoalServiceImpl extends BaseRelationServiceImpl<UserDailyGoalMapper, UserDailyGoal>
    implements UserDailyGoalService {
    
    @Autowired
    private LearningManager learningManager;
    
    public UserDailyGoalServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("goalTypeId");
    }
    
    @Override
    public UserDailyGoal getUserDailyGoal(Long userId) {
        if (userId == null) {
            return null;
        }
        
        // 获取当前日期
        Date today = new Date();
        return getUserDailyGoalByDate(userId, today);
    }
    
    @Override
    public UserDailyGoal getUserDailyGoalByDate(Long userId, Date date) {
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
        
        // 查询指定日期的学习目标
        LambdaQueryWrapper<UserDailyGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDailyGoal::getUserId, userId)
                .ge(UserDailyGoal::getGoalDate, startOfDay)
                .lt(UserDailyGoal::getGoalDate, endOfDay);
        
        return getOne(queryWrapper);
    }
    
    @Override
    public UserDailyGoal createUserDailyGoal(Long userId, Integer targetMinutes) {
        if (userId == null || targetMinutes == null || targetMinutes <= 0) {
            return null;
        }
        
        // 检查今天是否已经有目标
        UserDailyGoal existingGoal = getUserDailyGoal(userId);
        if (existingGoal != null) {
            return existingGoal;
        }
        
        // 创建新的每日目标
        UserDailyGoal dailyGoal = new UserDailyGoal();
        dailyGoal.setUserId(userId);
        dailyGoal.setGoalDate(new Date());
        dailyGoal.setTargetMinutes(targetMinutes);
        dailyGoal.setCompletedMinutes(0);
        dailyGoal.setProgressPercent(0);
        dailyGoal.setIsCompleted(0);
        dailyGoal.setCreateTime(new Date());
        dailyGoal.setUpdateTime(new Date());
        
        save(dailyGoal);
        return dailyGoal;
    }
    
    @Override
    public boolean updateLearningTime(Long userId, int minutesSpent) {
        if (userId == null || minutesSpent <= 0) {
            return false;
        }
        
        // 获取今天的学习目标
        UserDailyGoal dailyGoal = getUserDailyGoal(userId);
        if (dailyGoal == null) {
            // 如果没有设置目标，创建一个默认目标（30分钟）
            dailyGoal = createUserDailyGoal(userId, 30);
        }
        
        // 更新学习时间
        boolean isCompleted = learningManager.updateDailyLearningTime(dailyGoal, minutesSpent);
        
        // 计算进度百分比
        Integer targetMinutes = dailyGoal.getTargetMinutes();
        Integer completedMinutes = dailyGoal.getCompletedMinutes();
        if (targetMinutes != null && targetMinutes > 0 && completedMinutes != null) {
            int progressPercent = Math.min((int) (((double) completedMinutes / targetMinutes) * 100), 100);
            dailyGoal.setProgressPercent(progressPercent);
        }
        
        // 如果完成目标，设置完成时间
        if (isCompleted && dailyGoal.getCompletedTime() == null) {
            dailyGoal.setCompletedTime(new Date());
        }
        
        // 更新数据库
        dailyGoal.setUpdateTime(new Date());
        updateById(dailyGoal);
        
        return isCompleted;
    }
    
    @Override
    public boolean isDailyGoalCompleted(Long userId) {
        if (userId == null) {
            return false;
        }
        
        UserDailyGoal dailyGoal = getUserDailyGoal(userId);
        if (dailyGoal == null) {
            return false;
        }
        
        return learningManager.isDailyGoalCompleted(dailyGoal);
    }
    
    @Override
    public int getConsecutiveCompletedDays(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        // 获取用户所有已完成的每日目标，按日期降序排序
        LambdaQueryWrapper<UserDailyGoal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDailyGoal::getUserId, userId)
                .eq(UserDailyGoal::getIsCompleted, 1)
                .orderByDesc(UserDailyGoal::getGoalDate);
        
        List<UserDailyGoal> completedGoals = list(queryWrapper);
        if (completedGoals == null || completedGoals.isEmpty()) {
            return 0;
        }
        
        // 计算连续完成天数
        int consecutiveDays = 1;
        Date lastDate = completedGoals.get(0).getGoalDate();
        
        for (int i = 1; i < completedGoals.size(); i++) {
            Date currentDate = completedGoals.get(i).getGoalDate();
            
            // 计算日期差
            long diffMillis = lastDate.getTime() - currentDate.getTime();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
            
            if (diffDays == 1) {
                // 连续一天
                consecutiveDays++;
                lastDate = currentDate;
            } else if (diffDays > 1) {
                // 中断，结束计算
                break;
            }
        }
        
        return consecutiveDays;
    }
}





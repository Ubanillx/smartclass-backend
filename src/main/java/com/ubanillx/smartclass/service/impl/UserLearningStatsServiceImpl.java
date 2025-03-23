package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.mapper.UserLearningStatsMapper;
import com.ubanillx.smartclass.model.entity.UserLearningStats;
import com.ubanillx.smartclass.model.entity.UserLevel;
import com.ubanillx.smartclass.service.UserLearningStatsService;
import com.ubanillx.smartclass.service.UserLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户学习统计服务实现类
 */
@Service
@Slf4j
public class UserLearningStatsServiceImpl extends ServiceImpl<UserLearningStatsMapper, UserLearningStats> implements UserLearningStatsService {

    @Resource
    private UserLevelService userLevelService;

    @Override
    public UserLearningStats getUserLearningStatsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        QueryWrapper<UserLearningStats> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        UserLearningStats userLearningStats = this.getOne(queryWrapper);
        
        // 如果用户没有学习统计记录，则初始化一条
        if (userLearningStats == null) {
            long statsId = initUserLearningStats(userId);
            return this.getById(statsId);
        }
        
        return userLearningStats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long initUserLearningStats(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 查询是否已存在
        QueryWrapper<UserLearningStats> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        UserLearningStats existStats = this.getOne(queryWrapper);
        if (existStats != null) {
            return existStats.getId();
        }
        
        // 获取初始等级信息
        UserLevel userLevel = userLevelService.getUserLevelByExperience(0);
        ThrowUtils.throwIf(userLevel == null, ErrorCode.SYSTEM_ERROR, "系统未配置等级信息");
        
        // 创建初始学习统计记录
        UserLearningStats userLearningStats = new UserLearningStats();
        userLearningStats.setUserId(userId);
        userLearningStats.setLevel(userLevel.getLevel());
        userLearningStats.setExperience(0);
        userLearningStats.setNextLevelExp(userLevel.getMaxExperience());
        userLearningStats.setLearningDays(0);
        userLearningStats.setContinuousCheckIn(0);
        userLearningStats.setTotalCheckIn(0);
        userLearningStats.setTotalPoints(0);
        userLearningStats.setTotalBadges(0);
        
        boolean result = this.save(userLearningStats);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "初始化学习统计失败");
        
        return userLearningStats.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUserExperience(Long userId, Integer experience) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (experience == null || experience <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "经验值必须为正数");
        }
        
        // 获取用户当前学习统计
        UserLearningStats userLearningStats = getUserLearningStatsByUserId(userId);
        ThrowUtils.throwIf(userLearningStats == null, ErrorCode.NOT_FOUND_ERROR, "用户学习统计不存在");
        
        // 计算新的经验值总量
        int newExperience = userLearningStats.getExperience() + experience;
        
        // 检查是否需要升级
        UserLevel currentLevel = userLevelService.getUserLevelByExperience(userLearningStats.getExperience());
        UserLevel newLevel = userLevelService.getUserLevelByExperience(newExperience);
        
        // 更新用户经验值
        UpdateWrapper<UserLearningStats> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.set("experience", newExperience);
        
        // 如果升级了，更新等级信息
        if (newLevel != null && currentLevel != null && !newLevel.getLevel().equals(currentLevel.getLevel())) {
            updateWrapper.set("level", newLevel.getLevel());
            
            // 获取下一级所需经验值
            UserLevel nextLevel = userLevelService.getNextUserLevel(newLevel.getLevel());
            if (nextLevel != null) {
                updateWrapper.set("nextLevelExp", nextLevel.getMinExperience());
            }
            
            log.info("用户{}升级，从{}级升至{}级", userId, currentLevel.getLevel(), newLevel.getLevel());
        }
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean addUserPoints(Long userId, Integer points) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (points == null || points <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "积分必须为正数");
        }
        
        // 获取用户当前学习统计
        UserLearningStats userLearningStats = getUserLearningStatsByUserId(userId);
        ThrowUtils.throwIf(userLearningStats == null, ErrorCode.NOT_FOUND_ERROR, "用户学习统计不存在");
        
        // 更新用户积分
        UpdateWrapper<UserLearningStats> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("totalPoints = totalPoints + " + points);
        
        return this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> userCheckIn(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 获取用户当前学习统计
        UserLearningStats userLearningStats = getUserLearningStatsByUserId(userId);
        ThrowUtils.throwIf(userLearningStats == null, ErrorCode.NOT_FOUND_ERROR, "用户学习统计不存在");
        
        // 检查是否今天已经打卡
        Date lastCheckInTime = userLearningStats.getLastCheckInTime();
        Date today = new Date();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        if (lastCheckInTime != null) {
            cal1.setTime(lastCheckInTime);
            cal2.setTime(today);
            
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                             cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            
            if (sameDay) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "今天已经打卡");
            }
        }
        
        // 检查是否连续打卡（昨天有打卡记录）
        boolean isContinuous = false;
        if (lastCheckInTime != null) {
            cal1.setTime(lastCheckInTime);
            cal2.setTime(today);
            
            // 将时间调整为 00:00:00，只比较日期
            cal1.set(Calendar.HOUR_OF_DAY, 0);
            cal1.set(Calendar.MINUTE, 0);
            cal1.set(Calendar.SECOND, 0);
            cal1.set(Calendar.MILLISECOND, 0);
            
            cal2.set(Calendar.HOUR_OF_DAY, 0);
            cal2.set(Calendar.MINUTE, 0);
            cal2.set(Calendar.SECOND, 0);
            cal2.set(Calendar.MILLISECOND, 0);
            
            // 检查是否是昨天
            cal1.add(Calendar.DAY_OF_MONTH, 1);
            isContinuous = cal1.getTimeInMillis() == cal2.getTimeInMillis();
        }
        
        // 更新打卡信息
        UpdateWrapper<UserLearningStats> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.set("lastCheckInTime", today);
        updateWrapper.setSql("totalCheckIn = totalCheckIn + 1");
        
        // 计算连续打卡天数
        int continuousCheckIn = userLearningStats.getContinuousCheckIn();
        if (isContinuous) {
            continuousCheckIn += 1;
            updateWrapper.set("continuousCheckIn", continuousCheckIn);
        } else {
            // 中断连续打卡，重置为1
            continuousCheckIn = 1;
            updateWrapper.set("continuousCheckIn", 1);
        }
        
        // 计算打卡奖励积分和经验值
        int basePoints = 5; // 基础积分
        int baseExperience = 10; // 基础经验值
        
        // 连续打卡奖励（每7天增加一个倍数）
        int multiplier = 1 + (continuousCheckIn / 7);
        int bonusPoints = basePoints * multiplier;
        int bonusExperience = baseExperience * multiplier;
        
        // 更新积分和经验值
        updateWrapper.setSql("totalPoints = totalPoints + " + bonusPoints);
        
        boolean result = this.update(updateWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "打卡失败");
        
        // 增加经验值（可能会触发升级）
        addUserExperience(userId, bonusExperience);
        
        // 返回打卡结果
        Map<String, Object> checkInResult = new HashMap<>();
        checkInResult.put("continuousCheckIn", continuousCheckIn);
        checkInResult.put("bonusPoints", bonusPoints);
        checkInResult.put("bonusExperience", bonusExperience);
        
        return checkInResult;
    }

    @Override
    public boolean updateLearningDays(Long userId, Date learningDate) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (learningDate == null) {
            learningDate = new Date(); // 默认为当天
        }
        
        // 获取用户当前学习统计
        UserLearningStats userLearningStats = getUserLearningStatsByUserId(userId);
        ThrowUtils.throwIf(userLearningStats == null, ErrorCode.NOT_FOUND_ERROR, "用户学习统计不存在");
        
        // 检查是否已经记录今天的学习
        Date lastRecordDate = userLearningStats.getUpdateTime();
        if (lastRecordDate != null) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(lastRecordDate);
            cal2.setTime(learningDate);
            
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            
            // 如果是同一天，并且学习天数已经加过，就不再增加
            if (sameDay) {
                return true;
            }
        }
        
        // 更新学习天数
        UpdateWrapper<UserLearningStats> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("learningDays = learningDays + 1");
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean updateUserLevel(Long userId, Integer newLevel, Integer nextLevelExp) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (newLevel == null || newLevel < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级不能为负数");
        }
        
        // 获取用户当前学习统计
        UserLearningStats userLearningStats = getUserLearningStatsByUserId(userId);
        ThrowUtils.throwIf(userLearningStats == null, ErrorCode.NOT_FOUND_ERROR, "用户学习统计不存在");
        
        // 更新用户等级
        UpdateWrapper<UserLearningStats> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.set("level", newLevel);
        
        // 如果提供了下一级所需经验值，也更新
        if (nextLevelExp != null && nextLevelExp > 0) {
            updateWrapper.set("nextLevelExp", nextLevelExp);
        }
        
        return this.update(updateWrapper);
    }

    @Override
    public boolean addUserBadgeCount(Long userId, Integer count) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 默认增加1个徽章
        if (count == null || count <= 0) {
            count = 1;
        }
        
        // 获取用户当前学习统计
        UserLearningStats userLearningStats = getUserLearningStatsByUserId(userId);
        ThrowUtils.throwIf(userLearningStats == null, ErrorCode.NOT_FOUND_ERROR, "用户学习统计不存在");
        
        // 更新徽章数量
        UpdateWrapper<UserLearningStats> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("totalBadges = totalBadges + " + count);
        
        return this.update(updateWrapper);
    }
} 
package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.model.entity.Achievement;
import com.ubanillx.smartclass.service.AchievementService;
import com.ubanillx.smartclass.mapper.AchievementMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【achievement(成就定义)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement>
    implements AchievementService{

    @Override
    public Long createAchievement(Achievement achievement) {
        // 设置创建和更新时间
        Date now = new Date();
        achievement.setCreateTime(now);
        achievement.setUpdateTime(now);
        // 设置未删除状态
        achievement.setIsDelete(0);
        
        // 保存成就并返回成就ID
        save(achievement);
        return achievement.getId();
    }

    @Override
    public boolean updateAchievement(Achievement achievement) {
        // 更新时间
        achievement.setUpdateTime(new Date());
        return updateById(achievement);
    }

    @Override
    public Achievement getAchievementById(Long id) {
        return getById(id);
    }

    @Override
    public IPage<Achievement> pageAchievements(Achievement condition, Page<Achievement> page) {
        LambdaQueryWrapper<Achievement> queryWrapper = new LambdaQueryWrapper<>();
        
        // 设置查询条件
        if (condition != null) {
            // 根据分类查询
            if (StringUtils.hasText(condition.getCategory())) {
                queryWrapper.eq(Achievement::getCategory, condition.getCategory());
            }
            
            // 根据等级查询
            if (condition.getLevel() != null) {
                queryWrapper.eq(Achievement::getLevel, condition.getLevel());
            }
            
            // 根据条件类型查询
            if (StringUtils.hasText(condition.getConditionType())) {
                queryWrapper.eq(Achievement::getConditionType, condition.getConditionType());
            }
            
            // 根据名称模糊查询
            if (StringUtils.hasText(condition.getName())) {
                queryWrapper.like(Achievement::getName, condition.getName());
            }
            
            // 根据是否隐藏查询
            if (condition.getIsHidden() != null) {
                queryWrapper.eq(Achievement::getIsHidden, condition.getIsHidden());
            }
            
            // 根据是否是彩蛋成就查询
            if (condition.getIsSecret() != null) {
                queryWrapper.eq(Achievement::getIsSecret, condition.getIsSecret());
            }
        }
        
        // 只查询未删除的成就
        queryWrapper.eq(Achievement::getIsDelete, 0);
        
        // 按排序字段排序
        queryWrapper.orderByAsc(Achievement::getSort);
        
        return page(page, queryWrapper);
    }

    @Override
    public List<Achievement> getAchievementsByCategory(String category) {
        LambdaQueryWrapper<Achievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Achievement::getCategory, category);
        queryWrapper.eq(Achievement::getIsDelete, 0);
        queryWrapper.orderByAsc(Achievement::getSort);
        return list(queryWrapper);
    }

    @Override
    public List<Achievement> getAchievementsByLevel(Integer level) {
        LambdaQueryWrapper<Achievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Achievement::getLevel, level);
        queryWrapper.eq(Achievement::getIsDelete, 0);
        queryWrapper.orderByAsc(Achievement::getSort);
        return list(queryWrapper);
    }

    @Override
    public boolean deleteAchievement(Long achievementId) {
        LambdaUpdateWrapper<Achievement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Achievement::getId, achievementId);
        updateWrapper.set(Achievement::getIsDelete, 1);
        updateWrapper.set(Achievement::getUpdateTime, new Date());
        return update(updateWrapper);
    }
}





package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.model.entity.AchievementDisplay;
import com.ubanillx.smartclass.service.AchievementDisplayService;
import com.ubanillx.smartclass.mapper.AchievementDisplayMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【achievement_display(成就展示配置)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class AchievementDisplayServiceImpl extends ServiceImpl<AchievementDisplayMapper, AchievementDisplay>
    implements AchievementDisplayService {

    @Override
    public List<AchievementDisplay> getAchievementDisplays(Long achievementId) {
        if (achievementId == null) {
            return null;
        }
        
        LambdaQueryWrapper<AchievementDisplay> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AchievementDisplay::getAchievementId, achievementId)
                .eq(AchievementDisplay::getIsDelete, 0)
                .orderByAsc(AchievementDisplay::getSort);
        
        return list(queryWrapper);
    }
    
    @Override
    public AchievementDisplay getAchievementDisplayByType(Long achievementId, String displayType) {
        if (achievementId == null || displayType == null) {
            return null;
        }
        
        LambdaQueryWrapper<AchievementDisplay> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AchievementDisplay::getAchievementId, achievementId)
                .eq(AchievementDisplay::getDisplayType, displayType)
                .eq(AchievementDisplay::getIsDelete, 0);
        
        return getOne(queryWrapper);
    }
    
    @Override
    public Long createAchievementDisplay(AchievementDisplay display) {
        if (display == null || display.getAchievementId() == null) {
            return null;
        }
        
        // 设置创建和更新时间
        Date now = new Date();
        display.setCreateTime(now);
        display.setUpdateTime(now);
        // 设置未删除状态
        display.setIsDelete(0);
        
        // 保存展示配置并返回ID
        save(display);
        return display.getId();
    }
    
    @Override
    public boolean updateAchievementDisplay(AchievementDisplay display) {
        if (display == null || display.getId() == null) {
            return false;
        }
        
        // 更新时间
        display.setUpdateTime(new Date());
        return updateById(display);
    }
    
    @Override
    public boolean deleteAchievementDisplay(Long displayId) {
        if (displayId == null) {
            return false;
        }
        
        // 逻辑删除
        LambdaUpdateWrapper<AchievementDisplay> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AchievementDisplay::getId, displayId)
                .set(AchievementDisplay::getIsDelete, 1)
                .set(AchievementDisplay::getUpdateTime, new Date());
        
        return update(updateWrapper);
    }
}





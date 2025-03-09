package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.AchievementDisplay;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liulo
* @description 针对表【achievement_display(成就展示配置)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface AchievementDisplayService extends IService<AchievementDisplay> {

    /**
     * 获取成就展示配置列表
     * @param achievementId 成就ID
     * @return 成就展示配置列表
     */
    List<AchievementDisplay> getAchievementDisplays(Long achievementId);
    
    /**
     * 获取指定类型的成就展示配置
     * @param achievementId 成就ID
     * @param displayType 展示类型
     * @return 成就展示配置
     */
    AchievementDisplay getAchievementDisplayByType(Long achievementId, String displayType);
    
    /**
     * 创建成就展示配置
     * @param display 展示配置信息
     * @return 展示配置ID
     */
    Long createAchievementDisplay(AchievementDisplay display);
    
    /**
     * 更新成就展示配置
     * @param display 展示配置信息
     * @return 是否更新成功
     */
    boolean updateAchievementDisplay(AchievementDisplay display);
    
    /**
     * 删除成就展示配置
     * @param displayId 展示配置ID
     * @return 是否删除成功
     */
    boolean deleteAchievementDisplay(Long displayId);
}

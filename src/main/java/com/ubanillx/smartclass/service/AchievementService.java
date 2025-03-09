package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.user.UserQueryRequest;
import com.ubanillx.smartclass.model.entity.Achievement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.LoginUserVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author liulo
* @description 针对表【achievement(成就定义)】的数据库操作Service
* @createDate 2025-02-27 21:52:01
*/
public interface AchievementService extends IService<Achievement> {

    /**
     * 创建成就
     * @param achievement 成就信息
     * @return 成就ID
     */
    Long createAchievement(Achievement achievement);
    
    /**
     * 更新成就
     * @param achievement 成就信息
     * @return 是否更新成功
     */
    boolean updateAchievement(Achievement achievement);
    
    /**
     * 根据ID获取成就
     * @param id 成就ID
     * @return 成就信息
     */
    Achievement getAchievementById(Long id);
    
    /**
     * 分页查询成就
     * @param condition 查询条件（可包含category/level/conditionType等）
     * @param page 分页参数
     * @return 成就分页列表
     */
    IPage<Achievement> pageAchievements(Achievement condition, Page<Achievement> page);
    
    /**
     * 根据分类获取成就列表
     * @param category 成就分类
     * @return 成就列表
     */
    List<Achievement> getAchievementsByCategory(String category);
    
    /**
     * 根据等级获取成就列表
     * @param level 成就等级
     * @return 成就列表
     */
    List<Achievement> getAchievementsByLevel(Integer level);
    
    /**
     * 逻辑删除成就
     * @param achievementId 成就ID
     * @return 是否删除成功
     */
    boolean deleteAchievement(Long achievementId);
}

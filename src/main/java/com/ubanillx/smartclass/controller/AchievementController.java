package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.manager.AchievementManager;
import com.ubanillx.smartclass.model.entity.Achievement;
import com.ubanillx.smartclass.model.entity.AchievementDisplay;
import com.ubanillx.smartclass.model.entity.UserAchievement;
import com.ubanillx.smartclass.service.AchievementDisplayService;
import com.ubanillx.smartclass.service.AchievementService;
import com.ubanillx.smartclass.service.UserAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成就控制器
 */
@RestController
@RequestMapping("/api/achievement")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserAchievementService userAchievementService;

    @Autowired
    private AchievementDisplayService achievementDisplayService;

    @Autowired
    private AchievementManager achievementManager;

    // ==================== 成就定义相关接口 ====================

    /**
     * 创建成就
     * @param achievement 成就信息
     * @return 成就ID
     */
    @PostMapping("/create")
    public BaseResponse<Long> createAchievement(@RequestBody Achievement achievement) {
        if (achievement == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        Long achievementId = achievementService.createAchievement(achievement);
        return ResultUtils.success(achievementId);
    }

    /**
     * 更新成就
     * @param achievement 成就信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateAchievement(@RequestBody Achievement achievement) {
        if (achievement == null || achievement.getId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        boolean result = achievementService.updateAchievement(achievement);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取成就
     * @param id 成就ID
     * @return 成就信息
     */
    @GetMapping("/get")
    public BaseResponse<Achievement> getAchievementById(@RequestParam Long id) {
        if (id == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "ID不能为空");
        }
        Achievement achievement = achievementService.getAchievementById(id);
        return ResultUtils.success(achievement);
    }

    /**
     * 分页查询成就
     * @param current 当前页码
     * @param size 每页大小
     * @param condition 查询条件
     * @return 成就分页结果
     */
    @GetMapping("/page")
    public BaseResponse<IPage<Achievement>> pageAchievements(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            Achievement condition) {
        Page<Achievement> page = new Page<>(current, size);
        IPage<Achievement> achievementPage = achievementService.pageAchievements(condition, page);
        return ResultUtils.success(achievementPage);
    }

    /**
     * 根据分类获取成就列表
     * @param category 成就分类
     * @return 成就列表
     */
    @GetMapping("/category")
    public BaseResponse<List<Achievement>> getAchievementsByCategory(@RequestParam String category) {
        if (category == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "分类不能为空");
        }
        List<Achievement> achievements = achievementService.getAchievementsByCategory(category);
        return ResultUtils.success(achievements);
    }

    /**
     * 根据等级获取成就列表
     * @param level 成就等级
     * @return 成就列表
     */
    @GetMapping("/level")
    public BaseResponse<List<Achievement>> getAchievementsByLevel(@RequestParam Integer level) {
        if (level == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "等级不能为空");
        }
        List<Achievement> achievements = achievementService.getAchievementsByLevel(level);
        return ResultUtils.success(achievements);
    }

    /**
     * 逻辑删除成就
     * @param achievementId 成就ID
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAchievement(@RequestParam Long achievementId) {
        if (achievementId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "ID不能为空");
        }
        boolean result = achievementService.deleteAchievement(achievementId);
        return ResultUtils.success(result);
    }

    // ==================== 用户成就相关接口 ====================

    /**
     * 获取用户成就列表
     * @param userId 用户ID
     * @return 用户成就列表
     */
    @GetMapping("/user/list")
    public BaseResponse<List<UserAchievement>> getUserAchievements(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        List<UserAchievement> userAchievements = userAchievementService.getUserAchievements(userId);
        return ResultUtils.success(userAchievements);
    }

    /**
     * 获取用户已完成的成就列表
     * @param userId 用户ID
     * @return 已完成的成就列表
     */
    @GetMapping("/user/completed")
    public BaseResponse<List<UserAchievement>> getUserCompletedAchievements(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        List<UserAchievement> completedAchievements = userAchievementService.getUserCompletedAchievements(userId);
        return ResultUtils.success(completedAchievements);
    }

    /**
     * 获取用户未完成的成就列表
     * @param userId 用户ID
     * @return 未完成的成就列表
     */
    @GetMapping("/user/uncompleted")
    public BaseResponse<List<UserAchievement>> getUserUncompletedAchievements(@RequestParam Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        List<UserAchievement> uncompletedAchievements = userAchievementService.getUserUncompletedAchievements(userId);
        return ResultUtils.success(uncompletedAchievements);
    }

    /**
     * 创建用户成就记录
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @return 用户成就记录
     */
    @PostMapping("/user/create")
    public BaseResponse<UserAchievement> createUserAchievement(
            @RequestParam Long userId,
            @RequestParam Long achievementId) {
        if (userId == null || achievementId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取成就信息
        Achievement achievement = achievementService.getAchievementById(achievementId);
        if (achievement == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "成就不存在");
        }
        
        // 创建用户成就记录
        UserAchievement userAchievement = achievementManager.createUserAchievement(userId, achievement);
        if (userAchievement == null) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "创建用户成就记录失败");
        }
        
        // 保存用户成就记录
        userAchievementService.save(userAchievement);
        
        return ResultUtils.success(userAchievement);
    }

    /**
     * 更新用户成就进度
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @param progressIncrement 进度增量
     * @return 是否完成成就
     */
    @PostMapping("/user/update-progress")
    public BaseResponse<Boolean> updateUserAchievementProgress(
            @RequestParam Long userId,
            @RequestParam Long achievementId,
            @RequestParam Integer progressIncrement) {
        if (userId == null || achievementId == null || progressIncrement == null || progressIncrement <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取用户成就记录
        UserAchievement userAchievement = userAchievementService.getUserAchievement(userId, achievementId);
        if (userAchievement == null) {
            // 如果不存在，创建一个新的记录
            Achievement achievement = achievementService.getAchievementById(achievementId);
            if (achievement == null) {
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "成就不存在");
            }
            userAchievement = achievementManager.createUserAchievement(userId, achievement);
            userAchievementService.save(userAchievement);
        }
        
        // 获取成就信息
        Achievement achievement = achievementService.getAchievementById(achievementId);
        if (achievement == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "成就不存在");
        }
        
        // 更新进度
        boolean isCompleted = achievementManager.updateAchievementProgress(userAchievement, achievement, progressIncrement);
        
        // 保存更新
        userAchievementService.updateById(userAchievement);
        
        return ResultUtils.success(isCompleted);
    }

    /**
     * 发放成就奖励
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @return 是否发放成功
     */
    @PostMapping("/user/grant-reward")
    public BaseResponse<Boolean> grantAchievementReward(
            @RequestParam Long userId,
            @RequestParam Long achievementId) {
        if (userId == null || achievementId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取用户成就记录
        UserAchievement userAchievement = userAchievementService.getUserAchievement(userId, achievementId);
        if (userAchievement == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "用户成就记录不存在");
        }
        
        // 获取成就信息
        Achievement achievement = achievementService.getAchievementById(achievementId);
        if (achievement == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "成就不存在");
        }
        
        // 发放奖励
        boolean result = achievementManager.grantAchievementReward(userAchievement, achievement);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "发放奖励失败，可能已经发放过或未完成成就");
        }
        
        // 保存更新
        userAchievementService.updateById(userAchievement);
        
        return ResultUtils.success(true);
    }

    // ==================== 成就展示相关接口 ====================

    /**
     * 获取成就展示配置
     * @param achievementId 成就ID
     * @return 成就展示配置列表
     */
    @GetMapping("/display/list")
    public BaseResponse<List<AchievementDisplay>> getAchievementDisplays(@RequestParam Long achievementId) {
        if (achievementId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "成就ID不能为空");
        }
        List<AchievementDisplay> displays = achievementDisplayService.getAchievementDisplays(achievementId);
        return ResultUtils.success(displays);
    }

    /**
     * 获取指定类型的成就展示配置
     * @param achievementId 成就ID
     * @param displayType 展示类型
     * @return 成就展示配置
     */
    @GetMapping("/display/type")
    public BaseResponse<AchievementDisplay> getAchievementDisplayByType(
            @RequestParam Long achievementId,
            @RequestParam String displayType) {
        if (achievementId == null || displayType == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        AchievementDisplay display = achievementDisplayService.getAchievementDisplayByType(achievementId, displayType);
        return ResultUtils.success(display);
    }

    /**
     * 创建成就展示配置
     * @param display 展示配置信息
     * @return 展示配置ID
     */
    @PostMapping("/display/create")
    public BaseResponse<Long> createAchievementDisplay(@RequestBody AchievementDisplay display) {
        if (display == null || display.getAchievementId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        Long displayId = achievementDisplayService.createAchievementDisplay(display);
        return ResultUtils.success(displayId);
    }

    /**
     * 更新成就展示配置
     * @param display 展示配置信息
     * @return 是否更新成功
     */
    @PostMapping("/display/update")
    public BaseResponse<Boolean> updateAchievementDisplay(@RequestBody AchievementDisplay display) {
        if (display == null || display.getId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        boolean result = achievementDisplayService.updateAchievementDisplay(display);
        return ResultUtils.success(result);
    }

    /**
     * 删除成就展示配置
     * @param displayId 展示配置ID
     * @return 是否删除成功
     */
    @PostMapping("/display/delete")
    public BaseResponse<Boolean> deleteAchievementDisplay(@RequestParam Long displayId) {
        if (displayId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "ID不能为空");
        }
        boolean result = achievementDisplayService.deleteAchievementDisplay(displayId);
        return ResultUtils.success(result);
    }
} 
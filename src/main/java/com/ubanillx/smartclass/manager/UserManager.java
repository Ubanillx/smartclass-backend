package com.ubanillx.smartclass.manager;

import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserLevel;
import org.springframework.stereotype.Component;

/**
 * 用户管理类
 */
@Component
public class UserManager {

    /**
     * 检查用户是否有效
     * @param user 用户
     * @return 是否有效
     */
    public boolean isUserValid(User user) {
        if (user == null) {
            return false;
        }
        // 检查是否被删除
        if (user.getIsDelete() != null && user.getIsDelete() == 1) {
            return false;
        }
        // 检查角色是否被禁用
        if ("ban".equals(user.getUserRole())) {
            return false;
        }
        return true;
    }

    /**
     * 检查用户是否为管理员
     * @param user 用户
     * @return 是否为管理员
     */
    public boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        return "admin".equals(user.getUserRole());
    }

    /**
     * 检查用户是否为教师
     * @param user 用户
     * @return 是否为教师
     */
    public boolean isTeacher(User user) {
        if (user == null) {
            return false;
        }
        return "teacher".equals(user.getUserRole());
    }

    /**
     * 检查用户是否为学生
     * @param user 用户
     * @return 是否为学生
     */
    public boolean isStudent(User user) {
        if (user == null) {
            return false;
        }
        return "student".equals(user.getUserRole());
    }

    /**
     * 获取用户等级信息
     * @param user 用户
     * @param userLevel 用户等级
     * @return 等级描述
     */
    public String getLevelDescription(User user, UserLevel userLevel) {
        if (user == null || userLevel == null) {
            return "未知等级";
        }
        
        Integer level = userLevel.getLevel();
        if (level == null) {
            return "未知等级";
        }
        
        switch (level) {
            case 1:
                return "初学者";
            case 2:
                return "进阶学习者";
            case 3:
                return "熟练学习者";
            case 4:
                return "专业学习者";
            case 5:
                return "大师级学习者";
            default:
                return "等级 " + level;
        }
    }

    /**
     * 计算下一级所需经验值
     * @param userLevel 用户等级
     * @return 下一级所需经验值
     */
    public Integer calculateNextLevelExperience(UserLevel userLevel) {
        if (userLevel == null || userLevel.getLevel() == null) {
            return 100; // 默认初始经验值
        }
        
        Integer currentLevel = userLevel.getLevel();
        // 简单的经验值计算公式：100 * 当前等级 * 当前等级
        return 100 * currentLevel * currentLevel;
    }
} 
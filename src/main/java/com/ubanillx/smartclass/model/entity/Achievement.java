package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 成就定义
 * @TableName achievement
 */
@TableName(value ="achievement")
@Data
public class Achievement {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 成就图标URL
     */
    private String iconUrl;

    /**
     * 成就徽章URL
     */
    private String badgeUrl;

    /**
     * 成就横幅URL
     */
    private String bannerUrl;

    /**
     * 成就分类，如：学习、社交、活动等
     */
    private String category;

    /**
     * 成就等级：1-普通，2-稀有，3-史诗，4-传说
     */
    private Integer level;

    /**
     * 成就点数
     */
    private Integer points;

    /**
     * 获取条件描述
     */
    private String achievementCondition;

    /**
     * 条件类型，如：course_complete, login_days, article_read等
     */
    private String conditionType;

    /**
     * 条件值，如完成10门课程，登录30天等
     */
    private Integer conditionValue;

    /**
     * 是否隐藏成就：0-否，1-是，隐藏成就不会提前显示给用户
     */
    private Integer isHidden;

    /**
     * 是否是彩蛋成就：0-否，1-是，彩蛋成就是特殊发现的成就
     */
    private Integer isSecret;

    /**
     * 奖励类型，如：points, badge, coupon等
     */
    private String rewardType;

    /**
     * 奖励值，如积分数量、优惠券ID等
     */
    private String rewardValue;

    /**
     * 排序，数字越小排序越靠前
     */
    private Integer sort;

    /**
     * 创建管理员id
     */
    private Long adminId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Achievement other = (Achievement) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getIconUrl() == null ? other.getIconUrl() == null : this.getIconUrl().equals(other.getIconUrl()))
            && (this.getBadgeUrl() == null ? other.getBadgeUrl() == null : this.getBadgeUrl().equals(other.getBadgeUrl()))
            && (this.getBannerUrl() == null ? other.getBannerUrl() == null : this.getBannerUrl().equals(other.getBannerUrl()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getPoints() == null ? other.getPoints() == null : this.getPoints().equals(other.getPoints()))
            && (this.getAchievementCondition() == null ? other.getAchievementCondition() == null : this.getAchievementCondition().equals(other.getAchievementCondition()))
            && (this.getConditionType() == null ? other.getConditionType() == null : this.getConditionType().equals(other.getConditionType()))
            && (this.getConditionValue() == null ? other.getConditionValue() == null : this.getConditionValue().equals(other.getConditionValue()))
            && (this.getIsHidden() == null ? other.getIsHidden() == null : this.getIsHidden().equals(other.getIsHidden()))
            && (this.getIsSecret() == null ? other.getIsSecret() == null : this.getIsSecret().equals(other.getIsSecret()))
            && (this.getRewardType() == null ? other.getRewardType() == null : this.getRewardType().equals(other.getRewardType()))
            && (this.getRewardValue() == null ? other.getRewardValue() == null : this.getRewardValue().equals(other.getRewardValue()))
            && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
            && (this.getAdminId() == null ? other.getAdminId() == null : this.getAdminId().equals(other.getAdminId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getIconUrl() == null) ? 0 : getIconUrl().hashCode());
        result = prime * result + ((getBadgeUrl() == null) ? 0 : getBadgeUrl().hashCode());
        result = prime * result + ((getBannerUrl() == null) ? 0 : getBannerUrl().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getPoints() == null) ? 0 : getPoints().hashCode());
        result = prime * result + ((getAchievementCondition() == null) ? 0 : getAchievementCondition().hashCode());
        result = prime * result + ((getConditionType() == null) ? 0 : getConditionType().hashCode());
        result = prime * result + ((getConditionValue() == null) ? 0 : getConditionValue().hashCode());
        result = prime * result + ((getIsHidden() == null) ? 0 : getIsHidden().hashCode());
        result = prime * result + ((getIsSecret() == null) ? 0 : getIsSecret().hashCode());
        result = prime * result + ((getRewardType() == null) ? 0 : getRewardType().hashCode());
        result = prime * result + ((getRewardValue() == null) ? 0 : getRewardValue().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", iconUrl=").append(iconUrl);
        sb.append(", badgeUrl=").append(badgeUrl);
        sb.append(", bannerUrl=").append(bannerUrl);
        sb.append(", category=").append(category);
        sb.append(", level=").append(level);
        sb.append(", points=").append(points);
        sb.append(", achievementCondition=").append(achievementCondition);
        sb.append(", conditionType=").append(conditionType);
        sb.append(", conditionValue=").append(conditionValue);
        sb.append(", isHidden=").append(isHidden);
        sb.append(", isSecret=").append(isSecret);
        sb.append(", rewardType=").append(rewardType);
        sb.append(", rewardValue=").append(rewardValue);
        sb.append(", sort=").append(sort);
        sb.append(", adminId=").append(adminId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}
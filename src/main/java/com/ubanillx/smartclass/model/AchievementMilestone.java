package com.ubanillx.smartclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 成就里程碑
 * @TableName achievement_milestone
 */
@TableName(value ="achievement_milestone")
@Data
public class AchievementMilestone {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 里程碑名称
     */
    private String name;

    /**
     * 里程碑描述
     */
    private String description;

    /**
     * 里程碑图标URL
     */
    private String iconUrl;

    /**
     * 里程碑横幅URL
     */
    private String bannerUrl;

    /**
     * 里程碑分类
     */
    private String category;

    /**
     * 所需成就点数
     */
    private Integer requiredPoints;

    /**
     * 奖励类型
     */
    private String rewardType;

    /**
     * 奖励值
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
        AchievementMilestone other = (AchievementMilestone) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getIconUrl() == null ? other.getIconUrl() == null : this.getIconUrl().equals(other.getIconUrl()))
            && (this.getBannerUrl() == null ? other.getBannerUrl() == null : this.getBannerUrl().equals(other.getBannerUrl()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getRequiredPoints() == null ? other.getRequiredPoints() == null : this.getRequiredPoints().equals(other.getRequiredPoints()))
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
        result = prime * result + ((getBannerUrl() == null) ? 0 : getBannerUrl().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getRequiredPoints() == null) ? 0 : getRequiredPoints().hashCode());
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
        sb.append(", bannerUrl=").append(bannerUrl);
        sb.append(", category=").append(category);
        sb.append(", requiredPoints=").append(requiredPoints);
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
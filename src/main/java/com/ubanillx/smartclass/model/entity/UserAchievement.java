package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户成就
 * @TableName user_achievement
 */
@TableName(value ="user_achievement")
@Data
public class UserAchievement implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 成就id
     */
    private Long achievementId;

    /**
     * 当前进度值
     */
    private Integer progress;

    /**
     * 目标进度值
     */
    private Integer progressMax;

    /**
     * 进度百分比
     */
    private Integer progressPercent;

    /**
     * 是否完成：0-否，1-是
     */
    private Integer isCompleted;

    /**
     * 完成时间
     */
    private Date completedTime;

    /**
     * 是否已发放奖励：0-否，1-是
     */
    private Integer isRewarded;

    /**
     * 奖励发放时间
     */
    private Date rewardTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

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
        UserAchievement other = (UserAchievement) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAchievementId() == null ? other.getAchievementId() == null : this.getAchievementId().equals(other.getAchievementId()))
            && (this.getProgress() == null ? other.getProgress() == null : this.getProgress().equals(other.getProgress()))
            && (this.getProgressMax() == null ? other.getProgressMax() == null : this.getProgressMax().equals(other.getProgressMax()))
            && (this.getProgressPercent() == null ? other.getProgressPercent() == null : this.getProgressPercent().equals(other.getProgressPercent()))
            && (this.getIsCompleted() == null ? other.getIsCompleted() == null : this.getIsCompleted().equals(other.getIsCompleted()))
            && (this.getCompletedTime() == null ? other.getCompletedTime() == null : this.getCompletedTime().equals(other.getCompletedTime()))
            && (this.getIsRewarded() == null ? other.getIsRewarded() == null : this.getIsRewarded().equals(other.getIsRewarded()))
            && (this.getRewardTime() == null ? other.getRewardTime() == null : this.getRewardTime().equals(other.getRewardTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAchievementId() == null) ? 0 : getAchievementId().hashCode());
        result = prime * result + ((getProgress() == null) ? 0 : getProgress().hashCode());
        result = prime * result + ((getProgressMax() == null) ? 0 : getProgressMax().hashCode());
        result = prime * result + ((getProgressPercent() == null) ? 0 : getProgressPercent().hashCode());
        result = prime * result + ((getIsCompleted() == null) ? 0 : getIsCompleted().hashCode());
        result = prime * result + ((getCompletedTime() == null) ? 0 : getCompletedTime().hashCode());
        result = prime * result + ((getIsRewarded() == null) ? 0 : getIsRewarded().hashCode());
        result = prime * result + ((getRewardTime() == null) ? 0 : getRewardTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", achievementId=").append(achievementId);
        sb.append(", progress=").append(progress);
        sb.append(", progressMax=").append(progressMax);
        sb.append(", progressPercent=").append(progressPercent);
        sb.append(", isCompleted=").append(isCompleted);
        sb.append(", completedTime=").append(completedTime);
        sb.append(", isRewarded=").append(isRewarded);
        sb.append(", rewardTime=").append(rewardTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
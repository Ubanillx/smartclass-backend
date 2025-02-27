package com.ubanillx.smartclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户里程碑
 * @TableName user_milestone
 */
@TableName(value ="user_milestone")
@Data
public class UserMilestone {
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
     * 里程碑id
     */
    private Long milestoneId;

    /**
     * 当前成就点数
     */
    private Integer currentPoints;

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
        UserMilestone other = (UserMilestone) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getMilestoneId() == null ? other.getMilestoneId() == null : this.getMilestoneId().equals(other.getMilestoneId()))
            && (this.getCurrentPoints() == null ? other.getCurrentPoints() == null : this.getCurrentPoints().equals(other.getCurrentPoints()))
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
        result = prime * result + ((getMilestoneId() == null) ? 0 : getMilestoneId().hashCode());
        result = prime * result + ((getCurrentPoints() == null) ? 0 : getCurrentPoints().hashCode());
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
        sb.append(", milestoneId=").append(milestoneId);
        sb.append(", currentPoints=").append(currentPoints);
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
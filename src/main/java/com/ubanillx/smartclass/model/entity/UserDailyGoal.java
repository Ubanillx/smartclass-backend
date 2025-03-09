package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户每日学习目标
 * @TableName user_daily_goal
 */
@TableName(value ="user_daily_goal")
@Data
public class UserDailyGoal {
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
     * 目标日期
     */
    private Date goalDate;

    /**
     * 目标学习时间（分钟）
     */
    private Integer targetMinutes;

    /**
     * 已完成学习时间（分钟）
     */
    private Integer completedMinutes;

    /**
     * 总目标数
     */
    private Integer totalGoals;

    /**
     * 已完成目标数
     */
    private Integer completedGoals;

    /**
     * 完成百分比
     */
    private Integer progressPercent;

    /**
     * 是否全部完成：0-否，1-是
     */
    private Integer isCompleted;

    /**
     * 全部完成时间
     */
    private Date completedTime;

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
        UserDailyGoal other = (UserDailyGoal) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getGoalDate() == null ? other.getGoalDate() == null : this.getGoalDate().equals(other.getGoalDate()))
            && (this.getTargetMinutes() == null ? other.getTargetMinutes() == null : this.getTargetMinutes().equals(other.getTargetMinutes()))
            && (this.getCompletedMinutes() == null ? other.getCompletedMinutes() == null : this.getCompletedMinutes().equals(other.getCompletedMinutes()))
            && (this.getTotalGoals() == null ? other.getTotalGoals() == null : this.getTotalGoals().equals(other.getTotalGoals()))
            && (this.getCompletedGoals() == null ? other.getCompletedGoals() == null : this.getCompletedGoals().equals(other.getCompletedGoals()))
            && (this.getProgressPercent() == null ? other.getProgressPercent() == null : this.getProgressPercent().equals(other.getProgressPercent()))
            && (this.getIsCompleted() == null ? other.getIsCompleted() == null : this.getIsCompleted().equals(other.getIsCompleted()))
            && (this.getCompletedTime() == null ? other.getCompletedTime() == null : this.getCompletedTime().equals(other.getCompletedTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getGoalDate() == null) ? 0 : getGoalDate().hashCode());
        result = prime * result + ((getTargetMinutes() == null) ? 0 : getTargetMinutes().hashCode());
        result = prime * result + ((getCompletedMinutes() == null) ? 0 : getCompletedMinutes().hashCode());
        result = prime * result + ((getTotalGoals() == null) ? 0 : getTotalGoals().hashCode());
        result = prime * result + ((getCompletedGoals() == null) ? 0 : getCompletedGoals().hashCode());
        result = prime * result + ((getProgressPercent() == null) ? 0 : getProgressPercent().hashCode());
        result = prime * result + ((getIsCompleted() == null) ? 0 : getIsCompleted().hashCode());
        result = prime * result + ((getCompletedTime() == null) ? 0 : getCompletedTime().hashCode());
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
        sb.append(", goalDate=").append(goalDate);
        sb.append(", targetMinutes=").append(targetMinutes);
        sb.append(", completedMinutes=").append(completedMinutes);
        sb.append(", totalGoals=").append(totalGoals);
        sb.append(", completedGoals=").append(completedGoals);
        sb.append(", progressPercent=").append(progressPercent);
        sb.append(", isCompleted=").append(isCompleted);
        sb.append(", completedTime=").append(completedTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
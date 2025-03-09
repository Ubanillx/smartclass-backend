package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户学习统计
 * @TableName user_learning_stats
 */
@TableName(value ="user_learning_stats")
@Data
public class UserLearningStats {
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
     * 当前等级
     */
    private Integer level;

    /**
     * 当前经验值
     */
    private Integer experience;

    /**
     * 下一级所需经验值
     */
    private Integer nextLevelExp;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 学习天数
     */
    private Integer learningDays;

    /**
     * 连续打卡天数
     */
    private Integer continuousCheckIn;

    /**
     * 总打卡天数
     */
    private Integer totalCheckIn;

    /**
     * 总积分
     */
    private Integer totalPoints;

    /**
     * 获得徽章数
     */
    private Integer totalBadges;

    /**
     * 最后打卡时间
     */
    private Date lastCheckInTime;

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
        UserLearningStats other = (UserLearningStats) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getExperience() == null ? other.getExperience() == null : this.getExperience().equals(other.getExperience()))
            && (this.getNextLevelExp() == null ? other.getNextLevelExp() == null : this.getNextLevelExp().equals(other.getNextLevelExp()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getLearningDays() == null ? other.getLearningDays() == null : this.getLearningDays().equals(other.getLearningDays()))
            && (this.getContinuousCheckIn() == null ? other.getContinuousCheckIn() == null : this.getContinuousCheckIn().equals(other.getContinuousCheckIn()))
            && (this.getTotalCheckIn() == null ? other.getTotalCheckIn() == null : this.getTotalCheckIn().equals(other.getTotalCheckIn()))
            && (this.getTotalPoints() == null ? other.getTotalPoints() == null : this.getTotalPoints().equals(other.getTotalPoints()))
            && (this.getTotalBadges() == null ? other.getTotalBadges() == null : this.getTotalBadges().equals(other.getTotalBadges()))
            && (this.getLastCheckInTime() == null ? other.getLastCheckInTime() == null : this.getLastCheckInTime().equals(other.getLastCheckInTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getExperience() == null) ? 0 : getExperience().hashCode());
        result = prime * result + ((getNextLevelExp() == null) ? 0 : getNextLevelExp().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getLearningDays() == null) ? 0 : getLearningDays().hashCode());
        result = prime * result + ((getContinuousCheckIn() == null) ? 0 : getContinuousCheckIn().hashCode());
        result = prime * result + ((getTotalCheckIn() == null) ? 0 : getTotalCheckIn().hashCode());
        result = prime * result + ((getTotalPoints() == null) ? 0 : getTotalPoints().hashCode());
        result = prime * result + ((getTotalBadges() == null) ? 0 : getTotalBadges().hashCode());
        result = prime * result + ((getLastCheckInTime() == null) ? 0 : getLastCheckInTime().hashCode());
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
        sb.append(", level=").append(level);
        sb.append(", experience=").append(experience);
        sb.append(", nextLevelExp=").append(nextLevelExp);
        sb.append(", nickname=").append(nickname);
        sb.append(", learningDays=").append(learningDays);
        sb.append(", continuousCheckIn=").append(continuousCheckIn);
        sb.append(", totalCheckIn=").append(totalCheckIn);
        sb.append(", totalPoints=").append(totalPoints);
        sb.append(", totalBadges=").append(totalBadges);
        sb.append(", lastCheckInTime=").append(lastCheckInTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
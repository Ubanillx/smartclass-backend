package com.ubanillx.smartclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户学习进度
 * @TableName user_course_progress
 */
@TableName(value ="user_course_progress")
@Data
public class UserCourseProgress {
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
     * 课程id
     */
    private Long courseId;

    /**
     * 小节id
     */
    private Long sectionId;

    /**
     * 学习进度(百分比)
     */
    private Integer progress;

    /**
     * 观看时长(秒)
     */
    private Integer watchDuration;

    /**
     * 上次观看位置(秒)
     */
    private Integer lastPosition;

    /**
     * 是否完成：0-否，1-是
     */
    private Integer isCompleted;

    /**
     * 完成时间
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
        UserCourseProgress other = (UserCourseProgress) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCourseId() == null ? other.getCourseId() == null : this.getCourseId().equals(other.getCourseId()))
            && (this.getSectionId() == null ? other.getSectionId() == null : this.getSectionId().equals(other.getSectionId()))
            && (this.getProgress() == null ? other.getProgress() == null : this.getProgress().equals(other.getProgress()))
            && (this.getWatchDuration() == null ? other.getWatchDuration() == null : this.getWatchDuration().equals(other.getWatchDuration()))
            && (this.getLastPosition() == null ? other.getLastPosition() == null : this.getLastPosition().equals(other.getLastPosition()))
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
        result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
        result = prime * result + ((getSectionId() == null) ? 0 : getSectionId().hashCode());
        result = prime * result + ((getProgress() == null) ? 0 : getProgress().hashCode());
        result = prime * result + ((getWatchDuration() == null) ? 0 : getWatchDuration().hashCode());
        result = prime * result + ((getLastPosition() == null) ? 0 : getLastPosition().hashCode());
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
        sb.append(", courseId=").append(courseId);
        sb.append(", sectionId=").append(sectionId);
        sb.append(", progress=").append(progress);
        sb.append(", watchDuration=").append(watchDuration);
        sb.append(", lastPosition=").append(lastPosition);
        sb.append(", isCompleted=").append(isCompleted);
        sb.append(", completedTime=").append(completedTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
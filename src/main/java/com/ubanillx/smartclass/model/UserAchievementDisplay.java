package com.ubanillx.smartclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户成就展示记录
 * @TableName user_achievement_display
 */
@TableName(value ="user_achievement_display")
@Data
public class UserAchievementDisplay {
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
     * 成就展示配置id
     */
    private Long achievementDisplayId;

    /**
     * 是否启用展示：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isPinned;

    /**
     * 自定义标题，为空则使用默认标题
     */
    private String customTitle;

    /**
     * 自定义图片URL，为空则使用默认图片
     */
    private String customImageUrl;

    /**
     * 展示次数
     */
    private Integer displayCount;

    /**
     * 最后展示时间
     */
    private Date lastDisplayTime;

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
        UserAchievementDisplay other = (UserAchievementDisplay) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAchievementId() == null ? other.getAchievementId() == null : this.getAchievementId().equals(other.getAchievementId()))
            && (this.getAchievementDisplayId() == null ? other.getAchievementDisplayId() == null : this.getAchievementDisplayId().equals(other.getAchievementDisplayId()))
            && (this.getIsEnabled() == null ? other.getIsEnabled() == null : this.getIsEnabled().equals(other.getIsEnabled()))
            && (this.getIsPinned() == null ? other.getIsPinned() == null : this.getIsPinned().equals(other.getIsPinned()))
            && (this.getCustomTitle() == null ? other.getCustomTitle() == null : this.getCustomTitle().equals(other.getCustomTitle()))
            && (this.getCustomImageUrl() == null ? other.getCustomImageUrl() == null : this.getCustomImageUrl().equals(other.getCustomImageUrl()))
            && (this.getDisplayCount() == null ? other.getDisplayCount() == null : this.getDisplayCount().equals(other.getDisplayCount()))
            && (this.getLastDisplayTime() == null ? other.getLastDisplayTime() == null : this.getLastDisplayTime().equals(other.getLastDisplayTime()))
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
        result = prime * result + ((getAchievementDisplayId() == null) ? 0 : getAchievementDisplayId().hashCode());
        result = prime * result + ((getIsEnabled() == null) ? 0 : getIsEnabled().hashCode());
        result = prime * result + ((getIsPinned() == null) ? 0 : getIsPinned().hashCode());
        result = prime * result + ((getCustomTitle() == null) ? 0 : getCustomTitle().hashCode());
        result = prime * result + ((getCustomImageUrl() == null) ? 0 : getCustomImageUrl().hashCode());
        result = prime * result + ((getDisplayCount() == null) ? 0 : getDisplayCount().hashCode());
        result = prime * result + ((getLastDisplayTime() == null) ? 0 : getLastDisplayTime().hashCode());
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
        sb.append(", achievementDisplayId=").append(achievementDisplayId);
        sb.append(", isEnabled=").append(isEnabled);
        sb.append(", isPinned=").append(isPinned);
        sb.append(", customTitle=").append(customTitle);
        sb.append(", customImageUrl=").append(customImageUrl);
        sb.append(", displayCount=").append(displayCount);
        sb.append(", lastDisplayTime=").append(lastDisplayTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
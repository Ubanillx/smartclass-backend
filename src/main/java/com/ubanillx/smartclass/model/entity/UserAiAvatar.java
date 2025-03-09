package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 用户AI分身关联
 * @TableName user_ai_avatar
 */
@TableName(value ="user_ai_avatar")
@Data
public class UserAiAvatar implements Serializable {
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
     * AI分身id
     */
    private Long aiAvatarId;

    /**
     * 是否收藏：0-否，1-是
     */
    private Integer isFavorite;

    /**
     * 最后使用时间
     */
    private Date lastUseTime;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 用户评分，1-5分
     */
    private BigDecimal userRating;

    /**
     * 用户反馈
     */
    private String userFeedback;

    /**
     * 用户自定义设置，JSON格式
     */
    private String customSettings;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

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
        UserAiAvatar other = (UserAiAvatar) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAiAvatarId() == null ? other.getAiAvatarId() == null : this.getAiAvatarId().equals(other.getAiAvatarId()))
            && (this.getIsFavorite() == null ? other.getIsFavorite() == null : this.getIsFavorite().equals(other.getIsFavorite()))
            && (this.getLastUseTime() == null ? other.getLastUseTime() == null : this.getLastUseTime().equals(other.getLastUseTime()))
            && (this.getUseCount() == null ? other.getUseCount() == null : this.getUseCount().equals(other.getUseCount()))
            && (this.getUserRating() == null ? other.getUserRating() == null : this.getUserRating().equals(other.getUserRating()))
            && (this.getUserFeedback() == null ? other.getUserFeedback() == null : this.getUserFeedback().equals(other.getUserFeedback()))
            && (this.getCustomSettings() == null ? other.getCustomSettings() == null : this.getCustomSettings().equals(other.getCustomSettings()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAiAvatarId() == null) ? 0 : getAiAvatarId().hashCode());
        result = prime * result + ((getIsFavorite() == null) ? 0 : getIsFavorite().hashCode());
        result = prime * result + ((getLastUseTime() == null) ? 0 : getLastUseTime().hashCode());
        result = prime * result + ((getUseCount() == null) ? 0 : getUseCount().hashCode());
        result = prime * result + ((getUserRating() == null) ? 0 : getUserRating().hashCode());
        result = prime * result + ((getUserFeedback() == null) ? 0 : getUserFeedback().hashCode());
        result = prime * result + ((getCustomSettings() == null) ? 0 : getCustomSettings().hashCode());
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
        sb.append(", aiAvatarId=").append(aiAvatarId);
        sb.append(", isFavorite=").append(isFavorite);
        sb.append(", lastUseTime=").append(lastUseTime);
        sb.append(", useCount=").append(useCount);
        sb.append(", userRating=").append(userRating);
        sb.append(", userFeedback=").append(userFeedback);
        sb.append(", customSettings=").append(customSettings);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
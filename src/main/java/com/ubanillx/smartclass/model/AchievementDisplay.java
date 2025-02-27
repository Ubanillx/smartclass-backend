package com.ubanillx.smartclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 成就展示配置
 * @TableName achievement_display
 */
@TableName(value ="achievement_display")
@Data
public class AchievementDisplay {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成就id
     */
    private Long achievementId;

    /**
     * 展示类型：profile(个人资料页), card(成就卡片), banner(成就横幅), popup(弹窗通知)
     */
    private String displayType;

    /**
     * 展示标题，为空则使用成就名称
     */
    private String title;

    /**
     * 展示副标题
     */
    private String subtitle;

    /**
     * 展示图片URL，为空则使用成就图标
     */
    private String imageUrl;

    /**
     * 背景颜色，十六进制颜色代码
     */
    private String backgroundColor;

    /**
     * 文字颜色，十六进制颜色代码
     */
    private String textColor;

    /**
     * 动画类型
     */
    private String animationType;

    /**
     * 展示时长(秒)，0表示永久展示
     */
    private Integer displayDuration;

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
        AchievementDisplay other = (AchievementDisplay) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAchievementId() == null ? other.getAchievementId() == null : this.getAchievementId().equals(other.getAchievementId()))
            && (this.getDisplayType() == null ? other.getDisplayType() == null : this.getDisplayType().equals(other.getDisplayType()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getSubtitle() == null ? other.getSubtitle() == null : this.getSubtitle().equals(other.getSubtitle()))
            && (this.getImageUrl() == null ? other.getImageUrl() == null : this.getImageUrl().equals(other.getImageUrl()))
            && (this.getBackgroundColor() == null ? other.getBackgroundColor() == null : this.getBackgroundColor().equals(other.getBackgroundColor()))
            && (this.getTextColor() == null ? other.getTextColor() == null : this.getTextColor().equals(other.getTextColor()))
            && (this.getAnimationType() == null ? other.getAnimationType() == null : this.getAnimationType().equals(other.getAnimationType()))
            && (this.getDisplayDuration() == null ? other.getDisplayDuration() == null : this.getDisplayDuration().equals(other.getDisplayDuration()))
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
        result = prime * result + ((getAchievementId() == null) ? 0 : getAchievementId().hashCode());
        result = prime * result + ((getDisplayType() == null) ? 0 : getDisplayType().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSubtitle() == null) ? 0 : getSubtitle().hashCode());
        result = prime * result + ((getImageUrl() == null) ? 0 : getImageUrl().hashCode());
        result = prime * result + ((getBackgroundColor() == null) ? 0 : getBackgroundColor().hashCode());
        result = prime * result + ((getTextColor() == null) ? 0 : getTextColor().hashCode());
        result = prime * result + ((getAnimationType() == null) ? 0 : getAnimationType().hashCode());
        result = prime * result + ((getDisplayDuration() == null) ? 0 : getDisplayDuration().hashCode());
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
        sb.append(", achievementId=").append(achievementId);
        sb.append(", displayType=").append(displayType);
        sb.append(", title=").append(title);
        sb.append(", subtitle=").append(subtitle);
        sb.append(", imageUrl=").append(imageUrl);
        sb.append(", backgroundColor=").append(backgroundColor);
        sb.append(", textColor=").append(textColor);
        sb.append(", animationType=").append(animationType);
        sb.append(", displayDuration=").append(displayDuration);
        sb.append(", sort=").append(sort);
        sb.append(", adminId=").append(adminId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}
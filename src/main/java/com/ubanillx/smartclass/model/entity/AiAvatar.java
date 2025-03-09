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
 * AI分身
 * @TableName ai_avatar
 */
@TableName(value ="ai_avatar")
@Data
public class AiAvatar implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * AI分身名称
     */
    private String name;

    /**
     * AI分身描述
     */
    private String description;

    /**
     * AI分身头像URL
     */
    private String avatarUrl;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 分类，如：学习助手、语言教练、职业顾问等
     */
    private String category;

    /**
     * 性格特点描述
     */
    private String personality;

    /**
     * 能力描述
     */
    private String abilities;

    /**
     * 提示词模板
     */
    private String promptTemplate;

    /**
     * 模型类型，如：GPT-4、Claude等
     */
    private String modelType;

    /**
     * 模型配置，JSON格式
     */
    private String modelConfig;

    /**
     * 是否公开：0-否，1-是
     */
    private Integer isPublic;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 评分，1-5分
     */
    private BigDecimal rating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 创建者id，可以是管理员或用户
     */
    private Long creatorId;

    /**
     * 创建者类型：admin/user
     */
    private String creatorType;

    /**
     * 排序，数字越小排序越靠前
     */
    private Integer sort;

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
        AiAvatar other = (AiAvatar) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getAvatarUrl() == null ? other.getAvatarUrl() == null : this.getAvatarUrl().equals(other.getAvatarUrl()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getPersonality() == null ? other.getPersonality() == null : this.getPersonality().equals(other.getPersonality()))
            && (this.getAbilities() == null ? other.getAbilities() == null : this.getAbilities().equals(other.getAbilities()))
            && (this.getPromptTemplate() == null ? other.getPromptTemplate() == null : this.getPromptTemplate().equals(other.getPromptTemplate()))
            && (this.getModelType() == null ? other.getModelType() == null : this.getModelType().equals(other.getModelType()))
            && (this.getModelConfig() == null ? other.getModelConfig() == null : this.getModelConfig().equals(other.getModelConfig()))
            && (this.getIsPublic() == null ? other.getIsPublic() == null : this.getIsPublic().equals(other.getIsPublic()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getUsageCount() == null ? other.getUsageCount() == null : this.getUsageCount().equals(other.getUsageCount()))
            && (this.getRating() == null ? other.getRating() == null : this.getRating().equals(other.getRating()))
            && (this.getRatingCount() == null ? other.getRatingCount() == null : this.getRatingCount().equals(other.getRatingCount()))
            && (this.getCreatorId() == null ? other.getCreatorId() == null : this.getCreatorId().equals(other.getCreatorId()))
            && (this.getCreatorType() == null ? other.getCreatorType() == null : this.getCreatorType().equals(other.getCreatorType()))
            && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
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
        result = prime * result + ((getAvatarUrl() == null) ? 0 : getAvatarUrl().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getPersonality() == null) ? 0 : getPersonality().hashCode());
        result = prime * result + ((getAbilities() == null) ? 0 : getAbilities().hashCode());
        result = prime * result + ((getPromptTemplate() == null) ? 0 : getPromptTemplate().hashCode());
        result = prime * result + ((getModelType() == null) ? 0 : getModelType().hashCode());
        result = prime * result + ((getModelConfig() == null) ? 0 : getModelConfig().hashCode());
        result = prime * result + ((getIsPublic() == null) ? 0 : getIsPublic().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getUsageCount() == null) ? 0 : getUsageCount().hashCode());
        result = prime * result + ((getRating() == null) ? 0 : getRating().hashCode());
        result = prime * result + ((getRatingCount() == null) ? 0 : getRatingCount().hashCode());
        result = prime * result + ((getCreatorId() == null) ? 0 : getCreatorId().hashCode());
        result = prime * result + ((getCreatorType() == null) ? 0 : getCreatorType().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
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
        sb.append(", avatarUrl=").append(avatarUrl);
        sb.append(", tags=").append(tags);
        sb.append(", category=").append(category);
        sb.append(", personality=").append(personality);
        sb.append(", abilities=").append(abilities);
        sb.append(", promptTemplate=").append(promptTemplate);
        sb.append(", modelType=").append(modelType);
        sb.append(", modelConfig=").append(modelConfig);
        sb.append(", isPublic=").append(isPublic);
        sb.append(", status=").append(status);
        sb.append(", usageCount=").append(usageCount);
        sb.append(", rating=").append(rating);
        sb.append(", ratingCount=").append(ratingCount);
        sb.append(", creatorId=").append(creatorId);
        sb.append(", creatorType=").append(creatorType);
        sb.append(", sort=").append(sort);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
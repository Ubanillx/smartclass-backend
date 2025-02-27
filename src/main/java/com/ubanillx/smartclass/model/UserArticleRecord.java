package com.ubanillx.smartclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户文章阅读记录
 * @TableName user_article_record
 */
@TableName(value ="user_article_record")
@Data
public class UserArticleRecord {
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
     * 文章id
     */
    private Long articleId;

    /**
     * 阅读状态：0-未读，1-阅读中，2-已读完
     */
    private Integer readStatus;

    /**
     * 阅读进度(百分比)
     */
    private Integer readProgress;

    /**
     * 是否点赞
     */
    private Integer isLiked;

    /**
     * 用户笔记
     */
    private String userNotes;

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
        UserArticleRecord other = (UserArticleRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getArticleId() == null ? other.getArticleId() == null : this.getArticleId().equals(other.getArticleId()))
            && (this.getReadStatus() == null ? other.getReadStatus() == null : this.getReadStatus().equals(other.getReadStatus()))
            && (this.getReadProgress() == null ? other.getReadProgress() == null : this.getReadProgress().equals(other.getReadProgress()))
            && (this.getIsLiked() == null ? other.getIsLiked() == null : this.getIsLiked().equals(other.getIsLiked()))
            && (this.getUserNotes() == null ? other.getUserNotes() == null : this.getUserNotes().equals(other.getUserNotes()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getArticleId() == null) ? 0 : getArticleId().hashCode());
        result = prime * result + ((getReadStatus() == null) ? 0 : getReadStatus().hashCode());
        result = prime * result + ((getReadProgress() == null) ? 0 : getReadProgress().hashCode());
        result = prime * result + ((getIsLiked() == null) ? 0 : getIsLiked().hashCode());
        result = prime * result + ((getUserNotes() == null) ? 0 : getUserNotes().hashCode());
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
        sb.append(", articleId=").append(articleId);
        sb.append(", readStatus=").append(readStatus);
        sb.append(", readProgress=").append(readProgress);
        sb.append(", isLiked=").append(isLiked);
        sb.append(", userNotes=").append(userNotes);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
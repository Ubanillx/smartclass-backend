package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户单词学习记录
 * @TableName user_word_record
 */
@TableName(value ="user_word_record")
@Data
public class UserWordRecord {
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
     * 单词id
     */
    private Long wordId;

    /**
     * 学习状态：0-未学习，1-已学习，2-已掌握
     */
    private Integer learningStatus;

    /**
     * 学习进度（0-100）
     */
    private Integer learningProgress;

    /**
     * 复习次数
     */
    private Integer reviewCount;

    /**
     * 最后一次复习时间
     */
    private Date lastReviewTime;

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
        UserWordRecord other = (UserWordRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getWordId() == null ? other.getWordId() == null : this.getWordId().equals(other.getWordId()))
            && (this.getLearningStatus() == null ? other.getLearningStatus() == null : this.getLearningStatus().equals(other.getLearningStatus()))
            && (this.getLearningProgress() == null ? other.getLearningProgress() == null : this.getLearningProgress().equals(other.getLearningProgress()))
            && (this.getReviewCount() == null ? other.getReviewCount() == null : this.getReviewCount().equals(other.getReviewCount()))
            && (this.getLastReviewTime() == null ? other.getLastReviewTime() == null : this.getLastReviewTime().equals(other.getLastReviewTime()))
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
        result = prime * result + ((getWordId() == null) ? 0 : getWordId().hashCode());
        result = prime * result + ((getLearningStatus() == null) ? 0 : getLearningStatus().hashCode());
        result = prime * result + ((getLearningProgress() == null) ? 0 : getLearningProgress().hashCode());
        result = prime * result + ((getReviewCount() == null) ? 0 : getReviewCount().hashCode());
        result = prime * result + ((getLastReviewTime() == null) ? 0 : getLastReviewTime().hashCode());
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
        sb.append(", wordId=").append(wordId);
        sb.append(", learningStatus=").append(learningStatus);
        sb.append(", learningProgress=").append(learningProgress);
        sb.append(", reviewCount=").append(reviewCount);
        sb.append(", lastReviewTime=").append(lastReviewTime);
        sb.append(", userNotes=").append(userNotes);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
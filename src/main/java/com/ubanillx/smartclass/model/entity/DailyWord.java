package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 每日单词
 * @TableName daily_word
 */
@TableName(value ="daily_word")
@Data
public class DailyWord {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 单词
     */
    private String word;

    /**
     * 音标
     */
    private String pronunciation;

    /**
     * 发音音频URL
     */
    private String audioUrl;

    /**
     * 翻译
     */
    private String translation;

    /**
     * 例句
     */
    private String example;

    /**
     * 例句翻译
     */
    private String exampleTranslation;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 单词分类
     */
    private String category;

    /**
     * 单词笔记或补充说明
     */
    private String notes;

    /**
     * 发布日期
     */
    private Date publishDate;

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
        DailyWord other = (DailyWord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getWord() == null ? other.getWord() == null : this.getWord().equals(other.getWord()))
            && (this.getPronunciation() == null ? other.getPronunciation() == null : this.getPronunciation().equals(other.getPronunciation()))
            && (this.getAudioUrl() == null ? other.getAudioUrl() == null : this.getAudioUrl().equals(other.getAudioUrl()))
            && (this.getTranslation() == null ? other.getTranslation() == null : this.getTranslation().equals(other.getTranslation()))
            && (this.getExample() == null ? other.getExample() == null : this.getExample().equals(other.getExample()))
            && (this.getExampleTranslation() == null ? other.getExampleTranslation() == null : this.getExampleTranslation().equals(other.getExampleTranslation()))
            && (this.getDifficulty() == null ? other.getDifficulty() == null : this.getDifficulty().equals(other.getDifficulty()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getNotes() == null ? other.getNotes() == null : this.getNotes().equals(other.getNotes()))
            && (this.getPublishDate() == null ? other.getPublishDate() == null : this.getPublishDate().equals(other.getPublishDate()))
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
        result = prime * result + ((getWord() == null) ? 0 : getWord().hashCode());
        result = prime * result + ((getPronunciation() == null) ? 0 : getPronunciation().hashCode());
        result = prime * result + ((getAudioUrl() == null) ? 0 : getAudioUrl().hashCode());
        result = prime * result + ((getTranslation() == null) ? 0 : getTranslation().hashCode());
        result = prime * result + ((getExample() == null) ? 0 : getExample().hashCode());
        result = prime * result + ((getExampleTranslation() == null) ? 0 : getExampleTranslation().hashCode());
        result = prime * result + ((getDifficulty() == null) ? 0 : getDifficulty().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getNotes() == null) ? 0 : getNotes().hashCode());
        result = prime * result + ((getPublishDate() == null) ? 0 : getPublishDate().hashCode());
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
        sb.append(", word=").append(word);
        sb.append(", pronunciation=").append(pronunciation);
        sb.append(", audioUrl=").append(audioUrl);
        sb.append(", translation=").append(translation);
        sb.append(", example=").append(example);
        sb.append(", exampleTranslation=").append(exampleTranslation);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", category=").append(category);
        sb.append(", notes=").append(notes);
        sb.append(", publishDate=").append(publishDate);
        sb.append(", adminId=").append(adminId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}
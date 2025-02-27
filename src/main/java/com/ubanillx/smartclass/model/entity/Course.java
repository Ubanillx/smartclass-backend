package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 课程
 * @TableName course
 */
@TableName(value ="course")
@Data
public class Course {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程副标题
     */
    private String subtitle;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 课程价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 课程类型：0-公开课，1-付费课，2-会员课
     */
    private Integer courseType;

    /**
     * 难度等级：1-入门，2-初级，3-中级，4-高级，5-专家
     */
    private Integer difficulty;

    /**
     * 状态：0-未发布，1-已发布，2-已下架
     */
    private Integer status;

    /**
     * 课程分类id
     */
    private Long categoryId;

    /**
     * 讲师id
     */
    private Long teacherId;

    /**
     * 总时长(分钟)
     */
    private Integer totalDuration;

    /**
     * 总章节数
     */
    private Integer totalChapters;

    /**
     * 总小节数
     */
    private Integer totalSections;

    /**
     * 学习人数
     */
    private Integer studentCount;

    /**
     * 评分，1-5分
     */
    private BigDecimal ratingScore;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 学习要求
     */
    private String requirements;

    /**
     * 学习目标
     */
    private String objectives;

    /**
     * 目标受众
     */
    private String targetAudience;

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
        Course other = (Course) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getSubtitle() == null ? other.getSubtitle() == null : this.getSubtitle().equals(other.getSubtitle()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getCoverImage() == null ? other.getCoverImage() == null : this.getCoverImage().equals(other.getCoverImage()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getOriginalPrice() == null ? other.getOriginalPrice() == null : this.getOriginalPrice().equals(other.getOriginalPrice()))
            && (this.getCourseType() == null ? other.getCourseType() == null : this.getCourseType().equals(other.getCourseType()))
            && (this.getDifficulty() == null ? other.getDifficulty() == null : this.getDifficulty().equals(other.getDifficulty()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCategoryId() == null ? other.getCategoryId() == null : this.getCategoryId().equals(other.getCategoryId()))
            && (this.getTeacherId() == null ? other.getTeacherId() == null : this.getTeacherId().equals(other.getTeacherId()))
            && (this.getTotalDuration() == null ? other.getTotalDuration() == null : this.getTotalDuration().equals(other.getTotalDuration()))
            && (this.getTotalChapters() == null ? other.getTotalChapters() == null : this.getTotalChapters().equals(other.getTotalChapters()))
            && (this.getTotalSections() == null ? other.getTotalSections() == null : this.getTotalSections().equals(other.getTotalSections()))
            && (this.getStudentCount() == null ? other.getStudentCount() == null : this.getStudentCount().equals(other.getStudentCount()))
            && (this.getRatingScore() == null ? other.getRatingScore() == null : this.getRatingScore().equals(other.getRatingScore()))
            && (this.getRatingCount() == null ? other.getRatingCount() == null : this.getRatingCount().equals(other.getRatingCount()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getRequirements() == null ? other.getRequirements() == null : this.getRequirements().equals(other.getRequirements()))
            && (this.getObjectives() == null ? other.getObjectives() == null : this.getObjectives().equals(other.getObjectives()))
            && (this.getTargetAudience() == null ? other.getTargetAudience() == null : this.getTargetAudience().equals(other.getTargetAudience()))
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
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSubtitle() == null) ? 0 : getSubtitle().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getCoverImage() == null) ? 0 : getCoverImage().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getOriginalPrice() == null) ? 0 : getOriginalPrice().hashCode());
        result = prime * result + ((getCourseType() == null) ? 0 : getCourseType().hashCode());
        result = prime * result + ((getDifficulty() == null) ? 0 : getDifficulty().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCategoryId() == null) ? 0 : getCategoryId().hashCode());
        result = prime * result + ((getTeacherId() == null) ? 0 : getTeacherId().hashCode());
        result = prime * result + ((getTotalDuration() == null) ? 0 : getTotalDuration().hashCode());
        result = prime * result + ((getTotalChapters() == null) ? 0 : getTotalChapters().hashCode());
        result = prime * result + ((getTotalSections() == null) ? 0 : getTotalSections().hashCode());
        result = prime * result + ((getStudentCount() == null) ? 0 : getStudentCount().hashCode());
        result = prime * result + ((getRatingScore() == null) ? 0 : getRatingScore().hashCode());
        result = prime * result + ((getRatingCount() == null) ? 0 : getRatingCount().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getRequirements() == null) ? 0 : getRequirements().hashCode());
        result = prime * result + ((getObjectives() == null) ? 0 : getObjectives().hashCode());
        result = prime * result + ((getTargetAudience() == null) ? 0 : getTargetAudience().hashCode());
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
        sb.append(", title=").append(title);
        sb.append(", subtitle=").append(subtitle);
        sb.append(", description=").append(description);
        sb.append(", coverImage=").append(coverImage);
        sb.append(", price=").append(price);
        sb.append(", originalPrice=").append(originalPrice);
        sb.append(", courseType=").append(courseType);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", status=").append(status);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", teacherId=").append(teacherId);
        sb.append(", totalDuration=").append(totalDuration);
        sb.append(", totalChapters=").append(totalChapters);
        sb.append(", totalSections=").append(totalSections);
        sb.append(", studentCount=").append(studentCount);
        sb.append(", ratingScore=").append(ratingScore);
        sb.append(", ratingCount=").append(ratingCount);
        sb.append(", tags=").append(tags);
        sb.append(", requirements=").append(requirements);
        sb.append(", objectives=").append(objectives);
        sb.append(", targetAudience=").append(targetAudience);
        sb.append(", adminId=").append(adminId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}
package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 课程
 * @TableName course
 */
@TableName(value ="course")
@Data
public class Course implements Serializable {
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
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
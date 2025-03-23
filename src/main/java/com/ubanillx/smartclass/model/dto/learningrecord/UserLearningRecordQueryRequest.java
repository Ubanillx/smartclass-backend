package com.ubanillx.smartclass.model.dto.learningrecord;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询用户学习记录请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLearningRecordQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 起始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 记录类型，如：word_card, listening, course等
     */
    private String recordType;

    /**
     * 关联ID，如单词ID、课程ID等
     */
    private Long relatedId;

    /**
     * 课程中的课次或子活动编号
     */
    private Integer lessonNumber;

    /**
     * 最小学习时长(秒)
     */
    private Integer minDuration;

    /**
     * 最大学习时长(秒)
     */
    private Integer maxDuration;

    /**
     * 最小学习数量
     */
    private Integer minCount;

    /**
     * 最大学习数量
     */
    private Integer maxCount;

    /**
     * 最小积分
     */
    private Integer minPoints;

    /**
     * 最大积分
     */
    private Integer maxPoints;

    /**
     * 最小经验值
     */
    private Integer minExperience;

    /**
     * 最大经验值
     */
    private Integer maxExperience;

    /**
     * 最小正确率
     */
    private BigDecimal minAccuracy;

    /**
     * 最大正确率
     */
    private BigDecimal maxAccuracy;

    /**
     * 活动状态，如：in_progress, completed, failed
     */
    private String status;

    /**
     * 关键词（用于搜索备注）
     */
    private String keyword;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder;

    private static final long serialVersionUID = 1L;
} 
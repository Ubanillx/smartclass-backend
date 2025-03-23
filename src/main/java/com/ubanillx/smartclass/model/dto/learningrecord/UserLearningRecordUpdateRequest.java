package com.ubanillx.smartclass.model.dto.learningrecord;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 更新用户学习记录请求
 */
@Data
public class UserLearningRecordUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    
    /**
     * 记录日期
     */
    private Date recordDate;

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
     * 学习时长(秒)
     */
    private Integer duration;

    /**
     * 学习数量
     */
    private Integer count;

    /**
     * 获得积分
     */
    private Integer points;

    /**
     * 获得经验值
     */
    private Integer experience;

    /**
     * 正确率(百分比)
     */
    private BigDecimal accuracy;

    /**
     * 活动状态，如：in_progress, completed, failed
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
} 
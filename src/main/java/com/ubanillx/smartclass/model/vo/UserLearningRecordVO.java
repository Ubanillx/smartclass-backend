package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户学习记录视图
 */
@Data
public class UserLearningRecordVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 记录日期
     */
    private Date recordDate;

    /**
     * 记录类型，如：word_card, listening, course等
     */
    private String recordType;

    /**
     * 记录类型名称
     */
    private String recordTypeName;

    /**
     * 关联ID，如单词ID、课程ID等
     */
    private Long relatedId;

    /**
     * 关联内容名称（课程名、单词等）
     */
    private String relatedName;

    /**
     * 课程中的课次或子活动编号
     */
    private Integer lessonNumber;

    /**
     * 学习时长(秒)
     */
    private Integer duration;

    /**
     * 格式化的学习时长
     */
    private String formattedDuration;

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
     * 活动状态中文名称
     */
    private String statusName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
} 
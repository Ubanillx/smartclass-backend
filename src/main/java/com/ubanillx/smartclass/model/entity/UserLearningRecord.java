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
 * 用户学习记录
 * @TableName user_learning_record
 */
@TableName(value ="user_learning_record")
@Data
public class UserLearningRecord implements Serializable {
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
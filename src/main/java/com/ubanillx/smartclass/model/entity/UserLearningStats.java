package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户学习统计
 * @TableName user_learning_stats
 */
@TableName(value ="user_learning_stats")
@Data
public class UserLearningStats implements Serializable {
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
     * 当前等级
     */
    private Integer level;

    /**
     * 当前经验值
     */
    private Integer experience;

    /**
     * 下一级所需经验值
     */
    private Integer nextLevelExp;

    /**
     * 学习天数
     */
    private Integer learningDays;

    /**
     * 连续打卡天数
     */
    private Integer continuousCheckIn;

    /**
     * 总打卡天数
     */
    private Integer totalCheckIn;

    /**
     * 总积分
     */
    private Integer totalPoints;

    /**
     * 获得徽章数
     */
    private Integer totalBadges;

    /**
     * 最后打卡时间
     */
    private Date lastCheckInTime;

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
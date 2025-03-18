package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 成就定义
 * @TableName achievement
 */
@TableName(value ="achievement")
@Data
public class Achievement implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 成就图标URL
     */
    private String iconUrl;

    /**
     * 成就徽章URL
     */
    private String badgeUrl;

    /**
     * 成就横幅URL
     */
    private String bannerUrl;

    /**
     * 成就分类，如：学习、社交、活动等
     */
    private String category;

    /**
     * 成就等级：1-普通，2-稀有，3-史诗，4-传说
     */
    private Integer level;

    /**
     * 成就点数
     */
    private Integer points;

    /**
     * 获取条件描述
     */
    private String achievementCondition;

    /**
     * 条件类型，如：course_complete, login_days, article_read等
     */
    private String conditionType;

    /**
     * 条件值，如完成10门课程，登录30天等
     */
    private Integer conditionValue;

    /**
     * 是否隐藏成就：0-否，1-是，隐藏成就不会提前显示给用户
     */
    private Integer isHidden;

    /**
     * 是否是彩蛋成就：0-否，1-是，彩蛋成就是特殊发现的成就
     */
    private Integer isSecret;

    /**
     * 奖励类型，如：points, badge, coupon等
     */
    private String rewardType;

    /**
     * 奖励值，如积分数量、优惠券ID等
     */
    private String rewardValue;

    /**
     * 排序，数字越小排序越靠前
     */
    private Integer sort;

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
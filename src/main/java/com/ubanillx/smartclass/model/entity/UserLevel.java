package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户等级
 * @TableName user_level
 */
@TableName(value ="user_level")
@Data
public class UserLevel implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 等级数值
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 等级图标URL
     */
    private String iconUrl;

    /**
     * 最小经验值
     */
    private Integer minExperience;

    /**
     * 最大经验值
     */
    private Integer maxExperience;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 等级特权，JSON格式
     */
    private String privileges;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
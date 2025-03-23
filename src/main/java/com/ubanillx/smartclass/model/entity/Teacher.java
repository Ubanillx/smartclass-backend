package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 讲师
 * @TableName teacher
 */
@TableName(value ="teacher")
@Data
public class Teacher implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 讲师姓名
     */
    private String name;

    /**
     * 讲师头像URL
     */
    private String avatar;

    /**
     * 讲师职称
     */
    private String title;

    /**
     * 讲师简介
     */
    private String introduction;

    /**
     * 专业领域，JSON数组格式
     */
    private String expertise;

    /**
     * 关联的用户id，如果讲师也是平台用户
     */
    private Long userId;

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
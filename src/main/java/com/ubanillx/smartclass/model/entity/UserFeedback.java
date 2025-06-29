package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户反馈
 * @TableName user_feedback
 */
@TableName(value ="user_feedback")
@Data
public class UserFeedback implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 反馈类型
     */
    private String feedbackType;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 附件URL
     */
    private String attachment;

    /**
     * 处理状态：0-待处理，1-处理中，2-已处理
     */
    private Integer status;

    /**
     * 处理管理员ID
     */
    private Long adminId;

    /**
     * 处理时间
     */
    private Date processTime;

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
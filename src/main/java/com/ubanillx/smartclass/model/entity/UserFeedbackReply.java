package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 反馈回复
 * @TableName user_feedback_reply
 */
@TableName(value ="user_feedback_reply")
@Data
public class UserFeedbackReply implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的反馈ID
     */
    private Long feedbackId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者角色：0-用户，1-管理员
     */
    private Integer senderRole;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 附件URL
     */
    private String attachment;

    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;

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
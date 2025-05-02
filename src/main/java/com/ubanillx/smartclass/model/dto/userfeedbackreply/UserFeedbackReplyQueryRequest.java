package com.ubanillx.smartclass.model.dto.userfeedbackreply;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户反馈回复查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserFeedbackReplyQueryRequest extends PageRequest implements Serializable {

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
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;

    private static final long serialVersionUID = 1L;
} 
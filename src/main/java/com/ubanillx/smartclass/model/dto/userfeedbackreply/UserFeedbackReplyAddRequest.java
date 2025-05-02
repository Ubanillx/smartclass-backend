package com.ubanillx.smartclass.model.dto.userfeedbackreply;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户反馈回复添加请求
 */
@Data
public class UserFeedbackReplyAddRequest implements Serializable {

    /**
     * 关联的反馈ID
     */
    private Long feedbackId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 附件URL
     */
    private String attachment;

    private static final long serialVersionUID = 1L;
} 
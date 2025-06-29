package com.ubanillx.smartclass.model.dto.userfeedback;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户反馈创建请求
 */
@Data
public class UserFeedbackAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
} 
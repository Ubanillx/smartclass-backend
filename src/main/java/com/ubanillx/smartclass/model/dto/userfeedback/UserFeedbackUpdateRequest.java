package com.ubanillx.smartclass.model.dto.userfeedback;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户反馈更新请求
 */
@Data
public class UserFeedbackUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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
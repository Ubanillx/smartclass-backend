package com.ubanillx.smartclass.model.dto.userfeedback;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户反馈处理请求
 */
@Data
public class UserFeedbackProcessRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 处理状态：1-处理中，2-已处理
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
} 
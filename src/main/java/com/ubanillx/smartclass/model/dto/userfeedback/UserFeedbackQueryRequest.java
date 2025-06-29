package com.ubanillx.smartclass.model.dto.userfeedback;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户反馈查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserFeedbackQueryRequest extends PageRequest implements Serializable {

    /**
     * 反馈类型
     */
    private String feedbackType;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 处理状态：0-待处理，1-处理中，2-已处理
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
} 
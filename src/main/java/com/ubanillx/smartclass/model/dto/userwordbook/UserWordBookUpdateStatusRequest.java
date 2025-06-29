package com.ubanillx.smartclass.model.dto.userwordbook;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新生词本中单词学习状态请求
 */
@Data
public class UserWordBookUpdateStatusRequest implements Serializable {

    /**
     * 单词ID
     */
    private Long wordId;

    /**
     * 学习状态：0-未学习，1-已学习，2-已掌握
     */
    private Integer learningStatus;

    private static final long serialVersionUID = 1L;
} 
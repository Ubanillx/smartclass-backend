package com.ubanillx.smartclass.model.dto.userwordbook;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新生词本中单词收藏状态请求
 */
@Data
public class UserWordBookCollectionRequest implements Serializable {

    /**
     * 单词ID
     */
    private Long wordId;

    /**
     * 是否收藏：0-否，1-是
     */
    private Integer isCollected;

    private static final long serialVersionUID = 1L;
} 
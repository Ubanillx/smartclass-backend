package com.ubanillx.smartclass.model.dto.userwordbook;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加单词到生词本请求
 */
@Data
public class UserWordBookAddRequest implements Serializable {

    /**
     * 单词ID
     */
    private Long wordId;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    private static final long serialVersionUID = 1L;
} 
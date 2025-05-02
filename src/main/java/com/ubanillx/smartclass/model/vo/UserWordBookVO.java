package com.ubanillx.smartclass.model.vo;

import com.ubanillx.smartclass.model.entity.UserWordBook;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户生词本视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserWordBookVO extends UserWordBook implements Serializable {

    /**
     * 单词内容
     */
    private String word;

    /**
     * 单词翻译
     */
    private String translation;

    /**
     * 单词音标
     */
    private String phonetic;

    /**
     * 发音URL
     */
    private String pronunciation;

    /**
     * 单词示例
     */
    private String example;

    private static final long serialVersionUID = 1L;
} 
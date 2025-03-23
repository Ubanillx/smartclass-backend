package com.ubanillx.smartclass.model.dto.dailyword;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新每日单词请求
 */
@Data
public class DailyWordUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 单词
     */
    private String word;

    /**
     * 音标
     */
    private String pronunciation;

    /**
     * 发音音频URL
     */
    private String audioUrl;

    /**
     * 翻译
     */
    private String translation;

    /**
     * 例句
     */
    private String example;

    /**
     * 例句翻译
     */
    private String exampleTranslation;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 单词分类
     */
    private String category;

    /**
     * 单词笔记或补充说明
     */
    private String notes;

    /**
     * 发布日期
     */
    private Date publishDate;

    private static final long serialVersionUID = 1L;
}
package com.ubanillx.smartclass.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 每日单词视图
*/
@Data
public class DailyWordVO implements Serializable {

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

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
} 
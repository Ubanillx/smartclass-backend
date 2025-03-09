package com.ubanillx.smartclass.model.dto.dailyword;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询每日单词请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DailyWordQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 单词
     */
    private String word;

    /**
     * 翻译
     */
    private String translation;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 单词分类
     */
    private String category;

    /**
     * 发布日期
     */
    private Date publishDate;

    /**
     * 搜索关键词（同时搜索单词、翻译、例句）
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
} 
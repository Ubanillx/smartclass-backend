package com.ubanillx.smartclass.model.dto.dailyarticle;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询每日文章请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DailyArticleQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 文章分类
     */
    private String category;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 发布日期
     */
    private Date publishDate;

    /**
     * 搜索关键词（同时搜索标题、内容、摘要、标签）
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
} 
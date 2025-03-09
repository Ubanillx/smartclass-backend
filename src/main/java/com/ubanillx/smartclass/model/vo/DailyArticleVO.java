package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日文章视图对象
 */
@Data
public class DailyArticleVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者
     */
    private String author;

    /**
     * 来源
     */
    private String source;

    /**
     * 原文链接
     */
    private String sourceUrl;

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
     * 预计阅读时间(分钟)
     */
    private Integer readTime;

    /**
     * 发布日期
     */
    private Date publishDate;

    /**
     * 查看次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户是否已阅读过该文章
     */
    private Boolean hasRead;

    /**
     * 用户阅读进度（0-100）
     */
    private Integer readingProgress;

    /**
     * 用户是否点赞
     */
    private Boolean hasLiked;

    private static final long serialVersionUID = 1L;
} 
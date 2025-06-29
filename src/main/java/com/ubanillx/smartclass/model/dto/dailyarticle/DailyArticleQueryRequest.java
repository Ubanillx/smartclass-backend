package com.ubanillx.smartclass.model.dto.dailyarticle;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日文章查询请求
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
     * 文章摘要（模糊查询）
     */
    private String summary;
    
    /**
     * 文章内容（模糊查询）
     */
    private String content;

    /**
     * 作者
     */
    private String author;

    /**
     * 来源
     */
    private String source;

    /**
     *.文章分类
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
     * 发布日期起始
     */
    private Date publishDateStart;

    /**
     * 发布日期结束
     */
    private Date publishDateEnd;

    /**
     * 创建管理员id
     */
    private Long adminId;
    
    /**
     * 最小阅读时间
     */
    private Integer minReadTime;
    
    /**
     * 最大阅读时间
     */
    private Integer maxReadTime;

    /**
     * 最小查看次数
     */
    private Integer minViewCount;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
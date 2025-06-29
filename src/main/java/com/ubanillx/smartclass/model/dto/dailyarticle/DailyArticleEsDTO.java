package com.ubanillx.smartclass.model.dto.dailyarticle;

import com.ubanillx.smartclass.model.entity.DailyArticle;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日美文 ES 包装类
 */
@Document(indexName = "daily_article")
@Data
public class DailyArticleEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 文章标题
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /**
     * 文章内容
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    /**
     * 文章摘要
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String summary;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者
     */
    @Field(type = FieldType.Keyword)
    private String author;

    /**
     * 来源
     */
    @Field(type = FieldType.Keyword)
    private String source;

    /**
     * 原文链接
     */
    private String sourceUrl;

    /**
     * 文章分类
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * 标签，JSON数组格式
     */
    @Field(type = FieldType.Text, analyzer = "standard")
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
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date publishDate;

    /**
     * 创建管理员id
     */
    private Long adminId;

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
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param dailyArticle 每日美文对象
     * @return ES包装类
     */
    public static DailyArticleEsDTO objToDto(DailyArticle dailyArticle) {
        if (dailyArticle == null) {
            return null;
        }
        DailyArticleEsDTO dailyArticleEsDTO = new DailyArticleEsDTO();
        BeanUtils.copyProperties(dailyArticle, dailyArticleEsDTO);
        return dailyArticleEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param dailyArticleEsDTO ES包装类
     * @return 每日美文对象
     */
    public static DailyArticle dtoToObj(DailyArticleEsDTO dailyArticleEsDTO) {
        if (dailyArticleEsDTO == null) {
            return null;
        }
        DailyArticle dailyArticle = new DailyArticle();
        BeanUtils.copyProperties(dailyArticleEsDTO, dailyArticle);
        return dailyArticle;
    }
} 
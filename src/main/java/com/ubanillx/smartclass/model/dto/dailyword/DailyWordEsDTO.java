package com.ubanillx.smartclass.model.dto.dailyword;

import com.ubanillx.smartclass.model.entity.DailyWord;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日单词 ES 包装类
 */
@Document(indexName = "daily_word")
@Data
public class DailyWordEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 单词
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
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
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String translation;

    /**
     * 例句
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String example;

    /**
     * 例句翻译
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String exampleTranslation;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 单词分类
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * 单词笔记或补充说明
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String notes;

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
     * @param dailyWord 每日单词对象
     * @return ES包装类
     */
    public static DailyWordEsDTO objToDto(DailyWord dailyWord) {
        if (dailyWord == null) {
            return null;
        }
        DailyWordEsDTO dailyWordEsDTO = new DailyWordEsDTO();
        BeanUtils.copyProperties(dailyWord, dailyWordEsDTO);
        return dailyWordEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param dailyWordEsDTO ES包装类
     * @return 每日单词对象
     */
    public static DailyWord dtoToObj(DailyWordEsDTO dailyWordEsDTO) {
        if (dailyWordEsDTO == null) {
            return null;
        }
        DailyWord dailyWord = new DailyWord();
        BeanUtils.copyProperties(dailyWordEsDTO, dailyWord);
        return dailyWord;
    }
} 
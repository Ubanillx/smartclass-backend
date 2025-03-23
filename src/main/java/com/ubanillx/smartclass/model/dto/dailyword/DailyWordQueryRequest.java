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
     * 发布日期开始
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
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
package com.ubanillx.smartclass.model.dto.userwordbook;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户生词本查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserWordBookQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 单词id
     */
    private Long wordId;

    /**
     * 学习状态：0-未学习，1-已学习，2-已掌握
     */
    private Integer learningStatus;

    /**
     * 是否收藏：0-否，1-是
     */
    private Integer isCollected;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 单词内容（模糊查询）
     */
    private String word;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间起始
     */
    private Date createTimeStart;

    /**
     * 创建时间结束
     */
    private Date createTimeEnd;

    private static final long serialVersionUID = 1L;
} 
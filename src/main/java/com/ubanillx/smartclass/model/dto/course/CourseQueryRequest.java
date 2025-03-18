package com.ubanillx.smartclass.model.dto.course;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 课程查询请求
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CourseQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程副标题
     */
    private String subtitle;

    /**
     * 课程类型：0-公开课，1-付费课，2-会员课
     */
    private Integer courseType;

    /**
     * 难度等级：1-入门，2-初级，3-中级，4-高级，5-专家
     */
    private Integer difficulty;

    /**
     * 状态：0-未发布，1-已发布，2-已下架
     */
    private Integer status;

    /**
     * 课程分类id
     */
    private Long categoryId;

    /**
     * 讲师id
     */
    private Long teacherId;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;
    
    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 最低评分
     */
    private BigDecimal minRating;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

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
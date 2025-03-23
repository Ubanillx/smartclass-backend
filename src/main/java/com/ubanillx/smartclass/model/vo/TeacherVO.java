package com.ubanillx.smartclass.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 讲师视图（脱敏）
 */
@Data
public class TeacherVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 讲师姓名
     */
    private String name;

    /**
     * 讲师头像URL
     */
    private String avatar;

    /**
     * 讲师职称
     */
    private String title;

    /**
     * 讲师简介
     */
    private String introduction;

    /**
     * 专业领域，JSON数组格式
     */
    private String expertise;
    
    /**
     * 关联的用户信息，如果讲师也是平台用户
     */
    private UserVO userVO;
    
    /**
     * 课程数量
     */
    private Integer courseCount;
    
    /**
     * 学生数量
     */
    private Integer studentCount;
    
    /**
     * 平均评分
     */
    private Double averageRating;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
} 
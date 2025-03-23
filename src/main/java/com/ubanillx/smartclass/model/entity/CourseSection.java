package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 课程小节
 * @TableName course_section
 */
@TableName(value ="course_section")
@Data
public class CourseSection implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 章节id
     */
    private Long chapterId;

    /**
     * 小节标题
     */
    private String title;

    /**
     * 小节描述
     */
    private String description;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 时长(秒)
     */
    private Integer duration;

    /**
     * 排序，数字越小排序越靠前
     */
    private Integer sort;

    /**
     * 是否免费：0-否，1-是
     */
    private Integer isFree;

    /**
     * 资源类型：0-视频，1-音频，2-文档，3-图片，4-直播
     */
    private Integer resourceType;

    /**
     * 资源URL
     */
    private String resourceUrl;

    /**
     * 创建管理员id
     */
    private Long adminId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
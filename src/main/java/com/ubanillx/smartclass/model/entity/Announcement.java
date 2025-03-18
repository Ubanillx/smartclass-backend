package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 系统公告
 * @TableName announcement
 */
@TableName(value ="announcement")
@Data
public class Announcement implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 优先级，数字越大优先级越高
     */
    private Integer priority;

    /**
     * 状态：0-草稿，1-已发布，2-已下线
     */
    private Integer status;

    /**
     * 公告开始展示时间
     */
    private Date startTime;

    /**
     * 公告结束展示时间
     */
    private Date endTime;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 发布管理员id
     */
    private Long adminId;

    /**
     * 查看次数
     */
    private Integer viewCount;

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
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
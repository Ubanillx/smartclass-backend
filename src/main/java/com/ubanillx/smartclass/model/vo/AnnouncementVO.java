package com.ubanillx.smartclass.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统公告视图
*/
@Data
public class AnnouncementVO implements Serializable {

    /**
     * id
     */
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
     * 查看次数
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否已读（针对当前登录用户）
     */
    private Boolean hasRead;

    private static final long serialVersionUID = 1L;
} 
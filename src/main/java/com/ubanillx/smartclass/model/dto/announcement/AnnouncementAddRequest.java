package com.ubanillx.smartclass.model.dto.announcement;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告添加请求
*/
@Data
public class AnnouncementAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}
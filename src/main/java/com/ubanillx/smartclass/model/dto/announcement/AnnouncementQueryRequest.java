package com.ubanillx.smartclass.model.dto.announcement;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告查询请求
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementQueryRequest extends PageRequest implements Serializable {
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
     * 发布管理员id
     */
    private Long adminId;

    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 是否只查询有效公告（未删除、已发布、在有效期内的公告）
     */
    private Boolean isValid;

    private static final long serialVersionUID = 1L;
}
package com.ubanillx.smartclass.model.dto.annoucement;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告查询请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 公告标题（模糊查询）
     */
    private String title;

    /**
     * 公告内容（模糊查询）
     */
    private String content;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态：0-草稿，1-已发布，2-已下线
     */
    private Integer status;


    /**
     * 发布管理员id
     */
    private Long adminId;



    private static final long serialVersionUID = 1L;
}
package com.ubanillx.smartclass.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子评论回复视图对象
 */
@Data
public class PostCommentReplyVO implements Serializable {
    /**
     * 回复ID
     */
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 回复者ID
     */
    private Long userId;

    /**
     * 回复者信息
     */
    private UserVO userVO;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 国家
     */
    private String country;

    /**
     * 城市
     */
    private String city;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private static final long serialVersionUID = 1L;
} 
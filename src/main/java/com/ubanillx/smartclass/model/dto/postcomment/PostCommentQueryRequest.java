package com.ubanillx.smartclass.model.dto.postcomment;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子评论查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentQueryRequest extends PageRequest implements Serializable {
    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 评论者ID
     */
    private Long userId;

    /**
     * 评论内容（模糊查询）
     */
    private String content;

    private static final long serialVersionUID = 1L;
} 
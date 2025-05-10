package com.ubanillx.smartclass.model.dto.postcommentreply;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子评论回复查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentReplyQueryRequest extends PageRequest implements Serializable {
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
     * 回复内容（模糊查询）
     */
    private String content;

    private static final long serialVersionUID = 1L;
} 
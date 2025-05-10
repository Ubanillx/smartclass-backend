package com.ubanillx.smartclass.model.dto.postcommentreply;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 帖子评论回复添加请求
 */
@Data
public class PostCommentReplyAddRequest implements Serializable {
    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    /**
     * 评论ID
     */
    @NotNull(message = "评论ID不能为空")
    private Long commentId;

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    private String content;

    /**
     * 客户端IP（用于地理位置解析）
     */
    private String clientIp;

    private static final long serialVersionUID = 1L;
} 
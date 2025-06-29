package com.ubanillx.smartclass.model.dto.postthumb;

import java.io.Serializable;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 帖子点赞添加请求
 */
@Data
public class PostThumbAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    @NotNull(message = "帖子id不能为空")
    private Long postId;

    private static final long serialVersionUID = 1L;
}
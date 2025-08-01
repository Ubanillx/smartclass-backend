package com.ubanillx.smartclass.model.dto.post;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 编辑请求
*/
@Data
public class PostEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 客户端IP地址
     */
    private String clientIp;

    private static final long serialVersionUID = 1L;
}
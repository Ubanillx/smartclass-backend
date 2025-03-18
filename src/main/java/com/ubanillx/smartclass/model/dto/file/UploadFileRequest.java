package com.ubanillx.smartclass.model.dto.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传请求
*/
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    /**
     * 文件名称（可选）
     */
    private String filename;

    /**
     * 文件描述（可选）
     */
    private String description;

    /**
     * Base64编码的文件内容（可选）
     */
    private String base64Data;

    private static final long serialVersionUID = 1L;
}
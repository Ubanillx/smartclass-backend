package com.ubanillx.smartclass.model.dto.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传请求
*/
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 文件名称（可选）
     */
    private String filename;

    /**
     * 文件描述（可选）
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
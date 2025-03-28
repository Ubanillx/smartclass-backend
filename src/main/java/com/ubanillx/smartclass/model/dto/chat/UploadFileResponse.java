package com.ubanillx.smartclass.model.dto.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传文件响应DTO
 */
@Data
public class UploadFileResponse implements Serializable {
    
    /**
     * 文件ID
     */
    private String fileId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小（字节）
     */
    private Integer fileSize;
    
    /**
     * 文件类型
     */
    private String mimeType;
    
    /**
     * 文件扩展名
     */
    private String extension;
    
    /**
     * 上传时间
     */
    private Long createdAt;
    
    private static final long serialVersionUID = 1L;
} 
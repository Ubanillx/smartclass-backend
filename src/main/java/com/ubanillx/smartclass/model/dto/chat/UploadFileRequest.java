package com.ubanillx.smartclass.model.dto.chat;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 上传文件请求DTO
 */
@Data
public class UploadFileRequest implements Serializable {
    
    /**
     * AI分身ID
     */
    private Long aiAvatarId;
    
    /**
     * 上传的文件
     */
    private MultipartFile file;
    
    private static final long serialVersionUID = 1L;
} 
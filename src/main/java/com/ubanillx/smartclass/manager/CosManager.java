package com.ubanillx.smartclass.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.ubanillx.smartclass.config.CosClientConfig;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;

import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ubanillx.smartclass.constant.FileConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.enums.FileUploadBizEnum;

/**
 * Cos 对象存储操作
*/
@Component
@Slf4j
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;
    
    // 创建一个线程池，用于处理大文件上传
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * 获取COS访问地址
     *
     * @return COS访问地址
     */
    private String getCosHost() {
        return String.format("https://%s.cos.%s.myqcloud.com", cosClientConfig.getBucket(), cosClientConfig.getRegion());
    }

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param localFilePath 本地文件路径
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }
    
    /**
     * 上传视频文件
     *
     * @param key 唯一键
     * @param file 视频文件
     * @return 上传结果
     */
    public PutObjectResult putVideo(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 设置视频文件的元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("video/mp4"); // 默认设置为mp4，实际应用中应根据文件类型设置
        putObjectRequest.setMetadata(metadata);
        // 设置存储类型为标准存储
        putObjectRequest.setStorageClass(StorageClass.Standard);
        return cosClient.putObject(putObjectRequest);
    }
    
    /**
     * 上传文档文件
     *
     * @param key 唯一键
     * @param file 文档文件
     * @return 上传结果
     */
    public PutObjectResult putDocument(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 设置文档文件的元数据
        ObjectMetadata metadata = new ObjectMetadata();
        // 根据文件扩展名设置适当的ContentType
        String extension = key.substring(key.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                metadata.setContentType("application/pdf");
                break;
            case "doc":
            case "docx":
                metadata.setContentType("application/msword");
                break;
            case "ppt":
            case "pptx":
                metadata.setContentType("application/vnd.ms-powerpoint");
                break;
            case "xls":
            case "xlsx":
                metadata.setContentType("application/vnd.ms-excel");
                break;
            case "txt":
                metadata.setContentType("text/plain");
                break;
            default:
                metadata.setContentType("application/octet-stream");
        }
        putObjectRequest.setMetadata(metadata);
        return cosClient.putObject(putObjectRequest);
    }
    
    /**
     * 使用流上传对象
     *
     * @param key 唯一键
     * @param input 输入流
     * @param contentLength 内容长度
     * @param contentType 内容类型
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, InputStream input, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);
        
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, input, metadata);
        return cosClient.putObject(putObjectRequest);
    }
    
    /**
     * 异步上传大文件
     *
     * @param key 唯一键
     * @param file 文件
     */
    public void putLargeFileAsync(String key, File file) {
        executorService.submit(() -> {
            try {
                PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
                cosClient.putObject(putObjectRequest);
                log.info("Large file uploaded successfully: {}", key);
            } catch (Exception e) {
                log.error("Failed to upload large file: {}", key, e);
            }
        });
    }

    /**
     * 上传文件
     *
     * @param multipartFile
     * @param biz
     * @return
     */
    public String uploadFile(MultipartFile multipartFile, FileUploadBizEnum biz) {
        // 校验
        validFile(multipartFile, biz);
        // 文件目录: 根据业务、用户来划分
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s", biz.getValue(), filename);
        try {
            // 上传文件
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            // 公共读
            objectMetadata.setContentType(multipartFile.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(),
                    filepath, multipartFile.getInputStream(), objectMetadata);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
        return getCosHost() + filepath;
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param biz
     */
    public void validFile(MultipartFile multipartFile, FileUploadBizEnum biz) {
        // 文件大小校验
        long fileSize = multipartFile.getSize();
        // 文件后缀校验
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        
        // 处理通用文件类型
        if (FileUploadBizEnum.GENERAL.equals(biz)) {
            // 通用文件类型不限制文件类型，只限制大小
            if (fileSize > FileConstant.ONE_HUNDRED_MB) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过100MB");
            }
            return;
        }
        
        if (FileUploadBizEnum.USER_AVATAR.equals(biz)) {
            if (fileSize > FileConstant.ONE_MB * cosClientConfig.getUpload().getMaxAvatarSize()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 " + cosClientConfig.getUpload().getMaxAvatarSize() + "MB");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        } else if (FileUploadBizEnum.VIDEO.equals(biz)) {
            if (fileSize > FileConstant.ONE_MB * cosClientConfig.getUpload().getMaxVideoSize()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 " + cosClientConfig.getUpload().getMaxVideoSize() + "MB");
            }
            List<String> allowedVideoTypes = Arrays.asList(cosClientConfig.getUpload().getAllowedVideoTypes().split(","));
            if (!allowedVideoTypes.contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误，仅支持: " + cosClientConfig.getUpload().getAllowedVideoTypes());
            }
        } else if (FileUploadBizEnum.DOCUMENT.equals(biz)) {
            if (fileSize > FileConstant.ONE_MB * cosClientConfig.getUpload().getMaxDocumentSize()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 " + cosClientConfig.getUpload().getMaxDocumentSize() + "MB");
            }
            List<String> allowedDocumentTypes = Arrays.asList(cosClientConfig.getUpload().getAllowedDocumentTypes().split(","));
            if (!allowedDocumentTypes.contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误，仅支持: " + cosClientConfig.getUpload().getAllowedDocumentTypes());
            }
        } else if (FileUploadBizEnum.MATERIAL.equals(biz)) {
            if (fileSize > FileConstant.ONE_MB * cosClientConfig.getUpload().getMaxMaterialSize()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 " + cosClientConfig.getUpload().getMaxMaterialSize() + "MB");
            }
            List<String> allowedMaterialTypes = Arrays.asList(cosClientConfig.getUpload().getAllowedMaterialTypes().split(","));
            if (!allowedMaterialTypes.contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误，仅支持: " + cosClientConfig.getUpload().getAllowedMaterialTypes());
            }
        }
    }
}

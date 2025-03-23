package com.ubanillx.smartclass.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云对象存储客户端
*/
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 区域
     */
    private String region;

    /**
     * 桶名
     */
    private String bucket;
    
    /**
     * 文件上传配置
     */
    private UploadConfig upload = new UploadConfig();
    
    /**
     * 文件上传配置类
     */
    @Data
    public static class UploadConfig {
        /**
         * 最大头像大小（MB）
         */
        private int maxAvatarSize = 2;
        
        /**
         * 最大视频大小（MB）
         */
        private int maxVideoSize = 100;
        
        /**
         * 最大文档大小（MB）
         */
        private int maxDocumentSize = 20;
        
        /**
         * 最大资料大小（MB）
         */
        private int maxMaterialSize = 50;
        
        /**
         * 允许的视频类型
         */
        private String allowedVideoTypes = "mp4,avi,mov,flv,wmv";
        
        /**
         * 允许的文档类型
         */
        private String allowedDocumentTypes = "pdf,doc,docx,ppt,pptx,xls,xlsx,txt";
        
        /**
         * 允许的资料类型
         */
        private String allowedMaterialTypes = "pdf,doc,docx,ppt,pptx,xls,xlsx,txt,zip,rar,7z";
    }

    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}
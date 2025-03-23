package com.ubanillx.smartclass.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 获取文件后缀（不带点）
     *
     * @param fileName 文件名
     * @return 文件后缀
     */
    public static String getSuffix(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }

    /**
     * 获取文件名（不带后缀）
     *
     * @param fileName 文件名
     * @return 文件名（不带后缀）
     */
    public static String getFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return fileName;
        }
        return fileName.substring(0, lastIndexOf);
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID().toString().replaceAll("-", "") + "." + getSuffix(originalFileName);
    }

    /**
     * 生成带前缀的唯一文件名
     *
     * @param prefix 前缀
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String prefix, String originalFileName) {
        return prefix + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + getSuffix(originalFileName);
    }
    
    /**
     * 判断文件类型是否为图片
     *
     * @param suffix 文件后缀
     * @return 是否为图片
     */
    public static boolean isImage(String suffix) {
        if (StringUtils.isBlank(suffix)) {
            return false;
        }
        return "jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix) 
                || "png".equalsIgnoreCase(suffix) || "gif".equalsIgnoreCase(suffix) 
                || "bmp".equalsIgnoreCase(suffix) || "webp".equalsIgnoreCase(suffix)
                || "svg".equalsIgnoreCase(suffix);
    }
    
    /**
     * 判断文件类型是否为视频
     *
     * @param suffix 文件后缀
     * @return 是否为视频
     */
    public static boolean isVideo(String suffix) {
        if (StringUtils.isBlank(suffix)) {
            return false;
        }
        return "mp4".equalsIgnoreCase(suffix) || "avi".equalsIgnoreCase(suffix) 
                || "mov".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix) 
                || "flv".equalsIgnoreCase(suffix) || "mkv".equalsIgnoreCase(suffix)
                || "webm".equalsIgnoreCase(suffix);
    }
    
    /**
     * 判断文件类型是否为文档
     *
     * @param suffix 文件后缀
     * @return 是否为文档
     */
    public static boolean isDocument(String suffix) {
        if (StringUtils.isBlank(suffix)) {
            return false;
        }
        return "doc".equalsIgnoreCase(suffix) || "docx".equalsIgnoreCase(suffix) 
                || "pdf".equalsIgnoreCase(suffix) || "txt".equalsIgnoreCase(suffix) 
                || "ppt".equalsIgnoreCase(suffix) || "pptx".equalsIgnoreCase(suffix)
                || "xls".equalsIgnoreCase(suffix) || "xlsx".equalsIgnoreCase(suffix);
    }
    
    /**
     * 判断文件类型是否为压缩文件
     *
     * @param suffix 文件后缀
     * @return 是否为压缩文件
     */
    public static boolean isArchive(String suffix) {
        if (StringUtils.isBlank(suffix)) {
            return false;
        }
        return "zip".equalsIgnoreCase(suffix) || "rar".equalsIgnoreCase(suffix) 
                || "7z".equalsIgnoreCase(suffix) || "tar".equalsIgnoreCase(suffix) 
                || "gz".equalsIgnoreCase(suffix);
    }
} 
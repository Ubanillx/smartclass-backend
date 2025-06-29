package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.manager.CosManager;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.enums.FileUploadBizEnum;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    /**
     * 通用文件上传
     *
     * @param file 上传的文件
     * @param request HTTP请求
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        // 直接使用通用文件类型
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.GENERAL;
        
        userService.getLoginUser(request);
        try {
            String url = cosManager.uploadFile(file, fileUploadBizEnum);
            return ResultUtils.success(url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传头像
     * 
     * @param file 头像文件
     * @param request HTTP请求
     * @return 头像URL
     */
    @PostMapping("/upload/avatar")
    public BaseResponse<String> uploadAvatar(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        try {
            // 校验文件大小和类型由cosManager处理
            String url = cosManager.uploadFile(file, FileUploadBizEnum.USER_AVATAR);
            // 更新用户头像
            User user = new User();
            user.setId(loginUser.getId());
            user.setUserAvatar(url);
            boolean result = userService.updateById(user);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(url);
        } catch (Exception e) {
            log.error("头像上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传视频
     * 
     * @param file 视频文件
     * @param request HTTP请求
     * @return 视频URL
     */
    @PostMapping("/upload/video")
    public BaseResponse<String> uploadVideo(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        userService.getLoginUser(request);
        try {
            String url = cosManager.uploadFile(file, FileUploadBizEnum.VIDEO);
            return ResultUtils.success(url);
        } catch (Exception e) {
            log.error("视频上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传文档
     * 
     * @param file 文档文件
     * @param request HTTP请求
     * @return 文档URL
     */
    @PostMapping("/upload/document")
    public BaseResponse<String> uploadDocument(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        userService.getLoginUser(request);
        try {
            String url = cosManager.uploadFile(file, FileUploadBizEnum.DOCUMENT);
            return ResultUtils.success(url);
        } catch (Exception e) {
            log.error("文档上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传课程资料
     *
     * @param file 课程资料文件
     * @param request HTTP请求
     * @return 资料URL
     */
    @PostMapping("/upload/material")
    public BaseResponse<String> uploadMaterial(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        userService.getLoginUser(request);
        try {
            String url = cosManager.uploadFile(file, FileUploadBizEnum.MATERIAL);
            return ResultUtils.success(url);
        } catch (Exception e) {
            log.error("课程资料上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败: " + e.getMessage());
        }
    }
}

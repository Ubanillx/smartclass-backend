package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.manager.CosManager;
import com.ubanillx.smartclass.model.dto.file.UploadFileRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.enums.FileUploadBizEnum;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

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
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                          UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址，使用cosManager的方法获取完整URL
            return ResultUtils.success(cosManager.uploadFile(multipartFile, fileUploadBizEnum));
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }
    
    /**
     * 上传头像
     * 
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload/avatar")
    public BaseResponse<String> uploadAvatar(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 校验文件大小和类型
        String url = cosManager.uploadFile(multipartFile, FileUploadBizEnum.USER_AVATAR);
        // 更新用户头像
        User user = new User();
        user.setId(loginUser.getId());
        user.setUserAvatar(url);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(url);
    }
    
    /**
     * 上传视频
     * 
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload/video")
    public BaseResponse<String> uploadVideo(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        userService.getLoginUser(request);
        String url = cosManager.uploadFile(multipartFile, FileUploadBizEnum.VIDEO);
        return ResultUtils.success(url);
    }
    
    /**
     * 上传文档
     * 
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload/document")
    public BaseResponse<String> uploadDocument(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        userService.getLoginUser(request);
        String url = cosManager.uploadFile(multipartFile, FileUploadBizEnum.DOCUMENT);
        return ResultUtils.success(url);
    }
    
    /**
     * 上传课程资料
     * 
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload/material")
    public BaseResponse<String> uploadMaterial(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        userService.getLoginUser(request);
        String url = cosManager.uploadFile(multipartFile, FileUploadBizEnum.MATERIAL);
        return ResultUtils.success(url);
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > 1024 * 1024L) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}

package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.entity.CourseMaterial;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.CourseMaterialService;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 课程资料接口
 */
@RestController
@RequestMapping("course/material")
@Slf4j
public class CourseMaterialController {

    @Resource
    private CourseMaterialService courseMaterialService;

    @Resource
    private UserService userService;
    
    @Resource
    private CourseService courseService;
    
    @Resource
    private FileController fileController;

    /**
     * 添加课程资料
     *
     * @param courseMaterial 课程资料信息
     * @param request HTTP请求
     * @return 资料ID
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addMaterial(@RequestBody CourseMaterial courseMaterial,
                                         HttpServletRequest request) {
        if (courseMaterial == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long materialId = courseMaterialService.addCourseMaterial(courseMaterial, loginUser.getId());
        return ResultUtils.success(materialId);
    }

    /**
     * 上传课程资料并关联
     *
     * @param file 资料文件
     * @param courseId 课程ID
     * @param title 资料标题
     * @param description 资料描述
     * @param request HTTP请求
     * @return 资料ID
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> uploadAndAddMaterial(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            HttpServletRequest request) {
        
        // 校验课程是否存在
        if (courseService.getById(courseId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程不存在");
        }
        
        // 上传文件
        BaseResponse<String> uploadResponse = fileController.uploadMaterial(file, request);
        String fileUrl = uploadResponse.getData();
        
        // 创建课程资料对象
        CourseMaterial courseMaterial = new CourseMaterial();
        courseMaterial.setCourseId(courseId);
        courseMaterial.setTitle(title);
        courseMaterial.setDescription(description);
        courseMaterial.setFileUrl(fileUrl);
        courseMaterial.setFileSize(file.getSize());
        courseMaterial.setFileType(file.getContentType());
        courseMaterial.setDownloadCount(0);
        
        // 保存课程资料
        User loginUser = userService.getLoginUser(request);
        long materialId = courseMaterialService.addCourseMaterial(courseMaterial, loginUser.getId());
        return ResultUtils.success(materialId);
    }

    /**
     * 删除课程资料
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteMaterial(@RequestBody DeleteRequest deleteRequest,
                                              HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        // 判断是否存在
        CourseMaterial oldMaterial = courseMaterialService.getById(id);
        if (oldMaterial == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = courseMaterialService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新课程资料
     *
     * @param courseMaterial 更新信息
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateMaterial(@RequestBody CourseMaterial courseMaterial,
                                              HttpServletRequest request) {
        if (courseMaterial == null || courseMaterial.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        CourseMaterial oldMaterial = courseMaterialService.getById(courseMaterial.getId());
        if (oldMaterial == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = courseMaterialService.updateById(courseMaterial);
        return ResultUtils.success(result);
    }
    
    /**
     * 更新课程资料文件
     *
     * @param file 新文件
     * @param materialId 资料ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/update/file")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateMaterialFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("materialId") Long materialId,
            HttpServletRequest request) {
        
        // 判断资料是否存在
        CourseMaterial oldMaterial = courseMaterialService.getById(materialId);
        if (oldMaterial == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "资料不存在");
        }
        
        // 上传新文件
        BaseResponse<String> uploadResponse = fileController.uploadMaterial(file, request);
        String fileUrl = uploadResponse.getData();
        
        // 更新资料信息
        CourseMaterial updatedMaterial = new CourseMaterial();
        updatedMaterial.setId(materialId);
        updatedMaterial.setFileUrl(fileUrl);
        updatedMaterial.setFileSize(file.getSize());
        updatedMaterial.setFileType(file.getContentType());
        
        boolean result = courseMaterialService.updateById(updatedMaterial);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取课程资料
     *
     * @param id 资料ID
     * @return 资料信息
     */
    @GetMapping("/get")
    public BaseResponse<CourseMaterial> getMaterialById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CourseMaterial material = courseMaterialService.getById(id);
        if (material == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(material);
    }

    /**
     * 获取课程的所有资料
     *
     * @param courseId 课程ID
     * @return 资料列表
     */
    @GetMapping("/list")
    public BaseResponse<List<CourseMaterial>> listMaterialsByCourse(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<CourseMaterial> materialList = courseMaterialService.getMaterialsByCourseId(courseId);
        return ResultUtils.success(materialList);
    }

    /**
     * 分页获取课程资料
     *
     * @param courseId 课程ID
     * @param current 当前页码
     * @param pageSize 页面大小
     * @return 分页结果
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<CourseMaterial>> listMaterialsByPage(Long courseId, 
                                                                long current, 
                                                                long pageSize) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (current <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<CourseMaterial> materialPage = courseMaterialService.listMaterialsByPage(courseId, current, pageSize);
        return ResultUtils.success(materialPage);
    }

    /**
     * 增加资料下载次数
     *
     * @param materialId 资料ID
     * @return 是否成功
     */
    @PostMapping("/download/count")
    public BaseResponse<Boolean> incrementDownloadCount(Long materialId) {
        if (materialId == null || materialId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = courseMaterialService.incrementDownloadCount(materialId);
        return ResultUtils.success(result);
    }
} 
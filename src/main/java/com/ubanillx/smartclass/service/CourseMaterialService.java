package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.CourseMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
* @author liulo
* @description 针对表【course_material(课程资料)】的数据库操作Service
* @createDate 2025-03-21 15:14:50
*/
public interface CourseMaterialService extends IService<CourseMaterial> {

    /**
     * 添加课程资料
     *
     * @param courseMaterial 课程资料
     * @param adminId 管理员ID
     * @return 资料ID
     */
    long addCourseMaterial(CourseMaterial courseMaterial, Long adminId);

    /**
     * 根据课程ID获取资料列表
     *
     * @param courseId 课程ID
     * @return 资料列表
     */
    List<CourseMaterial> getMaterialsByCourseId(Long courseId);

    /**
     * 分页获取课程资料
     *
     * @param courseId 课程ID
     * @param current 当前页码
     * @param size 页面大小
     * @return 分页结果
     */
    Page<CourseMaterial> listMaterialsByPage(Long courseId, long current, long size);

    /**
     * 增加资料下载次数
     *
     * @param materialId 资料ID
     * @return 是否成功
     */
    boolean incrementDownloadCount(Long materialId);
}

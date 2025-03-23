package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.CourseMaterialMapper;
import com.ubanillx.smartclass.model.entity.CourseMaterial;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.service.CourseMaterialService;
import com.ubanillx.smartclass.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程资料服务实现类
 */
@Service
@Slf4j
public class CourseMaterialServiceImpl extends ServiceImpl<CourseMaterialMapper, CourseMaterial>
        implements CourseMaterialService {

    @Resource
    private CourseService courseService;

    /**
     * 添加课程资料
     *
     * @param courseMaterial 课程资料
     * @param adminId 管理员ID
     * @return 资料ID
     */
    public long addCourseMaterial(CourseMaterial courseMaterial, Long adminId) {
        if (courseMaterial == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 校验课程资料
        validCourseMaterial(courseMaterial, true);

        // 检查课程是否存在
        Long courseId = courseMaterial.getCourseId();
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程不存在");
        }

        // 设置管理员ID和默认下载次数
        courseMaterial.setAdminId(adminId);
        courseMaterial.setDownloadCount(0);

        // 保存课程资料
        boolean result = this.save(courseMaterial);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return courseMaterial.getId();
    }

    /**
     * 根据课程ID获取资料列表
     *
     * @param courseId 课程ID
     * @return 资料列表
     */
    public List<CourseMaterial> getMaterialsByCourseId(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }

        QueryWrapper<CourseMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("courseId", courseId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByAsc("sort");

        return this.list(queryWrapper);
    }

    /**
     * 分页获取课程资料
     *
     * @param courseId 课程ID
     * @param current 当前页码
     * @param size 页面大小
     * @return 分页结果
     */
    public Page<CourseMaterial> listMaterialsByPage(Long courseId, long current, long size) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }

        QueryWrapper<CourseMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("courseId", courseId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByAsc("sort");

        Page<CourseMaterial> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }

    /**
     * 增加资料下载次数
     *
     * @param materialId 资料ID
     * @return 是否成功
     */
    public boolean incrementDownloadCount(Long materialId) {
        if (materialId == null || materialId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "资料ID不合法");
        }

        CourseMaterial material = this.getById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "资料不存在");
        }

        material.setDownloadCount(material.getDownloadCount() + 1);
        return this.updateById(material);
    }

    /**
     * 校验课程资料
     *
     * @param courseMaterial 课程资料
     * @param add 是否为添加操作
     */
    private void validCourseMaterial(CourseMaterial courseMaterial, boolean add) {
        if (courseMaterial == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long courseId = courseMaterial.getCourseId();
        String title = courseMaterial.getTitle();
        String fileUrl = courseMaterial.getFileUrl();

        // 创建时必填项校验
        if (add) {
            if (courseId == null || courseId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
            }
            if (StringUtils.isBlank(title)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "资料标题不能为空");
            }
            if (StringUtils.isBlank(fileUrl)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件URL不能为空");
            }
        }

        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "资料标题过长");
        }
    }
} 
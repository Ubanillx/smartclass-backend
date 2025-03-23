package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.teacher.TeacherQueryRequest;
import com.ubanillx.smartclass.model.entity.Teacher;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.TeacherVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 讲师服务
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 校验讲师信息
     *
     * @param teacher
     * @param add 是否为创建校验
     */
    void validTeacher(Teacher teacher, boolean add);

    /**
     * 获取查询条件
     *
     * @param teacherQueryRequest
     * @return
     */
    QueryWrapper<Teacher> getQueryWrapper(TeacherQueryRequest teacherQueryRequest);

    /**
     * 获取讲师封装
     *
     * @param teacher
     * @param request
     * @return
     */
    TeacherVO getTeacherVO(Teacher teacher, HttpServletRequest request);

    /**
     * 获取讲师封装列表
     *
     * @param teacherList
     * @param request
     * @return
     */
    List<TeacherVO> getTeacherVO(List<Teacher> teacherList, HttpServletRequest request);

    /**
     * 分页获取讲师封装
     *
     * @param teacherPage
     * @param request
     * @return
     */
    Page<TeacherVO> getTeacherVOPage(Page<Teacher> teacherPage, HttpServletRequest request);

    /**
     * 添加讲师
     *
     * @param teacher
     * @param adminId
     * @return
     */
    long addTeacher(Teacher teacher, Long adminId);

    /**
     * 获取推荐讲师列表
     *
     * @param expertise 专业领域
     * @param limit 返回数量限制
     * @param request
     * @return
     */
    List<TeacherVO> getRecommendTeachers(String expertise, int limit, HttpServletRequest request);
}

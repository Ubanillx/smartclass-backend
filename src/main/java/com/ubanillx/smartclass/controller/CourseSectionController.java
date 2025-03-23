package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.file.UploadVideoRequest;
import com.ubanillx.smartclass.model.entity.CourseSection;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.CourseChapterService;
import com.ubanillx.smartclass.service.CourseSectionService;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 课程小节接口
 */
@RestController
@RequestMapping("course/section")
@Slf4j
public class CourseSectionController {

    @Resource
    private CourseSectionService courseSectionService;

    @Resource
    private CourseChapterService courseChapterService;

    @Resource
    private CourseService courseService;

    @Resource
    private UserService userService;

    @Resource
    private FileController fileController;

    /**
     * 创建课程小节
     *
     * @param courseSection
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addSection(@RequestBody CourseSection courseSection, HttpServletRequest request) {
        if (courseSection == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        long id = courseSectionService.addCourseSection(courseSection, loginUser.getId());
        return ResultUtils.success(id);
    }

    /**
     * 上传视频并创建课程小节
     *
     * @param file 视频文件
     * @param uploadVideoRequest 上传请求
     * @param request HTTP请求
     * @return 课程小节ID
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> uploadVideoAndAddSection(
            @RequestParam("file") MultipartFile file,
            UploadVideoRequest uploadVideoRequest,
            HttpServletRequest request) {
        
        // 参数校验
        if (uploadVideoRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        Long courseId = uploadVideoRequest.getCourseId();
        Long chapterId = uploadVideoRequest.getChapterId();
        
        // 校验课程和章节是否存在
        if (courseService.getById(courseId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程不存在");
        }
        if (courseChapterService.getById(chapterId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "章节不存在");
        }
        
        // 上传视频
        BaseResponse<String> uploadResponse = fileController.uploadVideo(file, request);
        String videoUrl = uploadResponse.getData();
        
        // 创建课程小节对象
        CourseSection courseSection = new CourseSection();
        courseSection.setCourseId(courseId);
        courseSection.setChapterId(chapterId);
        courseSection.setTitle(uploadVideoRequest.getTitle());
        courseSection.setDescription(uploadVideoRequest.getDescription());
        courseSection.setVideoUrl(videoUrl);
        courseSection.setSort(uploadVideoRequest.getSort());
        
        // 计算视频时长（这里需要用到视频处理库，简化版先不实现）
        // 设置默认时长5分钟，实际应用中应该提取视频真实时长
        courseSection.setDuration(300);
        
        // 保存课程小节
        User loginUser = userService.getLoginUser(request);
        long sectionId = courseSectionService.addCourseSection(courseSection, loginUser.getId());
        return ResultUtils.success(sectionId);
    }

    /**
     * 删除课程小节
     *
     * @param deleteRequest 删除请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteSection(@RequestBody DeleteRequest deleteRequest,
                                              HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        // 判断是否存在
        CourseSection oldSection = courseSectionService.getById(id);
        if (oldSection == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = courseSectionService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新课程小节
     *
     * @param courseSection
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSection(@RequestBody CourseSection courseSection,
                                              HttpServletRequest request) {
        if (courseSection == null || courseSection.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        CourseSection oldSection = courseSectionService.getById(courseSection.getId());
        if (oldSection == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = courseSectionService.updateById(courseSection);
        return ResultUtils.success(result);
    }
    
    /**
     * 更新课程小节视频
     *
     * @param file 新视频文件
     * @param sectionId 课程小节ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/update/video")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSectionVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sectionId") Long sectionId,
            HttpServletRequest request) {
        
        // 判断课程小节是否存在
        CourseSection oldSection = courseSectionService.getById(sectionId);
        if (oldSection == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "课程小节不存在");
        }
        
        // 上传新视频
        BaseResponse<String> uploadResponse = fileController.uploadVideo(file, request);
        String videoUrl = uploadResponse.getData();
        
        // 更新课程小节视频URL
        CourseSection updatedSection = new CourseSection();
        updatedSection.setId(sectionId);
        updatedSection.setVideoUrl(videoUrl);
        
        // 计算视频时长（简化版先不实现）
        // 设置默认时长5分钟，实际应用中应该提取视频真实时长
        updatedSection.setDuration(300);
        
        boolean result = courseSectionService.updateById(updatedSection);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取课程小节
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<CourseSection> getSectionById(@RequestParam("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        CourseSection courseSection = courseSectionService.getById(id);
        if (courseSection == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        return ResultUtils.success(courseSection);
    }

    /**
     * 根据课程ID获取小节列表
     *
     * @param courseId
     * @return
     */
    @GetMapping("/list/course")
    public BaseResponse<List<CourseSection>> listSectionsByCourseId(@RequestParam("courseId") Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<CourseSection> sectionList = courseSectionService.getSectionsByCourseId(courseId);
        return ResultUtils.success(sectionList);
    }

    /**
     * 根据章节ID获取小节列表
     *
     * @param chapterId
     * @return
     */
    @GetMapping("/list/chapter")
    public BaseResponse<List<CourseSection>> listSectionsByChapterId(@RequestParam("chapterId") Long chapterId) {
        if (chapterId == null || chapterId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<CourseSection> sectionList = courseSectionService.getSectionsByChapterId(chapterId);
        return ResultUtils.success(sectionList);
    }

    /**
     * 分页获取课程小节列表
     *
     * @param courseId
     * @param chapterId
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<CourseSection>> listSectionsByPage(
            @RequestParam(value = "courseId", required = false) Long courseId,
            @RequestParam(value = "chapterId", required = false) Long chapterId,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize) {
        if ((courseId == null || courseId <= 0) && (chapterId == null || chapterId <= 0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID和章节ID不能同时为空");
        }

        QueryWrapper<CourseSection> wrapper = courseSectionService.getQueryWrapper(courseId, chapterId);
        Page<CourseSection> sectionPage = courseSectionService.page(new Page<>(current, pageSize), wrapper);
        return ResultUtils.success(sectionPage);
    }

    /**
     * 获取课程总时长
     *
     * @param courseId
     * @return
     */
    @GetMapping("/duration")
    public BaseResponse<Integer> getTotalDuration(@RequestParam("courseId") Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int totalDuration = courseSectionService.getTotalDuration(courseId);
        return ResultUtils.success(totalDuration);
    }

    /**
     * 获取小节数量
     *
     * @param courseId
     * @return
     */
    @GetMapping("/count")
    public BaseResponse<Integer> getSectionCount(@RequestParam("courseId") Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int count = courseSectionService.countSections(courseId);
        return ResultUtils.success(count);
    }
} 
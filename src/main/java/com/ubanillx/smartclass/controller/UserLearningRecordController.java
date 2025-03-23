package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.learningrecord.UserLearningRecordAddRequest;
import com.ubanillx.smartclass.model.dto.learningrecord.UserLearningRecordQueryRequest;
import com.ubanillx.smartclass.model.dto.learningrecord.UserLearningRecordUpdateRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;
import com.ubanillx.smartclass.model.vo.UserLearningRecordVO;
import com.ubanillx.smartclass.service.UserLearningRecordService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户学习记录接口
 */
@RestController
@RequestMapping("/user/learning/record")
@Slf4j
public class UserLearningRecordController {

    @Resource
    private UserLearningRecordService userLearningRecordService;

    @Resource
    private UserService userService;

    // region 管理员接口 - 增删改查

    /**
     * 创建学习记录（仅管理员）
     *
     * @param userLearningRecordAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserLearningRecord(@RequestBody UserLearningRecordAddRequest userLearningRecordAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLearningRecordAddRequest == null, ErrorCode.PARAMS_ERROR);
        
        UserLearningRecord userLearningRecord = new UserLearningRecord();
        BeanUtils.copyProperties(userLearningRecordAddRequest, userLearningRecord);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 添加学习记录
        long id = userLearningRecordService.addUserLearningRecord(userLearningRecord, loginUser.getId());
        return ResultUtils.success(id);
    }

    /**
     * 删除学习记录（仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserLearningRecord(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        long id = deleteRequest.getId();
        
        // 判断是否存在
        UserLearningRecord userLearningRecord = userLearningRecordService.getById(id);
        ThrowUtils.throwIf(userLearningRecord == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅管理员可删除
        boolean result = userLearningRecordService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新学习记录（仅管理员）
     *
     * @param userLearningRecordUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserLearningRecord(@RequestBody UserLearningRecordUpdateRequest userLearningRecordUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLearningRecordUpdateRequest == null || userLearningRecordUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        
        UserLearningRecord userLearningRecord = new UserLearningRecord();
        BeanUtils.copyProperties(userLearningRecordUpdateRequest, userLearningRecord);
        
        // 参数校验
        userLearningRecordService.validUserLearningRecord(userLearningRecord, false);
        
        // 判断是否存在
        long id = userLearningRecordUpdateRequest.getId();
        UserLearningRecord oldUserLearningRecord = userLearningRecordService.getById(id);
        ThrowUtils.throwIf(oldUserLearningRecord == null, ErrorCode.NOT_FOUND_ERROR);
        
        boolean result = userLearningRecordService.updateById(userLearningRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取学习记录（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserLearningRecordVO> getUserLearningRecordById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        
        UserLearningRecord userLearningRecord = userLearningRecordService.getById(id);
        ThrowUtils.throwIf(userLearningRecord == null, ErrorCode.NOT_FOUND_ERROR);
        
        UserLearningRecordVO userLearningRecordVO = userLearningRecordService.getUserLearningRecordVO(userLearningRecord, request);
        return ResultUtils.success(userLearningRecordVO);
    }

    /**
     * 分页获取学习记录列表（仅管理员）
     *
     * @param userLearningRecordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserLearningRecordVO>> listUserLearningRecordByPage(@RequestBody UserLearningRecordQueryRequest userLearningRecordQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLearningRecordQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        long current = userLearningRecordQueryRequest.getCurrent();
        long size = userLearningRecordQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<UserLearningRecord> userLearningRecordPage = userLearningRecordService.page(
                new Page<>(current, size),
                userLearningRecordService.getQueryWrapper(userLearningRecordQueryRequest)
        );
        
        Page<UserLearningRecordVO> userLearningRecordVOPage = userLearningRecordService.getUserLearningRecordVOPage(userLearningRecordPage, request);
        return ResultUtils.success(userLearningRecordVOPage);
    }

    // endregion

    // region 用户接口 - 我的学习记录

    /**
     * 分页获取当前用户的学习记录
     *
     * @param userLearningRecordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<UserLearningRecordVO>> listMyUserLearningRecordByPage(@RequestBody UserLearningRecordQueryRequest userLearningRecordQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLearningRecordQueryRequest == null, ErrorCode.PARAMS_ERROR);
        
        User loginUser = userService.getLoginUser(request);
        
        // 设置只查询当前用户的记录
        userLearningRecordQueryRequest.setUserId(loginUser.getId());
        
        long current = userLearningRecordQueryRequest.getCurrent();
        long size = userLearningRecordQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<UserLearningRecord> userLearningRecordPage = userLearningRecordService.page(
                new Page<>(current, size),
                userLearningRecordService.getQueryWrapper(userLearningRecordQueryRequest)
        );
        
        Page<UserLearningRecordVO> userLearningRecordVOPage = userLearningRecordService.getUserLearningRecordVOPage(userLearningRecordPage, request);
        return ResultUtils.success(userLearningRecordVOPage);
    }

    /**
     * 获取我的学习时长统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param request
     * @return
     */
    @GetMapping("/my/stats/duration")
    public BaseResponse<List<Map<String, Object>>> getMyLearningDurationStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        List<Map<String, Object>> stats = userLearningRecordService.getUserLearningDurationStats(
                loginUser.getId(), startDate, endDate);
        
        return ResultUtils.success(stats);
    }

    /**
     * 获取我的学习数量统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param request
     * @return
     */
    @GetMapping("/my/stats/count")
    public BaseResponse<List<Map<String, Object>>> getMyLearningCountStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        List<Map<String, Object>> stats = userLearningRecordService.getUserLearningCountStats(
                loginUser.getId(), startDate, endDate);
        
        return ResultUtils.success(stats);
    }

    /**
     * 获取我的积分和经验值统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param request
     * @return
     */
    @GetMapping("/my/stats/points")
    public BaseResponse<Map<String, Object>> getMyPointsAndExperienceStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        Map<String, Object> stats = userLearningRecordService.getUserPointsAndExperienceStats(
                loginUser.getId(), startDate, endDate);
        
        return ResultUtils.success(stats);
    }

    /**
     * 获取我的每日学习记录数量统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param request
     * @return
     */
    @GetMapping("/my/stats/daily")
    public BaseResponse<List<Map<String, Object>>> getMyDailyLearningStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        List<Map<String, Object>> stats = userLearningRecordService.getUserDailyLearningStats(
                loginUser.getId(), startDate, endDate);
        
        return ResultUtils.success(stats);
    }

    /**
     * 记录课程学习
     *
     * @param courseId 课程ID
     * @param sectionId 章节ID
     * @param duration 学习时长（秒）
     * @param progress 学习进度（百分比）
     * @param request
     * @return
     */
    @PostMapping("/record/course")
    public BaseResponse<Long> recordCourseStudy(
            @RequestParam Long courseId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam Integer duration,
            @RequestParam(required = false) Integer progress,
            HttpServletRequest request) {
        ThrowUtils.throwIf(courseId == null || courseId <= 0, ErrorCode.PARAMS_ERROR, "课程ID不合法");
        ThrowUtils.throwIf(duration == null || duration < 0, ErrorCode.PARAMS_ERROR, "学习时长不能为负数");
        
        User loginUser = userService.getLoginUser(request);
        
        long recordId = userLearningRecordService.recordCourseStudy(
                loginUser.getId(), courseId, sectionId, duration, progress);
        
        return ResultUtils.success(recordId);
    }

    /**
     * 记录单词学习
     *
     * @param wordId 单词ID
     * @param count 学习数量
     * @param accuracy 正确率
     * @param request
     * @return
     */
    @PostMapping("/record/word")
    public BaseResponse<Long> recordWordStudy(
            @RequestParam Long wordId,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Double accuracy,
            HttpServletRequest request) {
        ThrowUtils.throwIf(wordId == null || wordId <= 0, ErrorCode.PARAMS_ERROR, "单词ID不合法");
        
        User loginUser = userService.getLoginUser(request);
        
        long recordId = userLearningRecordService.recordWordStudy(
                loginUser.getId(), wordId, count, accuracy);
        
        return ResultUtils.success(recordId);
    }

    /**
     * 记录听力练习
     *
     * @param listeningId 听力ID
     * @param duration 学习时长（秒）
     * @param accuracy 正确率
     * @param request
     * @return
     */
    @PostMapping("/record/listening")
    public BaseResponse<Long> recordListeningPractice(
            @RequestParam Long listeningId,
            @RequestParam Integer duration,
            @RequestParam(required = false) Double accuracy,
            HttpServletRequest request) {
        ThrowUtils.throwIf(listeningId == null || listeningId <= 0, ErrorCode.PARAMS_ERROR, "听力ID不合法");
        ThrowUtils.throwIf(duration == null || duration < 0, ErrorCode.PARAMS_ERROR, "学习时长不能为负数");
        
        User loginUser = userService.getLoginUser(request);
        
        long recordId = userLearningRecordService.recordListeningPractice(
                loginUser.getId(), listeningId, duration, accuracy);
        
        return ResultUtils.success(recordId);
    }

    /**
     * 记录阅读练习
     *
     * @param articleId 文章ID
     * @param duration 学习时长（秒）
     * @param accuracy 正确率
     * @param request
     * @return
     */
    @PostMapping("/record/reading")
    public BaseResponse<Long> recordReadingPractice(
            @RequestParam Long articleId,
            @RequestParam Integer duration,
            @RequestParam(required = false) Double accuracy,
            HttpServletRequest request) {
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR, "文章ID不合法");
        ThrowUtils.throwIf(duration == null || duration < 0, ErrorCode.PARAMS_ERROR, "学习时长不能为负数");
        
        User loginUser = userService.getLoginUser(request);
        
        long recordId = userLearningRecordService.recordReadingPractice(
                loginUser.getId(), articleId, duration, accuracy);
        
        return ResultUtils.success(recordId);
    }

    // endregion
} 